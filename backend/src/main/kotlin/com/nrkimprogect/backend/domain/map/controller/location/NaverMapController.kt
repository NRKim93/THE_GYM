package com.nrkimprogect.backend.domain.map.controller.location

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.http.*
import org.springframework.web.client.HttpClientErrorException
import kotlin.math.*

@RestController
@RequestMapping("/api/v1/naver-map")
@CrossOrigin(origins = ["http://localhost:3000"])
class NaverMapController(
    private val restTemplate: RestTemplate
) {
    @Value("\${SPRING_SECURITY_NAVER_REVERSE_GEO_URI}")
    private lateinit var reverseGeoUrl: String

    @Value("\${SPRING_SECURITY_NAVER_MAP_CLIENT_ID}")
    private lateinit var clientId: String

    @Value("\${SPRING_SECURITY_NAVER_MAP_CLIENT_SECRET}")
    private lateinit var clientSecret: String

    /**
     * 📍 현재 위치의 주소(Reverse Geocode) 조회
     * @param lat 위도
     * @param lng 경도
     * @return 네이버 지도 API에서 반환하는 주소 정보
     */
    @GetMapping("/reverse-geocode")
    fun reverseGeocode(
        @RequestParam lat: Double,
        @RequestParam lng: Double
    ): ResponseEntity<Any> {
        val uri = UriComponentsBuilder.fromHttpUrl(reverseGeoUrl)
            .queryParam("coords", "$lng,$lat")  // ✅ 좌표 순서: (경도, 위도)
            .queryParam("orders", "roadaddr")  // ✅ 도로명 주소 우선 반환
            .queryParam("output", "json")
            .toUriString()

        val headers = HttpHeaders().apply {
            set("X-NCP-APIGW-API-KEY-ID", clientId)
            set("X-NCP-APIGW-API-KEY", clientSecret)
        }

        val requestEntity = HttpEntity<String>(headers)

        return try {
            val response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String::class.java)
            ResponseEntity.ok(response.body)
        } catch (ex : HttpClientErrorException) {
            // ❌ API 요청 실패 시 오류 메시지 반환
            ResponseEntity.status(ex.statusCode).body(mapOf("error" to "네이버 API 요청 실패", "message" to ex.localizedMessage))
        } catch (ex : Exception) {
            // ❌ 기타 예외 발생 시 처리
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to "서버 오류", "message" to ex.localizedMessage))
        }
    }
}