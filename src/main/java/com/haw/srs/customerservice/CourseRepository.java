package com.haw.srs.customerservice;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Optional<Course> findByCourseNumber(Long number);
}
