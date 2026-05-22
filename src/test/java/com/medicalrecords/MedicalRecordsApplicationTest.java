package com.medicalrecords;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MedicalRecordsApplicationTest {

    @Test
    void contextLoads() {
        // Verifies the full Spring context starts successfully with H2 + Liquibase
    }
}
