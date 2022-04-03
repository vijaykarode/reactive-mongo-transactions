package com.example.reactivemongotransactions

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class ReactiveMongoTransactionsApplication

fun main(args: Array<String>) {
    System.setProperty("os.arch", "i686_64")
    runApplication<ReactiveMongoTransactionsApplication>(*args)
}
