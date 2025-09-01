package com.urlshortener.exception

import com.urlshortener.dto.ResponseDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException): ResponseEntity<ResponseDto> {
        val message = "Required request parameter '${ex.parameterName}' is missing"
        log.error(message, ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseDto(message))
    }

    @ExceptionHandler(UniqueHashGenerationException::class)
    fun handleUniqueHashGeneration(ex: UniqueHashGenerationException): ResponseEntity<ResponseDto> {
        val message = ex.message ?: "Failed to generate unique short URL"
        log.error(message, ex)
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ResponseDto(message))
    }
}