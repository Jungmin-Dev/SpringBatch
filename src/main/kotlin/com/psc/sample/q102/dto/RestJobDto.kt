package com.psc.sample.q102.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import java.util.*

data class RestJobDto(
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("jobParameters")
    var jobParamsProperties: Properties? = null
) {
    fun getJobParameters(): JobParameters {
        val jobParametersBuilder = JobParametersBuilder()
        // jobParamsProperties가 null이 아닌 경우에만 추가
        if (jobParamsProperties != null) {
            for ((key, value) in jobParamsProperties!!) {
                jobParametersBuilder.addString(key.toString(), value.toString())
            }
        }
        return jobParametersBuilder.toJobParameters()
    }
}
