package com.babasdevel.cimeries;

import com.babasdevel.common.Hibernate;
import com.babasdevel.controllers.ControllerCourse;
import com.babasdevel.controllers.ControllerTeacher;
import com.babasdevel.models.*;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientCourse extends Cimeries{
    public List<Course> data;
    public Long count;
    public int getCourses(Teacher user){
        try {
            request = new Request.Builder()
                    .url(host.concat("mercedes/course/"))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", user.cookie.get(0), user.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(user.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", user.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    data = gson.fromJson(response.body().string(), new TypeToken<List<Course>>(){}.getType());
                    for (Course course : data) {
                        course.getSkills().forEach(skill -> skill.setCourse(course));
                        course.getPartials().forEach(partial -> partial.setCourse(course));
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
                    .url(host.concat("mercedes/course/count/"))
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
}
