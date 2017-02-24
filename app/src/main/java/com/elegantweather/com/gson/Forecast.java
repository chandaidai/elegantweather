package com.elegantweather.com.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/2/22 0022.
 *
 * 这是接口返回Forecast（7-10天天气预测）接口数据的对应实体类
 */

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {

        public String max;

        public String min;

    }

    public class More {

        @SerializedName("txt_d")
        public String info;

    }

}
