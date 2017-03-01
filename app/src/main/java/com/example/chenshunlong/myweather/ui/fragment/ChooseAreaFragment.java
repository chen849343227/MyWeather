package com.example.chenshunlong.myweather.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chenshunlong.myweather.R;
import com.example.chenshunlong.myweather.db.City;
import com.example.chenshunlong.myweather.db.County;
import com.example.chenshunlong.myweather.db.Province;
import com.example.chenshunlong.myweather.ui.activity.ChooseAreaActivity;
import com.example.chenshunlong.myweather.ui.activity.MainActivity;
import com.example.chenshunlong.myweather.ui.activity.WeatherActivity;
import com.example.chenshunlong.myweather.util.HttpUtil;
import com.example.chenshunlong.myweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by chenshunlong on 17-2-8.
 */

public class ChooseAreaFragment extends android.support.v4.app.Fragment {
    private static final int LEVEL_PROVINCE = 0;

    private static final int LEVEL_CITY = 1;

    private static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省
     */
    private Province selectProvince;

    /**
     * 选中的市
     */
    private City selectCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_btn);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(i);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectCity = cityList.get(i);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(i).getWeatherId();
                    Log.e("MainActivity", String.valueOf(getActivity()));
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvince();
                } else if (currentLevel == LEVEL_PROVINCE) {
                    getActivity().finish();
                }
            }
        });
        queryProvince();
    }

    /**
     * 遍历所有的省，优先从数据库查询，查询不到再从网上查询
     */
    private void queryProvince() {
        titleText.setText("中国");
        if (getActivity() instanceof MainActivity) {
            backButton.setVisibility(View.GONE);
        } else if (getActivity() instanceof ChooseAreaActivity) {
            backButton.setVisibility(View.VISIBLE);
        }
        //从数据库中查询所有的省
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            //当适配器的内容发生改变时，强制调用getView来刷新每个Item
            adapter.notifyDataSetChanged();
            //将列表移动到指定的position   这里position=0
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 遍历所有的市，优先从数据库查询，查询不到再从网上查询
     */
    private void queryCities() {
        titleText.setText(selectProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        //根据所选的省获取在数据库中对应的ID序列，然后去查找市
        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            //当适配器的内容发生改变时，强制调用getView来刷新每个Item
            adapter.notifyDataSetChanged();
            //将列表移动到指定的position   这里position=0
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 遍历所有的县，优先从数据库查询，查询不到再从网上查询
     */
    private void queryCounties() {
        titleText.setText(selectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        //根据所选的省获取在数据库中对应的ID序列，然后去查找市
        countyList = DataSupport.where("cityId = ?", String.valueOf(selectCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            //当适配器的内容发生改变时，强制调用getView来刷新每个Item
            adapter.notifyDataSetChanged();
            //将列表移动到指定的position   这里position=0
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型，从服务器上查询省,市,县数据
     *
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvince();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进入对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
