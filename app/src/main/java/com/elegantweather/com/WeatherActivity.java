package com.elegantweather.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.elegantweather.com.gson.Forecast;
import com.elegantweather.com.gson.Weather;
import com.elegantweather.com.service.AutoUpdateService;
import com.elegantweather.com.util.HttpUtil;
import com.elegantweather.com.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
       private String mWeatherId;
    private Button navButton;
    public DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置背景和状态栏融合的效果，5.0 以上才支持
        if(Build.VERSION.SDK_INT>=21){

            View decorView= getWindow().getDecorView();
            // 活动布局显示在状态栏上面
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            //状态栏设置透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);
        /*初始化各种控件*/
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        // 1首先是ScrollView 布局
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        //1.1 title城市名和更新时间
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        //1.2温度和 晴或阴
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        // 2再者是一个7天天气情况的布局
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        //生活指数
        aqiText = (TextView) findViewById(R.id.aqi_text);
        //pm25
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        //舒适度
        comfortText = (TextView) findViewById(R.id.comfort_text);
        //洗车指数
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        //运动
        sportText = (TextView) findViewById(R.id.sport_text);
        //  下拉刷新
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        // 设置下拉刷新颜色
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        // 找到滑动菜单的小房子 和滑动菜单的布局
        navButton = (Button) findViewById(R.id.nav_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);// 打开菜单
            }
        });




        /*  用SharedPreferences 取数据
        *
        * */


        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic",null);
        // 判断缓存中是否有图片
        if (bingPic !=null){
            Glide.with(this).load(bingPic).into(bingPicImg);


        }else {
            // 自己从服务器取图片
            loadBingPic();

        }
        String weatherString= prefs.getString("weather",null);
        if (weatherString !=null){
            // 说明有缓存，直接解析数据
            Weather weather= Utility.handleWeatherResponse(weatherString);
            // 定义一个weatherId 记录城市的天气Id
            mWeatherId=weather.basic.weatherId;
            // 并且显示出来 （显示方法还没创建）
            showWeatherInfo(weather);

        }else {
            // 没有缓存就去服务区查询 下拉刷新功能
            mWeatherId=getIntent().getStringExtra("weather_id");
            //String weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);// 滚动视图先隐藏
            // 请求服务区天气ID 的方法（等待创建）
            requestWeather(mWeatherId);

        }

        /*下拉刷新的逻辑*/

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

    }

    /*
    *
    * 加载必应的每日图片的方法
    *
    * */


    public void loadBingPic(){
        // 获取接口地址
        String requestBingPic="http://guolin.tech/api/bing_pic";
        // 设置请求主体
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final  String bingPic=response.body().string();
                //添加到缓存中
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 加载图片
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });

            }
        });


    }


    /*
    *  创建请求服务器天气信息的方法，要根据天气的id来
    *
    * */

    public  void requestWeather(final String weatherId){
        //拼出接口的地址  String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=7b75d169119247d0a5aa1865f99d7b55";
        //向这个地址发送请求
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 定义响应的主体 类型是String
                final String responseText=response.body().string();
                // 调用Utility.handleWeatherResponse() 将返回的JSON数据转换成 Weather对象
                final Weather weather=Utility.handleWeatherResponse(responseText);
                //切换到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 判断服务器返回状态是否OK
                        if (weather !=null && "ok".equals(weather.status)){
                            //说明返回成功，需要储存到缓存中SharedPreferences
                            SharedPreferences.Editor editor=
                             PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            // 显示出来天气信息（方法待创建）
                            showWeatherInfo(weather);

                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false); // 下拉刷新结束 并且隐藏
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false); // 下拉刷新结束 并且隐藏

                    }
                });
            }


        });
        loadBingPic();
    }


    /*
    * 创建显示天气信息Weather的数据的方法,从Weather中获取数据 显示在控件上
    *
    * */
    private void showWeatherInfo(Weather weather){
        // 获取weather 中实体类的数据
        String cityName= weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split("")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleUpdateTime.setText(updateTime);
        titleCity.setText(cityName);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_tiem,forecastLayout,false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }
        if (weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);

        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        /*启动服务*/
        if(weather !=null && "ok".equals(weather.status)){
            Intent intent =new Intent(this, AutoUpdateService.class);
            stopService(intent);

        }else {
            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
        }

    }




}

