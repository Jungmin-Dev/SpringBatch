package com.psc.sample.q102.batch

import com.psc.sample.q102.dto.CoinMarket
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.json.JacksonJsonObjectReader
import org.springframework.batch.item.json.JsonItemReader
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.PlatformTransactionManager


/**
 * json 파일 불러와서 읽기
 * @property chunkSize Int
 */
@Configuration
class JsonJob1 {

    private final val chunkSize = 5

    @Bean
    fun jsonJob1_batchJob1(jobRepository: JobRepository?, step: Step?): Job {
        return JobBuilder("JsonJob1", jobRepository!!)
            .start(step!!)
            .build()
    }

    @Bean
    fun jsonJob1_batchStep1(jobRepository: JobRepository?, transactionManager: PlatformTransactionManager?): Step {
        return StepBuilder("jsonJob1_batchStep1", jobRepository!!)
            .chunk<CoinMarket, CoinMarket>(chunkSize, transactionManager!!)
            .reader(jsonJob1_jsonReader())
            .writer { coinMarkets ->
                coinMarkets.forEach { coinMarket ->
                    println("1. ${coinMarket.market}, 2. ${coinMarket.korean_name}, 3.${coinMarket.english_name}")
                    println(coinMarket.toString())
                }
            }
            .build()
    }

    @Bean
    fun jsonJob1_jsonReader(): JsonItemReader<CoinMarket> {
        return JsonItemReaderBuilder<CoinMarket>()
            .jsonObjectReader(JacksonJsonObjectReader(CoinMarket::class.java))
            .resource(ClassPathResource("sample/jsonJob1_input.json"))
            .name("jsonJob_jsonReader")
            .build()
    }


}
