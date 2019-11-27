package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeamPage extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;

    ImageView imageView;
    TextView teamname, category, can;
    Button manage, admit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_page);
        String id = SharedPreference.getAttribute(getApplicationContext(),"id");
        String team = SharedPreference.getAttribute(getApplicationContext(),"teamname");
        teamname = (TextView) findViewById(R.id.teamname);
        category = (TextView) findViewById(R.id.category);
        can = (TextView) findViewById(R.id.can);
        imageView = (ImageView) findViewById(R.id.imageView4);
        manage = (Button) findViewById(R.id.button);
        admit = (Button) findViewById(R.id.button2);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetService service = retrofit.create(GetService.class);
        Call<TeamInfo> call = service.getGroupinfo(team, id);
        Call<Calculate> call1 = service.calculateObjective(id, team);
        call.enqueue(new Callback<TeamInfo>(){
            @Override
            public void onResponse(Call<TeamInfo> call, Response<TeamInfo> response) {
                if (response.isSuccessful()) {

                    TeamInfo dummy = response.body();
                    teamname.setText(SharedPreference.getAttribute(getApplicationContext(),"teamname"));
                    category.setText(dummy.category);
                    can.setText(Integer.toString(dummy.can));

                    String buffer = dummy.img;

                    byte[] a = string2Bin(buffer);
                    writeToFile("profile.jpg", a);

                    File file = new File(getApplicationContext().getFilesDir().toString()+"/profile.jpg");
                    if(file.exists()){
                        String filepath = file.getPath();
                        Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                        imageView.setImageBitmap(bitmap);
                    }

                    SharedPreference.setAttribute(getApplicationContext(),"objs", dummy.objectives);

                    String[] obj = dummy.objectives.split(";");


                } else
                {
                    Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<TeamInfo> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
            }
        });
        call1.enqueue(new Callback<Calculate>() {
            @Override
            public void onResponse(Call<Calculate> call, Response<Calculate> response) {
                if(response.isSuccessful()){
                    Calculate dummy = response.body();
                }
            }

            @Override
            public void onFailure(Call<Calculate> call, Throwable t) {

            }
        });
        admit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdmitPage.class);
                startActivity(intent);
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
