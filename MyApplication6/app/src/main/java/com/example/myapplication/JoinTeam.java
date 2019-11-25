package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class JoinTeam extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;
    boolean ismentor = false;
    boolean mentor_submit = false;
    TextView teamname, member_count, category1, category2, teamname1, intro, peroid, obj, admit, mentor_pay, time;
    ImageView imageView;
    ListView objlist;
    Button mentorbt, submit;
    EditText can;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);

        teamname = (TextView) findViewById(R.id.teamname);
        member_count = (TextView) findViewById(R.id.member_count);
        category1 = (TextView) findViewById(R.id.category1);
        category2 = (TextView) findViewById(R.id.category2);
        teamname1 = (TextView) findViewById(R.id.teamname1);
        intro = (TextView) findViewById(R.id.intro);
        peroid = (TextView) findViewById(R.id.peroid);
        obj = (TextView) findViewById(R.id.obj);
        admit = (TextView) findViewById(R.id.admit);
        mentor_pay = (TextView) findViewById(R.id.mentor_pay);
        time = (TextView) findViewById(R.id.time);

        imageView = (ImageView) findViewById(R.id.imageView5);
        objlist = (ListView) findViewById(R.id.objlist);

        mentorbt =(Button) findViewById(R.id.mentorbt);
        submit = (Button) findViewById(R.id.submit);
        can = (EditText) findViewById(R.id.can);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetService service = retrofit.create(GetService.class);

        Call<JoinList> call = service.showJoinList(SharedPreference.getAttribute(getApplicationContext(),"teamname"));

        call.enqueue(new Callback<JoinList>(){
            @Override
            public void onResponse(Call<JoinList> call, Response<JoinList> response) {
                if (response.isSuccessful()) {
                    final MyObjectiveAdapter mMyAdapter = new MyObjectiveAdapter();
                    JoinList dummy = response.body();
                    teamname.setText(dummy.teamname);
                    member_count.setText(dummy.member_count);
                    category1.setText(dummy.category1);
                    category2.setText(dummy.category2);
                    teamname1.setText(dummy.teamname1);
                    intro.setText(dummy.intro);
                    peroid.setText(dummy.peroid);
                    obj.setText(dummy.obj);
                    admit.setText(dummy.admit);
                    mentor_pay.setText(dummy.mentor_pay);
                    time.setText(dummy.time);
                    if(dummy.ismentor.equals("1")){
                        ismentor = true;
                    }

                    String[] s = dummy.objlist.split(";");
                    for(int i = 0; i<s.length; i++){
                        mMyAdapter.addItem(s[i]);
                    }
                    objlist.setAdapter(mMyAdapter);

                    if(dummy.img != null){
                        String buffer = dummy.img;

                        byte[] a = string2Bin(buffer);
                        writeToFile("profile.jpg", a);

                        File file = new File(getApplicationContext().getFilesDir().toString()+"/profile.jpg");
                        if(file.exists()){
                            String filepath = file.getPath();
                            Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                            imageView.setImageBitmap(bitmap);
                        }
                    }


                } else
                {
                    Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<JoinList> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
            }
        });
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String can1 = can.getText().toString();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                GetService service = retrofit.create(GetService.class);

                Call<Dummy> call = service.submitTeam(SharedPreference.getAttribute(getApplicationContext(),"teamname"),SharedPreference.getAttribute(getApplicationContext(),"id"), Integer.parseInt(can1), mentor_submit);
                call.enqueue(new Callback<Dummy>(){
                    @Override
                    public void onResponse(Call<Dummy> call, Response<Dummy> response) {
                        if (response.isSuccessful()) {
                            Dummy dummy = response.body();
                            if(dummy.isCheck()){
                                Toast.makeText(getApplicationContext(),"신청이 완료되었습니다!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"신청 실패!", Toast.LENGTH_LONG).show();
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

        mentorbt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!ismentor){
                    Toast.makeText(getApplicationContext(),"이미 멘토가 지원 되었거나 멘토가 필요없는 소모임 입니다!",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"멘토로 지원 되었습니다!",Toast.LENGTH_LONG).show();
                    mentor_submit = true;
                }
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