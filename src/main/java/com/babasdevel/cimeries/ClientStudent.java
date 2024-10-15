package com.babasdevel.cimeries;

import com.babasdevel.models.CountModel;
import com.babasdevel.models.Grade;
import com.babasdevel.models.Student;
import com.babasdevel.models.Teacher;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

public class ClientStudent extends Cimeries{
    public List<Student> data;
    public Long count;
    public int getStudents(Teacher auth, Grade grade){
        try {
            request = new Request.Builder()
                    .url(host.concat(String.format("mercedes/student/%d/", grade.getId())))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", auth.cookie.get(0), auth.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(auth.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", auth.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    data = gson.fromJson(response.body().string(), new TypeToken<List<Student>>(){}.getType());
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
                    .url(host.concat("mercedes/student/count/"))
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
}
