package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.MyItem;
import com.example.myapplication.R;

import java.util.ArrayList;

public class DateAdapter extends BaseAdapter {
    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<MyItem> mItems = new ArrayList<>();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public MyItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.date_custom, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        TextView tv_date = (TextView) convertView.findViewById(R.id.tv_name) ;
        TextView tv_time = (TextView) convertView.findViewById(R.id.tv_contents) ;
        TextView tv_user = (TextView) convertView.findViewById(R.id.user) ;
        TextView tv_state = (TextView) convertView.findViewById(R.id.textView25) ;

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        MyItem myItem = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        tv_date.setText(myItem.getName());
        tv_time.setText(myItem.getContents());
        tv_user.setText(myItem.getCount());
        tv_state.setText(myItem.getState());

        if(tv_state.getText()=="마감"){
            tv_state.setBackgroundColor(context.getResources().getColor(R.color.attendYes));
        }

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */

        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String date, String time, String user, String state) {

        MyItem mItem = new MyItem();

        /* MyItem에 아이템을 setting한다. */
        mItem.setName(date);
        mItem.setContents(time);
        mItem.setCount(user);
        mItem.setState(state);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);

    }
}
