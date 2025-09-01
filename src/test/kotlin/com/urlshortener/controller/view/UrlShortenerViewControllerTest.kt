package com.urlshortener.controller.view

import com.urlshortener.constants.Constants.DOMAIN
import com.urlshortener.service.UrlShortener
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class UrlShortenerViewControllerTest {

    private lateinit var mockMvc: MockMvc
    private val urlShortener: UrlShortener = mock()

    @BeforeEach
    fun setup() {
        val controller = UrlShortenerViewController(urlShortener)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `GET home should return url_form view`() {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
            .andExpect(view().name("url_form"))
    }

    @Test
    fun `POST shortenUrlAjax should return short URL`() {
        val originalUrl = "https://example.com"
        val shortUrl = "$DOMAIN/abc123"

        whenever(urlShortener.shorten(originalUrl)).thenReturn(shortUrl)

        mockMvc.perform(
            post("/shorten")
                .param("originalUrl", originalUrl)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(shortUrl))
    }

    @Test
    fun `POST shortenUrlAjax should return invalid URL message`() {
        val invalidUrl = "invalid-url"

        mockMvc.perform(
            post("/shorten")
                .param("originalUrl", invalidUrl)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Invalid url: $invalidUrl"))
    }

    @Test
    fun `POST retrieveUrlAjax should return original URL`() {
        val shortUrl = "$DOMAIN/abc123"
        val originalUrl = "https://example.com"

        whenever(urlShortener.retrieve(shortUrl)).thenReturn(originalUrl)

        mockMvc.perform(
            post("/retrieve")
                .param("shortUrl", shortUrl)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(originalUrl))
    }

    @Test
    fun `POST retrieveUrlAjax should return invalid domain`() {
        val invalidShortUrl = "https://wrong.ly/abc123"

        mockMvc.perform(
            post("/retrieve")
                .param("shortUrl", invalidShortUrl)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Invalid url: $invalidShortUrl"))
    }

    @Test
    fun `POST retrieveUrlAjax should return not found message`() {
        val shortUrl = "$DOMAIN/unknown"

        whenever(urlShortener.retrieve(shortUrl)).thenReturn(null)

        mockMvc.perform(
            post("/retrieve")
                .param("shortUrl", shortUrl)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Short URL not found"))
    }
}