package com.miska.core.base.suricata

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.miska.Miska
import com.miska.core.server.AlertRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class SuricataLogAnalyzer {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val processingAddresses = ConcurrentHashMap<Int, String>()
    private var rules: Set<Rule> = emptySet()
    private val suricataFirewallManager = SuricataFirewallManager()

    init {
        parseRules()
    }

    suspend fun analyzeAndMakeADecision(alert: AlertRequest) {
        if (processingAddresses.putIfAbsent(alert.hashCode(), alert.signature) != null) {
            info("${alert.srcIp} is already being processed.")

            return
        }

        scope.launch {
            val verdict = analyze(alert)

            makeADecision(verdict)

            processingAddresses.remove(alert.hashCode())
        }
    }

    private suspend fun makeADecision(verdict: Verdict) {
        if (verdict.action == "block" && verdict.targets != null) {
            val decisionRules = verdict.decisionRules

            verdict.targets!!.forEach { targetIp ->
                val decisionRule = decisionRules!!.removeFirst().second

                if (suricataFirewallManager.blockAddress(targetIp, verdict.alert.signature, decisionRule.timeout)) {

                    alert(
                        """
                        *Verdict:* `blocked ip $targetIp`
                        *Rule name:* `${decisionRule.name}`
                        *Date of detection:* `${verdict.alert.timestamp}`
                        *Signature:* `${verdict.alert.signature}`
                        *Category:* `${verdict.alert.category}`
                        *Severity:* `${verdict.alert.severity}`
                        """
                    )
                }
            }
        }

        if (verdict.action == "alert") {
            alert(
                """
                    *Verdict:* `a notification analysis is required`
                    *Date of detection:* `${verdict.alert.timestamp}`
                    *Signature:* `${verdict.alert.signature}`
                    *Category:* `${verdict.alert.category}`
                    *Severity:* `${verdict.alert.severity}`
                """
            )
        }
    }

    private suspend fun analyze(alert: AlertRequest): Verdict {
        val verdict = Verdict(alert)
        val matchingRules = findMatchesWithRules(alert)

        val blockedRules =
            matchingRules.filter { rule -> rule.action == "block" }.distinctBy { rule -> rule.target }

        if (blockedRules.isNotEmpty()) {
            val targets: MutableList<String> = ArrayList()
            val decisionRules: MutableList<Pair<String, Rule>> = ArrayList()

            val blockSrcRule = blockedRules.firstOrNull { rule -> rule.target == "src" }
            val blockDestRule = blockedRules.firstOrNull { rule -> rule.target == "dst" }

            if (blockSrcRule != null) {
                targets.add(alert.srcIp)
                decisionRules.add(Pair(alert.srcIp, blockSrcRule))
            }

            if (blockDestRule != null) {
                targets.add(alert.destIp)
                decisionRules.add(Pair(alert.destIp, blockDestRule))
            }

            if (targets.isNotEmpty()) {
                verdict.action = "block"
                verdict.targets = targets
            }

            verdict.decisionRules = decisionRules
        } else {
            val skipRules = matchingRules.filter { rule -> rule.action == "skip" }

            if (skipRules.isNotEmpty()) {
                verdict.decisionRules = listOf(Pair("stub", skipRules.first()))
            } else if (alert.severity in 1..2) {
                verdict.action = "alert"
            }
        }

        return verdict
    }

    private suspend fun findMatchesWithRules(alert: AlertRequest): Set<Rule> = rules.filter { rule ->
        rule.signaturesIds.contains(alert.signatureId) ||
                rule.signaturesMatches.any { alert.signature.startsWith(it) } ||
                rule.categoriesMatches.any { alert.category.startsWith(it) }
    }.toSet()

    private fun parseRules() {
        val filePath = Miska.app.getConfig().suricataIps.rulesFilepath
        val filename = Miska.app.getConfig().suricataIps.rulesFilename
        val dir = Miska.getBaseJarDir()

        val rulesFile = File(dir + File.separator + filePath + filename)

        if (rulesFile.exists()) {
            try {
                val wrapper = Gson().fromJson(rulesFile.readText(), RulesWrapper::class.java)

                rules = wrapper.rules
            } catch (exception: JsonSyntaxException) {
                info("Incorrect format of the rules file data")
                error("Incorrect format of the rules file data")
            } catch (exception: Exception) {
                info("Failed to download the rules file")
                error("Failed to download the rules file")
            }
        }

        info("Rules for comparison loaded: ${rules.count()}")
    }

    fun reloadRules() = parseRules()

}