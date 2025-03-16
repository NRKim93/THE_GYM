package com.nrkimprogect.backend.domain.map.controller.place

import com.nrkimprogect.backend.domain.map.service.place.PlaceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/places")
class PlaceController (
    private val placeService: PlaceService
) {
    @GetMapping
    fun getNearGyms(
        @RequestParam lat: Double,
        @RequestParam lng: Double
    ) : ResponseEntity<Any> {
        return  try {
            val  place = placeService.searchGym(lat,lng)
            ResponseEntity.ok(place)
        } catch (e: Exception) {
            println("❌ [ERROR] 헬스장 데이터 조회 실패: ${e.message}")
            e.printStackTrace()
            ResponseEntity.status(500).body("서버 내부 오류 발생")
        }
    }
}