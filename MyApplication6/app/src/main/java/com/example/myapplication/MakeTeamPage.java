package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.RetrofitInterface.GetIP;
import com.example.myapplication.RetrofitInterface.GetService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MakeTeamPage extends AppCompatActivity {

    ArrayAdapter<CharSequence> adspin1, adspin2; //어댑터를 선언했습니다. adspint1(스포츠,언어..) adspin2(축구,농구..)
    private static final String BASE = GetIP.BASE;
    ArrayAdapter<CharSequence> countspin; // 어댑터에 인원수 선언
    private ListAdapter adapter;
    public ArrayList<ListItem> listViewItemList = new ArrayList<ListItem>(); //리스트뷰
    private ArrayList<ListItem> filteredItemList = listViewItemList; //리스트뷰 임시저장소
    int member_count = 0;
    int num = 0;
    String choice_do="";
    String choice_se="";
    //검색시 선택된 매세지를 띄우기 위한 선언하였습니다. 그냥 선언안하고 인자로 넘기셔도 됩니다.
    String start ="";
    String end = "";
    private TextView textView_startDate;
    private TextView textView_finishDate;
    private DatePickerDialog.OnDateSetListener callbackMethod_start;
    private DatePickerDialog.OnDateSetListener callbackMethod_finish;
    String mentor;
    Button postimg, post, add,ret;
    EditText teamname, objective, admit, pay, time, intro, can;
    RadioButton yes, no;
    ImageView imageView;
    ListView listView;
    ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_team_page);

        postimg = (Button) findViewById(R.id.post);
        post = (Button) findViewById(R.id.button_신청);
        add = (Button) findViewById(R.id.add);

        teamname = (EditText) findViewById(R.id.editText_name);
        objective = (EditText) findViewById(R.id.editText_goal);
        listView = (ListView) findViewById(R.id.listv);
        admit = (EditText) findViewById(R.id.editText_인증);
        pay = (EditText) findViewById(R.id.editText_mento_pay);
        time = (EditText) findViewById(R.id.editText_모임시간);
        intro = (EditText) findViewById(R.id.editText_intro);
        can = (EditText) findViewById(R.id.editText);

        yes = (RadioButton) findViewById(R.id.radioButton_mento_yes);
        no = (RadioButton) findViewById(R.id.radioButton_mento_no);
        imageView = (ImageView) findViewById(R.id.imageView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        ret = (Button)findViewById(R.id.ret);

        ret.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
            }
        });

        final Spinner spin_count = (Spinner) findViewById(R.id.spinner_count);
        countspin = ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_count,android.R.layout.simple_spinner_dropdown_item);
        spin_count.setAdapter(countspin);

        num = 0;
        adapter = new ListAdapter();
        listView.setAdapter(adapter);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        spin_count.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String count = countspin.getItem(i).toString();
                switch (count){
                    case "2": member_count=2;break;
                    case "3": member_count=3;break;
                    case "4": member_count=4;break;
                    case "5": member_count=5;break;
                    case "6": member_count=6;break;
                    case "7": member_count=7;break;
                    case "8": member_count=8;break;
                    case "9": member_count=9;break;
                    case "10": member_count=10;break;
                    case "11": member_count=11;break;
                    default: member_count=0; break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Spinner spin1 = (Spinner) findViewById(R.id.spinner);
        final Spinner spin2 = (Spinner) findViewById(R.id.spinner2);
        Button btn_refresh = (Button) findViewById(R.id.btn_refresh);
//xml과 class에 변수들을 연결해줍니다. final를 사용한 이유는 spin2가 함수안에서 사용되기 때문에 코딩전체로 선언한 것입니다.
        adspin1 = ArrayAdapter.createFromResource(this, R.array.spinner_do, android.R.layout.simple_spinner_dropdown_item);
//처번째 어댑터에 값을 넣습니다. this=는 현재class를 의미합니다. R.array.spinner_do는 이곳에 도시를 다 쓸 경우 코딩이 길어지기 때문에 value->string.xml에 따로 String값들을 선언해두었습니다.
//R.layout.simple_~~~는 안드로이드에서 기본제공하는 spinner 모양입니다. 다른것도 있는데 비슷합니다.
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//이부분이 정확히 말로 설명을 못하겠습니다. 아무튼 필요합니다. 헤헤 고수분들 도와주세요.
        spin1.setAdapter(adspin1);
//어댑터에 값들을 spinner에 넣습니다. 여기까지 하시면 첫번째 spinner에 값들이 들어갈 것입니다.
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num ++;
                //Toast.makeText(MainActivity.this,num+ "추가되었습니다.", Toast.LENGTH_SHORT).show();
                adapter.addItem(num);
                adapter.notifyDataSetChanged();
            }
        });

        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //첫번째 spinner 클릭시 이벤트 발생입니다. setO 정도까지 치시면 자동완성됩니다. 뒤에도 마찬가지입니다.
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//제대로 자동완성하셨다면 이부분이 자동으로 만들어 질 것입니다. int i는 포지션이라 하여 제가 spinner에 몇번째걸 선택했는지 값이 들어갑니다. 필요하겠죠? ㅎㅎ
                if (adspin1.getItem(i).equals("스포츠")) {
//spinner에 값을 가져와서 i 보이시나요 제가 클릭 한것이 스포츠인지 확인합니다.
                    choice_do = "스포츠";//버튼 클릭시 출력을 위해 값을 넣었습니다.
                    adspin2 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_do_sports, android.R.layout.simple_spinner_dropdown_item);
//스포츠일 경우에 두번째 spinner에 값을 넣습니다.
//그냥 this가 아닌 Main~~~인 이유는 그냥 this는 메인엑티비티인 경우만 가능합니다.
//지금과 같이 다른 함수안이나 다른 클래스에서는 꼭 자신의 것을 넣어주어야 합니다.
//혹시나 다른 class -> Public View밑에서 작업하시는 분은 view명.getContext()로 해주셔야 합니다.
//예로 View rootView =~~ 선언하신 경우에는 rootView.getContext()써주셔야합니다. this가 아니라요.
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);
//두번째 어댑터값을 두번째 spinner에 넣었습니다.

                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        //저희는 두번째 선택된 값도 필요하니 이안에 두번째 spinner 선택 이벤트를 정의합니다.
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_se = adspin2.getItem(i).toString();
//두번째 선택된 값을 choice_se에 넣습니다.
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
//아무것도 선택안될시 부분입니다. 자동완성됩니다.
                        }
                    });
                } else if (adspin1.getItem(i).equals("인문학")) {
//똑같은 소스에 반복입니다. 인문학부분입니다.
                    choice_do = "인문학";
                    adspin2 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_do_inmunhak, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_se = adspin2.getItem(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } else if (adspin1.getItem(i).equals("언어")) {
                    choice_do = "언어";
                    adspin2 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_do_language, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_se = adspin2.getItem(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }
                else if (adspin1.getItem(i).equals("악기")) {
                    choice_do = "악기";
                    adspin2 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_do_music, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_se = adspin2.getItem(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }
                else if (adspin1.getItem(i).equals("공예")) {
                    choice_do = "공예";
                    adspin2 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_do_handmade, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_se = adspin2.getItem(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            //버튼 클릭시 이벤트입니다.
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), choice_do + "=" + choice_se, Toast.LENGTH_SHORT).show();
                //선택된 대분류 와 소분류를 Toast로 화면에 보여줍니다.
            }
        });

        this.InitializeView();
        this.InitializeLister();

        postimg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay.setText("0");
                pay.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(can.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"캔을 입력해 주세요.",Toast.LENGTH_LONG).show();
                }
                else if(Integer.parseInt(can.getText().toString())>Integer.parseInt(SharedPreference.getAttribute(getApplicationContext(),"can"))){
                    Toast.makeText(getApplicationContext(),"캔이 부족합니다!",Toast.LENGTH_LONG).show();
                }
                else{
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    if(yes.isChecked()){
                        mentor = "1";
                    }
                    if(no.isChecked()){
                        mentor = "0";
                    }
                    String text1 = teamname.getText().toString();
                    String text2 = objective.getText().toString();
                    String text4 = admit.getText().toString();
                    String text5 = pay.getText().toString();
                    String text6 = time.getText().toString();
                    String text7 = intro.getText().toString();
                    String text8 = Integer.toString(member_count);
                    String text9 = can.getText().toString();

                    ArrayList<String> s = new ArrayList<String>();
                    for(int i=0;i<listViewItemList.size();i++){
                        s.add(listViewItemList.get(i).getName());
                    }
                    String text3="";
                    for (int i=0;i<s.size();i++){
                        if(s.get(i)==""){
                            continue;
                        }
                        if(i==0){
                            text3 = text3 + s.get(i);
                        }
                        else{
                            text3 = text3+";"+s.get(i);
                        }
                    }

                    File imageBytes = new File(getApplicationContext().getCacheDir().toString()+"/teamtemp.jpg");
                    GetService retrofitInterface = retrofit.create(GetService.class);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("image", "common.jpg", requestFile);

                    String id = SharedPreference.getAttribute(getApplicationContext(),"id");
                    RequestBody fullName = RequestBody.create(MediaType.parse("multipart/form-data"), id);
                    RequestBody tn = RequestBody.create(MediaType.parse("multipart/form-data"), text1);
                    RequestBody ob = RequestBody.create(MediaType.parse("multipart/form-data"), text2);
                    RequestBody obs = RequestBody.create(MediaType.parse("multipart/form-data"), text3);
                    RequestBody ad = RequestBody.create(MediaType.parse("multipart/form-data"), text4);
                    RequestBody pa = RequestBody.create(MediaType.parse("multipart/form-data"), text5);
                    RequestBody ti = RequestBody.create(MediaType.parse("multipart/form-data"), text6);
                    RequestBody it = RequestBody.create(MediaType.parse("multipart/form-data"), text7);
                    RequestBody st = RequestBody.create(MediaType.parse("multipart/form-data"), start);
                    RequestBody ed = RequestBody.create(MediaType.parse("multipart/form-data"), end);
                    RequestBody mt = RequestBody.create(MediaType.parse("multipart/form-data"), mentor);
                    RequestBody mc = RequestBody.create(MediaType.parse("multipart/form-data"), text8);
                    RequestBody cg1 = RequestBody.create(MediaType.parse("multipart/form-data"), choice_do);
                    RequestBody cg2 = RequestBody.create(MediaType.parse("multipart/form-data"), choice_se);
                    RequestBody can1 = RequestBody.create(MediaType.parse("multipart/form-data"), text9);

                    Call<Dummy> call = retrofitInterface.postteam(body, fullName, tn, ob, obs, ad, pa, ti, it, st, ed, mt, mc, cg1, cg2, can1);

                    call.enqueue(new Callback<Dummy>() {
                        @Override
                        public void onResponse(Call<Dummy> call, retrofit2.Response<Dummy> response) {
                            Dummy dummy = response.body();
                            if(dummy.isCheck()){
                                Toast.makeText(getApplicationContext(), "소모임 만들기 성공!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "이미 존재하는 소모임 이름입니다!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Dummy> call, Throwable t) {

                        }
                    });
                }
            }

        });
    }

    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return filteredItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();
            final ViewHolder holder;

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.objectives_custom, parent, false);

                holder.editText1 = (EditText)convertView.findViewById(R.id.editText1);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }

            holder.ref = position;
            // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
            final ListItem listViewItem = filteredItemList.get(position);
            Toast.makeText(getApplicationContext(),listViewItem.getName(), Toast.LENGTH_LONG);
            holder.editText1.setText(listViewItem.getName());

            holder.editText1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    filteredItemList.get(holder.ref).setName(s.toString());
                }
            });
            return convertView;
        }



        public void addItem(int num) {
            /*ViewHolder holder = new ViewHolder();
            View convertView = new View(getApplicationContext());
            holder.textView = (TextView) convertView.findViewById(R.id.textView1);

            holder.textView.setText("세부목표"+num+":");*/
            ListItem item = new ListItem();
            item.setName("");

            listViewItemList.add(item);
        }
    }
    public class ViewHolder {
        EditText editText1;
        TextView textView;
        int ref;
    }

    public void InitializeView(){
        textView_startDate = (TextView)findViewById(R.id.textView_startDate);
        textView_finishDate = (TextView)findViewById(R.id.textView_finishDate);
    }

    public void InitializeLister(){
        callbackMethod_start = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                textView_startDate.setText(year+"."+(monthOfYear+1)+"."+dayOfMonth);
                start = year+"."+(monthOfYear+1)+"."+dayOfMonth;
            }
        };
        callbackMethod_finish = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                textView_finishDate.setText(year+"."+(monthOfYear+1)+"."+dayOfMonth);
                end = year+"."+(monthOfYear+1)+"."+dayOfMonth;
            }
        };
    }

    public void OnClickHandler_start(View view){
        final Calendar c = Calendar.getInstance();
        int year=c.get(Calendar.YEAR)-1;
        int month=c.get(Calendar.MONTH)+1;
        int day=c.get(Calendar.DAY_OF_MONTH);
        Calendar cal=new GregorianCalendar(Locale.KOREA);
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR,8);
        DatePickerDialog dialog_start = new DatePickerDialog(this,callbackMethod_start,year,month,day);
        dialog_start.getDatePicker().setMinDate(cal.getTimeInMillis());
        dialog_start.show();
    }
    public void OnClickHandler_finish(View view){
        final Calendar c = Calendar.getInstance();
        int year=c.get(Calendar.YEAR)-1;
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_MONTH);
        Calendar cal=new GregorianCalendar(Locale.KOREA);
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR,22);
        DatePickerDialog dialog_finish = new DatePickerDialog(this,callbackMethod_finish,year,month,day);
        dialog_finish.getDatePicker().setMinDate(cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_YEAR,70);
        dialog_finish.getDatePicker().setMaxDate(cal.getTimeInMillis());
        dialog_finish.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0)
        {
            if(resultCode == RESULT_OK)
            {
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);

                    imageView.setImageBitmap(img);
                    File f = new File(getApplicationContext().getCacheDir(), "teamtemp.jpg");
                    f.createNewFile();

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG,50,bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    //uploadImage(f);
                    in.close();
                }catch(Exception e)
                {

                }
            }
            else if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

}
