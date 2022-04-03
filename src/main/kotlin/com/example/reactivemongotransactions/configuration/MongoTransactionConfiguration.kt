package com.example.reactivemongotransactions.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator

@Configuration
class MongoTransactionConfiguration {
    @Bean
    fun reactiveTransactionmanager(reactiveMongoDatabaseFactory: ReactiveMongoDatabaseFactory) =
        ReactiveMongoTransactionManager(reactiveMongoDatabaseFactory)

    @Bean
    fun transactionalOperator(reactiveTransactionManager: ReactiveTransactionManager) =
        TransactionalOperator.create(reactiveTransactionManager)
}