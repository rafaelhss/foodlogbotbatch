package com.foodlog.sender.sentmessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rafael on 04/07/17.
 */
@Service
public class SentMessageService {

    @Autowired
    SentMessageRepository sentMessageRepository;

    public void logSentMessage(long sentId, String msgType) {
        //Log Message
        SentMessage sentMessage = new SentMessage();
        sentMessage.setSentDate(new Date());
        sentMessage.setMessageType(msgType);
        sentMessage.setSentId(sentId);

        sentMessageRepository.deleteByMessageType(msgType);
        sentMessageRepository.save(sentMessage);
    }

    public boolean isSent(long sentId, String msgType){
        return (sentMessageRepository.findBySentIdAndMessageType(sentId, msgType) == null);
    }

    public void clearAllByPastDays(int days){
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -days);
        sentMessageRepository.deleteBySentDateBefore(cal.getTime());
    }





}
