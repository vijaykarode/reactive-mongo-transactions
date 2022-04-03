package com.example.reactivemongotransactions.service

import com.example.reactivemongotransactions.model.ApplicationData
import com.example.reactivemongotransactions.repository.ApplicationRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Component
class ApplicationService(
    val applicationRepository: ApplicationRepository
) {
    @Transactional
    fun processWithTransactional(applicationData: ApplicationData): Mono<ApplicationData> {
        return applicationRepository
            .save(applicationData)
            .doOnNext { checkForEvenValue(it.value) }
    }

    fun processWithoutTransactional(applicationData: ApplicationData): Mono<ApplicationData> {
        return applicationRepository
            .save(applicationData)
            .doOnNext { checkForEvenValue(it.value) }
    }

    private fun checkForEvenValue(value: Int): Int {
        return value.also {
            if (it.mod(2) != 0)
                throw RuntimeException("Even value expected")
        }
    }
}