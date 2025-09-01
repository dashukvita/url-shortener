package com.urlshortener.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.web.bind.MissingServletRequestParameterException

internal class GlobalExceptionHandlerUnitTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `handleMissingParam returns 400 with correct message`() {
        val ex = MissingServletRequestParameterException("name", "String")
        val response = handler.handleMissingParam(ex)

        assertEquals(400, response.statusCode.value())
        assertEquals("Required request parameter 'name' is missing", response.body?.data)
    }

    @Test
    fun `handleUniqueHashGeneration returns 409 with correct message`() {
        val ex = UniqueHashGenerationException("Test hash generation failed")
        val response = handler.handleUniqueHashGeneration(ex)

        assertEquals(409, response.statusCode.value())
        assertEquals("Test hash generation failed", response.body?.data)
    }
}
