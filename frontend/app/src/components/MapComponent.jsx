import React, { useEffect, useState } from "react";
import axios from "axios";

const MapComponent = () => {
    const [map, setMap] = useState(null);
    const [gyms, setGyms] = useState([]);
    const [userLocation, setUserLocation] = useState(null);

    useEffect(() => {
        const initMap = (lat, lng) => {
            if (!window.naver || !window.naver.maps) {
                console.error("네이버 지도 API가 로드되지 않았습니다.");
                return;
            }

            const mapInstance = new window.naver.maps.Map('map', {
                center: new window.naver.maps.LatLng(lat, lng),
                zoom: 15,
            });

            new window.naver.maps.Marker({
                position: new window.naver.maps.LatLng(lat, lng),
                map: mapInstance,
                title: "내 위치",
                icon: {
                    content: '<div style="color:red; font-weight:bold;">📍</div>',
                    size: new window.naver.maps.Size(20, 20),
                    anchor: new window.naver.maps.Point(10, 10),
                }
            });

            // ✅ 지도에 반경 1km 원을 시각적으로 표시
            new window.naver.maps.Circle({
                map: mapInstance,
                center: new window.naver.maps.LatLng(lat, lng),
                radius: 1000,
                fillColor: 'rgba(0, 100, 255, 0.2)',
                strokeColor: '#0064ff',
                strokeWeight: 2,
            });

            setMap(mapInstance);
        };

        const getCurrentPosition = () => {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        const userLat = position.coords.latitude;
                        const userLng = position.coords.longitude;

                        console.log("📍 현재 위치:", userLat, userLng);

                        setUserLocation({ lat: userLat, lng: userLng });
                        initMap(userLat, userLng);
                        fetchGyms(userLat, userLng);
                    },
                    (error) => {
                        console.error("Geolocation Error: ", error);
                        initMap(37.5665, 126.9780);
                    }, {
                        enableHighAccuracy: true,
                        timeout: 10000,
                        maximumAge: 0
                    }
                );
            } else {
                console.error("Geolocation is not supported by this browser.");
                initMap(37.5665, 126.9780);
            }
        };

        // ✅ 네이버 지도 API 스크립트 동적 추가
        const script = document.createElement('script');
        script.src = `https://openapi.map.naver.com/openapi/v3/maps.js?ncpClientId=${process.env.REACT_APP_NAVER_MAP_CLIENT_ID}`;
        script.async = true;
        script.onload = () => {
            console.log("✅ 네이버 지도 API 로드 완료");
            getCurrentPosition();
        };
        document.head.appendChild(script);
    }, []);

// ✅ Haversine 공식 기반 거리 계산 (보다 정밀한 검증 추가)
    const calculateDistance = (lat1, lng1, lat2, lng2) => {
        const toRad = (value) => (value * Math.PI) / 180;
        const R = 6371.0088; // 지구 평균 반경 (km)

        console.log("📍 현재 위치 확인 (userLocation):", userLocation);
        console.log(`📏 거리 계산 입력 값: lat1=${lat1}, lng1=${lng1}, lat2=${lat2}, lng2=${lng2}`);

        // ✅ 유효한 좌표인지 검증
        if (
            lat1 == null || lng1 == null || lat2 == null || lng2 == null ||
            isNaN(lat1) || isNaN(lng1) || isNaN(lat2) || isNaN(lng2)
        ) {
            console.error("⚠️ 거리 계산 오류: 좌표 값이 유효하지 않음");
            return Infinity;
        }

        // ✅ 좌표가 같은 경우 0m 반환 (중복 거리 계산 방지)
        if (lat1 === lat2 && lng1 === lng2) {
            console.log("📏 동일한 위치이므로 거리 0m 반환");
            return 0;
        }

        // ✅ 위/경도 차이 계산
        const dLat = toRad(lat2 - lat1);
        const dLng = toRad(lng2 - lng1);

        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        const distance = R * c * 1000; // 미터 단위 변환

        console.log(`📏 계산된 거리: ${distance.toFixed(2)}m`);
        return distance;
    };

    // ✅ 헬스장 목록 가져오기
    const fetchGyms = async (lat, lng) => {
        try {
            console.log("🔍 헬스장 검색 요청:", lat, lng);
            const response = await axios.get("/api/v1/places", {
                params: { lat, lng }
            });

            console.log("✅ 헬스장 검색 결과:", response.data.items);
            setGyms(response.data.items);
        } catch (error) {
            console.error("API 호출 오류:", error);
        }
    };

    // ✅ 반경 내 헬스장만 지도에 표시
    useEffect(() => {
        if (!map || gyms.length === 0 || !userLocation) return;

        const userLat = userLocation.lat;
        const userLng = userLocation.lng;

        gyms.forEach(async (gym) => {

            let gymLat = parseFloat(gym.mapy);
            let gymLng = parseFloat(gym.mapx);

            console.log(`🔍 헬스장 원본 좌표 (${gym.title}):`, gymLat, gymLng);

            // 🛠️ 유효성 검사
            if (!gymLat || !gymLng || isNaN(gymLat) || isNaN(gymLng)) {
                console.warn(`⚠️ 좌표 값 오류 (마커 추가 안됨): ${gym.title}`, gymLat, gymLng);
                return;
            }

            const distance = calculateDistance(userLat, userLng, gymLat, gymLng);
            console.log(`📏 거리 계산: ${gym.title} = ${distance}m`);

            if (distance <= 1000) { // ✅ 반경 1km 이내만 표시
                console.log(`📍 마커 추가됨: ${gym.title} (${gymLat}, ${gymLng})`);
                new window.naver.maps.Marker({
                    position: new window.naver.maps.LatLng(gymLat, gymLng),
                    map: map,
                    title: gym.title,
                    icon: {
                        content: `<div style="color:forestgreen; font-weight:bold;">🏋️‍♂️</div>`,
                        size: new window.naver.maps.Size(20, 20),
                        anchor: new window.naver.maps.Point(10, 10),
                    }
                });
            } else {
                console.warn(`⛔ 반경 1km 초과: ${gym.title}`);
            }
        });
    }, [map, gyms, userLocation]);

    return (
        <div>
            <div id="map" style={{ width: '100%', height: '500px' }} />
        </div>
    );
};

export default MapComponent;
