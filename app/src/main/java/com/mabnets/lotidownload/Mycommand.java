package com.mabnets.lotidownload;

import android.content.Context;

import com.android.volley.Request;

import java.util.ArrayList;

public class Mycommand<T> {
    private ArrayList<Request<T>> RequestList=new ArrayList<>();
    private Context context;

    public Mycommand(Context context){this.context=context;}
    public void  add(Request<T> request){
            RequestList.add(request);
    }
    public  void remove(Request<T> request){
        RequestList.remove(request);
    }
    public void execute(){
        for(Request<T> request:RequestList){
            MySingleton.getInstance(context).addToRequestQueue(request);
        }
    }

}
