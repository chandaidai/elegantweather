package com.elegantweather.com.gson;

/**
 * Created by Administrator on 2017/2/22 0022.
 * 这是接口返回AQI（空气质量指数）接口数据的对应实体类
 */

public class AQI {
    public  AQICity city;
    public class AQICity{
        public String aqi;
        public  String pm25;

    }


}
