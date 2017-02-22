package com.elegantweather.com;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.elegantweather.com.db.City;
import com.elegantweather.com.db.County;
import com.elegantweather.com.db.Province;
import com.elegantweather.com.util.HttpUtil;
import com.elegantweather.com.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/20 0020.
 *
 * 这是一个选择区域内容的碎片，用来显示省，市，先的数据
 */

public class ChooseAreaFragment extends Fragment {
    // 定义几个常量 来表示当前选中的级别
    public  static  final  int LEVEL_PROVINCE=0;
    public  static  final  int Level_CITY=1;
    public  static  final  int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;


    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private List <String>dataList=new ArrayList<>();
    private ArrayAdapter adapter;
    // 省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private  List<County> countyList;

    // 选中省
    private  Province selectedProvince;
    //  选中市
    private  City selectedCity;
    //选中当前的级别
    private int currentLevel;


    //动态加载碎片，并且找到控件实例
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        // 获取打气筒的服务把一个布局转换成一个view
        View view=inflater.inflate(R.layout.choose_area,container,false);

        // 找到控件实例
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        //  定义一个适配器
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        // 返回这个view
        return view;
    }
    // 在这里写Activity与碎片创建关联时候的逻辑，这里是点击碎片里面的ListView条目使用的
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //  设置listView条目的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 判断需要加载的数据
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if (currentLevel==Level_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }

            }
        });
        // 设置返回按钮的点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //判断需要加载的back按钮
            public void onClick(View v) {
                if(currentLevel==LEVEL_COUNTY){
                    queryCities();

                }else if (currentLevel==Level_CITY) {
                    queryProvinces();

                }

            }
        });
        queryProvinces();

    }
/*
* 第一个自定义方法查询全国所有省的数据，优先从数据库查询，如果没有查询到再去服务器查询
*
* */
    public void queryProvinces(){
        // 首先将头部的title设置成中国返回按钮隐藏起来，因为省级别不需要返回了
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        //调用LitePal查询接口来从数据库中读取省的数据
       provinceList =  DataSupport.findAll(Province.class);
        // 如果读取到了就显示在界面上，如果没有读取到就通过服务器接口组装一个请求地址 然后就调用一个从服务器查询数据的方法（自定义）
        if(provinceList.size()>0){
            dataList.clear();// 清除缓存
            //遍历 集合
            for(Province province : provinceList){
                //添加省名
                dataList.add(province.getProvinceName());
            }adapter.notifyDataSetChanged();//改变条目
             listView.setSelection(0);// 定位指定位置
            currentLevel=LEVEL_PROVINCE;

            //如果没有读取到就通过服务器接口组装一个请求地址 然后就调用一个从服务器查询数据的方法（自定义）
        }else {
            String address="http://guolin.tech/api/china";
            // 使用自定义方法
            queryFormServer(address,"province");

        }

    }


/*
* 第二个自定义方法查询省内所有市的数据，优先从数据库查询，如果没有查询到再去服务器查询
*
* */
    public void queryCities() {
        // 首先将头部的title设置成省名
        titleText.setText(selectedProvince.getProvinceName());
        // 设置返回按钮
        backButton.setVisibility(View.VISIBLE);
        //调用LitePal查询接口来从数据库中读取省的数据
        cityList = DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        // 如果读取到了就显示在界面上，如果没有读取到就通过服务器接口组装一个请求地址 然后就调用一个从服务器查询数据的方法（自定义）
        if (cityList.size() > 0) {
            dataList.clear();// 清除缓存
            //遍历 集合
            for (City city : cityList) {
                //添加省名
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();//改变条目
            listView.setSelection(0);// 定位指定位置
            currentLevel=Level_CITY;

            //如果没有读取到就通过服务器接口组装一个请求地址 然后就调用一个从服务器查询数据的方法（自定义）
        } else {
            int provinceCode=selectedProvince.getProvinceCode();// 省代号
            String address ="http://guolin.tech/api/china/"+provinceCode;
            // 自定义方法
            queryFormServer(address,"city");

        }

}


 /*
* 第三个自定义查询市内所有县的数据，优先从数据库查询，如果没有查询到再去服务器查询
*
* */
    public void queryCounties() {
        // 首先将头部的title设置成市名
        titleText.setText(selectedCity.getCityName());
        // 设置返回按钮
        backButton.setVisibility(View.VISIBLE);
        //调用LitePal查询接口来从数据库中读取省的数据
        countyList = DataSupport.where("cityid=?", String.valueOf(selectedCity.getId())).find(County.class);
        // 如果读取到了就显示在界面上，如果没有读取到就通过服务器接口组装一个请求地址 然后就调用一个从服务器查询数据的方法（自定义）
        if (countyList.size() > 0) {
            dataList.clear();// 清除缓存
            //遍历 集合
            for (County county : countyList) {
                //添加名
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();//改变条目
            listView.setSelection(0);// 定位指定位置
            currentLevel = LEVEL_COUNTY;

            //如果没有读取到就通过服务器接口组装一个请求地址 然后就调用一个从服务器查询数据的方法（自定义）
        } else {
            int provinceCode = selectedProvince.getProvinceCode();// 省代号
            int cityCode=selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode+"/"+cityCode;
            // 自定义方法
            queryFormServer(address, "county");

        }

    }



    /*
    * 自定义方法
    * 根据根据传入的地址和类型从服务器上查询省市县的数据
    *
    * */
    private void queryFormServer(String address,final String type){
        showProgressDialog();//显示进度
        // 调用HttpUtil.sendOkHttpRequest 向服务器发送请求
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            // 1：响应的数据回调请求的方法
            @Override
            public void onResponse(Call call, Response response) throws IOException {
               //1.1新建一个请求的主体
                String responseText= response.body().string();
                //1.2 请求结果
                  boolean result=false;

                //:2：在这里用调用Utility工具类的方法来解析和处理数据 储存到数据库中 并且在这里判断是省？市？县？的请求
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);

                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());

                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());

                }
                //3再次调用queryProvinces（自定义的） ，来从新载入数据,这就是更新UI了，用runOnUiThread 来实现从子线程到主线程的切换
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();//加载完成先关闭进度
                            if ("province".equals(type)){
                                queryProvinces();

                            }else if ("city".equals(type)){
                                queryCities();

                            }else if ("county".equals(type)){

                                queryCounties();
                            }


                        }
                    });

                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread 方法回到主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();// 关闭进度
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }


/**显示进度对话框
 *
 */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }


/*
* 关闭进度对话框
* */

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
