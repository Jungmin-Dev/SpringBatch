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
import org.springframework.batch.item.file.MultiResourceItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.ResourcePatternUtils
import org.springframework.transaction.PlatformTransactionManager
import java.io.IOException

/**
 * 다중 파일 txt -> DB 저장
 * @property resourceLoader ResourceLoader
 * @property entityManagerFactory EntityManagerFactory
 * @property chunkSize Int
 * @constructor
 */
@Configuration
class CsvToJpaJob2(
    private val resourceLoader: ResourceLoader,
    private val entityManagerFactory: EntityManagerFactory
) {

    private final val chunkSize = 5

    @Bean
    fun csvToJpaJob2_batchBuild(jobRepository: JobRepository, step: Step): Job {
        return JobBuilder("csvToJpaJob2", jobRepository)
            .start(step)
            .build()
    }

    @Bean
    fun csvToJpaJob2_batchStep1(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("csvToJpaJob2_batchStep1", jobRepository)
            .chunk<TwoDto, Dept>(chunkSize, transactionManager)
            .reader(csvToJpaJob2_FileReader())
            .processor(csvToJpaJob2_processor())
            .writer(csvToJpaJob2_dbItemWriter())
            .build()
    }

    @Bean
    fun csvToJpaJob2_dbItemWriter(): JpaItemWriter<Dept> {
        val jpaItemWriter: JpaItemWriter<Dept> = JpaItemWriter<Dept>()
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory)
        return jpaItemWriter
    }

    @Bean
    fun csvToJpaJob2_processor(): ItemProcessor<TwoDto, Dept> {
        return ItemProcessor { twoDto ->
            Dept(twoDto.one.toInt(), twoDto.two, "기타")
        }
    }

    @Bean
    fun csvToJpaJob2_FileReader(): MultiResourceItemReader<TwoDto> {
        val twoDtoMultiResourceItemReader: MultiResourceItemReader<TwoDto> = MultiResourceItemReader()

        try {
            twoDtoMultiResourceItemReader.setResources(
                ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                    .getResources("classpath:sample/csvToJpaJob2/*.txt")
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        twoDtoMultiResourceItemReader.setDelegate(multiFileItemReader())
        return twoDtoMultiResourceItemReader
    }

    @Bean
    fun multiFileItemReader(): FlatFileItemReader<TwoDto> {
        val flatFileItemReader: FlatFileItemReader<TwoDto> = FlatFileItemReader()

        flatFileItemReader.setLineMapper { line, lineNumber ->
            val lines = line.split("#")
            TwoDto(lines[0], lines[1])
        }

        return flatFileItemReader
    }
}
