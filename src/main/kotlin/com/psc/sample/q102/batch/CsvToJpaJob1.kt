package com.psc.sample.q102.batch

import com.psc.sample.q102.domain.Dept
import com.psc.sample.q102.dto.TwoDto
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.PlatformTransactionManager

/**
 * 단일파일 Csv -> DB 저장
 * @property entityManagerFactory EntityManagerFactory
 * @property chunkSize Int
 * @constructor
 */
@Configuration
class CsvToJpaJob1
    (
    private val entityManagerFactory: EntityManagerFactory
) {

    private val chunkSize = 1

    @Bean
    fun csvToJpaJob1_batchBuild(jobRepository: JobRepository?, step: Step): Job {
        return JobBuilder("csvToJpaJob1", jobRepository!!)
            .start(step)
            .build()
    }

    @Bean
    fun csvToJpaJob_batchStep1(
        jobRepository: JobRepository?,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("csvToJpaJob_batchStep1", jobRepository!!)
            .chunk<TwoDto, Dept>(chunkSize, transactionManager)
            .reader(csvToJpaJob1_FileReader())
            .processor(csvToJpaJob1_processor())
            .writer(csvToJpaJob1_dbItemWriter())
            .build()
    }

    @Bean
    fun csvToJpaJob1_processor(): ItemProcessor<in TwoDto, out Dept> {
        return ItemProcessor { twoDto ->
            Dept(twoDto.one.toInt(), twoDto.two, "기타")
        }
    }

    @Bean
    fun csvToJpaJob1_dbItemWriter(): JpaItemWriter<in Dept> {
        val jpaItemWriter = JpaItemWriter<Dept>()
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory)
        return jpaItemWriter
    }


    @Bean
    fun csvToJpaJob1_FileReader(): FlatFileItemReader<TwoDto> {
        val flatFileItemReader = FlatFileItemReader<TwoDto>()
        flatFileItemReader.setResource(ClassPathResource("/sample/csvToJpaJob1_input.csv"))

        val dtoDefaultLineMapper = DefaultLineMapper<TwoDto>()
        val delimitedLineTokenizer = DelimitedLineTokenizer()
        delimitedLineTokenizer.setNames("one", "two")
        delimitedLineTokenizer.setDelimiter(",")

        /**
         * 아래 사항으로 진행 시 파싱 에러가 나서 그 아래 코드 사용
         */
//        val beanWrapperFieldSetMapper = BeanWrapperFieldSetMapper<TwoDto>()
//        beanWrapperFieldSetMapper.setTargetType(TwoDto::class.java)
//        dtoDefaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper)

        /**
         * 위 에러 해결 방안
         */
        dtoDefaultLineMapper.setFieldSetMapper { fieldSet ->
            val name: String = fieldSet.readString("one")
            val age: String = fieldSet.readString("two")
            TwoDto(name, age)
        }

        dtoDefaultLineMapper.setLineTokenizer(delimitedLineTokenizer)
        flatFileItemReader.setLineMapper(dtoDefaultLineMapper)
        return flatFileItemReader
    }
}
