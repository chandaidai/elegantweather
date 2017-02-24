package com.elegantweather.com.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/2/22 0022.
 *
 * 这是接口返回Suggestion（生活指数）接口数据的对应实体类
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort {

        @SerializedName("txt")
        public String info;

    }

    public class CarWash {

        @SerializedName("txt")
        public String info;

    }

    public class Sport {

        @SerializedName("txt")
        public String info;

    }

}
