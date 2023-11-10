package com.psc.sample.q102.batch

import com.psc.sample.q102.dto.CoinMarket
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller
import org.springframework.batch.item.json.JacksonJsonObjectReader
import org.springframework.batch.item.json.JsonFileItemWriter
import org.springframework.batch.item.json.JsonItemReader
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.transaction.PlatformTransactionManager

/**
 * json 파일 읽어서 json 파일 만들기
 * @property chunkSize Int
 */
@Configuration
class JsonJob2 {

    private final val chunkSize = 10

    @Bean
    fun JsonJob2_batchJob1(jobRepository: JobRepository?, step: Step?): Job {
        return JobBuilder("JsonJob2", jobRepository!!)
            .start(step!!)
            .build()
    }

    @Bean
    fun JsonJob2_batchStep1(jobRepository: JobRepository?, transactionManager: PlatformTransactionManager?): Step {
        return StepBuilder("JsonJob2_batchStep1", jobRepository!!)
            .chunk<CoinMarket, CoinMarket>(chunkSize, transactionManager!!)
            .reader(JsonJob2_jsonReader())
            .processor(jsonJob2_processor())
            .writer(jsonJob2_jsonWriter())
            .build()
    }

    private fun jsonJob2_processor(): ItemProcessor<CoinMarket, CoinMarket> {
        return ItemProcessor { coinMarket ->
            if (coinMarket.market.startsWith("KRW-")) {
                return@ItemProcessor CoinMarket(coinMarket.market, coinMarket.korean_name, coinMarket.english_name)
            } else {
                return@ItemProcessor null
            }
        }
    }

    @Bean
    fun JsonJob2_jsonReader(): JsonItemReader<CoinMarket> {
        return JsonItemReaderBuilder<CoinMarket>()
            .jsonObjectReader(JacksonJsonObjectReader(CoinMarket::class.java))
            .resource(ClassPathResource("sample/jsonJob1_input.json"))
            .name("jsonJob2_jsonReader")
            .build()
    }

    @Bean
    fun jsonJob2_jsonWriter(): JsonFileItemWriter<CoinMarket> {
        return JsonFileItemWriterBuilder<CoinMarket>()
            .jsonObjectMarshaller(JacksonJsonObjectMarshaller())
            .resource(FileSystemResource("output/jsonJob2_output.json"))
            .name("jsonJob2_jsonWriter")
            .build()
    }
}
