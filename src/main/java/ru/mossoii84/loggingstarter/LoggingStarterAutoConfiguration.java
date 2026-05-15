package ru.mossoii84.loggingstarter;


import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import ru.mossoii84.loggingstarter.aspect.LogExecutionAspect;
import ru.mossoii84.loggingstarter.webfilter.WebLoggingFilter;
import ru.mossoii84.loggingstarter.webfilter.WebLoggingRequestBodyAdvice;

/**
 * Она говорит Spring Boot: «Создавай этот бин (объект) только в том случае, если в настройках включены определенные параметры».
 * -@ConditionalOnProperty(prefix = "logging", value = {"enable", "log-body"}, havingValue = "true")
 * Spring пойдет в application.properties (или application.yml) и будет искать две настройки:
 * logging.enable и logging.log-body
 * Бин активируется только если обе эти настройки имеют значение true.*
 * app.yaml должны быть строки
 * logging:
 *   enable: true
 *   log-body: true
 */


@AutoConfiguration
@ConditionalOnProperty(prefix = "logging", value = "enable", havingValue = "true", matchIfMissing = true)
public class LoggingStarterAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "logging", value = "log-exec-time", havingValue = "true")
    public LogExecutionAspect logExecutionAspect() {
        return new LogExecutionAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "enable", havingValue = "true", matchIfMissing = true)
    public WebLoggingFilter webLoggingFilter() {
        return new WebLoggingFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging", value = {"enable","log-body"}, havingValue = "true")
    public WebLoggingRequestBodyAdvice webLoggingRequestBodyAdvice() {
        return new WebLoggingRequestBodyAdvice();
    }


//    уже не нужно если через OAP, а не через proxy в другом проекте
//    public static void println(String str) {System.out.println("welcome - LoggingStarter!");}
}