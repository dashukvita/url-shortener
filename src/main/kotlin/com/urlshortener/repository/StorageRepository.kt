package com.urlshortener.repository

import com.urlshortener.model.UrlDocument
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface StorageRepository : MongoRepository<UrlDocument, String> {
    fun findUrlDocumentByShortUrl(shortUrl: String): UrlDocument?
    fun findUrlDocumentByLongUrl(longUrl: String): UrlDocument?
}