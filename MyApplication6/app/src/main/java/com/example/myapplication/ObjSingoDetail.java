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

public class ObjSingoDetail extends Activity {
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
        setContentView(R.layout.activity_singo_detail);

        String teamName = getIntent().getExtras().getString("teamName");
        String id = getIntent().getExtras().getString("id");
        String objective = getIntent().getExtras().getString("obj");
        position = getIntent().getExtras().getInt("position");


        final TextView tvsd_name = findViewById(R.id.tvosd_name);
        final TextView tvsd_id = findViewById(R.id.tvosd_id);
        final TextView tvsd_detail = findViewById(R.id.tvosd_detail);
        final TextView tvsd_obj = findViewById(R.id.tvosd_objs);
        Button btn_singo_approve = findViewById(R.id.obtn_singo_approval);
        Button btn_deny = findViewById(R.id.obtn_deny);
        final ImageView iv_image = findViewById(R.id.ivosd_image);

        Call<ObjSingoList> call_getSingoDetail = service.getObjSingoDetail(teamName, objective, id);
        call_getSingoDetail.enqueue(new Callback<ObjSingoList>() {
            @Override
            public void onResponse(Call<ObjSingoList> call, Response<ObjSingoList> response) {
                if (response.isSuccessful()) {
                    ObjSingoList dummy = response.body();
                    tvsd_name.setText(dummy.name);
                    tvsd_id.setText(dummy.id);
                    tvsd_obj.setText(dummy.objective);
                    tvsd_detail.setText(dummy.reportmsg);
                    if (dummy.img != null) {
                        byte[] b = new byte[dummy.img.length];
                        for (int i = 0; i < dummy.img.length; i++) {
                            b[i] = (byte) dummy.img[i];
                        }
                        iv_image.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
                    }
                    Log.e("getObjSingoDetail", "response success");
                } else {
                    Log.e("getObjSingoDetail", "response fail");
                }
            }

            @Override
            public void onFailure(Call<ObjSingoList> call, Throwable t) {
                Log.e("getObjSingoDetail", "onFailure");
            }
        });
        btn_singo_approve.setOnClickListener(v -> {
            Call<Dummy> call_postSingo = service.postObjSingo("accept", tvsd_name.getText().toString(), tvsd_id.getText().toString(), tvsd_obj.getText().toString());
            call_postSingo.enqueue(dummies);
        });
        btn_deny.setOnClickListener(v -> {
            Call<Dummy> call_postSingo = service.postObjSingo("deny", tvsd_name.getText().toString(), tvsd_id.getText().toString(), tvsd_obj.getText().toString());
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
