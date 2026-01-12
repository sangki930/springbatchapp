package com.example.batch.domain

import java.time.LocalDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PersonRepository : JpaRepository<Person, Long> {
    fun findByLastConsentDateBefore(date: LocalDate, pageable: Pageable): Page<Person>
    fun findByIsConsentExpired(isExpired: Boolean, pageable: Pageable): Page<Person>
}
