package com.elegantweather.com.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/2/22 0022.
 *
 * 这是接口返回Now（实况天气）接口数据的对应实体类
 */


public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt")
        public String info;

    }

}
