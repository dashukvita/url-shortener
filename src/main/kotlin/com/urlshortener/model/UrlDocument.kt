package com.urlshortener.model

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@Document(collection = "urls")
data class UrlDocument(
    @Id
    val id: String? = null,

    @field:NotNull
    @field:NotEmpty
    @Field(name = "shortUrl")
    @Indexed(unique = true)
    val shortUrl: String,

    @field:NotNull
    @field:NotEmpty
    @Field(name = "longUrl")
    @Indexed(unique = true)
    val longUrl: String,

    val createdAt: Instant = Instant.now()
)
