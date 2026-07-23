package com.charging.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.entity.Station;

import java.util.List;

public interface StationService extends IService<Station> {

    List<Station> findNearbyStations(Double longitude, Double latitude, Double radiusKm);
}
