package com.igaraleon.app.ws.entity;

import javax.persistence.*;

@Entity
public class CourseRating {
    @EmbeddedId
    private CourseRatingKey id;

    @ManyToOne
    @MapsId("student_id")
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @MapsId("course_id")
    @JoinColumn(name = "course_id")
    private Course course;

    private int rating;

    public CourseRatingKey getId() {
        return id;
    }

    public void setId(CourseRatingKey id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
