package com.kubradaniskan.SpringBootCrudeIslem.service.impl;

import com.kubradaniskan.SpringBootCrudeIslem.entity.Customer;
import com.kubradaniskan.SpringBootCrudeIslem.repository.CustomerRepository;
import com.kubradaniskan.SpringBootCrudeIslem.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;


    /***
     * throw new hata fırlatmak için kullanılır.
     * IllegalArgumentException parametrelerin yanlış olması durumunda hata fırlatmak için
     * @param customer
     * @return
     */
    @Override
    public Customer addCustomer(Customer customer) {
        try {
            // Adın boşluk kontrolü
            if (customer.getName() == null || customer.getName().trim().isEmpty()) {

                throw new IllegalArgumentException("Müşteri adı boş olamaz.");

            }

            // Soyad boşluk kontrolü
            if (customer.getSurname() == null || customer.getSurname().trim().isEmpty()) {

                throw new IllegalArgumentException("Müşteri soyadı boş olamaz.");
            }

            // Aynı isim ve soyad ile bir müşteri olup olmadığını kontrol et
            if (customerRepository.existsByNameAndSurname(customer.getName(), customer.getSurname()))
            {
                throw new IllegalArgumentException("Bu isim ve soyad ile bir müşteri zaten mevcut.");
            }

            // Veri tabanına yeni müşteri kaydet
            return customerRepository.save(customer);

        } catch (DataAccessException e) {

            // Veri tabanı kontrolü

            throw new RuntimeException("Veritabanı bağlantısı hatası. Lütfen tekrar deneyin.", e);

        }
        catch (IllegalArgumentException e)
        {


            throw e;

        } catch (Exception e) {

            throw new RuntimeException("Müşteri eklenirken bir hata oluştu.", e);
        }
    }


    @Override
    public List<Customer> findAllCustomer() {
        try {

            return customerRepository.findAll();

        }
        catch (DataAccessException e) {

            throw new RuntimeException("Veri tabanı bağlantısı hatası. Lütfen tekrar deneyin.", e);

        }
        catch (Exception e) {

            throw new RuntimeException("Müşteriler alınırken bir hata oluştu.", e);
        }
    }

    @Override
    public Customer getCustomerById(Long customerId) {
        try
        {
            return customerRepository.findById(customerId).orElse(null);

        }
        catch (DataAccessException e)
        {
            throw new RuntimeException("Veri tabanı bağlantısı hatası. Lütfen tekrar deneyin.", e);

        }
        catch (Exception e) {

            throw new RuntimeException("Müşteri bilgisi alınırken bir hata oluştu.", e);
        }
    }

    @Override
    public void deleteCustomerById(Long id) {
        try {
            // Müşteri var mı kontrol ediyoruz.
            Customer customer = customerRepository.findById(id).orElse(null);

            if (customer == null) {
                throw new IllegalArgumentException("Müşteri bulunamadı.");
            }

            // Eğer müşteri varsa, sil
            customerRepository.deleteById(id);

        } catch (DataAccessException e) {

            throw new RuntimeException("Veri tabanı bağlantısı hatası. Lütfen tekrar deneyin.", e);

        }

        catch (Exception e)
        {
            throw new RuntimeException("Müşteri silinirken bir hata oluştu.", e);
        }
    }

    @Override
    public boolean existsByNameAndSurname(String name, String surname) {

        try {
            // Postman ad soyad ile arama
            return customerRepository.existsByNameAndSurname(name, surname);
        } catch (DataAccessException e) {

            throw new RuntimeException("Veri tabanı bağlantısı hatası. Lütfen tekrar deneyin.", e);

        }
        catch (Exception e) {

            throw new RuntimeException("Veri tabanı hatası oluştu.", e);
        }
    }

    @Override
    public List<Customer> searchCustomer(String name, String surname) {
        try {
            if (name != null && !name.trim().isEmpty() && surname != null && !surname.trim().isEmpty()) {
                return customerRepository.findByNameAndSurname(name, surname);
            }

            if (name != null && !name.trim().isEmpty()) {
                return customerRepository.findByName(name);
            }

            if (surname != null && !surname.trim().isEmpty()) {
                return customerRepository.findBySurname(surname);
            }

            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new RuntimeException("Veri tabanı bağlantısı hatası. Lütfen tekrar deneyin.", e);

        }
        catch (Exception e) {

            throw new RuntimeException("Müşteri arama işlemi sırasında bir hata oluştu.", e);
        }
    }
}