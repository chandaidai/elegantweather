package com.elegantweather.com.util;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/2/19 0019.
 *
 * 这是一个使用HTTP协议访问网络的工具类
 */

public class HttpUtil {

    // 设置请求忘了的方法  里面接收两个参数，第一个是添加一个url，第二个是接收返回的数据
    public  static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        //1先new实例
        OkHttpClient client= new OkHttpClient();
        // 2创建一个请求对象  //2.1连缀方法 这里只连缀一个url
        Request request=new Request.Builder().url(address).build();
        //3调用newCall 的方法来创建一个Call的对象，并且调用它的Execute来发送请求并获取服务器返回的数据
        client.newCall(request).enqueue(callback);

    }
}
