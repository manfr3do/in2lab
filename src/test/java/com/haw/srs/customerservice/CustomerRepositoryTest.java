package com.haw.srs.customerservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        this.customerRepository.deleteAll();

        Customer customer = this.customerRepository.save(new Customer("Stefan", "Sarstedt", Gender.MALE,
                "stefan.sarstedt@haw-hamburg.de",
                new PhoneNumber("+49", "040", "428758434")));
    }

    @Test
    void findCustomerByLastNameSuccess() {
        assertThat(customerRepository.findByLastName("Sarstedt").isPresent()).isTrue();
    }

    @Test
    void findCustomerByLastNameFail() {
        assertThat(customerRepository.findByLastName("notExisting").isPresent()).isFalse();
    }
}
