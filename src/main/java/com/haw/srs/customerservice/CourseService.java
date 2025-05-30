package com.haw.srs.customerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MailGateway mailGateway;

    /**
     * Schreibt einen Teilnehmenden in den Kurs ein.
     * 
     * @param lastName der Nachname des Teilnehmenden.
     * @param course der Kurs in dem eingeschrieben werden soll.
     * @throws CustomerNotFoundException
     */
    @Transactional
    public void enrollInCourse(String lastName, Course course) throws CustomerNotFoundException {
        Customer customer = customerRepository
                .findByLastName(lastName)
                .orElseThrow(() -> new CustomerNotFoundException(lastName));

        customer.addCourse(course);
        customerRepository.save(customer);
    }

    @Transactional
    public void transferCourses(String fromCustomerLastName, String toCustomerLastName) throws CustomerNotFoundException {
        Customer from = customerRepository
                .findByLastName(fromCustomerLastName)
                .orElseThrow(() -> new CustomerNotFoundException(fromCustomerLastName));
        Customer to = customerRepository
                .findByLastName(toCustomerLastName)
                .orElseThrow(() -> new CustomerNotFoundException(toCustomerLastName));

        to.getCourses().addAll(from.getCourses());
        from.getCourses().clear();

        customerRepository.save(from);
        customerRepository.save(to);
    }

    /**
     * Cancels a course membership. An Email is sent to all possible participants on the waiting list for this course.
     * If customer is not member of the provided course, the operation is ignored.
     *
     * @throws IllegalArgumentException if customerNumber==null or courseNumber==null
     */
    @Transactional
    public void cancelMembership(Long customerNumber, Long courseNumber) throws CustomerNotFoundException, CourseNotFoundException, MembershipMailNotSent {

        // some implementation goes here
        // find customer, find course, look for membership, remove membership, etc.
        Customer customer = customerRepository
                .findById(customerNumber)
                .orElseThrow(() -> new CustomerNotFoundException(customerNumber));

        Course course = courseRepository
                .findById(courseNumber)
                .orElseThrow(() -> new CourseNotFoundException(courseNumber));
        
        course.verringereTeilnehmer();
        customer.removeCourse(course);
        customerRepository.save(customer);
        String customerMail = "customer@domain.com";

        boolean mailWasSent = mailGateway.sendMail(customerMail, "Oh, we're sorry that you canceled your membership!", "Some text to make her/him come back again...");
        if (!mailWasSent) {
            // do some error handling here (including e.g. transaction rollback, etc.)
            // ...
            throw new MembershipMailNotSent(customerMail);
        }
    }
}
