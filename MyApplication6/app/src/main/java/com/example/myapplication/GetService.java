package com.example.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GetService {
    @GET("/test/{position},{position1}")
    Call<Dummy> listDummies(@Path("position") String position,@Path("position1") String position1);
}
