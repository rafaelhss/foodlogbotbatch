package com.foodlog.sender.sentmessage;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by rafael on 09/06/17.
 */
@SuppressWarnings("unused")
@Repository
public interface SentMessageRepository extends JpaRepository<SentMessage,Long> {
    public void deleteBySentDateBefore(Date yesterday);
    public SentMessage findBySentId(Long sentId);
}
