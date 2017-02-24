package com.elegantweather.com.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * Created by Administrator on 2017/2/22 0022.
 *
 * 这是接口返回Weather（实例类的引用类）
 */
public class Weather {
    //  返回状态
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
