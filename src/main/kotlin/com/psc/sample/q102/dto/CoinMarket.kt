package com.psc.sample.q102.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 코틀린의 data class는 디폴트 생성자를 제공하지 않기 때문에 코드처럼
 * @JsonSetter를 넣어주거나 @JsonProperty 를 넣어줘야 합니다.
 * @property market String
 * @property korean_name String
 * @property english_name String
 * @constructor
 */
data class CoinMarket(
    @JsonProperty("market")
    val market: String,
    @JsonProperty("korean_name")
    val korean_name: String,
    @JsonProperty("english_name")
    val english_name: String
) {
}
