package com.psc.sample.q102.Scheduler

import com.psc.sample.q102.batch.RestJob1
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException
import org.springframework.batch.core.repository.JobRestartException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


/**
 * cron 을 이용한 스케줄러
 */
@Component
class BatchScheduler(
    private val jobLauncher: JobLauncher? = null,
    private val batchConfig: RestJob1? = null // Job 클래스
) {
    private val log = KotlinLogging.logger {}

    @Scheduled(cron = "*/10 * * * * *")
    fun runJob() {

        // job parameter 설정
        val confMap: MutableMap<String, JobParameter<*>> = HashMap()
        confMap.put("time", JobParameter(System.currentTimeMillis(), Long::class.java))
        val jobParameters = JobParameters(confMap)
        try {
            batchConfig?.let { jobLauncher!!.run(it.textJob1_batchBuild(null, null), jobParameters) }
        } catch (e: JobExecutionAlreadyRunningException) {
            log.error(e.message)
        } catch (e: JobInstanceAlreadyCompleteException) {
            log.error(e.message)
        } catch (e: JobParametersInvalidException) {
            log.error(e.message)
        } catch (e: JobRestartException) {
            log.error(e.message)
        }
    }
}
