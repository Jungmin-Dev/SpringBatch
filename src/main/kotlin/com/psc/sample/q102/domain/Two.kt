package com.psc.sample.q102.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Two(
    @Id
    val one: String,
    val two: String
) {

}
