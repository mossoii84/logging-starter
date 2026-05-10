package ru.mossoii84.loggingstarter;


import org.springframework.context.annotation.Bean;
import ru.mossoii84.loggingstarter.aspect.LogExecutionAspect;

public class LoggingStarterAutoConfiguration {



    @Bean
    public LogExecutionAspect logExecutionAspect() {
        return new LogExecutionAspect();
    }









//    уже не нужно если через OAP, а не через proxy в другом проектеы
//    public static void println(String str) {
//        System.out.println("welcome - LoggingStarter!");
//    }
}