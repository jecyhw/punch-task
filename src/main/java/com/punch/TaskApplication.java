package com.punch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@RestController
public class TaskApplication {
	private final RestTemplate restTemplate;

	static private final Logger logger = LoggerFactory.getLogger(TaskApplication.class);

	@Autowired
	public TaskApplication(HttpMessageConverters messageConverters) {
		this.restTemplate = new RestTemplate(messageConverters.getConverters());
	}

	public static void main(String[] args) {
		SpringApplication.run(TaskApplication.class, args);
	}

	@Schedules( { @Scheduled(cron = "0 55 17 * * ?", zone = "Asia/Shanghai"), @Scheduled(cron = "0 41 9 * * ?", zone = "Asia/Shanghai") })
	public void punchWorkTask() {
		long end = System.currentTimeMillis() + 60 * 60 * 1000;
		while (System.currentTimeMillis() < end) {
			try {
				try {
					restTemplate.getForObject("https://punch-cnic.herokuapp.com/health/check", String.class);
				} catch (Exception e) {
					restTemplate.getForObject("https://punch-cnic-1.herokuapp.com/health/check", String.class);
				}
				Thread.sleep(1000 * 60 * 5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "health", method = RequestMethod.GET)
	public String health() {
		return "SUCCESS";
	}
}
