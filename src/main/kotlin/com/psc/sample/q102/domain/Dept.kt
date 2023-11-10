package com.psc.sample.q102.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Dept(
    @Id
    val deptNo: Int,
    val dName: String,
    val loc: String
) {


}
