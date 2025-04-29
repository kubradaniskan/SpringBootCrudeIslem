package com.kubradaniskan.SpringBootCrudeIslem.controller;

import com.kubradaniskan.SpringBootCrudeIslem.entity.Customer;
import com.kubradaniskan.SpringBootCrudeIslem.model.ApiResponse;
import com.kubradaniskan.SpringBootCrudeIslem.service.ICustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    public CustomerController(ICustomerService customerService) {

        this.customerService = customerService;
    }



    // YENİ MÜŞTERİ KAYDETME
    @PostMapping("/save")
    public ResponseEntity<Object> addCustomer(@RequestBody Customer customer) {
        try {

            customerService.addCustomer(customer);

            return new ResponseEntity<>(new ApiResponse("Müşteri başarıyla eklendi.", customer),
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {

            // Aynı müşteri varsa
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), "Aynı müşteri mevcuttur."),
                    HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {

            // Veri tabanı için kontrol
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), "Müşteri kaydedilirken sistem hatası oluştu"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateCustomer(@PathVariable("id") Long id, @RequestBody Customer updatedCustomer) {
        try {
            logger.info("Müşteri güncelleniyor, ID: {}", id);

            Customer updated = customerService.updateCustomer(id, updatedCustomer);

            return new ResponseEntity<>(new ApiResponse("Müşteri başarıyla güncellendi.", updated),
                    HttpStatus.OK);
        } 
        catch (IllegalArgumentException e) {
            logger.error("Güncelleme hatası: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), "Güncellemede hata oluştu."),
                    HttpStatus.NOT_FOUND);
        }
        catch (Exception e)
        {
            logger.error("Müşteri güncellenirken hata oluştu: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Müşteri güncellenirken hata oluştu.", "Güncelleme başarısız"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // TÜM MÜŞTERİLERİ GETİRME
    @GetMapping("/all")
    public ResponseEntity<Object> getAllCustomer() {
        try {
            logger.info("Müşteriler getiriliyor...");

            List<Customer> allCustomer = customerService.findAllCustomer();

            if (allCustomer.isEmpty()) {
                throw new IllegalArgumentException("Veri tabanında müşteri bulunamadı.");
            }

            logger.info("Müşteri listesi başarıyla getirildi: toplam müşteri: {}", allCustomer.size());

            return new ResponseEntity<>(new ApiResponse("Tüm müşteriler başarıyla getirildi", allCustomer),
                    HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.error("Hata: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), "Hiç müşteri bulunmamakta.s"),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Müşteri listesi alınırken bir hata oluştu: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Müşteri listesi alınırken bir hata oluştu.", "Sunucu hatası"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // İSTENİLEN MÜŞTERİYİ GETİRME
    @GetMapping("/{id}")
    public ResponseEntity<Object> getCustomerId(@PathVariable("id") Long id) {
        try {
            logger.info("Müşteri bilgileri id ile alınıyor: {}", id);

            Customer customerById = customerService.getCustomerById(id);

            if (customerById == null) {
                throw new IllegalArgumentException("Müşteri bulunamadı.");
            }

            logger.info("Müşteri bulundu: {}", customerById);

            return new ResponseEntity<>(new ApiResponse("Müşteri bulundu", customerById),
                    HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.error("Hata: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Müşteri bulunurken bir hata oluştu: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Müşteri bulunurken bir hata oluştu.", "Beklenmeyen hata"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // MÜŞTERİ SİLME
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteCustomerById(@PathVariable("id") Long id) {
        try {
            logger.info("Müşteri siliniyor, id: {}", id);

            // Müşteri silme
            customerService.deleteCustomerById(id);

            logger.info("Müşteri başarıyla silindi, id: {}", id);

            return new ResponseEntity<>(new ApiResponse("Müşteri başarıyla silindi", "Başarıyla silindi."),
                    HttpStatus.ACCEPTED);

        } catch (IllegalArgumentException e) {
            logger.error("Hata: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Müşteri silinirken bir hata oluştu: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Müşteri silinirken bir hata oluştu.", "Silme işlemi başarısız"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //TÜM MÜŞTERİLERİ SİLME
    @DeleteMapping("/delete/all")
        public ResponseEntity<Object> deleteAllCustomer(){
            try
            {
            logger.info("Tüm müşteriler siliniyor...");

            customerService.deleteAllCustomer();

            logger.info("Tüm müşteriler başarıyla silindi");

            return new ResponseEntity<>(new ApiResponse("Tüm müşteriler başarıyla silindi","Silme işlemi başarılı"),
                    HttpStatus.ACCEPTED);
            }
            catch(Exception e) {

                logger.info("Tüm müşteriler silinirken bir hata oluştu! {}", e.getMessage());

                return new ResponseEntity<>(new ApiResponse("Tüm müşteriler silinirken hata oluştu","Silme işlemi başarısız"),
                HttpStatus.INTERNAL_SERVER_ERROR);
            }


        }


    // MÜŞTERİ ARAMA
    @PostMapping("/search")
    public ResponseEntity<Object> searchCustomer(@RequestBody SearchRequest searchRequest) {
        try {
            //trim(boşluk silme) .isEmpty() de tırnak içindeki boşluğu da boşluk kabul ediyor.
            if ((searchRequest.getName() == null || searchRequest.getName().trim().isEmpty()) &&
                    (searchRequest.getSurname() == null || searchRequest.getSurname().trim().isEmpty())) {
                throw new IllegalArgumentException("Boş değer girildi.");
            }

            List<Customer> customers = customerService.searchCustomer(searchRequest.getName(),
                    searchRequest.getSurname());

            if (customers.isEmpty()) {
                throw new IllegalArgumentException("Aradığınız kriterlere uygun müşteri bulunamadı.");
            }

            return new ResponseEntity<>(new ApiResponse("Müşteriler başarıyla bulundu", customers),
                    HttpStatus.OK);

        } catch (IllegalArgumentException e) {

            logger.error("Hata: {}", e.getMessage());

            return new ResponseEntity<>(new ApiResponse(e.getMessage(), "Arama işlemi başarısız"),
                    HttpStatus.BAD_REQUEST);

        } catch (Exception e) {

            logger.error("Arama yapılırken bir hata oluştu: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Arama yapılırken bir hata oluştu.", "Arama işlemi başarısız."),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("searchLetter")
    public ResponseEntity<Object> searchCustomerLetter(@RequestBody SearchRequest searchRequest) {
        try {
            if ((searchRequest.getName() == null || searchRequest.getName().trim().isEmpty()) &&
                    (searchRequest.getSurname() == null || searchRequest.getSurname().trim().isEmpty())) {
                throw new IllegalArgumentException("Lütfen aramak için bir harf giriniz.");
            }

            List<Customer> customers = customerService.searchCustomerLetter(searchRequest.getName());

            if (customers.isEmpty()) {

                throw new IllegalArgumentException("Aradığınız harfle başlayan müşteri bulunmamaktadır.");

            }

            return new ResponseEntity<>(new ApiResponse("Müşteriler başarıyla bulundu", customers),
                    HttpStatus.OK);
        }
        catch(IllegalArgumentException e) {

            logger.error("Hata: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), "Arama işlemi başarısız."),
                    HttpStatus.BAD_REQUEST);


        }
        catch (Exception e) {

            logger.error("Arama yapılırken bir hata oluştu: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Arama yapılırken hata oluştu.", "Arama işlemi başarısız."),
                    HttpStatus.INTERNAL_SERVER_ERROR);


            }
        }



    // Veri tabanı bağlantısı hatası için kontrol
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleDatabaseException(DataAccessException ex)
    {
        logger.error("Veri tabanı bağlantısı hatası: {}", ex.getMessage());

        return new ResponseEntity<>(new ApiResponse("Veri tabanı bağlantısı koptu. Lütfen tekrar deneyin.", "Tekrar deneyiniz."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // SQL hataları için
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)

    public ResponseEntity<Object> handleSQLException(SQLException ex) {

        logger.error("SQL hatası: {}", ex.getMessage());

        return new ResponseEntity<>(new ApiResponse("Veri tabanı bağlantısı koptu. Lütfen tekrar deneyin.", "Tekrar deneyiniz."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

