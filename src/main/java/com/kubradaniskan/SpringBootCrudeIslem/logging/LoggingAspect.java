package com.kubradaniskan.SpringBootCrudeIslem.logging;

import com.kubradaniskan.SpringBootCrudeIslem.model.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.MDC;

@Aspect
@Component
public class LoggingAspect {

    //amacımız Controller içindeki tüm metotlara ait gelen istekleri ve hataları JSOn formatında loglama
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    //pointcut ile controller metodları seçilir
    @Pointcut("execution(* com.kubradaniskan.SpringBootCrudeIslem.controller..*(..))")
    public void controllerMethods() {}

    //before ile  istek öncesi loglama yapılır (HTTP metotları, IP gibi parametre)
    @Before("controllerMethods()")
    public void logRequest() {
        HttpServletRequest request = getCurrentRequest();

        //gelen http isteklerime ait bilgileri topladım
        logger.info("Request | method={} | uri={} | ip={} | params={}",
                request.getMethod(),
                request.getRequestURI(),
                getClientIp(request),
                getFormattedParams(request.getParameterMap()));
    }

    //Başarılı dönüleri loglamak için after kullanılır
    @AfterReturning(value = "controllerMethods()", returning = "result")
    public void logSuccess(Object result) {
        HttpServletRequest request = getCurrentRequest();

        logger.info("Success | method={} | uri={} | ip={} | response={}",
                request.getMethod(),
                request.getRequestURI(),
                getClientIp(request),
                (result instanceof ApiResponse) ? ((ApiResponse) result).toJson() : String.valueOf(result));
    }

     //Hata durumlarını kodlamak için After
    @AfterThrowing(value = "controllerMethods()", throwing = "ex")
    public void logError(Exception ex) {
        HttpServletRequest request = getCurrentRequest();

        logger.error("Error | method={} | uri={} | ip={} | message={}",
                request.getMethod(),
                request.getRequestURI(),
                getClientIp(request),
                ex.getMessage(), ex);
    }

    // mevcut request nesnesini alır
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    // IP adresini alır, proxy varsa başlıktan okur
    private String getClientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        return (header != null && !header.isEmpty()) ? header.split(",")[0] : request.getRemoteAddr();
    }

    //Parametreleri JSON benzeri stringe çevirir
    private String getFormattedParams(Map<String, String[]> params) {
        return params.entrySet().stream()
                .map(entry -> "\"" + entry.getKey() + "\":\"" + Arrays.toString(entry.getValue()) + "\"")
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
