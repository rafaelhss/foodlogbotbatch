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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FoodLogBotBatch implements CommandLineRunner {

	private static final String BOT_ID = "374481790:AAHgscpBDG2zs4VsDbeg140VmSVZZeItPEw";


	@Autowired
	ScheduledMealRepository scheduledMealRepository;

	@Override
	public void run(String... args) {
		try {
			for (ScheduledMeal scheduledMeal : scheduledMealRepository.findAll()) {
				System.out.println(scheduledMeal.getName());
				System.out.println(scheduledMeal.getTarget_time());
				new Sender(BOT_ID).sendResponse(153350155, scheduledMeal.getName());
			}
		} catch (Exception ex){
			System.out.println("errroooooooooooooooooooooooooooooooooooooooooo: " + ex.getMessage());
		}


		System.out.println("samba");
		new Sender(BOT_ID).sendResponse(153350155, "samba");

	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(FoodLogBotBatch.class, args);
	}

}
