package com.example.batch.config

import com.example.batch.domain.Person
import com.example.batch.domain.PersonRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.data.RepositoryItemReader
import org.springframework.batch.item.data.RepositoryItemWriter
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDate

@Configuration
class PrivacyConsentBatchConfig(
    private val personRepository: PersonRepository
) {

    @Bean
    fun privacyConsentReader(): RepositoryItemReader<Person> {
        return RepositoryItemReaderBuilder<Person>()
            .name("privacyConsentReader")
            .repository(personRepository)
            .methodName("findByIsConsentExpired")
            .arguments(listOf(false))
            .pageSize(10)
            .sorts(mapOf("id" to Sort.Direction.ASC))
            .build()
    }

    @Bean
    fun privacyConsentProcessor(): ItemProcessor<Person, Person> {
        return ItemProcessor { person ->
            val oneYearAgo = LocalDate.now().minusYears(1)
            if (person.lastConsentDate.isBefore(oneYearAgo)) {
                person.copy(isConsentExpired = true)
            } else {
                person
            }
        }
    }

    @Bean
    fun privacyConsentWriter(): RepositoryItemWriter<Person> {
        return RepositoryItemWriterBuilder<Person>()
            .repository(personRepository)
            .methodName("save")
            .build()
    }

    @Bean
    fun privacyConsentStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("privacyConsentStep", jobRepository)
            .chunk<Person, Person>(10, transactionManager)
            .reader(privacyConsentReader())
            .processor(privacyConsentProcessor())
            .writer(privacyConsentWriter())
            .build()
    }

    @Bean
    fun privacyConsentJob(
        jobRepository: JobRepository,
        privacyConsentStep: Step
    ): Job {
        return JobBuilder("privacyConsentJob", jobRepository)
            .start(privacyConsentStep)
            .build()
    }
}
