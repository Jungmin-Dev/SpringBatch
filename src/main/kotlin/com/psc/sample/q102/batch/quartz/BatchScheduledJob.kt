package com.psc.sample.q102.batch.quartz

/**
 * 쿼츠를 이용한 스케줄러
 */
//class BatchScheduledJob(
//    private val job: Job,
//    private val jobExplorer: JobExplorer,
//    private val jobLauncher: JobLauncher
//) : QuartzJobBean() {
//    override fun executeInternal(context: JobExecutionContext) {
//        val jobParameters = JobParametersBuilder(this.jobExplorer)
//            .getNextJobParameters(this.job)
//            .toJobParameters()
//        try {
//            this.jobLauncher.run(this.job, jobParameters);
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}

