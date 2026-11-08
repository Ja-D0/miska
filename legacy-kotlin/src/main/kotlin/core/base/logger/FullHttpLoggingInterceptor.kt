package com.miska.core.base.logger

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.GzipSource
import okio.use
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

internal fun Buffer.isProbablyUtf8(): Boolean {
    try {
        val prefix = Buffer()
        val byteCount = size.coerceAtMost(64)
        copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) {
                break
            }
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
        }
        return true
    } catch (_: EOFException) {
        return false // Truncated UTF-8 sequence.
    }
}

class FullHttpLoggingInterceptor @JvmOverloads constructor(
    private val logger: Logger = Logger.DEFAULT
) : Interceptor {

    @Volatile
    private var headersToRedact = emptySet<String>()

    @set:JvmName("level")
    @Volatile
    var level = Level.NONE

    enum class Level {
        /** No logs. */
        NONE,

        /**
         * Logs request and response lines.
         *
         * Example:
         * ```
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * ```
         */
        BASIC,

        /**
         * Logs request and response lines and their respective headers.
         *
         * Example:
         * ```
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * ```
         */
        HEADERS,

        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         *
         * Example:
         * ```
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * ```
         */
        BODY
    }

    fun interface Logger {
        fun log(message: String)

        companion object {
            /** A [Logger] defaults output appropriate for the current platform. */
            @JvmField
            val DEFAULT: Logger = DefaultLogger()

            private class DefaultLogger : Logger {
                override fun log(message: String) {
                    Platform.get().log(message)
                }
            }
        }
    }

    fun redactHeader(name: String) {
        val newHeadersToRedact = TreeSet(String.CASE_INSENSITIVE_ORDER)
        newHeadersToRedact += headersToRedact
        newHeadersToRedact += name
        headersToRedact = newHeadersToRedact
    }

    /**
     * Sets the level and returns this.
     *
     * This was deprecated in OkHttp 4.0 in favor of the [level] val. In OkHttp 4.3 it is
     * un-deprecated because Java callers can't chain when assigning Kotlin vals. (The getter remains
     * deprecated).
     */
    fun setLevel(level: Level) = apply {
        this.level = level
    }

    @JvmName("-deprecated_level")
    @Deprecated(
        message = "moved to var",
        replaceWith = ReplaceWith(expression = "level"),
        level = DeprecationLevel.ERROR
    )
    fun getLevel(): Level = level

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = this.level

        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }

        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS

        val requestBody = request.body

        val connection = chain.connection()
        var logMessage = StringBuilder() // Use StringBuilder for efficient string concatenation
        var requestStartMessage = "\n" +
                ("--> ${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}")
        if (!logHeaders && requestBody != null) {
            requestStartMessage += " (${requestBody.contentLength()}-byte body)"
        }
        logMessage.append(requestStartMessage).append("\n")

        if (logHeaders) {
            val headers = request.headers

            if (requestBody != null) {
                // Request body headers are only present when installed as a network interceptor. When not
                // already present, force them to be included (if available) so their values are known.
                requestBody.contentType()?.let {
                    if (headers["Content-Type"] == null) {
                        logMessage.append("Content-Type: $it").append("\n")
                    }
                }
                if (requestBody.contentLength() != -1L) {
                    if (headers["Content-Length"] == null) {
                        logMessage.append("Content-Length: ${requestBody.contentLength()}").append("\n")
                    }
                }
            }

            for (i in 0 until headers.size) {
                logMessage.append(logHeader(headers, i)).append("\n")
            }

            if (!logBody || requestBody == null) {
                logMessage.append("--> END ${request.method}").append("\n")
            } else if (bodyHasUnknownEncoding(request.headers)) {
                logMessage.append("--> END ${request.method} (encoded body omitted)").append("\n")
            } else if (requestBody.isDuplex()) {
                logMessage.append("--> END ${request.method} (duplex request body omitted)").append("\n")
            } else if (requestBody.isOneShot()) {
                logMessage.append("--> END ${request.method} (one-shot body omitted)").append("\n")
            } else {
                val buffer = Buffer()
                requestBody.writeTo(buffer)

                val contentType = requestBody.contentType()
                val charset: Charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

                logMessage.append("\n")
                if (buffer.isProbablyUtf8()) {
                    logMessage.append(buffer.readString(charset)).append("\n")
                    logMessage.append("--> END ${request.method} (${requestBody.contentLength()}-byte body)")
                        .append("\n")
                } else {
                    logMessage.append(
                        "--> END ${request.method} (binary ${requestBody.contentLength()}-byte body omitted)"
                    ).append("\n")
                }
            }
        }

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logMessage.append("<-- HTTP FAILED: $e").append("\n")
            logger.log(logMessage.toString()) // Log the accumulated message *before* throwing
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        logMessage.append(
            "<-- ${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms${if (!logHeaders) ", $bodySize body" else ""})"
        ).append("\n")

        if (logHeaders) {
            val headers = response.headers
            for (i in 0 until headers.size) {
                logMessage.append(logHeader(headers, i)).append("\n")
            }

            if (!logBody || !response.promisesBody()) {
                logMessage.append("<-- END HTTP").append("\n")
            } else if (bodyHasUnknownEncoding(response.headers)) {
                logMessage.append("<-- END HTTP (encoded body omitted)").append("\n")
            } else {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                var buffer = source.buffer

                var gzippedLength: Long? = null
                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    gzippedLength = buffer.size
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }

                val contentType = responseBody.contentType()
                val charset: Charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

                if (!buffer.isProbablyUtf8()) {
                    logMessage.append("\n")
                    logMessage.append("<-- END HTTP (binary ${buffer.size}-byte body omitted)").append("\n")
                    logger.log(logMessage.toString()) // Log here to avoid early return issues
                    return response
                }

                if (contentLength != 0L) {
                    logMessage.append("\n")
                    logMessage.append(buffer.clone().readString(charset)).append("\n")
                }

                if (gzippedLength != null) {
                    logMessage.append("<-- END HTTP (${buffer.size}-byte, $gzippedLength-gzipped-byte body)")
                        .append("\n")
                } else {
                    logMessage.append("<-- END HTTP (${buffer.size}-byte body)").append("\n")
                }
            }
        }

        logger.log(logMessage.toString()) // Finally, log the entire message
        return response
    }

    private fun logHeader(headers: Headers, i: Int): String {
        val value = if (headers.name(i) in headersToRedact) "██" else headers.value(i)
        return headers.name(i) + ": " + value
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }
}