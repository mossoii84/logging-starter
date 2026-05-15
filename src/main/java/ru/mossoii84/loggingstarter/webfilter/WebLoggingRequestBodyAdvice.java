package ru.mossoii84.loggingstarter.webfilter;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

/**
 * Если LoggingFilter (который мы обсуждали в начале) перехватывает «сырой» поток данных на уровне HTTP-пакета,
 * то RequestBodyAdvice работает глубже — на уровне Spring MVC и Jackson.
 * Он позволяет «вклиниться» в процесс превращения JSON из тела запроса в Java-объект (десериализация).
 * <p>
 * Его основные задачи:
 * Чтение тела запроса: Вы можете перехватить JSON до того, как он попадет в аргументы метода контроллера (@RequestBody).
 * Логирование: Это идеальное место для записи входящих данных в логи, так как здесь у вас есть доступ и к типу объекта,и к самому телу.
 * Модификация: Вы можете изменить данные «на лету» (например, обрезать лишние пробелы в строках или добавить технические поля) до того, как контроллер их увидит.
 */

@ControllerAdvice
public class WebLoggingRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingRequestBodyAdvice.class);


    @Autowired
    private HttpServletRequest request;

    @Override
    public Object afterBodyRead(@NonNull Object body, @NonNull HttpInputMessage inputMessage, @NonNull MethodParameter parameter,
                                @NonNull Type targetType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        log.info("Http запрос: метод={} адрес={} body={}",
                request.getMethod(),
                request.getRequestURL(),
                body);
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Type targetType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
}