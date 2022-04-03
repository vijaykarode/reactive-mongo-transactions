package com.example.reactivemongotransactions.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("data")
data class ApplicationData(
    val value: Int
) {
    @Id
    lateinit var id: String
}
