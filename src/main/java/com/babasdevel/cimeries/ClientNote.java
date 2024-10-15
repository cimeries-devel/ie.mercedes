package com.babasdevel.cimeries;

import com.babasdevel.controllers.*;
import com.babasdevel.models.*;
import com.google.gson.reflect.TypeToken;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import javax.swing.*;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class ClientNote extends Cimeries{
    public List<Note> data;
    public Note note;
    public Long count;
    public boolean isNext = true;
    private String urlPagination;
    public ClientNote(){
        urlPagination = host.concat("mercedes/notes/all/");
    }
    public int getAllNotes(Teacher teacher){
        try {
            request = new Request.Builder()
                    .url(urlPagination)
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    Pagination pagination = gson.fromJson(response.body().string(), Pagination.class);
                    ControllerTeacher controllerTeacher = new ControllerTeacher();
                    ControllerGrade controllerGrade = new ControllerGrade();
                    ControllerStudent controllerStudent = new ControllerStudent();
                    ControllerPartial controllerPartial = new ControllerPartial();
                    ControllerCourse controllerCourse = new ControllerCourse();
                    ControllerSkill controllerSkill = new ControllerSkill();

                    data = new ArrayList<>();
                    for (NoteModel model : pagination.results) {
                        Teacher t = controllerTeacher.get(model.teacher);
                        Grade grade = controllerGrade.get(model.grade);
                        Student student = controllerStudent.get(model.student);
                        Partial partial = controllerPartial.get(model.partial);
                        Course course = controllerCourse.get(model.course);
                        Skill skill = controllerSkill.get(model.skill);
                        note = new Note();
                        note.setId(model.id);
                        note.setNote(model.note);
                        note.setObservation(model.observation);
                        note.setCreated(model.created);
                        note.setUpdated(model.updated);
                        note.setTeacher(t);
                        note.setGrade(grade);
                        note.setStudent(student);
                        note.setPartial(partial);
                        note.setCourse(course);
                        note.setSkill(skill);
                        data.add(note);
                    }
                    if (pagination.next == null){
                        isNext = false;
                    } else {
                        urlPagination = host.concat(pagination.next.substring(pagination.next.indexOf("v1")+3));
                    }
                    break;
                case Codes.CODE_BAD_REQUEST: //error get permission
                    break;
                default:
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
    public int getAllNotes(Teacher teacher, Grade grade, Course course){
        try {
            request = new Request.Builder()
                    .url(host.concat(String.format("mercedes/notes/%d/%d/", grade.getId(), course.getId())))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    List<NoteModel> models = gson.fromJson(response.body().string(), new TypeToken<List<NoteModel>>(){}.getType());
                    ControllerTeacher controllerTeacher = new ControllerTeacher();
                    ControllerGrade controllerGrade = new ControllerGrade();
                    ControllerStudent controllerStudent = new ControllerStudent();
                    ControllerPartial controllerPartial = new ControllerPartial();
                    ControllerCourse controllerCourse = new ControllerCourse();
                    ControllerSkill controllerSkill = new ControllerSkill();
                    ControllerLevel controllerLevel = new ControllerLevel();
                    data = new ArrayList<>();
                    for (NoteModel model : models) {
                        if (controllerTeacher.get(model.teacher) == null) controllerTeacher.getOnlyMe(teacher, model.teacher);
                        Teacher t = controllerTeacher.get(model.teacher);
                        grade = controllerGrade.get(model.grade);
                        Student student = controllerStudent.get(model.student);
                        course = controllerCourse.get(model.course);
                        Skill skill = controllerSkill.get(model.skill);
                        Level level = controllerLevel.get(model.level);
                        note = new Note();
                        note.setId(model.id);
                        note.setNote(model.note);
                        note.setObservation(model.observation);
                        note.setCreated(model.created);
                        note.setUpdated(model.updated);
                        note.setTeacher(t);
                        note.setGrade(grade);
                        note.setStudent(student);
                        note.setPartial(controllerPartial.get(model.partial));
                        note.setCourse(course);
                        note.setSkill(skill);
                        note.setLevel(level);
                        data.add(note);
                    }
                    break;
                case Codes.CODE_BAD_REQUEST: //error get permission
                    break;
                default:
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
    public int getAllNotes(Teacher teacher, Grade grade){
        try {
            request = new Request.Builder()
                    .url(host.concat(String.format("mercedes/notes/%d/", grade.getId())))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    List<NoteModel> models = gson.fromJson(response.body().string(), new TypeToken<List<NoteModel>>(){}.getType());
                    ControllerTeacher controllerTeacher = new ControllerTeacher();
                    ControllerGrade controllerGrade = new ControllerGrade();
                    ControllerStudent controllerStudent = new ControllerStudent();
                    ControllerPartial controllerPartial = new ControllerPartial();
                    ControllerCourse controllerCourse = new ControllerCourse();
                    ControllerSkill controllerSkill = new ControllerSkill();
                    ControllerLevel controllerLevel = new ControllerLevel();
                    data = new ArrayList<>();
                    for (NoteModel model : models) {
                        if (controllerTeacher.get(model.teacher) == null) controllerTeacher.getOnlyMe(teacher, model.teacher);
                        Teacher t = controllerTeacher.get(model.teacher);
                        grade = controllerGrade.get(model.grade);
                        Student student = controllerStudent.get(model.student);
                        Course course = controllerCourse.get(model.course);
                        Skill skill = controllerSkill.get(model.skill);
                        Level level = controllerLevel.get(model.level);
                        note = new Note();
                        note.setId(model.id);
                        note.setNote(model.note);
                        note.setObservation(model.observation);
                        note.setCreated(model.created);
                        note.setUpdated(model.updated);
                        note.setTeacher(t);
                        note.setGrade(grade);
                        note.setStudent(student);
                        note.setPartial(controllerPartial.get(model.partial));
                        note.setCourse(course);
                        note.setSkill(skill);
                        note.setLevel(level);
                        data.add(note);
                    }
                    break;
                case Codes.CODE_BAD_REQUEST: //error get permission
                    break;
                default:
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
    public int post(Teacher teacher, List<NoteModel> notes){
        try {
            MediaType mediaType = MediaType.parse("application/json");
            requestBody = RequestBody.create(gson.toJson(notes), mediaType);
            request = new Request.Builder()
                    .url(host.concat("mercedes/notes/"))
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_CREATED:
                    assert response.body() != null;
                    List<NoteModel> models = gson.fromJson(response.body().string(), new TypeToken<List<NoteModel>>(){}.getType());
                    ControllerTeacher controllerTeacher = new ControllerTeacher();
                    ControllerGrade controllerGrade = new ControllerGrade();
                    ControllerStudent controllerStudent = new ControllerStudent();
                    ControllerPartial controllerPartial = new ControllerPartial();
                    ControllerCourse controllerCourse = new ControllerCourse();
                    ControllerSkill controllerSkill = new ControllerSkill();
                    data = new ArrayList<>();
                    for (NoteModel model : models) {
                        Teacher t = controllerTeacher.get(model.teacher);
                        Grade grade = controllerGrade.get(model.grade);
                        Student student = controllerStudent.get(model.student);
                        Course course = controllerCourse.get(model.course);
                        Partial partial = controllerPartial.get(model.partial);
                        Skill skill = controllerSkill.get(model.skill);
                        note = new Note();
                        note.setId(model.id);
                        note.setNote(model.note);
                        note.setObservation(model.observation);
                        note.setCreated(model.created);
                        note.setUpdated(model.updated);
                        note.setTeacher(t);
                        note.setGrade(grade);
                        note.setStudent(student);
                        note.setPartial(partial);
                        note.setCourse(course);
                        note.setSkill(skill);
                        note.setLevel(teacher.level);
                        data.add(note);
                    }
                    break;
                case Codes.CODE_BAD_REQUEST: //error get permission
                    break;
                default:
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
    public int getCount(Teacher auth){
        try {
            request = new Request.Builder()
                    .url(host.concat("mercedes/notes/count/"))
                    .get()
                    .addHeader("Content-Type", CONTENT_JSON)
                    .addHeader("Cookie", String.format("%s; %s", auth.cookie.get(0), auth.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(auth.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", auth.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    CountModel model = gson.fromJson(response.body().string(), CountModel.class);
                    count = model.quantity;
                    break;
                case Codes.CODE_BAD_REQUEST: //error get permission
                    break;
                default:
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
    static class Pagination {
        public Long count;
        public String next;
        public String previous;
        public List<NoteModel> results;

        @Override
        public String toString() {
            return "Pagination{" +
                    "count=" + count +
                    ", next='" + next + '\'' +
                    ", previous='" + previous + '\'' +
                    '}';
        }
    }
}
