package com.example.reactivemongotransactions.repository.annotation

import com.example.reactivemongotransactions.repository.MongoConfig
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext

@DataMongoTest
@DirtiesContext
@Import(MongoConfig::class)
annotation class DataMongoTestWithReplicaSet

