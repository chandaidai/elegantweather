package com.elegantweather.com.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/2/19 0019.
 *
 * 这是数据库表结构对应的实体类之一 Province （省）
 */

public class Province extends DataSupport {
    // 定义ID
    private int id;
    // 定义省名
    private String provinceName;
    // 定义省的代号
    private  int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
