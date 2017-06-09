package com.foodlog.sender.sentmessage;

import jdk.nashorn.internal.objects.annotations.Getter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by rafael on 09/06/17.
 */

@Entity
@Table(name = "sent_message")
public class SentMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    private Long sentId;
    private Date sentDate;


    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Long getSentId() {
        return sentId;
    }

    public void setSentId(Long sentId) {
        this.sentId = sentId;
    }
}
