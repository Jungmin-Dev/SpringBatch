package com.psc.sample.q102.domain

import org.springframework.data.jpa.repository.JpaRepository

interface TwoRepository : JpaRepository<Two, String> {
}
