package com.example.myapplication;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface GetService {
    @GET("/test/{position},{position1},{position2},{position3},{position4}")
    Call<Dummy> listDummies(@Path("position") String position,@Path("position1") String position1,@Path("position2") String position2, @Path("position3") String position3,@Path("position4") String position4);

    @GET("/id/{position}")
    Call<LogInfo> listLogInfo(@Path("position") String position);

    @FormUrlEncoded
    @POST("/task")
    Call<Dummy> dummycheck(@Field("task") String string);

    @Multipart
    @POST("/images/upload")
    Call<Dummy> uploadImage(@Part MultipartBody.Part image, @Part("id") RequestBody id);

    @GET("/myinfo/{position}")
    Call<MyInfo> callMyInfo(@Path("position") String id);
}