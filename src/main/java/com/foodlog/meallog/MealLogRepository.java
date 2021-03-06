package com.foodlog.meallog;

import com.foodlog.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;


/**
 * Spring Data JPA repository for the MealLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MealLogRepository extends JpaRepository<MealLog,Long> {

    MealLog findTop1ByUserOrderByMealDateTimeDesc(User currentUser);

    List<MealLog> findByUserAndMealDateTimeBetweenOrderByMealDateTimeDesc(User currentUser, Instant yesterday, Instant tomorrow);
}
