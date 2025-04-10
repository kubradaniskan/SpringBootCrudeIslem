package com.kubradaniskan.SpringBootCrudeIslem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="customer")
@Getter
@Setter
@ToString
public class Customer {

@Id
@GeneratedValue(strategy = GenerationType.AUTO)

@Column(name="id")
private Long id;

@Column(name="name")
private String name;

@Column(name="surname")
private String surname;

}
