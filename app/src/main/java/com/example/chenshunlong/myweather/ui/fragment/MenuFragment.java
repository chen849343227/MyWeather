package com.example.chenshunlong.myweather.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chenshunlong.myweather.R;
import com.example.chenshunlong.myweather.ui.activity.ChooseAreaActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenshunlong on 17-2-9.
 */

public class MenuFragment extends Fragment {


    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter adapter;
    private List<String> dataList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment,container,false);
        titleText = (TextView) view.findViewById(R.id.menu_title_text);
       // backButton = (Button) view.findViewById(R.id.menu_back_btn);
        listView = (ListView) view.findViewById(R.id.menu_list_view);
        dataList.add("切换城市");
        dataList.add("退出");
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //String item = dataList.get(i);
                if(i==0){
                    Intent intent = new Intent(getActivity(), ChooseAreaActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }else if(i ==1){
                    getActivity().finish();
                }
            }
        });
    }
}
