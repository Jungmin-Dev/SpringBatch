package com.psc.sample.q102.domain

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit

@SpringBootTest
class TestDeptRepository {
    @Autowired
    lateinit var deptRepository: DeptRepository

    /**
     * 테스트 데이터 생성
     */
    @Test
    @Commit
    fun dept01() {
        for (i in 1..100) {
            deptRepository.save(Dept(i, "dName_$i", "loc_$i"))
        }
    }

}
