package com.nalazoocare.mobilepaymentpractice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {


    private WebView wv;
    private RelativeLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wv = findViewById(R.id.wv);
        mContainer = findViewById(R.id.container);


        //추가
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        /* http://developer.android.com/about/versions/android-5.0-changes.html#BehaviorWebView */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // È¥ÇÕµÈ ÄÁÅÙÃ÷ Çã¿ë
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);										 // Cookie Çã¿ë
            cookieManager.setAcceptThirdPartyCookies(wv, true);
        }

        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("URL", url);
                if(url.startsWith("intent")){
                    return checkAppInstalled(view, url, "intent");
                }else if (url != null && url.startsWith("market://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            startActivity(intent);
                        }
                        return true;
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if(url.startsWith("http://") || url.startsWith("https://")) {
                    view.loadUrl(url);
                }
                else {
                    return checkAppInstalled(view, url , "customLink");
                }

                return true;
            }
        });

        try {
            clickBtnBuy();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    private void clickBtnBuy() throws UnsupportedEncodingException {

                String postData = "DAOUTRX="+ URLEncoder.encode("P","UTF-8")
                        + "&PAYMETHOD=" + URLEncoder.encode("CARD","UTF-8")
                        + "&CERTTYPE ="+ URLEncoder.encode("01","UTF-8")
                        + "&CPID=" +  URLEncoder.encode("CTS16654","UTF-8")
                        +"&ORDERNO=" + URLEncoder.encode("20201223111827","UTF-8")
                        + "&PRODUCTTYPE=" + URLEncoder.encode("2","UTF-8")
                        + "&AMOUNT=" + URLEncoder.encode("22","UTF-8")
                        +"&PRODUCTNAME=" + URLEncoder.encode("aaaaaaaaaa","UTF-8")
                        + "&PRODUCTCODE=" +  URLEncoder.encode("A001","UTF-8")
                        + "&USERID="+  URLEncoder.encode("tester","UTF-8")
                        + "&BILLTYPE=" + URLEncoder.encode("1","UTF-8")
                        + "&SETTDATE=" + URLEncoder.encode("202012240424")
                        + "&AUTHNO=" + URLEncoder.encode("111111")
                        + "&TAXFREECD=" + URLEncoder.encode("00");



//        HashMap<String, String> serverReq = new HashMap<>();
//        serverReq.put("TYPE", "P");
//        serverReq.put("PAYMETHOD", "CARD");
//        serverReq.put("CERTTYPE", "01");
//        serverReq.put("CPID", "CTS16654");
//        serverReq.put("ORDERNO", "20201223111827");
//        serverReq.put("PRODUCTTYPE", "2");
//        serverReq.put("AMOUNT", "1004");
//        serverReq.put("PRODUCTNAME", "테스트상품");
//        serverReq.put("PRODUCTCODE", "A001");
//        serverReq.put("USERID", "tester");
//        serverReq.put("BILLTYPE", "1");

//        String postData = String.valueOf(serverReq);
        caller(postData);
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    private void caller(String postData) {


        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onCloseWindow(WebView window) {
                mContainer.removeView(window);
                window.destroy();
                Log.d("MEME", "window close");
            }
        });

//        wv.setWebViewClient(new PaymentWebView());
//        wv.getSettings().setJavaScriptEnabled(true);
//        wv.getSettings().setSavePassword(false);
//        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        wv.setWebChromeClient(new WebChromeClient());
        String url = "https://ssltest.payjoa.co.kr/m/card_webview/DaouCardMng.jsp";
        wv.postUrl(url,postData.getBytes());

        //된더거
//        String url = "https://ssltest.payjoa.co.kr/m/card_webview/DaouCardMng.jsp";
//        wv.loadUrl(url);

//        AndroidBridge ab = new AndroidBridge(wv,this);
//        wv.addJavascriptInterface(ab, "Android");

//        String url = "http://192.168.0.116:3000/totalpayment";
//        String url = "https://ssltest.payjoa.co.kr/m/card/DaouCardMng.jsp";
//        wv.postUrl(url,postData.getBytes());
//        wv.loadUrl(url);



    }


    private boolean checkAppInstalled(WebView view , String url , String type){
        if(type.equals("intent")){
            return intentSchemeParser(view, url);
        } else if(type.equals("customLink")){
            return customSchemeParser(view, url);
        }
        return false;
    }

    private boolean intentSchemeParser(WebView view , String url) {
        boolean returnValue = false;
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            if(getPackageManager().resolveActivity(intent , 0) == null){
                String pakagename = intent.getPackage();
                if(pakagename != null) {
//                    if(url.contains("lotteappcard") || url.contains("cloudpay") || url.contains("citispayapp")
//                    		|| url.contains("hdcardappcardansimclick")){
                    Uri uri = Uri.parse("market://details?id="+pakagename);
                    intent = new Intent(Intent.ACTION_VIEW , uri);
                    //view.getContext().startActivity(intent);
                    startActivity(intent);
//                    }
                    return true;
                }
            }
            Uri uri = Uri.parse(intent.getDataString());
            intent = new Intent(Intent.ACTION_VIEW , uri);
            //view.getContext().startActivity(intent);
            //((Activity) view.getContext()).startActivity(intent);
            startActivity(intent);
            return true;

        } catch (URISyntaxException e) {
            //e.printStackTrace();
        }
        return false;
    }

    private boolean customSchemeParser(WebView view , String url) {
        String packageName = null;
        if(url.startsWith("shinhan-sr-ansimclick://")) {        //½ÅÇÑ ¾ÛÄ«µå
            packageName = "com.shcard.smartpay";
        }else if(url.startsWith("mpocket.online.ansimclick://")) {  //»ï¼º¾ÛÄ«µå
            packageName = "kr.co.samsungcard.mpocket";
        } else if(url.startsWith("hdcardappcardansimclick://")) {       //Çö´ë¾È½É°áÁ¦
            packageName = "com.hyundaicard.appcard";
        } else if(url.startsWith("droidxantivirusweb:")){               //droidx ¹é½Å
            packageName = "net.nshc.droidxantivirus";
        } else if(url.startsWith("vguardstart://") || url.startsWith("vguardend://")){  //vguard¹é½Å
            packageName = "kr.co.shiftworks.vguardweb";
        } else if(url.startsWith("hanaansim")){         //ÇÏ³ª¿ÜÈ¯¾ÛÄ«µå
            packageName = "com.ilk.visa3d";
        } else if(url.startsWith("nhappcardansimclick://")) { //³óÇù¾ÛÄ«µå
            packageName = "nh.smart.mobilecard";
        } else if(url.startsWith("ahnlabv3mobileplus")){
            packageName = "com.ahnlab.v3mobileplus";
        } else if(url.startsWith("smartxpay-transfer://")){
            packageName = "kr.co.uplus.ecredit";
        }
        else {
            return false;
        }

        Intent intent = null;
        //ÇÏµåÄÚµùµÈ ÆÐÅ°Áö¸íÀ¸·Î ¾Û ¼³Ä¡¿©ºÎ¸¦ ÆÇ´ÜÇÏ¿© ÇØ´ç ¾Û ½ÇÇà ¶Ç´Â ¸¶ÄÏ ÀÌµ¿
        if(chkAppInstalled(view,packageName)){

            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                Uri uri = Uri.parse(intent.getDataString());
                intent = new Intent(Intent.ACTION_VIEW , uri);
//                view.getContext().startActivity(intent);
                startActivity(intent);
                return true;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
           /* intent = view.getContext().getPackageManager().getLaunchIntentForPackage(packagePath);
            view.getContext().startActivity(intent);*/
        } else {
            Uri uri = Uri.parse("market://details?id="+packageName);
            intent = new Intent(Intent.ACTION_VIEW , uri);
//            view.getContext().startActivity(intent);
            startActivity(intent);
            return true;
        }

        return false;
    }


    private boolean chkAppInstalled(WebView view , String packagePath){
        boolean appInstalled = false;
        try {
//            view.getContext().getPackageManager().getPackageInfo(packagePath, PackageManager.GET_ACTIVITIES);
            getPackageManager().getPackageInfo(packagePath, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch(PackageManager.NameNotFoundException e){
            appInstalled = false;
        }
        return appInstalled;
    }

    private class PaymentWebView extends WebViewClient {
        private static final int DIALOG_ISP = 0;
        private static final int DIALOG_CARD_APP = 1;
        private static final String URL_SCHEME = "http://192.168.0.116:3000/payment";

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("MEME", "hi 페이 ");

            // TODO Auto-generated method stub
            Log.d("URL", url);
            if(url.startsWith("intent")){
                return checkAppInstalled(view, url, "intent");
            }else if (url != null && url.startsWith("market://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null) {
                        startActivity(intent);
                    }
                    return true;
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if(url.startsWith("http://") || url.startsWith("https://")) {
                view.loadUrl(url);
            }
            else {
                return checkAppInstalled(view, url , "customLink");
            }
            return true;
        }
    }
}