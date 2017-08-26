package com.foodlog.scheduledmeal;

import com.foodlog.user.User;
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

    List<ScheduledMeal> findByUser(User currentUser);
}
