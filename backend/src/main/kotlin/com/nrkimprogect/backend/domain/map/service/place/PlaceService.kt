package com.nrkimprogect.backend.domain.map.service.place

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
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
    @Value("\${spring.security.naver.reverse-geo-uri}")
    private lateinit var reverseGeoUri: String

    @Value("\${spring.security.naver.local-search-uri}")
    private lateinit var searchApiUri: String

    @Value("\${spring.security.naver.map-client-id}")
    private lateinit var mapClientId: String

    @Value("\${spring.security.naver.map-client-pw}")
    private lateinit var mapClientSecret: String

    @Value("\${spring.security.oauth2.client.registration.naver.client-id}")
    private  lateinit var clientId : String

    @Value("\${spring.security.oauth2.client.registration.naver.client-secret}")
    private  lateinit var clientSecret : String

    /**
     * 네이버 지도 Reverse Geocode API를 사용하여 위도, 경도로부터 주소를 가져옴
     */
    fun getAddressFromCoordinates(lat: Double, lng: Double): String {
        val uri = UriComponentsBuilder.fromUriString(reverseGeoUri)
            .queryParam("coords", "$lng,$lat") // 네이버는 "경도,위도" 순서!
            .queryParam("sourcecrs", "EPSG:4326")
            .queryParam("output", "json")
            .queryParam("orders", "legalcode,admcode")
            .build()
            .toUriString()

        val headers = HttpHeaders().apply {
            add("X-NCP-APIGW-API-KEY-ID", mapClientId)
            add("X-NCP-APIGW-API-KEY", mapClientSecret)
        }

        val entity = HttpEntity<Unit>(headers)

        return try {
            val response = restTemplate.exchange(uri, HttpMethod.GET, entity, String::class.java)
            println("✅ Reverse Geocode 응답: ${response.body}")
            response.body ?: "{}"
        } catch (e: Exception) {
            println("❌ Reverse Geocode API 요청 실패: ${e.message}")
            "{}"
        }
    }

    /**
     * Reverse Geocode API 응답에서 area1, area2, area3을 추출하여 지역 검색에 사용할 주소 문자열 반환
     */

    private fun extractAddress(jsonResponse: String): String {
        return try {
            val mapper = ObjectMapper()
            val rootNode: JsonNode = mapper.readTree(jsonResponse)

            val results = rootNode.path("results")
            if (results.isEmpty) return ""

            val region = results[0].path("region")
            val area1 = region.path("area1").path("name").asText()
            val area2 = region.path("area2").path("name").asText()
            val area3 = region.path("area3").path("name").asText()

            val address = "$area1 $area2 $area3 헬스장"
            address
        } catch (e: Exception) {
            println("❌ 주소 파싱 실패: ${e.message}")
            "헬스장" // 실패 시 기본 검색어 사용
        }
    }


    /**
     * 네이버 지역 검색 API를 사용하여 헬스장 목록을 가져옴
     */
    fun searchGym(lat : Double, lng : Double) : String {
        val addressInfo = getAddressFromCoordinates(lat, lng)
        val query = extractAddress(addressInfo)

        // 📌 네이버 지역 검색 API 호출 URL 생성
        val uri = UriComponentsBuilder.fromUriString(searchApiUri)
            .queryParam("query", query)
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

        return try {
            val response = restTemplate.exchange(uri, HttpMethod.GET, entity, String::class.java)
            println("✅ 네이버 지역 검색 API 응답: ${response.body}")
            response.body ?: "{}"
        } catch (e: Exception) {
            println("❌ 네이버 지역 검색 API 요청 실패: ${e.message}")
            "{}"
        }
    }
}