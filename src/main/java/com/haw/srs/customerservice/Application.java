package com.haw.srs.customerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@Component
class PopulateTestDataRunner implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final CourseRepository courseRepository;
    private final CourseService courseService;

    @Autowired
    public PopulateTestDataRunner(CustomerRepository customerRepository, CourseRepository courseRepository, CourseService courseService) {
        this.customerRepository = customerRepository;
        this.courseRepository = courseRepository;
        this.courseService = courseService;
    }

    @Override
    public void run(String... args) {
        // Arrays.asList(
        //                 "Miller,Doe,Smith".split(","))
        //         .forEach(
        //                 name -> customerRepository.save(new Customer("Jane", name, Gender.FEMALE, name + "@dummy.org", null))
        //         );
        Customer c1 = new Customer("John", "D", Gender.MALE);
        Customer c2 = new Customer("Jane", "Doe", Gender.FEMALE);
        Course course1 = new Course("SEA1");
        Course course2 = new Course("SEA2");
        customerRepository.save(c1);
        customerRepository.save(c2);
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseService.enrollInCourse(c1.getLastName(), course1);
        courseService.enrollInCourse(c2.getLastName(), course1);
        // c1.addCourse(course1);
        // c2.addCourse(course1);


        // courseService.enrollInCourse(c2.getLastName(), new Course("SEA2"));
    }
}
