package com.haw.srs.customerservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByLastName(String lastName);

    Optional<Customer> findCustomerByFirstName(String firstName);

    // Optional<Customer> findByCustomerNumber(Long number); -- Überflüssig, da findById schon in JPA existiert

}
