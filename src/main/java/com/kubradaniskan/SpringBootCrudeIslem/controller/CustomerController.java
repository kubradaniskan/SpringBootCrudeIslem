package com.kubradaniskan.SpringBootCrudeIslem.controller;

import com.kubradaniskan.SpringBootCrudeIslem.entity.Customer;
import com.kubradaniskan.SpringBootCrudeIslem.model.ApiResponse;
import com.kubradaniskan.SpringBootCrudeIslem.service.ICustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final ICustomerService customerService;

    // Constructor Injection ile bağımlılığı alıyoruz
    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    // YENİ MÜŞTERİ KAYDETME
    @PostMapping("/save")
    public ResponseEntity<Object> addCustomer(@RequestBody Customer customer) {
        try {
            // Müşteri eklenmeye çalışılıyor
            customerService.addCustomer(customer);

            // Eğer hata yoksa, işlem başarılı
            return new ResponseEntity<>(new ApiResponse("Müşteri başarıyla eklendi.", customer), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Eğer müşteri adı veya soyadı boşsa veya aynı müşteri varsa
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // Veritabanı hatası veya genel hata
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // TÜM MÜŞTERİLERİ GETİRME
    @GetMapping("/all")
    public ResponseEntity<Object> getAllCustomer() {
        try {
            logger.info("Tüm müşteriler getiriliyor...");

            List<Customer> allCustomer = customerService.findAllCustomer();

            if (allCustomer.isEmpty()) {
                throw new IllegalArgumentException("Veritabanında müşteri bulunamadı.");
            }

            logger.info("Müşteri listesi başarıyla getirildi: toplam müşteri: {}", allCustomer.size());

            return new ResponseEntity<>(new ApiResponse("Tüm müşteriler başarıyla getirildi", allCustomer), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.error("Hata: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Müşteri listesi alınırken bir hata oluştu: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Müşteri listesi alınırken bir hata oluştu.", null), HttpStatus.INTERNAL_SERVER_ERROR);
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

            return new ResponseEntity<>(new ApiResponse("Müşteri bulundu", customerById), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.error("Hata: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Müşteri bulunurken bir hata oluştu: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Müşteri bulunurken bir hata oluştu.", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // MÜŞTERİ SİLME
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteCustomerById(@PathVariable("id") Long id) {
        try {
            logger.info("Müşteri siliniyor, id: {}", id);

            // Müşteri silme işlemi
            customerService.deleteCustomerById(id);

            logger.info("Müşteri başarıyla silindi, id: {}", id);

            return new ResponseEntity<>(new ApiResponse("Müşteri başarıyla silindi", null), HttpStatus.ACCEPTED);

        } catch (IllegalArgumentException e) {
            logger.error("Hata: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Müşteri silinirken bir hata oluştu: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Müşteri silinirken bir hata oluştu.", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // İSİM VE/VEYA SOYADLA MÜŞTERİ ARAMA
    @PostMapping("/search")
    public ResponseEntity<Object> searchCustomer(@RequestBody SearchRequest searchRequest) {
        try {
            if ((searchRequest.getName() == null || searchRequest.getName().trim().isEmpty()) &&
                    (searchRequest.getSurname() == null || searchRequest.getSurname().trim().isEmpty())) {
                throw new IllegalArgumentException("Boş değer girildi.");
            }

            List<Customer> customers = customerService.searchCustomer(searchRequest.getName(), searchRequest.getSurname());

            if (customers.isEmpty()) {
                throw new IllegalArgumentException("Aradığınız kriterlere uygun müşteri bulunamadı.");
            }

            return new ResponseEntity<>(new ApiResponse("Müşteriler başarıyla bulundu", customers), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.error("Hata: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Arama yapılırken bir hata oluştu: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Arama yapılırken bir hata oluştu.", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
