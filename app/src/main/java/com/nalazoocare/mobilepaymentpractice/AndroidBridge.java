package com.nalazoocare.mobilepaymentpractice;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Created by nalazoo.yeomeme@gmail.com on 2020-12-24
 */
public class AndroidBridge {


    final public Handler handler = new Handler();
    private WebView mAppView;
    private Context mContext;
    public AndroidBridge(WebView _mAppView, Context _mContext) {
        mAppView = _mAppView;
        mContext = _mContext;
    }

    @JavascriptInterface
    public void call(final String _msg){
        handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}