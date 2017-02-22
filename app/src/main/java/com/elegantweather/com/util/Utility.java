package com.elegantweather.com.util;

import android.text.TextUtils;

import com.elegantweather.com.db.City;
import com.elegantweather.com.db.County;
import com.elegantweather.com.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/2/19 0019.
 *
 * 这是一个解析和处理服务器返回的省市县数据的工具类，返回的数据是Json格式的
 */

public class Utility {
    /*定义一个方法来解析服务器返回的省Province级数据名字解释：操作省的响应
    *
    *
    */
    public static boolean handleProvinceResponse(String response){
    if (!TextUtils.isEmpty(response)){
        try {
            // 第一步 因为在服务器定义的是JSON数组 所有将服务器返回的数据传入到一个JSONArray 对象中
            JSONArray allProvinces  =new JSONArray(response);
            // 对这个对象进行遍历 从中取出的每一个元素都是一个JSONObject 对象
            for (int i=0;i<allProvinces.length();i++){
                // 取出所有的JSONObject 对象
                JSONObject provinceObject=allProvinces.getJSONObject(i);
                //创建省的对象
                Province province= new Province();
                //设置省的名字数据
                province.setProvinceName(provinceObject.getString("name"));
                //设置省的代号
                province.setProvinceCode(provinceObject.getInt("id"));
                //设置到数据库中
                province.save();
            }
            return  true;

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    return false;
    }

    /*定义一个方法来解析服务器返回的市City级数据名字解释：操作省的响应
    *
    *
    */
    public static boolean handleCityResponse(String response, int provinceId ){
        if (!TextUtils.isEmpty(response)){
            try {
                // 第一步 因为在服务器定义的是JSON数组 所有将服务器返回的数据传入到一个JSONArray 对象中
                JSONArray allCities  =new JSONArray(response);
                // 对这个对象进行遍历 从中取出的每一个元素都是一个JSONObject 对象
                for (int i=0;i<allCities.length();i++){
                    // 取出所有的JSONObject 对象
                    JSONObject cityObject=allCities.getJSONObject(i);
                    //创建省的对象
                    City city= new City();
                    //设置省的名字数据
                    city.setCityName(cityObject.getString("name"));
                    //设置省的代号
                    city.setCityCode(cityObject.getInt("id"));
                    //设置所在省的id
                    city.setProvinceId(provinceId);
                    //设置到数据库中
                    city.save();
                }
                return  true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }


    /*定义一个方法来解析服务器返回的县County级数据名字解释：操作省的响应
    *
    *
    */
    public static boolean handleCountyResponse(String response , int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                // 第一步 因为在服务器定义的是JSON数组 所有将服务器返回的数据传入到一个JSONArray 对象中
                JSONArray allCounties  =new JSONArray(response);
                // 对这个对象进行遍历 从中取出的每一个元素都是一个JSONObject 对象
                for (int i=0;i<allCounties.length();i++){
                    // 取出所有的JSONObject 对象
                    JSONObject countyObject=allCounties.getJSONObject(i);
                    //创建县的对象
                    County county= new County();
                    //设置县的名字数据
                    county.setCountyName(countyObject.getString("name"));
                    //设置所在市的id
                    county.setCityID(cityId);
                    //设置对应天气的ID
                    county.setWeatherId(countyObject.getString("weather_id"));
                    //设置到数据库中
                    county.save();
                }
                return  true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }


}
