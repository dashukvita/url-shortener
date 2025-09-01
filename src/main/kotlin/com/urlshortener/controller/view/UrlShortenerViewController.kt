package com.urlshortener.controller.view

import com.urlshortener.service.UrlShortener
import com.urlshortener.validation.UrlValidator
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import com.urlshortener.constants.Constants.DOMAIN

@Controller
class UrlShortenerViewController(
    private val urlShortener: UrlShortener
) {
    @GetMapping("/")
    fun home(
        model: Model,
        @RequestParam(required = false) message: String?
    ): String {
        model.addAttribute("message", message)
        return "url_form"
    }

    @PostMapping("/shorten")
    @ResponseBody
    fun shortenUrlAjax(@RequestParam originalUrl: String): String {
        return if (UrlValidator.isNotValidUrl(originalUrl)) {
            "Invalid url: $originalUrl"
        } else {
            urlShortener.shorten(originalUrl)
        }
    }

    @PostMapping("/retrieve")
    @ResponseBody
    fun retrieveUrlAjax(@RequestParam shortUrl: String): String {
        if (!shortUrl.startsWith(DOMAIN)) {
            return "Invalid url: $shortUrl"
        }
        return urlShortener.retrieve(shortUrl) ?: "Short URL not found"
    }
}