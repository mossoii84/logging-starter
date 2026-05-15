package ru.mossoii84.loggingstarter.webfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class WebLoggingFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingFilter.class);

    private static String formatQueryString(HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString()).map(qs -> "?" + qs).orElse(Strings.EMPTY);
    }

    /*
      Filter — это обычный «перехватчик» (Interceptor) от tomcat.
    */
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String method = request.getMethod();
        String requestUrl = request.getRequestURI() + formatQueryString(request);
        String headersRequest = inlineHeadersRequest(request);
        log.info("Запрос: {} {} {}", method, requestUrl, headersRequest);

        // перехватывает и кэшируем body - > сохраняет копию в памяти -> позволяет читать его много раз (без этого читает-> исчезать)
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        // чтобы читать body response (в нем нет header !)
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            String headerResponse = inlineHeadersResponse(response);

            String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            log.info("Ответ: {}, body={}, статус={}", headerResponse, responseBody, response.getStatus());

            // иначе клиент получит пустой экран
            responseWrapper.copyBodyToResponse();
        }
    }

    private String inlineHeadersRequest(HttpServletRequest request) {
        Map<String, String> headersMap = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(it -> it, request::getHeader));

        String inlineHeaders = headersMap.entrySet().stream().map(entry -> {
            String headerName = entry.getKey();
            String headerValue = entry.getValue();

            return headerName + "=" + headerValue;
        }).collect(Collectors.joining(","));
        return "headers = {" + inlineHeaders + "}";
    }

    private String inlineHeadersResponse(HttpServletResponse response) {
        // 1. Берем все заголовки, которые ЕСТЬ в коллекции names
        Map<String, String> headersMap = response.getHeaderNames().stream().collect(Collectors.toMap(name -> name, response::getHeader));

        headersMap.putIfAbsent("Content-Type", response.getContentType());
        headersMap.putIfAbsent("Character-Encoding", response.getCharacterEncoding());
        headersMap.putIfAbsent("Locale", response.getLocale().toString());

        String inlineHeaders = headersMap.entrySet().stream().map(entry ->
                entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(","));

        return "headers = {" + inlineHeaders + "}";
    }

}