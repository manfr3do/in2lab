package com.haw.srs.customerservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/courses")
public class CourseFacade {
    
    private final CourseRepository courseRepository;

    @Autowired
    public CourseFacade(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Course getCourse(@PathVariable("id") Long courseId) throws CourseNotFoundException {
        return courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Course createCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable("id") Long id, @RequestBody Course updatedCourse) {
        if (!updatedCourse.getId().equals(id)) {
           throw new IllegalArgumentException("ID in path and body do not match");
        }

        Course existingCourse = courseRepository.findById(id)
        .orElseThrow(() -> new CourseNotFoundException(id));
        existingCourse.setName(updatedCourse.getName());

        return courseRepository.save(updatedCourse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCourse(@PathVariable("id") Long id) throws CourseNotFoundException {
    Course course = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));

    // Alle Beziehungen zu Kunden entfernen (bidirektional)
    for (Customer customer : new ArrayList<>(course.getCustomers())) {
        customer.getCourses().remove(course);
    }
    course.getCustomers().clear();

    courseRepository.delete(course);
    }

    /* Sorgt dafür, dass wir einen 404 bekommen wenn die Exception geworfen wird */
    @ExceptionHandler(CourseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCourseNotFound(CourseNotFoundException ex) {
        return ex.getMessage();
    }
}
