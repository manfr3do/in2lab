package com.haw.srs.customerservice;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;



@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Gender gender;

    private String email;

    private PhoneNumber phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    // @JsonManagedReference("customer-course")
    // @JsonIgnore
    @JoinTable(
        name = "customer_course",
        joinColumns = @JoinColumn(name = "customer_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @Setter(AccessLevel.NONE)
    private List<Course> courses = new ArrayList<>();

    public Customer(String firstName, String lastName, Gender gender, String email, PhoneNumber phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Customer(String firstName, String lastName, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = null;
        this.phoneNumber = null;
    }

    public void addCourse(Course course) {
        this.courses.add(course);
        course.erhoeheTeilnehmer();
        course.getCustomers().add(this);
    }

    public void removeCourse(Course course) {
        if (courses.contains(course)) {
            courses.remove(course);
            course.getCustomers().remove(this);
            course.verringereTeilnehmer();
            
        } else {
            throw new IllegalArgumentException("Kein Kurs mit den Namen gefunden.");
        }
    }
}
