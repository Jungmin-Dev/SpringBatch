package com.psc.sample.q102.batch

import com.psc.sample.q102.domain.Two
import com.psc.sample.q102.dto.TwoDto
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.transaction.PlatformTransactionManager

/**
 * jobParameters 받아서 inFileName 지정
 * txt -> DB 저장
 * @property entityManagerFactory EntityManagerFactory
 * @property chunkSize Int
 * @constructor
 */
@Configuration
class CsvToJpaJob3(
    private val entityManagerFactory: EntityManagerFactory
) {

    private final val chunkSize: Int = 5


    @Bean
    fun csvToJpaJob3_batchBuild(jobRepository: JobRepository, step: Step): Job {
        return JobBuilder("csvToJpaJob3", jobRepository)
            .start(step)
            .build()
    }

    @Bean
    fun csvToJpaJob3_batchStep1(jobRepository: JobRepository, transactionManager: PlatformTransactionManager): Step {
        return StepBuilder("csvToJpaJob3_batchStep1", jobRepository)
            .chunk<TwoDto, Two>(chunkSize, transactionManager)
            .reader(csvToJpaJob3_Reader(null))
            .processor(csvToJpaJob3_processor())
            .writer(csvToJpaJob3_dbItemWriter())
            .build()
    }

    @Bean
    fun csvToJpaJob3_processor(): ItemProcessor<TwoDto, Two> {
        return ItemProcessor { twoDto -> Two(twoDto.one, twoDto.two) }
    }

    @Bean
    fun csvToJpaJob3_dbItemWriter(): JpaItemWriter<Two> {
        val jpaItemWriter: JpaItemWriter<Two> = JpaItemWriter<Two>()
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory)
        return jpaItemWriter
    }

    @Bean
    @StepScope
    fun csvToJpaJob3_Reader(@Value("#{jobParameters[inFileName]}") inFileName: String?): FlatFileItemReader<TwoDto> {

        val customRecordSeparatorPolicy = object : SimpleRecordSeparatorPolicy() {
            override fun postProcess(record: String): String {
                if (record.indexOf(":") == -1) {
                    return ""
                }
                return record.trim()
            }
        }

        val tokenizer = DelimitedLineTokenizer(":")

        return FlatFileItemReaderBuilder<TwoDto>()
            .name("csvToJpaJob3_Reader")
            .resource(FileSystemResource(inFileName!!))
            .lineTokenizer(tokenizer) // LineTokenizer를 설정
            .fieldSetMapper { fieldSet ->
                TwoDto(
                    fieldSet.readString(0),
                    fieldSet.readString(1)
                )
            }
            .recordSeparatorPolicy(customRecordSeparatorPolicy)
            .build()
    }
}
