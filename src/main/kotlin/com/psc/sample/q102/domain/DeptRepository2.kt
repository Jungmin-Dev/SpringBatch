package com.psc.sample.q102.domain

import org.springframework.data.jpa.repository.JpaRepository

interface DeptRepository2 : JpaRepository<Dept, Int> {
}
