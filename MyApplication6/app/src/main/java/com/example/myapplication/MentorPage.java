package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MentorPage extends AppCompatActivity {

    private static final String BASE = GetIP.BASE;
    ListView mentorlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_page);

        mentorlist = (ListView)findViewById(R.id.mentorlist);
    }

    private void dataSetting(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        String id = SharedPreference.getAttribute(getApplicationContext(),"id");
        GetService service = retrofit.create(GetService.class);

        Call<List<MentorTeamList>> call = service.showMentorTeamList(id);
        call.enqueue(new Callback<List<MentorTeamList>>(){
            @Override
            public void onResponse(Call<List<MentorTeamList>> call, Response<List<MentorTeamList>> response) {
                Bitmap bitmap;
                final MentorAdapter mMyAdapter = new MentorAdapter();

                if (response.isSuccessful()) {
                    List<MentorTeamList> dummy = response.body();

                    for(MentorTeamList d:dummy){
                        if(d.getCategory1().equals(SharedPreference.getAttribute(getApplicationContext(),"category1")) && d.getState().equals("모집중")&&d.getMentor().equals("1")){
                            byte[] a = string2Bin(d.getMainimg());
                            writeToFile("teamprofile.jpg", a);

                            File file = new File(getApplicationContext().getFilesDir().toString()+"/teamprofile.jpg");

                            if(file.exists()){
                                String filepath = file.getPath();
                                bitmap = BitmapFactory.decodeFile(filepath);
                                mMyAdapter.addItem(bitmap, d.getName(), d.getContent(),d.getPay(),d.getState(),d.getCategory1()+" / "+d.getCategory2());
                            }
                        }
                    }
                    /* 리스트뷰에 어댑터 등록 */
                    mentorlist.setAdapter(mMyAdapter);

                    mentorlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            SharedPreference.setAttribute(getApplicationContext(), "teamname", mMyAdapter.getItem(i).getName());
                            Intent intent = new Intent(getApplicationContext(), JoinTeam.class);
                            startActivity(intent);
                        }
                    });

                } else
                {
                    Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<MentorTeamList>> call, Throwable t) {
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
        //Log.e("asdvggg",getApplicationContext().getFilesDir().toString()+"/"+filename);
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
