package com.haw.srs.customerservice;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
class CourseNotFoundException extends RuntimeException {

    private final Long courseNumber;

    CourseNotFoundException(Long courseNumber) {
        super(String.format("Could not find course with number %d.", courseNumber));

        this.courseNumber = courseNumber;
    }
}