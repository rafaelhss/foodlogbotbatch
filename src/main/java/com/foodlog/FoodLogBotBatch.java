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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;


@SpringBootApplication
public class FoodLogBotBatch implements CommandLineRunner {

	@Autowired
	public ScheduledMealBatch scheduledMealBatch;
	@Autowired
	public ReportElapsedMealTimeBatch reportElapsedMealTimeBatch;
	@Autowired
	public ReportTargetMissBatch reportTargetMissBatch;


	@Override
	@Transactional
	public void run(String... args) {


		System.out.println("########## Start scheduledMealBatch");
		System.out.println("########## Start scheduledMealBatch");
		System.out.println("########## Start scheduledMealBatch");
		System.out.println("########## Start scheduledMealBatch");

		scheduledMealBatch.run();

		System.out.println("########## End scheduledMealBatch");
		System.out.println("########## End scheduledMealBatch");
		System.out.println("########## End scheduledMealBatch");
		System.out.println("########## End scheduledMealBatch");

		System.out.println("");

		System.out.println("########## Start reportElapsedMealTimeBatch");
		System.out.println("########## Start reportElapsedMealTimeBatch");
		System.out.println("########## Start reportElapsedMealTimeBatch");
		System.out.println("########## Start reportElapsedMealTimeBatch");

		reportElapsedMealTimeBatch.run();

		System.out.println("########## End reportElapsedMealTimeBatch");
		System.out.println("########## End reportElapsedMealTimeBatch");
		System.out.println("########## End reportElapsedMealTimeBatch");
		System.out.println("########## End reportElapsedMealTimeBatch");

		System.out.println("");

		System.out.println("########## Start reportTargetMissBatch");
		System.out.println("########## Start reportTargetMissBatch");
		System.out.println("########## Start reportTargetMissBatch");
		System.out.println("########## Start reportTargetMissBatch");

		reportTargetMissBatch.run();


		System.out.println("########## End reportTargetMissBatch");
		System.out.println("########## End reportTargetMissBatch");
		System.out.println("########## End reportTargetMissBatch");
		System.out.println("########## End reportTargetMissBatch");
	}


	public static void main(String[] args) throws Exception {
		SpringApplication.run(FoodLogBotBatch.class, args);
	}

}
