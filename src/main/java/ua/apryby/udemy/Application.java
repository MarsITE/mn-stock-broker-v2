package ua.apryby.udemy;

import io.micronaut.runtime.Micronaut;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "mn-stock-broker-v2",
                version = "0.1",
                description = "Udemy practice",
                license = @License(name = "PAV")
        )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
