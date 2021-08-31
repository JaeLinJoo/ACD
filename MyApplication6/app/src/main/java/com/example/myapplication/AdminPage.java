package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.myapplication.Adapter.AttendSingoHistoryAdapter;
import com.example.myapplication.Adapter.BeforeApproveAdapter;
import com.example.myapplication.Adapter.ObjSingoHistoryAdapter;
import com.example.myapplication.RetrofitInterface.GetIP;
import com.example.myapplication.RetrofitInterface.GetService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminPage extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    GetService service = retrofit.create(GetService.class);

    ListView listView_osh, listView_ba, listView_ash;
    final BeforeApproveAdapter beforeApproveAdapter = new BeforeApproveAdapter();
    final ObjSingoHistoryAdapter objSingoHistoryAdapter = new ObjSingoHistoryAdapter();
    final AttendSingoHistoryAdapter attendSingoHistoryAdapter = new AttendSingoHistoryAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        listView_osh = findViewById(R.id.listview_obj_singo_history);
        listView_ash = findViewById(R.id.listview_attend_singo_history);
        listView_ba = findViewById(R.id.listview_before_approval);

        Call<List<ObjSingoList>> call_objsingoHistory = service.getObjSingoHistory();
        Call<List<AttendSingoList>> call_attendSingoHistory = service.getAttendSingoHistory();
        Call<List<TeamList>> call_before_approval = service.getBeforeApproval();


        //목표인증///////////////////////////////////////
        call_objsingoHistory.enqueue(new Callback<List<ObjSingoList>>() {
            @Override
            public void onResponse(Call<List<ObjSingoList>> call, Response<List<ObjSingoList>> response) {
                if (response.isSuccessful()) {
                    listView_osh.setAdapter(objSingoHistoryAdapter);
                    List<ObjSingoList> objSingoListList = response.body();

                    Log.e("getObjSingoHistory", "" + objSingoListList);

                    for (int i = 0; i < objSingoListList.size(); i++) {
                        ObjSingoList dummy = objSingoListList.get(i);
                        objSingoHistoryAdapter.addItem(dummy.name, dummy.id, dummy.objective);
                        Log.e("신고목록 성공?", "" + dummy.name);
                        objSingoHistoryAdapter.notifyDataSetChanged();
                    }
                    listView_osh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent1, View view, int position1, long id) {
                            Log.e("getObjSingoHistory", "Clicked " + objSingoHistoryAdapter.getItem(position1).getId());
                            Intent intent = new Intent(getApplicationContext(), ObjSingoDetail.class);
                            intent.putExtra("teamName", objSingoHistoryAdapter.getItem(position1).name);
                            intent.putExtra("id", objSingoHistoryAdapter.getItem(position1).id);
                            intent.putExtra("obj", objSingoHistoryAdapter.getItem(position1).objective);
                            intent.putExtra("position", position1);
                            startActivityForResult(intent, 0);
                        }
                    });
                } else {
                    Log.e("getObjSingoHistory", "response fail");
                }
            }

            @Override
            public void onFailure(Call<List<ObjSingoList>> call, Throwable t) {
                Log.e("getObjSingoHistory", "실패");
            }
        });

        //출석인증///////////////////////////////////////

        call_attendSingoHistory.enqueue(new Callback<List<AttendSingoList>>() {
            @Override
            public void onResponse(Call<List<AttendSingoList>> call, Response<List<AttendSingoList>> response) {
                if (response.isSuccessful()) {
                    listView_ash.setAdapter(attendSingoHistoryAdapter);
                    List<AttendSingoList> attendSingoLists = response.body();

                    for (int i = 0; i < attendSingoLists.size(); i++) {
                        AttendSingoList dummy = attendSingoLists.get(i);
                        attendSingoHistoryAdapter.addItem(dummy.name, dummy.date, dummy.user);
                        Log.e("신고목록", "" + dummy.name);
                        attendSingoHistoryAdapter.notifyDataSetChanged();
                    }
                    listView_ash.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent1, View view, int position1, long id) {
                            Log.e("getAttendSingoHistory", "Clicked " + attendSingoHistoryAdapter.getItem(position1).getName());
                            Intent intent = new Intent(getApplicationContext(), AttendSingoDetail.class);
                            intent.putExtra("teamName", attendSingoHistoryAdapter.getItem(position1).name);
                            intent.putExtra("date", attendSingoHistoryAdapter.getItem(position1).date);
                            intent.putExtra("user", attendSingoHistoryAdapter.getItem(position1).user);
                            intent.putExtra("position", position1);

                            startActivityForResult(intent, 1);
                        }
                    });
                } else {
                    Log.e("getAttendSingoHistory", "response fail");
                }
            }

            @Override
            public void onFailure(Call<List<AttendSingoList>> call, Throwable t) {
                Log.e("getAttendSingoHistory", "실패");
            }
        });

        //소모임 승인///////////////////////////////////////
        call_before_approval.enqueue(new Callback<List<TeamList>>() {
            @Override
            public void onResponse(Call<List<TeamList>> call, Response<List<TeamList>> response) {
                if (response.isSuccessful()) {
                    listView_ba.setAdapter(beforeApproveAdapter);

                    List<TeamList> teamListList = response.body();
                    Log.e("getBeforeApproval", "" + teamListList);
                    for (int i = 0; i < teamListList.size(); i++) {
                        String dummy = teamListList.get(i).getName();
                        beforeApproveAdapter.addItem(dummy);
                        Log.e("getBeforeApproval", "" + dummy);
                        beforeApproveAdapter.notifyDataSetChanged();
                    }

                    listView_ba.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent1, View view, int position1, long id) {
                            Log.e("getBeforeApproval", "Clicked " + beforeApproveAdapter.getItem(position1));
                            Intent intent = new Intent(getApplicationContext(), Ba_Intro.class);
                            intent.putExtra("teamName", beforeApproveAdapter.getItem(position1));
                            intent.putExtra("position", position1);
                            startActivityForResult(intent, 2);
                        }
                    });

                } else
                    Log.e("getBeforeApproval", "response 실패");
            }

            @Override
            public void onFailure(Call<List<TeamList>> call, Throwable t) {
                Log.e("getBeforeApproval", "onFailureeee");
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                int pos = data.getExtras().getInt("pos");
                objSingoHistoryAdapter.removeItem(pos);
                objSingoHistoryAdapter.notifyDataSetChanged();
                Log.e("onActivityResult_singo", "Obj::성공적");
            } else {
                Log.e("onActivityResult_singo", "Obj::취소");
            }
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                int pos = data.getExtras().getInt("pos");
                attendSingoHistoryAdapter.removeItem(pos);
                attendSingoHistoryAdapter.notifyDataSetChanged();
                Log.e("onActivityResult_singo", "Attend::성공적");
            } else {
                Log.e("onActivityResult_singo", "Attend::취소");
            }
        }else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                int pos = data.getExtras().getInt("pos");
                beforeApproveAdapter.removeItem(pos);
                beforeApproveAdapter.notifyDataSetChanged();
                Log.e("onActivityResult_ba", "성공적");
            } else {
                Log.e("onActivityResult_ba", "취소");
            }
        }
    }
}

