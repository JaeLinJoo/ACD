package com.example.myapplication;

<<<<<<< HEAD
import androidx.appcompat.app.AppCompatActivity;

=======
>>>>>>> lab_acd/master
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
<<<<<<< HEAD
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
=======
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Adapter.MyMentorAdapter;
import com.example.myapplication.RetrofitInterface.GetIP;
import com.example.myapplication.RetrofitInterface.GetService;

>>>>>>> lab_acd/master
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MentorPage extends AppCompatActivity {
<<<<<<< HEAD

    private static final String BASE = GetIP.BASE;
    ListView mentorlist;
=======
    private static final String BASE = GetIP.BASE;
    GridView mentorlist ;
    Button ret;
>>>>>>> lab_acd/master

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_page);

<<<<<<< HEAD
        mentorlist = (ListView)findViewById(R.id.mentorlist);
    }

    private void dataSetting(){
=======
        ret = (Button)findViewById(R.id.ret);
        mentorlist = (GridView)findViewById(R.id.mentorlist);

        ret.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
            }
        });

>>>>>>> lab_acd/master
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        String id = SharedPreference.getAttribute(getApplicationContext(),"id");
        GetService service = retrofit.create(GetService.class);

<<<<<<< HEAD
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
=======
        Call<List<TeamList>> call = service.showTeamList(id);
        call.enqueue(new Callback<List<TeamList>>(){
            @Override
            public void onResponse(Call<List<TeamList>> call, Response<List<TeamList>> response) {
                Bitmap bitmap;
                final MyMentorAdapter mMyAdapter = new MyMentorAdapter();
                if (response.isSuccessful()) {
                    List<TeamList> dummy = response.body();

                    for(TeamList d:dummy){
                        if(d.getState().equals("모집중") && d.getMentor().equals("1")){
                            byte[] b = new byte[d.getMainimg().length];

                            for(int i =0;i < b.length;i++){
                                b[i] = (byte)d.getMainimg()[i];
                            }

                            mMyAdapter.addItem(BitmapFactory.decodeByteArray(b, 0, b.length), d.getName(), d.getCount(),d.getMentor_pay(),d.getState(),d.getCategory1()+" / "+d.getCategory2());

>>>>>>> lab_acd/master
                        }
                    }
                    /* 리스트뷰에 어댑터 등록 */
                    mentorlist.setAdapter(mMyAdapter);
<<<<<<< HEAD

=======
>>>>>>> lab_acd/master
                    mentorlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            SharedPreference.setAttribute(getApplicationContext(), "teamname", mMyAdapter.getItem(i).getName());
                            Intent intent = new Intent(getApplicationContext(), JoinTeam.class);
                            startActivity(intent);
                        }
                    });
<<<<<<< HEAD

=======
>>>>>>> lab_acd/master
                } else
                {
                    Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                }
            }
<<<<<<< HEAD

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
=======
            @Override
            public void onFailure(Call<List<TeamList>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
>>>>>>> lab_acd/master
