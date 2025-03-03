import React, { useEffect, useState } from "react";
import PlaceListComponent from "./PlaceListComponent";
//  김누리
const MapComponent = () => {
    const [map, setMap] = useState(null);
    const [userLat, setUserLat] = useState(null);
    const [userLng, setUserLng] = useState(null);

    useEffect(() => {
        let map = null;

        const initMap = (lat, lng) => {
            const { naver } = window;
            if (!naver) return;

            const mapInstance = new naver.maps.Map('map', {
                center: new naver.maps.LatLng(lat, lng),
                zoom: 15,
            });

            new naver.maps.Marker({
                position: new naver.maps.LatLng(lat, lng),
                map: mapInstance,
                title: "내 위치",
                icon: {
                    content: '<div style="color:red; font-weight:bold;">📍</div>',
                    size: new naver.maps.Size(20, 20),
                    anchor: new naver.maps.Point(10, 10),
                }
            });

            setMap(mapInstance);
            setUserLat(lat);
            setUserLng(lng);
        };

        const getCurrentPosition = () => {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        const lat = position.coords.latitude;
                        const lng = position.coords.longitude;
                        initMap(lat, lng);
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

        const script = document.createElement('script');
        script.src = `https://openapi.map.naver.com/openapi/v3/maps.js?ncpClientId=${process.env.REACT_APP_NAVER_MAP_CLIENT_ID}`;
        script.async = true;
        script.onload = getCurrentPosition;
        document.head.appendChild(script);
    }, []);

    return (
        <div>
            <div id="map" style={{ width: '100%', height: '400px' }} />
            {userLat && userLng && <PlaceListComponent lat={userLat} lng={userLng} />}
        </div>
    );
};

export default MapComponent;
