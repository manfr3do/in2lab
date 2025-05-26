package com.haw.srs.customerservice;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Integer anzahlTeilnehmer = 0;

    @ManyToMany(mappedBy = "courses")
    // @JsonBackReference("customer-course")
    @JsonIgnore // Break cycle - set only on one relationship
    @Setter(AccessLevel.NONE)
    private List<Customer> customers = new ArrayList<>();

    public Course(String name) {
        this.name = name;
        // anzahlTeilnehmer = 0;
    }

    public Integer getAnzahlTeilnehmer() {
        return anzahlTeilnehmer;
    }

    public void erhoeheTeilnehmer() {
        anzahlTeilnehmer++;
    }

    public void verringereTeilnehmer() {
        anzahlTeilnehmer--;
    }
}
