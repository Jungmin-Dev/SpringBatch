package com.psc.sample.q102.batch

import com.psc.sample.q102.dto.OneDto
import org.springframework.batch.core.*
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.PlatformTransactionManager
import java.util.*

/**
 * REST 방식으로 잡 실행하기
 * 전송 데이터 :
 * {
 *     "name" : "job",
 *     "jobParameters" : {
 *         "foo": "bar",
 *         "baz": "quix",
 *         "requestDate":"20231108"
 *     }
 * }
 * @property chunkSize Int
 */
@Configuration
class RestJob1 {
    private final val chunkSize = 5

    @Bean(name = ["job"])
    fun textJob1_batchBuild(jobRepository: JobRepository?, transactionManager: PlatformTransactionManager?): Job {
        return JobBuilder("job", jobRepository!!)
            // "Job"에는 일반적으로 job parameters를 생성하는 incrementer가 필요합니다. 이 incrementer는 batch job의 실행을 식별하는 데 사용됩니다.
            .incrementer(RunIdIncrementer())
            .start(textJob1_batchStep1(jobRepository, transactionManager!!))
            .build()
    }

    @Bean
    @Primary
    fun textJob1_batchStep1(jobRepository: JobRepository, transactionManager: PlatformTransactionManager): Step {
        return StepBuilder("textJob1", jobRepository)
            .allowStartIfComplete(true) // 반복 작업을 위한 스텝 설정
            .chunk<OneDto, OneDto>(chunkSize, transactionManager)
            .reader(textJob1_FileReader(null))
            .writer { oneDto ->
                oneDto.forEach { item ->
                    println(item.one)
                }
            }
            .build()
    }

    @Bean
    @StepScope
    fun textJob1_FileReader(@Value("#{jobParameters[requestDate]}") requestDate: String?): FlatFileItemReader<OneDto> {
        val flatFileItemReader: FlatFileItemReader<OneDto> = FlatFileItemReader<OneDto>()
        flatFileItemReader.setResource(ClassPathResource("sample/textJob1_input.txt"))
        flatFileItemReader.setLineMapper { line, lineNumber -> OneDto("$lineNumber == $line ${requestDate} ") }
        return flatFileItemReader
    }
}

