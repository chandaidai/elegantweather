package com.elegantweather.com.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/2/19 0019.
 *
 * 这是数据库表结构对应的实体类之一 City（市）
 */

public class City extends DataSupport {
    // 定义ID
    private int id;
    // 定义市名
    private String cityName;
    // 定义市的代号
    private  int cityCode;
    // 定义当前的市 所属于省的ID
    private int provinceId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
