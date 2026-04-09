package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = "app")
public class BikeRentalSystemApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(BikeRentalSystemApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BikeRentalSystemApplication.class);
    }
}
