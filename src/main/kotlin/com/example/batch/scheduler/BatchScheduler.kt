package com.example.batch.scheduler

import java.time.LocalDateTime
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BatchScheduler(private val jobLauncher: JobLauncher, private val privacyConsentJob: Job) {

    @Scheduled(cron = "0 * * * * *") // 매 분마다 실행 (검증용)
    fun runPrivacyConsentJob() {
        val jobParameters =
                JobParametersBuilder()
                        .addString("datetime", LocalDateTime.now().toString())
                        .toJobParameters()

        jobLauncher.run(privacyConsentJob, jobParameters)
    }
}
