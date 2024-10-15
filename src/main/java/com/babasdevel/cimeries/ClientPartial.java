package com.babasdevel.cimeries;

import com.babasdevel.controllers.ControllerCourse;
import com.babasdevel.models.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class ClientPartial extends Cimeries{
    public List<Partial> data;
    public Long count;
    public int getPartial(Teacher teacher){
        try {
            request = new Request.Builder()
                    .url(host.concat("mercedes/partial/"))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    List<PartialModel> models = new Gson().fromJson(response.body().string(), new TypeToken<List<PartialModel>>(){}.getType());

                    ControllerCourse controllerCourse = new ControllerCourse();
                    data = new ArrayList<>();
                    for (PartialModel model : models) {
                        Course course = controllerCourse.get(model.course);
                        Partial partial = new Partial();
                        partial.setId(model.id);
                        partial.setPartial(model.partial);
                        partial.setCode(model.code);
                        partial.setCourse(course);
                        data.add(partial);
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
                    .url(host.concat("mercedes/partial/count/"))
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
                    CountModel model = new Gson().fromJson(response.body().string(), CountModel.class);
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
    static class PartialModel {
        public Long id;
        public Integer partial;
        public String code;
        public Long course;
    }
}
