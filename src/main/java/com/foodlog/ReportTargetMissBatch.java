package com.foodlog;

import com.foodlog.meallog.MealLog;
import com.foodlog.meallog.MealLogRepository;
import com.foodlog.scheduledmeal.ScheduledMealRepository;
import com.foodlog.sender.Sender;
import com.foodlog.sender.sentmessage.SentMessage;
import com.foodlog.sender.sentmessage.SentMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Created by rafa on 25/06/17.
 */
@Service
public class ReportTargetMissBatch {

    private static final String MSGTYPE = "DAY_REPORT" ;

    @Autowired
    private MealLogRepository mealLogRepository;

    @Autowired
    private ScheduledMealRepository scheduledMealRepository;

    @Autowired
    private SentMessageRepository sentMessageRepository;

    public void run() {



        Instant today = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).truncatedTo(ChronoUnit.DAYS).toInstant();
        System.out.println(today);

        Instant tomorrow = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).truncatedTo(ChronoUnit.DAYS).toInstant().plus(1, ChronoUnit.DAYS);
        System.out.println(tomorrow);

        System.out.println("today trunc?:" + today.getEpochSecond());
        if(sentMessageRepository.findBySentIdAndMessageType(today.getEpochSecond(), MSGTYPE) == null) {
            int expected = scheduledMealRepository.findDistinctTargetTimeBy().size();
            int hit = 0;
            int miss = 0;

            for (MealLog mealLog : mealLogRepository.findByMealDateTimeBetween(today, tomorrow)) {
                System.out.println(mealLog.getId() + " " + mealLog.getMealDateTime() + "     " + mealLog.getMealDateTime().atZone(ZoneId.of("America/Sao_Paulo")));
                if (mealLog.getScheduledMeal() != null) {
                    hit++;
                } else {
                    miss++;
                }
            }

            String message = "Report diario: ";
            if (expected == hit) {
                message += "Parabens, todas as " + expected + " refeicoes feitas no horario hoje";
            } else {
                if (expected == (hit + miss)) {
                    message += "Quantidade de refeicoes no dia (" + expected + ") de acordo com esperado! ";
                } else {
                    message += "Diferenca entre esperado (" + expected + ") e realzado (" + (hit + miss) + "): " + ((hit + miss) - expected) + ". ";
                }
                message += "Refeicoes no horario: " + hit + ", refeicoes fora do horario:" + miss + ". ";

            }

            System.out.println(message);

            new Sender(BatchConfigs.BOT_ID).sendResponse(153350155, message);

            //Log Message
            SentMessage sentMessage = new SentMessage();
            sentMessage.setSentDate(new Date());
            sentMessage.setMessageType(MSGTYPE);
            sentMessage.setSentId(today.getEpochSecond());

            sentMessageRepository.deleteByMessageType(MSGTYPE);
            sentMessageRepository.save(sentMessage);

        } else {
            System.out.println("Ja mandei mensagem do dia. nada farei.");
        }
    }
}
