package com.example.myapplication;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Adapter.DateAdapter;
import com.example.myapplication.RetrofitInterface.GetIP;
import com.example.myapplication.RetrofitInterface.GetService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeamPage extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;
    int objval1, objval2;
    ImageView imageView;
    TextView teamname, category, can;
    ListView listView;
    Button manage, admit, ret;
    int atin, atgr;
    String id, leaderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_page);
        id = SharedPreference.getAttribute(getApplicationContext(), "id");
        String team = SharedPreference.getAttribute(getApplicationContext(), "teamname");
        teamname = (TextView) findViewById(R.id.teamname);
        category = (TextView) findViewById(R.id.category);
        listView = (ListView) findViewById(R.id.list6);
        can = (TextView) findViewById(R.id.can);
        imageView = (ImageView) findViewById(R.id.imageView4);
        manage = (Button) findViewById(R.id.button);
        admit = (Button) findViewById(R.id.button2);
        final BarChart barChart = (BarChart) findViewById(R.id.chart);

        ret = (Button) findViewById(R.id.ret);

        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final GetService service = retrofit.create(GetService.class);
        Call<TeamInfo> call = service.getGroupinfo(team, id);
        Call<Calculate> call1 = service.calculateObjective(id, team);
        Call<List<Date>> call2 = service.showdate(team);
        //Call<Calculate> call3 = service.calculateAttend(SharedPreference.getAttribute(getApplicationContext(),"id"), SharedPreference.getAttribute(getApplicationContext(), "teamname"));
        call.enqueue(new Callback<TeamInfo>() {
            @Override
            public void onResponse(Call<TeamInfo> call, Response<TeamInfo> response) {
                if (response.isSuccessful()) {

                    TeamInfo dummy = response.body();
                    teamname.setText(SharedPreference.getAttribute(getApplicationContext(), "teamname"));
                    category.setText(dummy.category);
                    can.setText(Integer.toString(dummy.can));
                    leaderName=dummy.leader;
                    byte[] b = new byte[dummy.img.length];

                    for (int i = 0; i < dummy.img.length; i++) {
                        b[i] = (byte) dummy.img[i];
                    }
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));

                    SharedPreference.setAttribute(getApplicationContext(), "objs", dummy.objectives);

                    String[] obj = dummy.objectives.split(";");


                } else {
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
                if (response.isSuccessful()) {
                    Calculate dummy = response.body();
                    objval1 = dummy.individual;
                    objval2 = dummy.group;
                    atin = dummy.aindividual;
                    atgr = dummy.agroup;
                    ArrayList<String> labelList = new ArrayList<>();
                    labelList.add("개인 출석률");
                    labelList.add("팀 출석률");
                    labelList.add("개인 달성률");
                    labelList.add("팀 달성률");

                    ArrayList<Integer> valList = new ArrayList<>();
                    valList.add(atin);
                    valList.add(atgr);
                    valList.add(objval1);
                    valList.add(objval2);

                    ArrayList<BarEntry> entries = new ArrayList<>();
                    for (int i = 0; i < valList.size(); i++) {
                        entries.add(new BarEntry((Integer) valList.get(i), i));
                    }
                    barChart.setDescription("");
                    BarDataSet depenses = new BarDataSet(entries, "달성률");
                    depenses.setAxisDependency(YAxis.AxisDependency.LEFT);

                    ArrayList<String> labels = new ArrayList<>();
                    for (int i = 0; i < labelList.size(); i++) {
                        labels.add((String) labelList.get(i));
                    }
                    BarData data = new BarData(labels, depenses);
                    depenses.setColors(ColorTemplate.COLORFUL_COLORS);
                    YAxis y = barChart.getAxisLeft();
                    y.setAxisMaxValue(100);
                    y.setAxisMinValue(0);
                    barChart.setData(data);
                    barChart.animateXY(1000, 1000);
                    barChart.invalidate();
                }
            }

            @Override
            public void onFailure(Call<Calculate> call, Throwable t) {

            }
        });
        call2.enqueue(new Callback<List<Date>>() {
            @Override
            public void onResponse(Call<List<Date>> call, Response<List<Date>> response) {

                if (response.isSuccessful()) {
                    final DateAdapter mMyAdapter = new DateAdapter();
                    List<Date> dummy = response.body();
                    for (Date d : dummy) {
                        if (d.user != null) {
                            mMyAdapter.addItem(d.date, d.time, d.user.replace(";", ","), d.state);
                        } else {
                            mMyAdapter.addItem(d.date, d.time, "", d.state);
                        }

                    }
                    listView.setAdapter(mMyAdapter);

                } else {
                    Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Date>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
            }
        });
        /*call3.enqueue(new Callback<Calculate>() {
            @Override
            public void onResponse(Call<Calculate> call, Response<Calculate> response) {
                if(response.isSuccessful()){
                    Calculate dummy = response.body();
                    atin = dummy.individual;
                    atgr = dummy.group;
                    lock = 1;
                }
            }

            @Override
            public void onFailure(Call<Calculate> call, Throwable t) {

            }
        });*/
        admit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdmitPage.class);
                startActivity(intent);
            }
        });
        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!leaderName.equals(id)) {
                    Toast.makeText(getApplicationContext(), "팀장만 수정 가능합니다", Toast.LENGTH_LONG).show();
                } else {

                    Intent intent = new Intent(getApplicationContext(), AttendPage.class);
                    startActivity(intent);
                }
            }
        });
    }
}
