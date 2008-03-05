/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.fastcodingtools.util.sheduler;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

import com.fastcodingtools.util.log.log4j.LogFastLevel;



/**
 * @author macg
 *
 */
public class SchedulerJobs {

	private static final Logger		logger	= Logger.getLogger(SchedulerJobs.class.getName());
	private static SchedulerJobs instance = new SchedulerJobs();
	private Scheduler	sched;
	private HashMap<String, Object[]> jobs;

	public static SchedulerJobs getInstance() {

		return instance;
	}

	private SchedulerJobs() {
		createSheduler();
		jobs = new HashMap<String, Object[]>();
	}

	private void createSheduler() {

        try {
			SchedulerFactory sf = new StdSchedulerFactory();
			sched = sf.getScheduler();
		} catch (SchedulerException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public void shedule(String name,
					    String group,
					    Class<? extends Job> jobClass,
					    HashMap<String, Object> parameters,
					    long repeatInterval) {

		try {

			//começa a executar no segunto seguinte ao start
			Date runTime = TriggerUtils.getEvenSecondDate(new Date());
			JobDetail jobDetail = new JobDetail(name, group, jobClass);
			jobDetail.getJobDataMap().putAll(parameters);

			SimpleTrigger trigger = new SimpleTrigger(	name + "Trigger",
														group,
														runTime,
														null,
														SimpleTrigger.REPEAT_INDEFINITELY,
														repeatInterval);


			jobs.put(jobDetail.getFullName(), new Object[] {jobDetail,trigger});
			//sched.scheduleJob(jobDetail, trigger);
		} catch (Exception e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public void start() {
		try {

			if(sched.isShutdown()) {
				createSheduler();
			}

			for(String jobDetailName : jobs.keySet()) {
				sched.scheduleJob((JobDetail)jobs.get(jobDetailName)[0], (Trigger)jobs.get(jobDetailName)[1]);
			}

			sched.start();
		} catch (SchedulerException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public boolean isStarted() {

		boolean started = false;

		try {
			started = sched.isStarted() && !sched.isShutdown();
		} catch (SchedulerException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return started;
	}

	public void shutdown(boolean waitForJobsToComplete) {

		try {
			sched.shutdown(waitForJobsToComplete);
		} catch (SchedulerException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}
}
