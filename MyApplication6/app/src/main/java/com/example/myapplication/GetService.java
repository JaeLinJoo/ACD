package com.example.myapplication;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
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

    @Multipart
    @POST("/postteam")
    Call<Dummy> postteam(@Part MultipartBody.Part image, @Part("id") RequestBody id, @Part("teamName") RequestBody tn, @Part("objective") RequestBody ob, @Part("objectives") RequestBody obs, @Part("admit") RequestBody ad, @Part("pay") RequestBody pa, @Part("time") RequestBody ti, @Part("intro") RequestBody it, @Part("start") RequestBody st, @Part("end") RequestBody ed, @Part("mentor") RequestBody mt, @Part("member_count") RequestBody mc, @Part("category1") RequestBody cg1, @Part("category2") RequestBody cg2, @Part("can") RequestBody can);

    @GET("/showTeamList/{position}")
    Call<List<TeamList>> showTeamList(@Path("position") String id);

    @GET("/join/{position}")
    Call<JoinList> showJoinList(@Path("position") String name);

    @FormUrlEncoded
    @POST("/submit")
    Call<DummyMessage> submitTeam(@Field("teamname") String teamname ,@Field("id") String id, @Field("can") int can, @Field("isMentor") boolean isMentor);

    @FormUrlEncoded
    @POST("/getGroupId")
    Call<TeamInfo> getGroupinfo(@Field("targetGroup") String groupId, @Field("targetID") String id);

    @Multipart
    @POST("/admit")
    Call<DummyMessage> getAdmit(@Part MultipartBody.Part image, @Part("id") RequestBody id, @Part("teamname") RequestBody teamname, @Part("objective") RequestBody objective);

    @FormUrlEncoded
    @POST("/showAdmit")
    Call<ObjectiveAdmit> showAdmit(@Field("id") String id, @Field("teamname") String teamname, @Field("objective") String objective);

    @FormUrlEncoded
    @POST("/admitlist")
    Call<List<AdmitList>> admitlist(@Field("teamname") String teamname);

    @FormUrlEncoded
    @POST("/calculateObjective")
    Call<Calculate> calculateObjective(@Field("id") String id, @Field("teamname") String teamname);

    @GET("/getcan/{id}")
    Call<MainPage.Getcan> getcan(@Path("id") String id);

    @GET("/testimg/{id}")
    Call<TestClass> test(@Path("id") String id);
}
