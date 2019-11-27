package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class CategoryList extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;
    TextView cg;
    ListView listView1, listView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        listView1 = (ListView) findViewById(R.id.list1);
        listView2 = (ListView) findViewById(R.id.list2);
        cg = (TextView) findViewById(R.id.category);

        cg.setText(SharedPreference.getAttribute(getApplicationContext(),"category1"));
        dataSetting();
    }
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
                final MyAdapter mMyAdapter = new MyAdapter();
                final MyAdapter mMyAdapter1 = new MyAdapter();
                if (response.isSuccessful()) {
                    List<TeamList> dummy = response.body();

                    for(TeamList d:dummy){
                        if(d.getCategory1().equals(SharedPreference.getAttribute(getApplicationContext(),"category1")) && d.getState().equals("모집중")){
                            byte[] a = string2Bin(d.getMainimg());
                            writeToFile("teamprofile.jpg", a);

                            File file = new File(getApplicationContext().getFilesDir().toString()+"/teamprofile.jpg");

                            if(file.exists()){
                                String filepath = file.getPath();
                                bitmap = BitmapFactory.decodeFile(filepath);
                                mMyAdapter.addItem(bitmap, d.getName(), d.getContent(),d.getCount(),d.getState(),d.getCategory1()+" / "+d.getCategory2());
                            }
                        }
                        if(d.getCategory1().equals(SharedPreference.getAttribute(getApplicationContext(),"category1")) && d.getState().equals("진행중")){
                            byte[] a = string2Bin(d.getMainimg());
                            writeToFile("teamprofile.jpg", a);

                            File file = new File(getApplicationContext().getFilesDir().toString()+"/teamprofile.jpg");

                            if(file.exists()){
                                String filepath = file.getPath();
                                bitmap = BitmapFactory.decodeFile(filepath);
                                mMyAdapter1.addItem(bitmap, d.getName(), d.getContent(),d.getCount(),d.getState(),d.getCategory1()+" / "+d.getCategory2());
                            }
                        }
                    }
                    /* 리스트뷰에 어댑터 등록 */
                    listView1.setAdapter(mMyAdapter);
                    listView2.setAdapter(mMyAdapter1);
                    listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            SharedPreference.setAttribute(getApplicationContext(), "teamname", mMyAdapter1.getItem(i).getName());
                            Intent intent = new Intent(getApplicationContext(), ChallengeTeam.class);
                            startActivity(intent);
                        }
                    });
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
