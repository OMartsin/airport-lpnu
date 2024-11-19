package dev.pz.airportlpnu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirportLpnuApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirportLpnuApplication.class, args);
    }

}
