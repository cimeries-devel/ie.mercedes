package com.babasdevel.models;

import com.babasdevel.common.Hibernate;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "tbl_student")
public class Student extends Hibernate {
    @Id
    private Long id;
    private String type_document;
    private String number_document;
    private String code_student;
    private String first_name;
    private String last_name_mother;
    private String last_name_father;
    private Boolean gender;
    private Boolean status;
    @Temporal(TemporalType.DATE)
    private Date birth_date;
    @Transient
    public String nl_1 = "";
    @Transient
    public String nl_2 = "";
    @Transient
    public String nl_3 = "";
    @Transient
    public String nl_4 = "";
    @Transient
    public String nl_5 = "";
    @Transient
    public String cd_1 = "";
    @Transient
    public String cd_2 = "";
    @Transient
    public String cd_3 = "";
    @Transient
    public String cd_4 = "";
    @Transient
    public String cd_5 = "";

    public Student(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeDocument() {
        return type_document;
    }

    public void setTypeDocument(String typeDocument) {
        this.type_document = typeDocument;
    }

    public String getNumberDocument() {
        return number_document;
    }

    public void setNumberDocument(String numberDocument) {
        this.number_document = numberDocument;
    }

    public String getCodeStudent() {
        return code_student;
    }

    public void setCodeStudent(String codeStudent) {
        this.code_student = codeStudent;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String firstName) {
        this.first_name = firstName;
    }

    public String getLastNameMother() {
        return last_name_mother;
    }

    public void setLastNameMother(String lastNameMother) {
        this.last_name_mother = lastNameMother;
    }

    public String getLastNameFather() {
        return last_name_father;
    }

    public void setLastNameFather(String lastNameFather) {
        this.last_name_father = lastNameFather;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getBirthDate() {
        return birth_date;
    }

    public void setBirthDate(Date birthDate) {
        this.birth_date = birth_date;
    }
    public String getFullName(){
        return String.format("%s %s, %s", getLastNameFather(), getLastNameMother(), getFirstName());
    }
}
