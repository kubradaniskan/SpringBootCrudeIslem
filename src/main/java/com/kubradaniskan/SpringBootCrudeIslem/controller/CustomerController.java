package com.kubradaniskan.SpringBootCrudeIslem.controller;

import com.kubradaniskan.SpringBootCrudeIslem.entity.Customer;
import com.kubradaniskan.SpringBootCrudeIslem.exception.DatabaseConnectionException;
import com.kubradaniskan.SpringBootCrudeIslem.exception.ResourceNotFoundException;
import com.kubradaniskan.SpringBootCrudeIslem.model.ApiResponse;
import com.kubradaniskan.SpringBootCrudeIslem.service.ICustomerService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api")
public class CustomerController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CustomerController.class);
    private final ICustomerService customerService;

    @Autowired
    private MessageSource messageSource;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    // MessageSource kullanarak uygun dilde mesajı alıyoruz
    private String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }

    // Yeni müşteri kaydetme
    @PostMapping("/save")
    public ResponseEntity<Object> addCustomer(@RequestBody Customer customer) {
        try {
            logger.info("Yeni müşteri kaydediliyor: {}", customer);
            Customer savedCustomer = customerService.addCustomer(customer);
            return new ResponseEntity<>(
                    new ApiResponse(getMessage("success.customer.added"), savedCustomer, "Success"),
                    HttpStatus.CREATED
            );
        } catch (DatabaseConnectionException ex) {
            logger.error("Veri tabanı bağlantısı hatası: {}", ex.getMessage());
            return new ResponseEntity<>(
                    new ApiResponse(getMessage("error.database.connection"),
                            getMessage("error.database.connection.details"), "Error"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        catch (IllegalArgumentException ex)
        {
            return new ResponseEntity<>(new ApiResponse(ex.getMessage(), getMessage("error.customer.exists"), "Warning"), HttpStatus.BAD_REQUEST);
        }
    }

    // Müşteri güncelleme
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateCustomer(@PathVariable("id") Long id, @RequestBody Customer updatedCustomer) {
        try {
            logger.info("Müşteri güncelleniyor, ID: {}", id);
            Customer updated = customerService.updateCustomer(id, updatedCustomer);
            return new ResponseEntity<>(new ApiResponse(getMessage("success.customer.updated"), updated, "Success"), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(new ApiResponse(ex.getMessage(), getMessage("warning.customer.notfound"), "Warning"), HttpStatus.NOT_FOUND);
        } catch (DatabaseConnectionException ex) {
            logger.error("Veri tabanı bağlantısı hatası: {}", ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(getMessage("error.database.connection"), getMessage("error.database.connection.details"), "Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Tüm müşterileri getirme
    @GetMapping("/all")
    public ResponseEntity<Object> getAllCustomer() {
        try {
            List<Customer> allCustomer = customerService.findAllCustomer();
            if (allCustomer.isEmpty()) {
                logger.warn("Müşteri listesi boş.");
                return new ResponseEntity<>(new ApiResponse(getMessage("warning.customer.list.empty"),
                        getMessage("warning.customer.notfound"), "Warning"), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new ApiResponse(getMessage("success.customer.all.found"),
                    allCustomer, "Success"), HttpStatus.OK);
        } catch (DatabaseConnectionException ex) {
            logger.error("Veri tabanı bağlantısı hatası: {}", ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(getMessage("error.database.connection"),
                    getMessage("error.database.connection.details"), "Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ID'ye göre müşteri getirme
    @GetMapping("/{id}")
    public ResponseEntity<Object> getCustomerById(@PathVariable("id") Long id) {
        try
        {
            logger.info("Müşteri bilgileri alınıyor, ID: {}", id);

            Customer customerById = customerService.getCustomerById(id);
            return new ResponseEntity<>(new ApiResponse(getMessage("success.customer.found"), customerById,
                    "Success"), HttpStatus.OK);
        }
        catch (ResourceNotFoundException ex)
        {
            return new ResponseEntity<>(new ApiResponse(ex.getMessage(), getMessage("warning.customer.notfound"),
                    "Warning"), HttpStatus.NOT_FOUND);

        }
        catch (DatabaseConnectionException ex) {

            logger.error("Veri tabanı bağlantısı hatası: {}", ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(getMessage("error.database.connection"),
                    getMessage("error.database.connection.details"), "Error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Müşteri silme (ID ile)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteCustomerById(@PathVariable("id") Long id) {
        try {
            logger.info("Müşteri siliniyor, ID: {}", id);
            customerService.deleteCustomerById(id);
            return new ResponseEntity<>(new ApiResponse(getMessage("success.customer.deleted"),
                    getMessage("success.customer.deleted"), "Success"),
                    HttpStatus.ACCEPTED);
        }

        catch (ResourceNotFoundException ex)
        {
            return new ResponseEntity<>(new ApiResponse(ex.getMessage(),
                    getMessage("warning.customer.notfound"), "Warning"),
                    HttpStatus.NOT_FOUND);
        }
        catch (DatabaseConnectionException ex)
        {
            logger.error("Veri tabanı bağlantısı hatası: {}", ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(getMessage("error.database.connection"),
                    getMessage("error.database.connection.details"), "Error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Tüm müşterileri silme
    @DeleteMapping("/delete/all")
    public ResponseEntity<Object> deleteAllCustomer() {
        try
        {
            logger.info("Tüm müşteriler siliniyor...");
            customerService.deleteAllCustomer();
            return new ResponseEntity<>(new ApiResponse(getMessage("success.customer.all.deleted"),
                    getMessage("success.customer.all.deleted"), "Success"),
                    HttpStatus.ACCEPTED);
        }

        catch (DatabaseConnectionException ex)
        {
            logger.error("Veri tabanı bağlantısı hatası: {}", ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(getMessage("error.database.connection"),
                    getMessage("error.database.connection.details"), "Error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // Müşteri arama (Ad ve Soyad ile)
    @PostMapping("/search")
    public ResponseEntity<Object> searchCustomer(@RequestBody SearchRequest searchRequest) {
        try
        {
            if ((searchRequest.getName() == null || searchRequest.getName().trim().isEmpty()) &&
                    (searchRequest.getSurname() == null || searchRequest.getSurname().trim().isEmpty())) {
                return new ResponseEntity<>(new ApiResponse(getMessage("error.customer.name.empty"),
                        getMessage("warning.customer.notfound"), "Warning"),
                        HttpStatus.BAD_REQUEST);
            }

            List<Customer> customers = customerService.searchCustomer(searchRequest.getName(),
                    searchRequest.getSurname());
            if (customers.isEmpty()) {

                return new ResponseEntity<>(new ApiResponse(getMessage("warning.customer.notfound"),
                        getMessage("warning.customer.notfound"), "Warning"),
                        HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(new ApiResponse(getMessage("success.customer.search.found"),
                    customers, "Success"),
                    HttpStatus.OK);
        }
        catch (DatabaseConnectionException ex)
        {
            logger.error("Veri tabanı bağlantısı hatası: {}", ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(getMessage("error.database.connection"),
                    getMessage("error.database.connection.details"), "Error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Harf ile müşteri arama
    @PostMapping("/searchLetter")
    public ResponseEntity<Object> searchCustomerLetter(@RequestBody SearchRequest searchRequest) {
        try
        {
            if (searchRequest.getName() == null || searchRequest.getName().trim().isEmpty()) {
                throw new IllegalArgumentException(getMessage("error.customer.letter.empty"));
            }

            List<Customer> customers = customerService.searchCustomerLetter(searchRequest.getName());
            if (customers.isEmpty())
            {
                throw new IllegalArgumentException(getMessage("error.customer.letter.notfound"));
            }

            return new ResponseEntity<>(new ApiResponse(getMessage("success.customer.search.found"),
                    customers, "Success"),
                    HttpStatus.OK);
        }
        catch (IllegalArgumentException ex)
        {
            return new ResponseEntity<>(new ApiResponse(ex.getMessage(), getMessage("warning.customer.notfound"), "Warning"), HttpStatus.BAD_REQUEST);
        }
        catch (DatabaseConnectionException ex)
        {
            logger.error("Veri tabanı bağlantısı hatası: {}", ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(getMessage("error.database.connection"),
                    getMessage("error.database.connection.details"), "Error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}