package com.kubradaniskan.SpringBootCrudeIslem.controller;

import com.kubradaniskan.SpringBootCrudeIslem.entity.Customer;
import com.kubradaniskan.SpringBootCrudeIslem.model.ApiResponse;
import com.kubradaniskan.SpringBootCrudeIslem.service.ICustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final ICustomerService customerService;

    // Constructor Injection ile bağımlılık alma
    public CustomerController(ICustomerService customerService)
    {
        this.customerService = customerService;
    }

    // YENİ MÜŞTERİ KAYDETME
    @PostMapping("/save")
    public ResponseEntity<Object> addCustomer(@RequestBody Customer customer)
    {
        try {
            customerService.addCustomer(customer);
            return new ResponseEntity<>(new ApiResponse("Müşteri başarıyla eklendi.", customer),
                    HttpStatus.CREATED);
        }
        catch (IllegalArgumentException e)
        {
            // Aynı müşteri varsa
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), "Aynı müşteri bulunmaktadır."),
                    HttpStatus.BAD_REQUEST);
        }
        catch (RuntimeException e) {
            // Veri tabanı için kontrol
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), "Veri tabanı bağlantısı koptu"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // TÜM MÜŞTERİLERİ GETİRME
    @GetMapping("/all")
    public ResponseEntity<Object> getAllCustomer() {
        try
        {
            logger.info("Müşteriler getiriliyor...");
            List<Customer> allCustomer = customerService.findAllCustomer();
            if (allCustomer.isEmpty()) {
                throw new IllegalArgumentException("Veritabanında müşteri bulunamadı.");
            }
            logger.info("Müşteri listesi başarıyla getirildi: toplam müşteri: {}", allCustomer.size());
            return new ResponseEntity<>(new ApiResponse("Tüm müşteriler başarıyla getirildi", allCustomer), HttpStatus.OK);
        }
        catch (IllegalArgumentException e)
        {
            logger.error("Hata: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), "Veritabanında müşteri bulunamadı."), HttpStatus.NOT_FOUND);
        }
        catch (Exception e)
        {
            logger.error("Müşteri listesi alınırken bir hata oluştu: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Müşteri listesi alınırken bir hata oluştu.", "Lütfen tekrar deneyiniz."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // İSTENİLEN MÜŞTERİYİ GETİRME
    @GetMapping("/{id}")
    public ResponseEntity<Object> getCustomerId(@PathVariable("id") Long id) {
        try
        {

            logger.info("Müşteri bilgileri id ile alınıyor: {}", id);
            Customer customerById = customerService.getCustomerById(id);
            if (customerById == null) {
                throw new IllegalArgumentException("Müşteri bulunamadı.");
            }
            logger.info("Müşteri bulundu: {}", customerById);

            return new ResponseEntity<>(new ApiResponse("Müşteri bulundu", customerById),
                    HttpStatus.OK);
        } catch (IllegalArgumentException e)
        {
            logger.error("Hata: {}", e.getMessage());

            return new ResponseEntity<>(new ApiResponse(e.getMessage(), "Belirtilen ID ile müşteri bulunamadı."), HttpStatus.NOT_FOUND);
        }
        catch (Exception e)
        {
            logger.error("Müşteri bulunurken bir hata oluştu: {}", e.getMessage());

            return new ResponseEntity<>(new ApiResponse("Müşteri bulunurken bir hata oluştu.", "Lütfen tekrar kontrol ediniz."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // MÜŞTERİ SİLME
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteCustomerById(@PathVariable("id") Long id) {
        try {
            logger.info("Müşteri siliniyor, id: {}", id);
            customerService.deleteCustomerById(id);
            logger.info("Müşteri başarıyla silindi, id: {}", id);

            return new ResponseEntity<>(new ApiResponse("Müşteri başarıyla silindi", "Başarılı"),
                    HttpStatus.ACCEPTED);
        }
        catch (IllegalArgumentException e)
        {
            logger.error("Hata: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), "ID değeri geçerli değil."),
                    HttpStatus.NOT_FOUND);

        }
        catch (Exception e)
        {

            logger.error("Müşteri silinirken bir hata oluştu: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Müşteri silinirken bir hata oluştu.", "Geçerli bir ID giriniz."),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // MÜŞTERİ ARAMA
    @PostMapping("/search")
    public ResponseEntity<Object> searchCustomer(@RequestBody SearchRequest searchRequest)
    {
        try
        {
            if ((searchRequest.getName() == null || searchRequest.getName().trim().isEmpty()) &&
                    (searchRequest.getSurname() == null || searchRequest.getSurname().trim().isEmpty()))
            {
                throw new IllegalArgumentException("Boş değer girildi.");
            }

            List<Customer> customers = customerService.searchCustomer(searchRequest.getName(), searchRequest.getSurname());

            if (customers.isEmpty()) {
                throw new IllegalArgumentException("Aradığınız kriterlere uygun müşteri bulunamadı.");
            }

            return new ResponseEntity<>(new ApiResponse("Müşteriler başarıyla bulundu", customers),
                    HttpStatus.OK);

        } catch (IllegalArgumentException e)
        {

            logger.error("Hata: {}", e.getMessage());

            return new ResponseEntity<>(new ApiResponse(e.getMessage(), "Aradığınız kriterlere uygun müşteri bulunamadı."),
                    HttpStatus.BAD_REQUEST);

        }
        catch (Exception e) {

            logger.error("Arama yapılırken bir hata oluştu: {}", e.getMessage());

            return new ResponseEntity<>(new ApiResponse("Arama yapılırken bir hata oluştu.", "Lütfen tekrar deneyiniz."),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Veri tabanı bağlantısı hatası
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)

    public ResponseEntity<Object> handleDatabaseException(DataAccessException ex)
    {
        logger.error("Veri tabanı bağlantısı hatası: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse("Veri tabanı bağlantısı koptu. Lütfen tekrar deneyin.", "Bağlantı hatası"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // SQL hataları için
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)

    public ResponseEntity<Object> handleSQLException(SQLException ex) {

        logger.error("SQL hatası: {}", ex.getMessage());

        return new ResponseEntity<>(new ApiResponse("Veri tabanı bağlantısı koptu. Lütfen tekrar deneyin.", "Veritabanı hatası"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
