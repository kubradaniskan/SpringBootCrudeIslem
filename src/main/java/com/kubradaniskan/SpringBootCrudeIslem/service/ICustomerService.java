package com.kubradaniskan.SpringBootCrudeIslem.service;

import com.kubradaniskan.SpringBootCrudeIslem.entity.Customer;

import java.util.List;

public interface ICustomerService {

    Customer addCustomer(Customer customer);

    List<Customer> findAllCustomer();

    Customer getCustomerById(Long customerId);

    void deleteCustomerById(Long customerId);

    void deleteAllCustomer();

    // Name ve surname ile müşteri var mı kontrolü
    boolean existsByNameAndSurname(String name, String surname);

    List<Customer> searchCustomer(String name, String surname);


    List<Customer> searchCustomerLetter(String name);
}