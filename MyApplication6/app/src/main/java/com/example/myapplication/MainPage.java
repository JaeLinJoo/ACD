package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainPage extends AppCompatActivity {
    private static final String BASE = "http://192.168.0.14:3002";
    private static final int REQUEST_CODE = 0;

    TextView textView;
    Button ret, tele, test, mypage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        textView = (TextView) findViewById(R.id.textView2);
        ret = (Button) findViewById(R.id.ret);
        tele = (Button) findViewById(R.id.tele);
        test = (Button) findViewById(R.id.test);
        mypage = (Button) findViewById(R.id.mypagebt);

        mypage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
        });

        ret.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        tele.setOnClickListener(new View.OnClickListener() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            String id = SharedPreference.getAttribute(getApplicationContext(),"id");
            @Override
            public void onClick(View v) {
                GetService service = retrofit.create(GetService.class);

                Call<LogInfo> call = service.listLogInfo(id);
                call.enqueue(dummies);
            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            String id = SharedPreference.getAttribute(getApplicationContext(),"id");
            @Override
            public void onClick(View v) {
                GetService service = retrofit.create(GetService.class);

                Call<Dummy> call = service.dummycheck(id);
                call.enqueue(new Callback<Dummy>(){
                    @Override
                    public void onResponse(Call<Dummy> call, Response<Dummy> response) {
                        if (response.isSuccessful()) {
                            Dummy dummy = response.body();
                            if(dummy.isCheck()){
                                textView.setText("성공");
                            }

                        } else
                        {
                            Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Dummy> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    Callback dummies = new Callback<LogInfo>(){
        @Override
        public void onResponse(Call<LogInfo> call, Response<LogInfo> response) {
            if (response.isSuccessful()) {
                LogInfo dummy = response.body();
                textView.setText(dummy.telenumber);
            } else
            {
                Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onFailure(Call<LogInfo> call, Throwable t) {
            Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
        }
    };
}
