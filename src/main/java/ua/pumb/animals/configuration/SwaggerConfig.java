package ua.pumb.animals.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${application.version}")
    private String version;

    @Bean
    public OpenAPI openApiInformation() {

        Contact contact = new Contact()
                .email("zlatnikovyevgeniy@gmail.com")
                .name("Yevhenii Pustovit");

        Info info = new Info()
                .contact(contact)
                .description("Here you can check available animals and sell some if you want.")
                .title("Animals store")
                .version(version)
                .license(new License().name("Apache 2.0").url("http://springdoc.org"));

        return new OpenAPI().info(info);
    }
}
