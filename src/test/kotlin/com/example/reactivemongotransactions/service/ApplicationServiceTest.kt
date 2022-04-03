package com.example.reactivemongotransactions.service

import com.example.reactivemongotransactions.model.ApplicationData
import com.example.reactivemongotransactions.repository.ApplicationRepository
import com.example.reactivemongotransactions.repository.annotation.DataMongoTestWithReplicaSet
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import reactor.test.StepVerifier

@DataMongoTestWithReplicaSet
@Import(ApplicationService::class)
internal class ApplicationServiceTest {
    private val evenApplicationData = ApplicationData(2)
    private val oddApplicationData = ApplicationData(1)
    @Autowired
    private lateinit var reactiveMongoOperations: ReactiveMongoOperations

    @Autowired
    private lateinit var applicationRepository: ApplicationRepository

    @Autowired
    private lateinit var applicationService: ApplicationService

    @BeforeEach
    fun setUp() {
        reactiveMongoOperations
            .collectionExists("data")
            .doOnNext {
                if(it==false)
                    reactiveMongoOperations.createCollection("data").block()
            }
            .block()
        applicationRepository.deleteAll().block()
    }

    @Test
    fun `should process and save the data when data is even and function invoked is Transactional`() {
        StepVerifier.create(applicationService.processWithTransactional(evenApplicationData))
            .consumeNextWith {
                it shouldBe evenApplicationData
            }
            .verifyComplete()

        applicationRepository.count().block() shouldBe 1
    }

    @Test
    fun `should not save the data when data is odd and function invoked is Transactional`() {
        StepVerifier.create(applicationService.processWithTransactional(oddApplicationData))
            .consumeErrorWith {
                (it is RuntimeException) shouldBe true
                it.message shouldBe "Even value expected"
            }
            .verify()

        applicationRepository.count().block() shouldBe 0

    }

    @Test
    fun `should process and save the data when data is even and function invoked is NOT Transactional`() {
        StepVerifier.create(applicationService.processWithoutTransactional(evenApplicationData))
            .consumeNextWith {
                it shouldBe evenApplicationData
            }
            .verifyComplete()

        applicationRepository.count().block() shouldBe 1
    }

    @Test
    fun `should still save the data when data is odd and function invoked is NOT Transactional`() {
        StepVerifier.create(applicationService.processWithoutTransactional(oddApplicationData))
            .consumeErrorWith {
                (it is RuntimeException) shouldBe true
                it.message shouldBe "Even value expected"
            }
            .verify()

        applicationRepository.count().block() shouldBe 1
    }
}