package com.haw.srs.customerservice;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PhoneNumberTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "+49-40-58967572",
            "+49-040-58967572",
            "+49-040-5896"
    })
    void createPhoneNumberSuccess(String phoneNumber) {
        new PhoneNumber(phoneNumber);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+4-040-5896",
            "49-040-5896",
            "+49-0-5896",
            "+49-040-896"
    })
    void createPhoneNumberFail(String phoneNumber) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new PhoneNumber(phoneNumber))
                .withMessageContaining("Invalid phone number");
    }
}