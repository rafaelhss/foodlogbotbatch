package com.foodlog;

import com.foodlog.meallog.MealLog;
import com.foodlog.meallog.MealLogRepository;
import com.foodlog.sender.Sender;
import com.foodlog.sender.sentmessage.SentMessage;
import com.foodlog.sender.sentmessage.SentMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * Created by rafael on 16/06/17.
 */
@Service
public class ReportElapsedMealTimeBatch {


    public static final String MSGTYPE = "hour";
    @Autowired
    private MealLogRepository mealLogRepository;

    @Autowired
    SentMessageRepository sentMessageRepository;

    public void run(){

        MealLog mealLog = mealLogRepository.findTop1ByOrderByMealDateTimeDesc();
        long elapsedMealTime = Duration.between(mealLog.getMealDateTime(), Instant.now()).getSeconds() / (60 * 60);

        String message;

        System.out.println(mealLog.getMealDateTime());
        System.out.println(Instant.now());

        // ElapsedMealTime eh sempre um numero redondo. A segunda condição evita que envie mensagem duplicada.
        if(elapsedMealTime >= 3 && sentMessageRepository.findBySentIdAndMessageType(elapsedMealTime, MSGTYPE) == null){
            message = "Hora de comer! Sua ultima refeição ";

            if(mealLog.getScheduledMeal() != null){
                message += "(" + mealLog.getScheduledMeal().getName() + ") ";
            }

            message += "ocorreu há " + elapsedMealTime + " horas";


            new Sender(BatchConfigs.BOT_ID).sendResponse(153350155, message);

            //Log Message
            SentMessage sentMessage = new SentMessage();
            sentMessage.setSentDate(new Date());
            sentMessage.setMessageType(MSGTYPE);
            sentMessage.setSentId(elapsedMealTime);

            sentMessageRepository.deleteByMessageType(MSGTYPE);
            sentMessageRepository.save(sentMessage);

        } else {
            message = "ta de boa";
        }

        System.out.println(message);




    }
}
