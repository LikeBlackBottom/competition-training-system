package com.competition.training;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.competition.training.module.**.mapper")
@SpringBootApplication
public class CompetitionTrainingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CompetitionTrainingApplication.class, args);
    }
}
