package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.RetrofitInterface.GetIP;
import com.example.myapplication.RetrofitInterface.GetService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AttendSingoDetail extends Activity {
    private static final String BASE = GetIP.BASE;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    GetService service = retrofit.create(GetService.class);
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_attend_singo_detail);

        String teamName = getIntent().getExtras().getString("teamName");
        String date = getIntent().getExtras().getString("date");
        String user = getIntent().getExtras().getString("user");
        position = getIntent().getExtras().getInt("position");

        final TextView tvash_name = findViewById(R.id.tvash_name);
        final TextView tvash_date = findViewById(R.id.tvash_date);
        final TextView tvash_detail = findViewById(R.id.tvash_detail);
        final TextView tvash_user = findViewById(R.id.tvash_user);
        Button btn_singo_approve = findViewById(R.id.abtn_singo_approval);
        Button btn_deny = findViewById(R.id.abtn_deny);
        final ImageView iv_image = findViewById(R.id.ivash_image);

        Call<AttendSingoList> call_getAttendSingoDetail = service.getAttendSingoDetail(teamName, date, user);
        call_getAttendSingoDetail.enqueue(new Callback<AttendSingoList>() {
            @Override
            public void onResponse(Call<AttendSingoList> call, Response<AttendSingoList> response) {
                if (response.isSuccessful()) {
                    AttendSingoList dummy = response.body();
                    tvash_name.setText(dummy.name);
                    tvash_date.setText(dummy.date);
                    tvash_user.setText(dummy.user);
                    tvash_detail.setText(dummy.reportmsg);
                    if (dummy.img != null) {
                        byte[] b = new byte[dummy.img.length];
                        for (int i = 0; i < dummy.img.length; i++) {
                            b[i] = (byte) dummy.img[i];
                        }
                        iv_image.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
                    }
                    Log.e("getAttendSingoDetail", "response success");
                } else {
                    Log.e("getAttendSingoDetail", "response fail");
                }
            }

            @Override
            public void onFailure(Call<AttendSingoList> call, Throwable t) {
                Log.e("getAttendSingoDetail", "onFailure");
            }
        });
        btn_singo_approve.setOnClickListener(v -> {
            Call<Dummy> call_postSingo = service.postAttendSingo("accept", tvash_name.getText().toString(), tvash_date.getText().toString());
            call_postSingo.enqueue(dummies);
        });
        btn_deny.setOnClickListener(v -> {
            Call<Dummy> call_postSingo = service.postAttendSingo("deny", tvash_name.getText().toString(), tvash_date.getText().toString());
            call_postSingo.enqueue(dummies);
        });
    }


    Callback dummies = new Callback<Dummy>() {
        @Override
        public void onResponse(Call<Dummy> call, Response<Dummy> response) {
            if (response.isSuccessful()) {
                Dummy dummy = response.body();
                if (dummy.check)
                    Log.e("AttendSingoPost", "승인");
                else
                    Log.e("AttendSingoPost", "거절");

                Intent intent = new Intent();
                intent.putExtra("pos",position);
                setResult(RESULT_OK, intent);
                finish();
            } else
                Log.e("AttendSingoPost", "response fail");
        }

        @Override
        public void onFailure(Call<Dummy> call, Throwable t) {
            Log.e("AttendSingoPost", "onFailure");
        }
    };

}
