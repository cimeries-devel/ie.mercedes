package com.babasdevel.models;

import com.babasdevel.common.Hibernate;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity(name = "tbl_teacher_course")
public class TeacherCourse extends Hibernate {
    @Id
    private Long id;
    private Boolean status;
    @ManyToOne
    @JoinColumn(name = "fk_teacher")
    private Teacher teacher;
    @ManyToOne
    @JoinColumn(name = "fk_course")
    private Course course;
    public TeacherCourse(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
