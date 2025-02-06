package de.uol.pgdoener.th1;

import org.openapitools.configuration.SpringDocConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {Th1Application.class, SpringDocConfiguration.class})
public class Th1Application {
    public static void main(String[] args) {
        //System.out.println(org.hibernate.Version.getVersionString());
        SpringApplication.run(Th1Application.class, args);
    }
}
