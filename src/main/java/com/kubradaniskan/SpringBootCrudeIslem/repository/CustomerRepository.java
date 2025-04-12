package com.kubradaniskan.SpringBootCrudeIslem.repository;

import com.kubradaniskan.SpringBootCrudeIslem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // İsim ve soyadla müşteri arama
    List<Customer> findByNameAndSurname(String name, String surname);

    // Sadece isimle müşteri arama
    List<Customer> findByName(String name);

    // Sadece soyadla müşteri arama
    List<Customer> findBySurname(String surname);

    // İsim ve soyad ile müşteri var mı kontrolü
    boolean existsByNameAndSurname(String name, String surname);


    //name ve surname ile ... başlayanı bulma
    //findbyNameStartName gibi bir değer yazıldığında hata verdi springe uygun yazılmalı
    List<Customer> findByNameStartingWith(String name);
    List<Customer> findBySurnameStartingWith(String surname);
}
