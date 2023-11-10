package com.psc.sample.q102.configuration


/**
 * 쿼츠 설정
 */
//@Configuration
//class QuartzConfiguration {
//    @Bean
//    fun quartzJobDetail(): JobDetail {
//        return JobBuilder.newJob(BatchScheduledJob::class.java)
//            .storeDurably()
//            .build()
//
//    }
//
//    @Bean
//    fun jobTrigger(): Trigger {
//        val scheduleBuilder: SimpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
//            // withRepeatCount 반복 횟수
//            .withIntervalInSeconds(5).withRepeatCount(2)
//        return TriggerBuilder.newTrigger()
//            .forJob(quartzJobDetail())
//            .withSchedule(scheduleBuilder)
//            .build()
//    }
//}
//
//
