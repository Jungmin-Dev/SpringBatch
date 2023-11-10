package com.psc.sample.q102.batch

import org.springframework.context.annotation.Configuration

/**
 * txt 파일 읽어서 print로 출력
 * @property chunkSize Int
 */
@Configuration
class TextJob1 {

//    private final val chunkSize = 5
//
//    @Bean
//    fun textJob1_batchBuild(jobRepository: JobRepository?, textJob1_batchStep1: Step?): Job {
//        return JobBuilder("textJob1", jobRepository!!)
//            .start(textJob1_batchStep1!!)
//            .build()
//    }
//
//    @Bean
//    @Primary
//    fun textJob1_batchStep1(jobRepository: JobRepository?, transactionManager: PlatformTransactionManager?): Step {
//        return StepBuilder("textJob1", jobRepository!!)
//            .allowStartIfComplete(true) // 반복 작업을 위한 스텝 설정
//            .chunk<OneDto, OneDto>(chunkSize, transactionManager!!)
//            .reader(textJob1_FileReader(null))
//            .writer { oneDto ->
//                oneDto.forEach { item ->
//                    println(item.one)
//                }
//            }
//            .build()
//    }
//
//    @Bean
//    @StepScope
//    fun textJob1_FileReader(@Value("#{jobParameters[requestDate]}") requestDate: String?): FlatFileItemReader<OneDto> {
//        val flatFileItemReader: FlatFileItemReader<OneDto> = FlatFileItemReader<OneDto>()
//        flatFileItemReader.setResource(ClassPathResource("sample/textJob1_input.txt"))
//        flatFileItemReader.setLineMapper { line, lineNumber -> OneDto("$lineNumber == $line ${requestDate} ") }
//        return flatFileItemReader
//    }

}
