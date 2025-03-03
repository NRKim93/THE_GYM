package com.nrkimprogect.backend.domain.map.service.place

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder


@Service
class PlaceService (
    private val restTemplate: RestTemplate // http 요청을 보내기 위해 주입
) {
    @Value("\${spring.security.naver.local-search-uri}")
    private lateinit var searchApiUri: String

    @Value("\${SPRING_SECURITY_NAVER_CLIENT_ID}")
    private lateinit var clientId: String

    @Value("\${SPRING_SECURITY_NAVER_CLIENT_SECRET}")
    private lateinit var clientSecret: String

    fun searchGym(lat : Double, lng : Double) : String {
        // 📌 네이버 지역 검색 API 호출 URL 생성
        val uri = UriComponentsBuilder.fromUriString(searchApiUri)
            .queryParam("query", "경복궁")
            .queryParam("display", 5)
            .queryParam("start", 1)
            .queryParam("sort", "random")
            .build()
            .toUriString()

        // 📌 요청 헤더 설정 (네이버 API 인증 정보)
        val headers = HttpHeaders().apply {
            add("X-Naver-Client-Id", clientId)
            add("X-Naver-Client-Secret", clientSecret)
        }

        val entity = HttpEntity<Unit>(headers)

        val response = restTemplate.exchange(uri, HttpMethod.GET, entity, String::class.java)

        return response.body ?: "{}"
    }
}