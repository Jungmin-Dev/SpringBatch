package com.psc.sample.q102.batch

import com.psc.sample.q102.dto.OneDto
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.transaction.PlatformTransactionManager

/**
 * txt 파일 읽어서 txt파일 만들기
 * @property chunkSize Int
 */
@Configuration
class TextJob2 {

    private final val chunkSize = 5

    @Bean
    fun textJob2_batchBuild(jobRepository: JobRepository, textJob2_batchStep1: Step?): Job {
        return JobBuilder("textJob2", jobRepository)
            .start(textJob2_batchStep1!!)
            .build()
    }

    @Bean

    fun textJob2_batchStep1(jobRepository: JobRepository?, transactionManager: PlatformTransactionManager?): Step {
        return StepBuilder("textJob2_batchStep1", jobRepository!!)
            .chunk<OneDto, OneDto>(chunkSize, transactionManager!!)
            .reader(textJob2_FileReader())
            .writer(textJob2_FileWriter())
            .build()
    }

    @Bean
    fun textJob2_FileReader(): FlatFileItemReader<OneDto> {
        val flatFileItemReader: FlatFileItemReader<OneDto> = FlatFileItemReader<OneDto>()
        flatFileItemReader.setResource(ClassPathResource("sample/textJob2_input.txt"))
        flatFileItemReader.setLineMapper { line, lineNumber -> OneDto("$lineNumber == $line") }
        return flatFileItemReader
    }

    @Bean
    fun textJob2_FileWriter(): FlatFileItemWriter<OneDto> {
        return FlatFileItemWriterBuilder<OneDto>()
            .name("textJob2_FileWriter")
            .resource(FileSystemResource("output/textJob_output.txt"))
            .lineAggregator { item ->
                item.one
            }
            .build()
    }

}
