package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;

public class TeamPage extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;
    //    Button btn_check = findViewById(R.id.button_check);
    TextView tv_subGoals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_page);

        tv_subGoals = findViewById(R.id.text_subGoals);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetService service = retrofit.create(GetService.class);
        Call<GroupInfo> call = service.getGroupinfo(SharedPreference.getAttribute(getApplicationContext(), "teamname"));    //이거 클릭한 팀아이디 넣도록 변경
        call.enqueue(dummies);


//        btn_check.setOnClickListener((v) -> {
//            Intent intent = new Intent(getApplicationContext(), CheckPage.class);
//            startActivity(intent);
//        });
    }

    Callback dummies = new Callback<GroupInfo>() {
        @Override
        public void onResponse(Call<GroupInfo> call, Response<GroupInfo> response) {
            if (response.isSuccessful()) {
                GroupInfo dummy = response.body();
                ArrayList<String> groupUserNames = new ArrayList<>();
                Collections.addAll(groupUserNames, dummy.user.split(";"));
                Log.e("유저이름들", groupUserNames.toString());

                String thisUserName = SharedPreference.getAttribute(getApplicationContext(), "id");
                Log.e("지금 유저이름", thisUserName);

                String[] checkNumArray = dummy.checknum.split(";");
                int[] allCheckNum = new int[checkNumArray.length];
                int sumAllCheckNum = 0;
                for (int i = 0; i < checkNumArray.length; i++) {
                    allCheckNum[i] = Integer.parseInt(checkNumArray[i]);
                    sumAllCheckNum += allCheckNum[i];
                }
                int groupPeriod = dummy.period;

                int thisCheckNum = allCheckNum[groupUserNames.indexOf(thisUserName)];
                Log.e("안에거만?", "" + groupUserNames.indexOf(thisUserName));
                Log.e("출석 몇번?", "" + thisCheckNum);
                int leng = checkNumArray.length;
                tv_subGoals.setText("" + groupPeriod);

                drawBarChart(sumAllCheckNum, thisCheckNum, groupPeriod, leng);
            }
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            Toast.makeText(TeamPage.this, "팀 아이디로 못불러왔나봄", Toast.LENGTH_SHORT).show();
        }
    };

    private void drawBarChart(float sumAllCheckNum, float thisCheckNum, float groupPeriod, float len) {
        Log.e("들어온 값들이?", "" + sumAllCheckNum + "" + thisCheckNum + "" + groupPeriod + "" + len);

        BarChart barChart = (BarChart) findViewById(R.id.chart);
        float solo_출석 = (thisCheckNum / groupPeriod) * 100;
        float team_출석 = ((sumAllCheckNum / len) / groupPeriod) * 100;

        ArrayList<String> labelList = new ArrayList<>();
        labelList.add("개인출석률");
        Log.e("그래서 개인  얼마?", "" + solo_출석);
        labelList.add("팀출석률");
        Log.e("그래서 팀  얼마?", "" + team_출석);
        labelList.add("개인달성률");
        labelList.add("팀달성률");

        ArrayList<Integer> valList = new ArrayList<>();
        valList.add((int)solo_출석);
        valList.add((int)team_출석);
        valList.add(25);
        valList.add(25);

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry((Integer) valList.get(i), i));
        }

        BarDataSet depenses = new BarDataSet(entries, "달성률");
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < labelList.size(); i++) {
            labels.add((String) labelList.get(i));
        }
        BarData data = new BarData(labels, depenses);
        depenses.setColors(ColorTemplate.COLORFUL_COLORS);

        barChart.getAxisLeft().setStartAtZero(true);
        barChart.setData(data);
        barChart.animateXY(1000, 1000);
        barChart.invalidate();
    }

}