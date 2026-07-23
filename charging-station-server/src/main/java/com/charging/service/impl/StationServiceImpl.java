package com.charging.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.entity.Station;
import com.charging.mapper.StationMapper;
import com.charging.service.StationService;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station>
        implements StationService {
    @Override
    public List<Station> findNearbyStations(Double longitude, Double latitude, Double
            radiusKm) {
        // 使用 Haversine 公式计算距离
        // 地球半径（公里）
        double earthRadius = 6371.0;

        // 构建 SQL 查询附近站点
        String sql = String.format(
                "SELECT *, " +
                        "(%f * ACOS(" +
                        "  COS(RADIANS(%f)) * COS(RADIANS(latitude)) * " +
                        "  COS(RADIANS(longitude) - RADIANS(%f)) + " +
                        "  SIN(RADIANS(%f)) * SIN(RADIANS(latitude))" +
                        ")) AS distance " +
                        "FROM t_station " +
                        "WHERE deleted = 0 AND status = 1 " +
                        "HAVING distance <= %f " +
                        "ORDER BY distance " +
                        "LIMIT 20",
                earthRadius, latitude, longitude, latitude, radiusKm
        );

        return baseMapper.selectBySql(sql);
    }
}
