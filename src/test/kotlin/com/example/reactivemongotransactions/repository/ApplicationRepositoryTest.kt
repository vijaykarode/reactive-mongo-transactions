package com.example.reactivemongotransactions.repository

import com.example.reactivemongotransactions.model.ApplicationData
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import reactor.test.StepVerifier

@DataMongoTest
internal class ApplicationRepositoryTest {
    @Autowired
    lateinit var applicationRepository: ApplicationRepository

    @Test
    fun `should save applicationData in DB`() {
        val applicationData = ApplicationData(value = 1)
        StepVerifier.create(applicationRepository.save(applicationData))
            .consumeNextWith {
                it.value shouldBe 1
                it.id.length shouldBeGreaterThan 1
            }
            .verifyComplete()
    }
}