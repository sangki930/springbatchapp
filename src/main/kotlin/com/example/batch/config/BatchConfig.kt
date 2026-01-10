package com.example.batch.config

import com.example.batch.domain.Person
import com.example.batch.domain.PersonRepository
import com.example.batch.processor.PersonItemProcessor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.ListItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class BatchConfig(
    private val personRepository: PersonRepository
) {

    @Bean
    fun personItemReader(): ItemReader<Person> {
        return ListItemReader(
            listOf(
                Person(name = "alice", age = 20),
                Person(name = "bob", age = 25),
                Person(name = "charlie", age = 30)
            )
        )
    }

    @Bean
    fun personItemProcessor(): PersonItemProcessor {
        return PersonItemProcessor()
    }

    @Bean
    fun personItemWriter(): ItemWriter<Person> {
        return ItemWriter { items ->
            personRepository.saveAll(items)
        }
    }

    @Bean
    fun importUserStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("importUserStep", jobRepository)
            .chunk<Person, Person>(10, transactionManager)
            .reader(personItemReader())
            .processor(personItemProcessor())
            .writer(personItemWriter())
            .build()
    }

    @Bean
    fun importUserJob(
        jobRepository: JobRepository,
        importUserStep: Step
    ): Job {
        return JobBuilder("importUserJob", jobRepository)
            .start(importUserStep)
            .build()
    }
}
