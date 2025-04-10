package com.kubradaniskan.SpringBootCrudeIslem.service.impl;

import com.kubradaniskan.SpringBootCrudeIslem.entity.Customer;
import com.kubradaniskan.SpringBootCrudeIslem.repository.CustomerRepository;
import com.kubradaniskan.SpringBootCrudeIslem.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer addCustomer(Customer customer) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Müşteri adı boş olamaz.");
        }

        if (customer.getSurname() == null || customer.getSurname().trim().isEmpty()) {
            throw new IllegalArgumentException("Müşteri soyadı boş olamaz.");
        }

        // Aynı name ve surname ile bir müşteri olup olmadığını kontrol et
        if (customerRepository.existsByNameAndSurname(customer.getName(), customer.getSurname())) {
            throw new IllegalArgumentException("Bu isim ve soyadla bir müşteri zaten mevcut.");
        }

        // Müşteriyi kaydet
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> findAllCustomer() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    @Override
    public void deleteCustomerById(Long id) {
        try {
            // Müşteri var mı diye kontrol et
            Customer customer = customerRepository.findById(id).orElse(null);

            if (customer == null) {
                throw new IllegalArgumentException("Müşteri bulunamadı.");
            }

            // Müşteri var, silme işlemi yapılacak
            customerRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Müşteri silinirken bir hata oluştu.");
        }
    }

    @Override
    public boolean existsByNameAndSurname(String name, String surname) {
        // Repository'de ilgili isim ve soyadı arayın
        return customerRepository.existsByNameAndSurname(name, surname);
    }

    @Override
    public List<Customer> searchCustomer(String name, String surname) {
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
    }
}
