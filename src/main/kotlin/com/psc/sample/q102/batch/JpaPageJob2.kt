package com.psc.sample.q102.batch

import com.psc.sample.q102.domain.Dept
import com.psc.sample.q102.domain.Dept2
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

/**
 * DEPT 테이블에 데이터를 DEPT2로 복사하는 Batch
 * @property entityManagerFactory EntityManagerFactory
 * @property chunkSize Int
 * @constructor
 */
@Configuration
class JpaPageJob2(
    private val entityManagerFactory: EntityManagerFactory
) {

    private final val chunkSize = 10

    @Bean
    fun JpaPageJob2_batchBuild(jobRepository: JobRepository?, sampleStep: Step?): Job {
        return JobBuilder("jpaPageJob2", jobRepository!!)
            .start(sampleStep!!)
            .build()
    }

    @Bean
    fun jpaPageJob2_step1(
        jobRepository: JobRepository?,
        transactionManager: PlatformTransactionManager?
    ): Step {
        return StepBuilder("jpaPageJob2_step1", jobRepository!!)
            .chunk<Dept, Dept2>(chunkSize, transactionManager!!)
            .reader(jpaPageJob2_dbItemReader())
            .processor(jpaPageJob2_processor())
            .writer(jpaPageJob2_dbItemWriter())
            // error skip(갯수 수정하고) 하고 다음 text 복사하기
//            .faultTolerant()
//            .skip(FlatFileParseException::class.java)
//            .skipLimit(2)
            .build()
    }

    private fun jpaPageJob2_processor(): ItemProcessor<Dept, Dept2> {
        return ItemProcessor<Dept, Dept2> { dept ->
            Dept2(dept.deptNo, "NEW_" + dept.dName, "NEW_" + dept.loc)
        }
    }

    @Bean
    fun jpaPageJob2_dbItemReader(): JpaPagingItemReader<Dept> {
        return JpaPagingItemReaderBuilder<Dept>()
            .name("jpaPageJob2_dbItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString("SELECT d FROM Dept d order by deptNo asc")
            .build()
    }

    @Bean
    fun jpaPageJob2_dbItemWriter(): JpaItemWriter<Dept2> {
        var jpaItemWriter = JpaItemWriter<Dept2>()
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory)
        return jpaItemWriter
    }

}
