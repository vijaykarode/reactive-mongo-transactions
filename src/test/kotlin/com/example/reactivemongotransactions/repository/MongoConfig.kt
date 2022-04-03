package com.example.reactivemongotransactions.repository

import com.mongodb.BasicDBList
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Feature
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.distribution.Versions
import de.flapdoodle.embed.process.runtime.Network
import org.bson.Document
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory
import reactor.kotlin.core.publisher.toMono
import java.util.*


@TestConfiguration
class MongoConfig : AbstractReactiveMongoConfiguration() {
    private val dbName: String = "admin"
    private val host: String = "localhost"
    val port = 27017

    @Value("\${spring.data.mongodb.database}")
    lateinit var userDbName: String

    @Bean
    @Primary
    fun transactionManager(
        dbFactory: ReactiveMongoDatabaseFactory,
        reactiveMongoOperations: ReactiveMongoOperations
    ): ReactiveMongoTransactionManager {
        return ReactiveMongoTransactionManager(dbFactory)
    }

    @Bean
    @Primary
    override fun reactiveMongoDbFactory(): ReactiveMongoDatabaseFactory {
        val starter = MongodStarter.getDefaultInstance()
        val features: EnumSet<Feature> = Version.Main.PRODUCTION.features
        val iFeatureAwareVersion: IFeatureAwareVersion = Versions.withFeatures(Version.Main.PRODUCTION, features)
        val mongodConfig = MongodConfigBuilder().version(iFeatureAwareVersion)
            .withLaunchArgument("--replSet", "rs0")
            .cmdOptions(MongoCmdOptionsBuilder().useNoJournal(false).build())
            .net(Net(port, Network.localhostIsIPv6())).build()
        val mongoDbExecutable: MongodExecutable = starter.prepare(mongodConfig)
        mongoDbExecutable.start()
        val reactiveMongoClient = reactiveMongoClient()
        members(reactiveMongoClient)
        return SimpleReactiveMongoDatabaseFactory(reactiveMongoClient, userDbName)
    }

    override fun getDatabaseName(): String {
        return dbName
    }

    override fun reactiveMongoClient(): MongoClient {
        return MongoClients.create("mongodb://$host:$port")
    }

    fun members(mongo: MongoClient) {
        val adminDatabase: MongoDatabase = mongo.getDatabase(dbName)
        val config = Document("_id", "rs0")
        val members = BasicDBList()
        members.add(
            Document("_id", 0)
                .append("host", "localhost:$port")
        )
        config["members"] = members
        adminDatabase.runCommand(Document("replSetInitiate", config)).toMono().block()
    }
}
