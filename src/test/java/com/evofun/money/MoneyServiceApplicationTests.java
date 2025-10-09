package com.evofun.money;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class MoneyServiceApplicationTests {

	@Container
	static final PostgreSQLContainer<?> POSTGRES =
			new PostgreSQLContainer<>("postgres:16-alpine")
					.withDatabaseName("money_test")
					.withUsername("test")
					.withPassword("test");

	@DynamicPropertySource
	static void props(DynamicPropertyRegistry r) {
		// DataSource -> Postgres из Testcontainers
		r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
		r.add("spring.datasource.username", POSTGRES::getUsername);
		r.add("spring.datasource.password", POSTGRES::getPassword);
		r.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

		// JPA
		r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
		r.add("spring.jpa.properties.hibernate.default_schema", () -> "money_schema");

		// Flyway: Main migrations folder + test folder with V0__test_bootstrap.sql
		r.add("spring.flyway.enabled", () -> true);
		r.add("spring.flyway.locations", () -> "classpath:db/migration,classpath:db/migration-test");
		r.add("spring.flyway.create-schemas", () -> true);
		r.add("spring.flyway.default-schema", () -> "money_schema");
		r.add("spring.flyway.schemas", () -> "money_schema");
	}

	@Test
	void contextLoads() {}
}
