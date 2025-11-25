package com.intelligent.intelligentstdyplanner.Repository;

import com.intelligent.intelligentstdyplanner.Model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
}
