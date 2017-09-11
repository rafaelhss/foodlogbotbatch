package com.foodlog;

import com.foodlog.meallog.MealLog;
import com.foodlog.meallog.MealLogRepository;
import com.foodlog.scheduledmeal.ScheduledMeal;
import com.foodlog.scheduledmeal.ScheduledMealRepository;
import com.foodlog.sender.Sender;
import com.foodlog.sender.sentmessage.SentMessage;
import com.foodlog.sender.sentmessage.SentMessageRepository;
import com.foodlog.user.User;
import com.foodlog.user.UserRepository;
import com.foodlog.user.UserTelegram;
import com.foodlog.user.UserTelegramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.StringJoiner;

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

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserTelegramRepository userTelegramRepository;

    public void run() {

        //TODO rever esse controle de mensages enviada
        for(User currentUser : userRepository.findAll()) {

            if (isTimeToSendReport(currentUser)) {
                long sentMessageId = LocalDate.now().getLong(ChronoField.DAY_OF_YEAR) + currentUser.getId();
                if (sentMessageRepository.findBySentIdAndMessageType(sentMessageId, MSGTYPE) == null) {
                    System.out.println("Vou mandar msg: " + sentMessageId);
                    if (SendTargetMissDayReport(currentUser)) {

                        //Log Message
                        SentMessage sentMessage = new SentMessage();
                        sentMessage.setSentDate(new Date());
                        sentMessage.setMessageType(MSGTYPE);
                        sentMessage.setSentId(sentMessageId);

                        sentMessageRepository.deleteByMessageType(MSGTYPE);
                        sentMessageRepository.save(sentMessage);
                    }

                } else {
                    System.out.println("Ja mandei mensagem do dia. nada farei. Id:" + sentMessageId);
                }
            } else {
                System.out.println("It is not time yet");
            }
        }
    }

    private boolean isTimeToSendReport(User currentUser) {
        MealLog mealLog = mealLogRepository.findTop1ByUserOrderByMealDateTimeDesc(currentUser);
        if(mealLog == null) {
            return false;
        }
        long elapsedMealTime = Duration.between(mealLog.getMealDateTime(), Instant.now()).getSeconds() / (60 * 60);
        return elapsedMealTime > BatchConfigs.SLEEP_INTERVAL;
    }

    public boolean SendTargetMissDayReport(User currentUser) {

        Instant yesterday = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).truncatedTo(ChronoUnit.DAYS).toInstant().minus(1, ChronoUnit.DAYS);
        System.out.println(yesterday);

        Instant tomorrow = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).truncatedTo(ChronoUnit.DAYS).toInstant().plus(1, ChronoUnit.DAYS);
        System.out.println(tomorrow);

        System.out.println("yesterday.getEpochSecond(): " + yesterday.getEpochSecond());

        int expected = getScheduledMeals(currentUser);
        int hit = 0;
        int miss = 0;

        MealLog lastProcessed = null;
        for (MealLog mealLog : mealLogRepository.findByUserAndMealDateTimeBetweenOrderByMealDateTimeDesc(currentUser,yesterday, tomorrow)) {
            System.out.println(mealLog.getId() + " " + mealLog.getMealDateTime() + "     " + mealLog.getMealDateTime().atZone(ZoneId.of("America/Sao_Paulo")));

            if(lastProcessed == null){
                lastProcessed = mealLog;
            }

            long elapsedMealTime = Duration.between(mealLog.getMealDateTime(), lastProcessed.getMealDateTime()).getSeconds() / (60 * 60);

            System.out.println("Achando a noite. " + mealLog.getMealDateTime() + "(epalsed: " + elapsedMealTime + "): " + (elapsedMealTime < BatchConfigs.SLEEP_INTERVAL));
            // Nao achei a noite, entao computa. Isso eh pra atender casos em que come de madruga
            if (elapsedMealTime < BatchConfigs.SLEEP_INTERVAL){
                if (mealLog.getScheduledMeal() != null) {
                    hit++;
                } else {
                    miss++;
                }
                lastProcessed = mealLog;
            } else {
                //achei a noite, acaba o processamento
                break;
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

        try {
            UserTelegram user = userTelegramRepository.findByUser(currentUser);
            new Sender(BatchConfigs.BOT_ID).sendResponse(user.getTelegramId(), message);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            //chama o image report para mandar o peso
            HttpURLConnection conn = (HttpURLConnection) new URL("https://foodlogbotimagebatch.herokuapp.com/timeline?userid=" + currentUser.getId()).openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
            new Sender(BatchConfigs.BOT_ID).sendResponse(153350155, "Erro ao mandar timeline: " + e.getMessage());
        }

        return true;
    }

    private int getScheduledMeals(User currentUser) {

        HashMap<String,String> aux = new HashMap<>();
        int count = 0;
        for(ScheduledMeal scheduledMeal:scheduledMealRepository.findByUser(currentUser)){
            if (aux.get(scheduledMeal.getTargetTime()) == null){
                aux.put(scheduledMeal.getTargetTime(),scheduledMeal.getTargetTime());
                count++;
            }
        }
        return count;
    }
}
