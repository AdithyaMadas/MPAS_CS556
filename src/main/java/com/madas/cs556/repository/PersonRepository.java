package com.madas.cs556.repository;


import com.madas.cs556.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository  extends JpaRepository<Person, Integer> {

}
