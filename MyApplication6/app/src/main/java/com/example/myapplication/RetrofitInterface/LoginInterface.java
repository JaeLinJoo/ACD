package com.example.myapplication.RetrofitInterface;

import com.example.myapplication.Dummy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LoginInterface {
    @GET("/login/{position},{position1}")
    Call<Dummy> listDummies(@Path("position") String position, @Path("position1") String position1);
}
