/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foodlog;

import com.foodlog.scheduledmeal.ScheduledMeal;
import com.foodlog.scheduledmeal.ScheduledMealRepository;
import com.foodlog.sender.Sender;
import com.foodlog.sender.sentmessage.SentMessage;
import com.foodlog.sender.sentmessage.SentMessageRepository;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;




@SpringBootApplication
public class FoodLogBotBatch implements CommandLineRunner {

	private static final String BOT_ID = "374481790:AAHgscpBDG2zs4VsDbeg140VmSVZZeItPEw";


	@Autowired
	ScheduledMealRepository scheduledMealRepository;

	@Autowired
	SentMessageRepository sentMessageRepository;

	@Override
	public void run(String... args) {

		sentMessageRepository.deleteBySentDateBefore(yesterday());

		try {
			System.out.println("########## here we gooooooo  ");
			for (ScheduledMeal scheduledMeal : scheduledMealRepository.findAll()) {

				if(checkTime(scheduledMeal)) {

					if(sentMessageRepository.findBySentId(scheduledMeal.getId()) == null) {

						new Sender(BOT_ID).sendResponse(153350155, scheduledMeal.getName()
								+ "(" + scheduledMeal.getTargetTime() + "):   "
								+ scheduledMeal.getDescription());

						//Log Message
						SentMessage sentMessage = new SentMessage();
						sentMessage.setSentDate(new Date());
						sentMessage.setSentId(scheduledMeal.getId());
						sentMessageRepository.save(sentMessage);
					}

				}
			}
		} catch (Exception ex){
			System.out.println("errroooooooooooooooooooooooooooooooooooooooooo: " + ex.getMessage());
			ex.printStackTrace();
		}

	}

	private Date yesterday() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}
	private boolean checkTime(ScheduledMeal scheduledMeal) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		Time target = new Time(formatter.parse(scheduledMeal.getTargetTime()).getTime());

		Calendar cal = Calendar.getInstance();
		cal.setTime(target);
		cal.add(Calendar.MINUTE, 20);
		Time after = new Time(cal.getTimeInMillis());

		cal.setTime(target);
		cal.add(Calendar.MINUTE, -20);
		Time before = new Time(cal.getTimeInMillis());


		//if(new Date().after(new Date(before.getTime())) && new Date().before(new Date(after.getTime()))){
		if(compareTimes(new Date(),new Date(before.getTime())) > 0 &&
                compareTimes(new Date(),new Date(after.getTime())) < 0){
            System.out.println("Eh nois");
            return true;
        }
		return false;
	}

	public int compareTimes(Date d1, Date d2)
	{
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

	public static void main(String[] args) throws Exception {
		SpringApplication.run(FoodLogBotBatch.class, args);
	}

}
