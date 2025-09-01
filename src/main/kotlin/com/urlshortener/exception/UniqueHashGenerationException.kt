package com.urlshortener.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.CONFLICT)
class UniqueHashGenerationException(message: String) : RuntimeException(message)