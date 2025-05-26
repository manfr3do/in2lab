package com.haw.srs.customerservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CourseServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CourseRepository courseRepository;

    @MockitoBean
    private MailGateway mailGateway;

    @BeforeEach
    void setup() {
        customerRepository.deleteAll();
    }

    @Test
    void enrollCustomerInCourseSuccess() throws CustomerNotFoundException {
        Customer customer = new Customer("Jane", "Doe", Gender.FEMALE, "jane.doe@mail.com", null);
        customerRepository.save(customer);

        assertThat(customer.getCourses()).size().isEqualTo(0);

        courseService.enrollInCourse(customer.getLastName(), new Course("Software Engineeeeeering 1"));

        assertThat(customerService.findCustomerByLastname(customer.getLastName()).getCourses())
                .size().isEqualTo(1);
       assertThat(customerService.findCustomerByLastname(customer.getLastName()).getCourses().get(0).getAnzahlTeilnehmer()).isEqualTo(1);
    }

    @Test
    void enrollCustomerInCourseFailBecauseOfCustomerNotFound() {
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> courseService.enrollInCourse("notExisting", new Course("Software Engineering 1")))
                .withMessageContaining("Could not find customer with lastname notExisting.");
    }

    @Test
    void transferCoursesSuccess() throws CustomerNotFoundException {
        Customer from = new Customer("John", "Smith", Gender.MALE);
        from.addCourse(new Course("Software Engineering 1"));
        from.addCourse(new Course("Software Engineering 2"));
        customerRepository.save(from);
        Customer to = new Customer("Eva", "Miller", Gender.FEMALE);
        customerRepository.save(to);

        assertThat(from.getCourses()).size().isEqualTo(2);
        assertThat(to.getCourses()).size().isEqualTo(0);

        courseService.transferCourses(from.getLastName(), to.getLastName());

        assertThat(customerService.findCustomerByLastname(from.getLastName()).getCourses())
                .size().isEqualTo(0);
        assertThat(customerService.findCustomerByLastname(to.getLastName()).getCourses())
                .size().isEqualTo(2);
    }

    @Test
    void cancelMembershipSuccess() throws CustomerNotFoundException, CourseNotFoundException, MembershipMailNotSent {
        // set up customer and course here
        // ...
        Customer customer = new Customer("Jane", "Doe", Gender.FEMALE, "jane.doe@mail.com", null);
        customerRepository.save(customer);
        Course course = new Course("Software Engineering 2");
        courseRepository.save(course);
        courseService.enrollInCourse(customer.getLastName(), course);
        
        // configure MailGateway-mock
        when(mailGateway.sendMail(anyString(), anyString(), anyString())).thenReturn(true);
        
        // you must change the parameters here, using 1 just for testing
        courseService.cancelMembership(customer.getId(), course.getId());
        assertThat(!customer.getCourses().contains(course));
    }

    @Test
    void cancelMembershipFailBecauseOfUnableToSendMail() {
        // set up customer and course here
        // ...
        Customer customer = new Customer("Jane", "Doe", Gender.FEMALE, "jane.doe@mail.com", null);
        customerRepository.save(customer);
        Course course = new Course("Software Engineering 2");
        courseRepository.save(course);
        try {
            courseService.enrollInCourse(customer.getLastName(), course);
        } catch (CustomerNotFoundException c) {
            System.out.println(c.getMessage());
        }
        // configure MailGateway-mock
        when(mailGateway.sendMail(anyString(), anyString(), anyString())).thenReturn(false);

        // you must change the parameters here, using 1 just for testing
        assertThatExceptionOfType(MembershipMailNotSent.class)
                .isThrownBy(() -> courseService.cancelMembership(customer.getId(), course.getId()))
                .withMessageContaining("Could not send membership mail to");
    }

    @Test
    void cancelMembershipSuccessBDDStyle() throws CustomerNotFoundException, CourseNotFoundException, MembershipMailNotSent {
        // set up customer and course here
        // ...
        Customer customer = new Customer("Jane", "Doe", Gender.FEMALE, "jane.doe@mail.com", null);
        customerRepository.save(customer);
        Course course = new Course("Software Engineering 2");
        courseRepository.save(course);
        courseService.enrollInCourse(customer.getLastName(), course);
        // configure MailGateway-mock with BDD-style
        given(mailGateway.sendMail(anyString(), anyString(), anyString())).willReturn(true);

        // you must change the parameters here, using 1 just for testing
        courseService.cancelMembership(customer.getId(), course.getId());
    }

    @Test
    @Transactional 
    void enrollCustomerInCourseSuccess_hibernateCacheTest() throws CustomerNotFoundException {
        Customer customer = new Customer("Jane", "Doe", Gender.FEMALE, "jane.doe@mail.com", null);
        customerRepository.save(customer);

        assertThat(customer.getCourses()).size().isEqualTo(0);

        courseService.enrollInCourse(customer.getLastName(), new Course("Software Engineering 1"));

        // works anyway because updated customer object is read from database
        assertThat(customerService.findCustomerByLastname(customer.getLastName()).getCourses())
                .size().isEqualTo(1);

        // the following assert fails because of separate transaction (incl. separate persistent object cache) in method "enrollInCourse"
        // put @Transactional before this method to fix this -> only a single transaction and therefore cache is used in this method
        // @Transactional
        assertThat(customer.getCourses()).size().isEqualTo(1);

    }
}