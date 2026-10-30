package dev.bakanov.routewise.road;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RoadRepository extends JpaRepository<Road, UUID> {

    boolean existsByFromCity_IdAndToCity_Id(UUID fromCityId, UUID toCityId);

    @Query("""
            select r from Road r
            join fetch r.fromCity
            join fetch r.toCity
            order by r.fromCity.name, r.toCity.name
            """)
    List<Road> findAllWithCities();
}
