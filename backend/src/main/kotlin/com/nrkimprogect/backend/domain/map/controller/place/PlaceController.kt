package com.nrkimprogect.backend.domain.map.controller.place

import com.nrkimprogect.backend.domain.map.service.place.PlaceService
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
    ) : String {
        println("GET NEAR GYM")
        return placeService.searchGym(lat, lng)
    }
}