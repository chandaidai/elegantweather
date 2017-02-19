package com.elegantweather.com.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/2/19 0019.
 *
 * 这是数据库表结构对应的实体类之一 County （县）
 */

public class County extends DataSupport {
    // 定义ID
    private int id;
    // 定义县名
    private String countyName;
    //对应县天气的ID
    private String weatherId;
    //对应县所属的市的ID
    private int cityID;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }
}
