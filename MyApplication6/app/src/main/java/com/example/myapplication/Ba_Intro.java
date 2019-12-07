package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.myapplication.RetrofitInterface.GetIP;
import com.example.myapplication.RetrofitInterface.GetService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Ba_Intro extends Activity {
    private static final String BASE = GetIP.BASE;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    GetService service = retrofit.create(GetService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ba_intro);
        String teamName = getIntent().getExtras().getString("teamName");
        int pos=getIntent().getExtras().getInt("position");

        TextView batv_name = findViewById(R.id.batv_name);
        TextView batv_cate1 = findViewById(R.id.batv_cate1);
        TextView batv_cate2 = findViewById(R.id.batv_cate2);
        TextView batv_start = findViewById(R.id.batv_start);
        TextView batv_end = findViewById(R.id.batv_end);
        TextView batv_count = findViewById(R.id.batv_count);
        TextView batv_mentor = findViewById(R.id.batv_mentor);
        TextView batv_obj = findViewById(R.id.batv_obj);
        TextView batv_objs = findViewById(R.id.batv_objs);
        TextView batv_admit = findViewById(R.id.batv_admit);

        Call<TeamList> call_teamList = service.getBaIntro(teamName);
        call_teamList.enqueue(new Callback<TeamList>() {
            @Override
            public void onResponse(Call<TeamList> call, Response<TeamList> response) {
                if (response.isSuccessful()) {
                    TeamList dummy = response.body();
                    String[] str_objs = dummy.getObjectives().split(";");
                    String objectives = "";
                    for (int i = 0; i < str_objs.length; i++) {
                        objectives += "* " + str_objs[i] + "\n";
                    }

                    batv_name.setText(dummy.getName());
                    batv_cate1.setText(dummy.getCategory1());
                    batv_cate2.setText(dummy.getCategory2());
                    batv_start.setText(dummy.getStart());
                    batv_end.setText(dummy.getEnd());
                    batv_count.setText(dummy.getCount() + "");
                    batv_mentor.setText(dummy.getMentor());
                    batv_obj.setText(dummy.getObjective());
                    batv_objs.setText(objectives);
                    batv_admit.setText(dummy.getAdmit());
                    Log.e("Ba_Intro", "response 성공");
                } else {
                    Log.e("Ba_Intro", "response 실패");
                }
            }

            @Override
            public void onFailure(Call<TeamList> call, Throwable t) {
                Log.e("Ba_Intro", "onFailureee");
            }
        });
        Button btn_approval = findViewById(R.id.button_before_approval);
        btn_approval.setOnClickListener((v) -> {
            Call<Dummy> call_bapproval = service.postApproval(teamName);
            Log.e("postApproval", teamName);
            call_bapproval.enqueue(new Callback<Dummy>() {
                @Override
                public void onResponse(Call<Dummy> call, Response<Dummy> response) {
                    if (response.isSuccessful()) {
                        Dummy dummy = response.body();
                        if (dummy.check) {
                            Log.e("postApproval", "승인 됨");
                        } else {
                            Log.e("postApproval", "승인 안됨");
                        }
                    } else {
                        Log.e("postApproval", "response 실패");
                    }
                }

                @Override
                public void onFailure(Call<Dummy> call, Throwable t) {
                    Log.e("postApproval", "OnFailure");
                }
            });
            Intent intent = new Intent();
            intent.putExtra("pos",pos);
            setResult(RESULT_OK, intent);
            finish();
        });

    }
}
