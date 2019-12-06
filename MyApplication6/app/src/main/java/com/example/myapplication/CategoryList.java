package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Adapter.MyAdapter;
import com.example.myapplication.RetrofitInterface.GetIP;
import com.example.myapplication.RetrofitInterface.GetService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoryList extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;
    TextView cg;
    GridView listView1, listView2;
    Button ret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        listView1 = (GridView) findViewById(R.id.list1);
        listView2 = (GridView) findViewById(R.id.list2);
        cg = (TextView) findViewById(R.id.category);
        ret = (Button)findViewById(R.id.ret);

        ret.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
            }
        });

        cg.setText("#"+SharedPreference.getAttribute(getApplicationContext(),"category1"));
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
                            byte[] b = new byte[d.getMainimg().length];

                            for(int i =0;i < b.length;i++){
                                b[i] = (byte)d.getMainimg()[i];
                            }

                            mMyAdapter.addItem(BitmapFactory.decodeByteArray(b, 0, b.length), d.getName(), d.getContent(),d.getCount(),d.getCategory1()+" / "+d.getCategory2());

                        }
                        if(d.getCategory1().equals(SharedPreference.getAttribute(getApplicationContext(),"category1")) && d.getState().equals("진행중")){
                            byte[] b = new byte[d.getMainimg().length];

                            for(int i =0;i < b.length;i++){
                                b[i] = (byte)d.getMainimg()[i];
                            }

                            mMyAdapter1.addItem(BitmapFactory.decodeByteArray(b, 0, b.length), d.getName(), d.getContent(),d.getCount(),d.getCategory1()+" / "+d.getCategory2());

                        }
                    }
                    /* 리스트뷰에 어댑터 등록 */
                    listView1.setAdapter(mMyAdapter);
                    listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            SharedPreference.setAttribute(getApplicationContext(), "teamname", mMyAdapter.getItem(i).getName());
                            Intent intent = new Intent(getApplicationContext(), JoinTeam.class);
                            startActivity(intent);
                        }
                    });
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
}
