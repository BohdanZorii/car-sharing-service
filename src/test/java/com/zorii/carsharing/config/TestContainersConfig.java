package com.zorii.carsharing.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@TestConfiguration
public class TestContainersConfig {

  @Bean
  public PostgreSQLContainer<?> postgresContainer() {
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("car_sharing")
        .withUsername("test")
        .withPassword("test");
    container.start();
    return container;
  }

  @Bean
  public DataSource dataSource(PostgreSQLContainer<?> postgresContainer) {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setUrl(postgresContainer.getJdbcUrl());
    dataSource.setUsername(postgresContainer.getUsername());
    dataSource.setPassword(postgresContainer.getPassword());
    return dataSource;
  }
}
