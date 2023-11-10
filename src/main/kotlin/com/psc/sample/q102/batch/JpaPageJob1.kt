package com.psc.sample.q102.batch

import com.psc.sample.q102.domain.Dept
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

/**
 * DEPT 테이블에 데이터를 출력하는 Batch
 * @property entityManagerFactory EntityManagerFactory
 * @property chunkSize Int
 * @constructor
 */
@Configuration
class JpaPageJob1(
    private val entityManagerFactory: EntityManagerFactory
) {

    private final val chunkSize = 10

    @Bean
    fun JpaPageJob1_batchBuild(jobRepository: JobRepository?, sampleStep: Step?): Job {
        return JobBuilder("jpaPageJob1", jobRepository!!)
            .start(sampleStep!!)
            .build()
    }

    @Bean
    fun JpaPageJob1_step1(
        jobRepository: JobRepository?,
        transactionManager: PlatformTransactionManager?
    ): Step {
        return StepBuilder("jpaPageJob1_step1", jobRepository!!)
            .chunk<Dept, Dept>(chunkSize, transactionManager!!)
            .reader(jpaPageJob1_dbItemReader())
            .writer(jpaPageJob1_printItemWriter())
            .build()
    }

    @Bean
    fun jpaPageJob1_dbItemReader(): JpaPagingItemReader<Dept> {
        return JpaPagingItemReaderBuilder<Dept>()
            .name("jpaPageJob1_dbItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString("SELECT d FROM Dept d order by deptNo asc")
            .build()
    }

    @Bean
    fun jpaPageJob1_printItemWriter(): ItemWriter<Dept> {
        return ItemWriter<Dept> { list ->
            list.forEach { dept ->
                println(dept.toString())
            }
        }
    }
}
