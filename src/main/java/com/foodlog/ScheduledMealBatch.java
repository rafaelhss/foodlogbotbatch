package com.foodlog;

import com.foodlog.BatchConfigs;
import com.foodlog.scheduledmeal.ScheduledMeal;
import com.foodlog.scheduledmeal.ScheduledMealRepository;
import com.foodlog.sender.Sender;
import com.foodlog.sender.sentmessage.SentMessage;
import com.foodlog.sender.sentmessage.SentMessageRepository;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by rafael on 16/06/17.
 */
@Service
public class ScheduledMealBatch {
    @Autowired
    ScheduledMealRepository scheduledMealRepository;

    @Autowired
    SentMessageRepository sentMessageRepository;


    public void run(){
        System.out.println("########## here we gooooooo  :" + new Date());

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -2);
        sentMessageRepository.deleteBySentDateBefore(cal.getTime());

        try {
            for (ScheduledMeal scheduledMeal : scheduledMealRepository.findAll()) {

                System.out.println("Verificando:" + scheduledMeal.getName() + "(" + scheduledMeal.getTargetTime() + ")");
                if(checkTime(scheduledMeal)) {

                    SentMessage sentMessage = sentMessageRepository.findBySentId(scheduledMeal.getId());

                    if(sentMessage == null) {

                        new Sender(BatchConfigs.BOT_ID).sendResponse(153350155, scheduledMeal.getName()
                                + "(" + scheduledMeal.getTargetTime() + "):   "
                                + scheduledMeal.getDescription());

                        //Log Message
                        sentMessage = new SentMessage();
                        sentMessage.setSentDate(new Date());
                        sentMessage.setSentId(scheduledMeal.getId());
                        sentMessageRepository.save(sentMessage);
                    } else {
                        System.out.println("Não vou mandar mensagem apesar de estarmos na janela. ja mandei: " + sentMessage.getSentId() + " - " + sentMessage.getSentId() + " em " + sentMessage.getSentDate());
                    }

                }
            }
        } catch (Exception ex){
            System.out.println("errrxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    private boolean checkTime(ScheduledMeal scheduledMeal) throws ParseException {

        String time[] = scheduledMeal.getTargetTime().split(":");

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));

        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        ZonedDateTime target = now.with(LocalTime.of(hour, minute));

        ZonedDateTime after = target.plusMinutes(20);
        ZonedDateTime before = target.minusMinutes(20);

        System.out.println("datas: now[" + now + "] target[" + target + "] before[" + before+"] after[" + after + "] Return:" + (now.isBefore(after) && now.isAfter(before)));

        return (now.isBefore(after) && now.isAfter(before));
    }


    private boolean checkTime2(ScheduledMeal scheduledMeal) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Brasilia"));


        Time target = new Time(dateFormat.parse(scheduledMeal.getTargetTime()).getTime());

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Brasilia"));
        cal.setTime(target);
        cal.add(Calendar.MINUTE, 20);
        Time after = new Time(cal.getTimeInMillis());

        cal.setTime(target);
        cal.add(Calendar.MINUTE, -20);
        Time before = new Time(cal.getTimeInMillis());

        cal.setTime(new Date());
        Time now = new Time(cal.getTimeInMillis());


        //if(new Date().after(new Date(before.getTime())) && new Date().before(new Date(after.getTime()))){
        if(compareTimes(new Date(now.getTime()),new Date(before.getTime())) > 0 &&
                compareTimes(new Date(now.getTime()),new Date(after.getTime())) < 0){
            System.out.println("Eh nois");
            return true;
        }
        return false;
    }

    public int compareTimes(Date d1, Date d2)
    {
        System.out.println("comparando: " + d1 + "     e     " + d2);
        DateTimeComparator comparator = DateTimeComparator.getTimeOnlyInstance();
        return comparator.compare(d1, d2);
    }

    private Date getDate(int add) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date current = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(current);
        cal.add(Calendar.MINUTE, add);

        return cal.getTime();

    }

}
