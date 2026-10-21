package com.bits.foodie;

import com.bits.foodie.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class FoodieApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodieApplication.class, args);
    }
}
