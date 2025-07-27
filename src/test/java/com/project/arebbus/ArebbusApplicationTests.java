package com.project.arebbus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "FRONTEND_URL=http://localhost:3000",
    "SECRET_KEY=test-secret-key-for-testing-only"
})
class ArebbusApplicationTests {

    @Test
    void contextLoads() {}

}
