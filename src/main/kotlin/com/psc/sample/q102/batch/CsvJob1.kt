package com.psc.sample.q102.batch

import com.psc.sample.q102.dto.TwoDto
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor
import org.springframework.batch.item.file.transform.DelimitedLineAggregator
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.WritableResource
import org.springframework.transaction.PlatformTransactionManager

/**
 * csv 형식 데이터를 불러와 csv 형식으로 데이터 만들기
 * @property chunkSize Int
 */
@Configuration
class CsvJob1 {

    private final val chunkSize = 5

    @Bean
    fun csvJob1_batchJob1(jobRepository: JobRepository?, step: Step): Job {
        return JobBuilder("csvJob1", jobRepository!!)
            .start(step)
            .build()
    }

    @Bean
    fun csvJob1_batchStep1(jobRepository: JobRepository?, transactionManager: PlatformTransactionManager?): Step {
        return StepBuilder("csvJob1_batchStep1", jobRepository!!)
            .chunk<TwoDto, TwoDto>(chunkSize, transactionManager!!)
            .reader(csvJob1_FileReader())
            .writer(csvJob1_FileWriter(FileSystemResource("output/csvJob1_output.csv")))
            .build()
    }


    @Bean
    fun csvJob1_FileReader(): FlatFileItemReader<TwoDto> {
        val flatFileItemReader: FlatFileItemReader<TwoDto> = FlatFileItemReader<TwoDto>()
        flatFileItemReader.setResource(ClassPathResource("sample/csvJob1_input.csv"))
        flatFileItemReader.setLinesToSkip(1)
        flatFileItemReader.setEncoding("UTF-8"); //인코딩 설정


        val dtoDefaultLineMapper: DefaultLineMapper<TwoDto> = DefaultLineMapper<TwoDto>()

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

    @Bean
    fun csvJob1_FileWriter(resource: WritableResource): FlatFileItemWriter<TwoDto> {
        val beanWrapperFieldExtractor: BeanWrapperFieldExtractor<TwoDto> = BeanWrapperFieldExtractor<TwoDto>()
        beanWrapperFieldExtractor.setNames(arrayOf("one", "two"))
        beanWrapperFieldExtractor.afterPropertiesSet()

        val dtodelimitedLineAggregator: DelimitedLineAggregator<TwoDto> = DelimitedLineAggregator<TwoDto>()
        dtodelimitedLineAggregator.setDelimiter("@")
        dtodelimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor)

        return FlatFileItemWriterBuilder<TwoDto>().name("csvJob1_FileWriter")
            .resource(resource)
            .lineAggregator(dtodelimitedLineAggregator)
            .build()
    }
}
