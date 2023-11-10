package com.psc.sample.q102.Controller

import com.psc.sample.q102.dto.RestJobDto
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RestJobController(
    @Autowired
    private val jobLauncher: JobLauncher,
    @Autowired
    private val jobExplorer: JobExplorer,
    @Qualifier("job") @Autowired
    private val job: Job,  // Job 빈을 직접 주입
) {
    @PostMapping(path = ["/run"])
    @Throws(Exception::class)
    fun runJob(@RequestBody request: RestJobDto): ExitStatus {
        // JobParametersBuilder 의 getNextJobParameters 메서드는 파라미터를 증가 시킨다 ex) run.id=1 이후 run.id=2 ..
        val jobParameters = JobParametersBuilder(
            request.getJobParameters(),
            jobExplorer
        )
            .getNextJobParameters(job)
            .toJobParameters()
        return jobLauncher.run(job, jobParameters).exitStatus
    }
}
