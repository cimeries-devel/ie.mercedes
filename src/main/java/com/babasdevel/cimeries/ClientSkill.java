package com.babasdevel.cimeries;

import com.babasdevel.controllers.ControllerCourse;
import com.babasdevel.models.*;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class ClientSkill extends Cimeries{
    public List<Skill> data;
    public Skill object;
    public Long count;
    public int getSkills(Teacher teacher){
        try {
            request = new Request.Builder()
                    .url(host.concat("mercedes/skill/"))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    data = gson.fromJson(response.body().string(), new TypeToken<ArrayList<SkillModel>>(){}.getType());
                    ControllerCourse controllerCourse = new ControllerCourse();
                    for (Skill skill : data) {
                        Course course = controllerCourse.get(skill.getCourse().getId());
                        if (course != null) course.save();
                    }
                    break;
                case Codes.CODE_BAD_REQUEST: //error get permission
                    return Codes.CODE_BAD_REQUEST;
                default:
                    return Codes.CODE_NOT_FOUNT;
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
                    .url(host.concat("mercedes/skill/count/"))
                    .get()
                    .addHeader("Content-Type", "application/json")
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
    static class SkillModel {
        public Long id;
        public String name;
        public boolean status;
        public Long course;
        public Integer index;
    }
}
