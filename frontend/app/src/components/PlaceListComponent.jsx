import React, { useEffect, useState } from "react";
import axios from "axios";

const PlaceListComponent = ({ lat, lng }) => {
    const [gyms, setGyms] = useState([]);

    useEffect(() => {
        const fetchGyms = async () => {
            try {
                const geoResponse = await axios.get("/api/v1/places",{
                    params: { lat, lng },
                    withCredentials: true
                });

                setGyms(geoResponse.data.items || []);
            } catch (error) {
                console.error("API 호출 오류:", error);
            }
        };

        if (lat && lng) fetchGyms();
    }, [lat, lng]);

    return (
        <div>
            <h2>근처 헬스장 목록</h2>
            <ul>
                {gyms.map((gym, index) => (
                    <li key={index}>{gym.title}</li>
                ))}
            </ul>
        </div>
    );
};

export default PlaceListComponent;
