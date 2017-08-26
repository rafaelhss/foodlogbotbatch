package com.foodlog.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the UserTelegram entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserTelegramRepository extends JpaRepository<UserTelegram,Long> {

    UserTelegram findOneByTelegramId(Integer id);

    UserTelegram findByUser(User user);
}
