package com.example.batch.processor

import com.example.batch.domain.Person
import org.springframework.batch.item.ItemProcessor

class PersonItemProcessor : ItemProcessor<Person, Person> {
    override fun process(item: Person): Person {
        val uppercaseName = item.name.uppercase()
        return Person(name = uppercaseName, age = item.age)
    }
}
