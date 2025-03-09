package com.microtik.core.spring

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ip")
class TestController {

    @GetMapping
    fun getBookmarks(): Int {
        return 1
    }

    @GetMapping("/{id}")
    fun getBookmarkById(@PathVariable id: Int): String {
        return "Ответ ЗБС"
    }
}