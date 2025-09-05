package com.kubradaniskan.SpringBootCrudeIslem.service.impl;

import com.kubradaniskan.SpringBootCrudeIslem.entity.Customer;
import com.kubradaniskan.SpringBootCrudeIslem.exception.DatabaseConnectionException;
import com.kubradaniskan.SpringBootCrudeIslem.exception.ResourceNotFoundException;
import com.kubradaniskan.SpringBootCrudeIslem.repository.CustomerRepository;
import com.kubradaniskan.SpringBootCrudeIslem.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MessageSource messageSource;

    // MessageSource ile dil desteği sağlanıyor
    private String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();  //kullanıcının dil tercihine göre hata dönmesi
        return messageSource.getMessage(key, args, locale);
    }

    @Override
    public Customer addCustomer(Customer customer) {
        try {
            //müşteri adı boş ise
            if (customer.getName() == null || customer.getName().trim().isEmpty()) {
                throw new IllegalArgumentException(getMessage("error.customer.name.empty"));
            }
            //müşteri soyadı boş ise
            if (customer.getSurname() == null || customer.getSurname().trim().isEmpty()) {
                throw new IllegalArgumentException(getMessage("error.customer.surname.empty"));
            }

            //Müşteri var ise
            if (customerRepository.existsByNameAndSurname(customer.getName(), customer.getSurname())) {
                throw new IllegalArgumentException(getMessage("error.customer.exists"));
            }

            return customerRepository.save(customer);
        } catch (DataAccessException ex) {
            // Veri tabanı bağlantısı hatası
            throw new DatabaseConnectionException(getMessage("error.database.connection"));
        }
    }


    @Override
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        try {
            //güncellenecek kullanıcı yok ise
            Customer existingCustomer = customerRepository.findById(id).orElseThrow(() ->
                    new ResourceNotFoundException(getMessage("error.customer.notfound", id)));

            if (updatedCustomer.getName() != null) {
                existingCustomer.setName(updatedCustomer.getName());
            }
            if (updatedCustomer.getSurname() != null) {
                existingCustomer.setSurname(updatedCustomer.getSurname());
            }

            return customerRepository.save(existingCustomer);
        } catch (DataAccessException ex) {

            throw new DatabaseConnectionException(getMessage("error.database.connection"));
        }
    }

    @Override
    public List<Customer> findAllCustomer() {
        try {
            return customerRepository.findAll();

        }
        catch (DataAccessException ex) {

            throw new DatabaseConnectionException(getMessage("error.database.connection"));
        }
    }

    @Override
    public Customer getCustomerById(Long customerId) {
        try {
            return customerRepository.findById(customerId).orElseThrow(() ->
                    new ResourceNotFoundException(getMessage("error.customer.notfound", customerId)));
        }
        catch (DataAccessException ex)
        {

            throw new DatabaseConnectionException(getMessage("error.database.connection"));
        }
    }

    @Override
    public void deleteCustomerById(Long id) {
        try
        {
            Customer customer = customerRepository.findById(id).orElseThrow(() ->
                    new ResourceNotFoundException(getMessage("error.customer.notfound.delete", id)));
            customerRepository.delete(customer);
        }
        catch (DataAccessException ex) {

            throw new DatabaseConnectionException(getMessage("error.database.connection"));
        }
    }

    @Override
    public void deleteAllCustomer() {
        try
        {
            customerRepository.deleteAll();
        }
        catch (DataAccessException ex) {

            throw new DatabaseConnectionException(getMessage("error.database.connection"));
        }
    }

    @Override
    public boolean existsByNameAndSurname(String name, String surname) {
        try {

            return customerRepository.existsByNameAndSurname(name, surname);
        }
        catch (DataAccessException ex) {

            throw new DatabaseConnectionException(getMessage("error.database.connection"));
        }
    }

    @Override
    public List<Customer> searchCustomer(String name, String surname) {
        try {
            return customerRepository.findByNameAndSurname(name, surname);
        }
        catch (DataAccessException ex) {

            throw new DatabaseConnectionException(getMessage("error.database.connection"));
        }
    }

    @Override
    public List<Customer> searchCustomerLetter(String letter) {
        try {
            return customerRepository.findByNameStartingWith(letter);
        }
        catch (DataAccessException ex) {

            throw new DatabaseConnectionException(getMessage("error.database.connection"));
        }
    }
}
