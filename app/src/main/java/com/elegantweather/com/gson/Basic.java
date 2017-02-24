package com.elegantweather.com.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/2/22 0022.
 *
 * 这是接口返回Basic（基本信息）接口数据的对应实体类
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }

}
