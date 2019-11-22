package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainPage extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;
    private static final int REQUEST_CODE = 0;

    TextView textView;
    Button ret, tele, test, mypage, sport, music;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        textView = (TextView) findViewById(R.id.textView2);
        ret = (Button) findViewById(R.id.ret);
        tele = (Button) findViewById(R.id.tele);
        sport = (Button) findViewById(R.id.sport);
        music = (Button) findViewById(R.id.music);
        test = (Button) findViewById(R.id.test);
        mypage = (Button) findViewById(R.id.mypagebt);
        listView = (ListView) findViewById(R.id.listview);


        dataSetting();

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

        sport.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreference.setAttribute(getApplicationContext(),"category1", "sport");
                Intent intent = new Intent(getApplicationContext(), CategoryList.class);
                startActivity(intent);
            }
        });

        music.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreference.setAttribute(getApplicationContext(),"category1", "music");
                Intent intent = new Intent(getApplicationContext(), CategoryList.class);
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
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MakeTeamPage.class);
                startActivity(intent);
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

    private void dataSetting(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        String id = SharedPreference.getAttribute(getApplicationContext(),"id");
        GetService service = retrofit.create(GetService.class);

        Call<List<TeamList>> call = service.showTeamList(id);
        call.enqueue(new Callback<List<TeamList>>(){
            @Override
            public void onResponse(Call<List<TeamList>> call, Response<List<TeamList>> response) {
                Bitmap bitmap;
                MyAdapter mMyAdapter = new MyAdapter();
                if (response.isSuccessful()) {
                    List<TeamList> dummy = response.body();

                    for(TeamList d:dummy){
                        byte[] a = string2Bin(d.getMainimg());
                        writeToFile("teamprofile.jpg", a);

                        File file = new File(getApplicationContext().getFilesDir().toString()+"/teamprofile.jpg");

                        if(file.exists()){
                            String filepath = file.getPath();
                            bitmap = BitmapFactory.decodeFile(filepath);
                            mMyAdapter.addItem(bitmap, d.getName(), d.getContent());
                        }
                    }
                    /* 리스트뷰에 어댑터 등록 */
                    listView.setAdapter(mMyAdapter);
                } else
                {
                    Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<TeamList>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public byte[] string2Bin(String str){
        byte[] result = new byte[str.length()];
        for(int i = 0; i<str.length(); i++){
            result[i] = (byte)Character.codePointAt(str, i);
        }
        return result;
    }

    public void writeToFile(String filename, byte[] pData) {
        if(pData == null){
            return;
        }
        int lByteArraySize = pData.length;

        try{
            File lOutFile = new File(getApplicationContext().getFilesDir().toString()+"/"+filename);
            FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);
            lFileOutputStream.flush();
            lFileOutputStream.write(pData);
            lFileOutputStream.close();
        }catch(Throwable e){
            e.printStackTrace(System.out);
        }
    }
}
