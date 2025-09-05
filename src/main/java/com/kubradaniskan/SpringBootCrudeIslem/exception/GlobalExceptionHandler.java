package com.kubradaniskan.SpringBootCrudeIslem.exception;

import com.kubradaniskan.SpringBootCrudeIslem.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.sql.SQLException;
import java.util.Locale;

@ControllerAdvice // global hata yakalayıcı olarak işlem yapar
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;

    // Locale ayarlarına göre mesaj döndürmek için
    //LocaleContextHolder.getLocale()) kullanıcının dil tercihine göre propertiesten bilgi çekiyor
    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale()); //locale uygun hata dönmesi için

    }

    //HANDLER Gelen HTTP isteğini karşılayan ve işleyen metot veya sınıftır.

    // Generic exception (beklenmedik hata)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        logger.error("Beklenmeyen hata: {}", ex.getMessage(), ex);

        ApiResponse response = new ApiResponse(
                getMessage("error.general"),
                getMessage("error.server.unexpected"),
                "Error"
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Geçersiz argüman hatası için
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Geçersiz istek: {}", ex.getMessage());

        ApiResponse response = new ApiResponse(
                ex.getMessage(),
                getMessage("error.badrequest"),
                "Warning"
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Veri tabanı bağlantısı hatası için
    @ExceptionHandler(DatabaseConnectionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleDatabaseConnectionException(DatabaseConnectionException ex) {
        logger.error("Veri tabanı bağlantısı hatası: {}", ex.getMessage(), ex);

        ApiResponse response = new ApiResponse(
                getMessage("error.database.connection"),
                getMessage("error.server.db.connection"),
                "Error"
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // SQL hatası (Veri tabanı sorgusu hatası)
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleSQLException(SQLException ex) {
        logger.error("SQL hatası: {}", ex.getMessage(), ex);

        ApiResponse response = new ApiResponse(
                getMessage("error.sql"),
                getMessage("error.server.sql"),
                "Error"
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Kaynak bulunamadı hatası (müşteri bulunamadığında vereceğiz örnek olarak)
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Kaynak bulunamadı: {}", ex.getMessage(), ex);

        ApiResponse response = new ApiResponse(
                ex.getMessage(), // servis katmanında çevrilmişti
                getMessage("error.resource.notfound"),
                "Warning"
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}