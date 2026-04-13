package com.pe.inventoryapp.backend.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.location.model.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
        @Query("""
                        SELECT l
                        FROM Location l JOIN FETCH l.subregion s JOIN FETCH s.region r
                        WHERE (:name IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', :name, '%')))
                        AND (:regionId IS NULL OR r.id = :regionId)
                        AND (:subregionId IS NULL OR s.id = :subregionId)
                        AND (:status IS NULL OR l.status = :status) ORDER BY l.id DESC
                        """)
        Page<Location> findAllByParams(
                        Pageable pageable,
                        @Param("name") String name,
                        @Param("regionId") Long regionId,
                        @Param("subregionId") Long subregionId,
                        @Param("status") Boolean status);

        @Query("SELECT COUNT(s) > 0 FROM Location l JOIN l.subregion s WHERE l.name = :name AND s.id = :subregionId")
        boolean existsByNameAndSubregionId(String name, Long subregionId);

        @Query("SELECT COUNT(s) > 0 FROM Location l JOIN l.subregion s WHERE l.name = :name AND s.id = :subregionId AND l.id != :id")
        boolean existsByNameAndSubregionIdAndIdNot(String name, Long subregionId, Long id);

        boolean existsByName(String name);

        Optional<Location> findByName(String name);

        @Query("""
                            SELECT l
                            FROM Location l JOIN FETCH l.subregion s JOIN FETCH s.region r
                            WHERE l.status = true
                            AND (:regionId IS NULL OR r.id = :regionId)
                            AND (:subregionId IS NULL OR s.id = :subregionId)
                            AND (:name IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', :name, '%'))) ORDER BY l.id DESC LIMIT 10
                        """)
        List<Location> findAllFirstTenLocationsByParams(String name, Long regionId, Long subregionId);

    @Query("""
            SELECT l FROM Location l
            JOIN FETCH l.subregion s
            JOIN FETCH s.region r
            WHERE l.id = :id
            """)
        Optional<Location> findByIdFull(Long id);

}
