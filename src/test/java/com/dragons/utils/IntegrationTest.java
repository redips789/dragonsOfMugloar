package com.dragons.utils;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "dragons_of_mugloar_app.host=http://localhost:${wiremock.server.port}"
        })
@AutoConfigureWireMock(port = 0, stubs = {
        "file:src/test/resources/stubs/",
        "file:src/test/resources/stubs/scenario/"
})
public @interface IntegrationTest {
}
