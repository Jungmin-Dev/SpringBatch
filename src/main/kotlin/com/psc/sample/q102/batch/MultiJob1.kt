package com.psc.sample.q102.batch

import com.psc.sample.q102.dto.TwoDto
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.transaction.PlatformTransactionManager

/**
 * jobParameters 이용하기
 * txt -> txt 만들기
 * @property log KLogger
 * @property chunkSize Int
 */
@Configuration
class MultiJob1 {

    private val log = KotlinLogging.logger {}

    private final val chunkSize: Int = 5

    @Bean
    fun multiJob1_batchBuild(jobRepository: JobRepository): Job {
        return JobBuilder("multiJob1", jobRepository)
            .start(multiJob1_batchStep1(null, null, null))
            .build()
    }

    @Bean
    @JobScope
    fun multiJob1_batchStep1(
        jobRepository: JobRepository?, transactionManager: PlatformTransactionManager?,
        @Value("#{jobParameters[version]}") version: String?
    ): Step {
        log.debug("----")
        log.debug(version)
        log.debug("----")

        return StepBuilder("multiJob1_batchStep1", jobRepository!!)
            .chunk<TwoDto, TwoDto>(chunkSize, transactionManager!!)
            .reader(multiJob1_Reader(null))
            .processor(multiJob1_processor(null))
            .writer(multiJob1_writer(null))
            .build()
    }

//    @Bean
//    @StepScope
//    fun multiJob1_Reader(@Value("#{jobParameters[inFileName]}") inFileName: String?): FlatFileItemReader<TwoDto> {
//        val customRecordSeparatorPolicy = object : SimpleRecordSeparatorPolicy() {
//            override fun postProcess(record: String): String {
//                if (record.indexOf(":") == -1) {
//                    return "";
//                }
//                return record.trim();
//            }
//        }
//        return FlatFileItemReaderBuilder<TwoDto>()
//            .name("multiJob1_Reader")
//            .resource(ClassPathResource("sample/$inFileName"))
//            .delimited().delimiter(":")
//            .names("one", "two")
//            .targetType(TwoDto::class.java)
//            .recordSeparatorPolicy(customRecordSeparatorPolicy)
//            .build()
//    }

    /**
     * 위 에러 해결 방안
     */
    @Bean
    @StepScope
    fun multiJob1_Reader(@Value("#{jobParameters[inFileName]}") inFileName: String?): FlatFileItemReader<TwoDto> {
        val customRecordSeparatorPolicy = object : SimpleRecordSeparatorPolicy() {
            override fun postProcess(record: String): String {
                if (record.indexOf(":") == -1) {
                    return ""
                }
                return record.trim()
            }
        }

        val tokenizer = DelimitedLineTokenizer() // DelimitedLineTokenizer를 생성
        tokenizer.setDelimiter(":") // 필드를 콜론으로 분리

        return FlatFileItemReaderBuilder<TwoDto>()
            .name("multiJob1_Reader")
            .resource(ClassPathResource("sample/$inFileName"))
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


    @Bean
    @StepScope
    fun multiJob1_processor(@Value("#{jobParameters[version]}") version: String?): ItemProcessor<TwoDto, TwoDto> {
        log.debug("processor + ${version}")
        return ItemProcessor { twoDto -> TwoDto(twoDto.one, twoDto.two) }
    }

    @Bean
    @StepScope
    fun multiJob1_writer(@Value("#{jobParameters[outFileName]}") outFileName: String?): FlatFileItemWriter<TwoDto> {
        return FlatFileItemWriterBuilder<TwoDto>()
            .name("multiJob1_writer")
            .resource(FileSystemResource("sample/${outFileName}"))
            .lineAggregator { item ->
                "${item.one} ----- ${item.two}"
            }
            .build()
    }
}
