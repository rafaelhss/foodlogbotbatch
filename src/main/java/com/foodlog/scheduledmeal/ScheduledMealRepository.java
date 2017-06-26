package com.foodlog.scheduledmeal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


/**
 * Spring Data JPA repository for the ScheduledMeal entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduledMealRepository extends JpaRepository<ScheduledMeal,Long> {
    public List<ScheduledMeal> findDistinctTargetTimeBy();

}
