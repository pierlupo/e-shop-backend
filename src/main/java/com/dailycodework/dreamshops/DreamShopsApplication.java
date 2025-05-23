package com.dailycodework.dreamshops;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;


@Slf4j
@SpringBootApplication
public class DreamShopsApplication {

	// Inject the HikariDataSource
	@Autowired
	private HikariDataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(DreamShopsApplication.class, args);
	}

	@PostConstruct
	public void logPoolStats() {
        log.info("Maximum Pool Size: {}", dataSource.getMaximumPoolSize());
        log.info("Minimum Idle: {}", dataSource.getMinimumIdle());
	}

	@PostConstruct
	public void testDriver() {
		try {
			Driver driver = DriverManager.getDriver("jdbc:mysql://localhost:3306/dream_shops_db");
            log.info("MySQL Driver is loaded: {}", driver.getClass().getName());
		} catch (SQLException e) {
			log.error("Failed to load MySQL driver", e);
		}
	}

}
