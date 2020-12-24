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
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
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
        wv.setWebViewClient(new PaymentWebView());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setSavePassword(false);
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

//        AndroidBridge ab = new AndroidBridge(wv,this);
//        wv.addJavascriptInterface(ab, "Android");

//        String url = "http://192.168.0.116:3000/totalpayment";
        String url = "https://ssltest.payjoa.co.kr/m/card/DaouCardMng.jsp";
        wv.postUrl(url,postData.getBytes());
//        wv.loadUrl(url);



    }

    private class PaymentWebView extends WebViewClient {
        private static final int DIALOG_ISP = 0;
        private static final int DIALOG_CARD_APP = 1;
        private static final String URL_SCHEME = "http://192.168.0.116:3000/payment";

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("MEME", "hi 페이 ");


            Intent intent = null;
            if (url.startsWith("intent://")) {
                try {
                    Context context = view.getContext();
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                    if (intent != null) {
                        view.stopLoading();

                        PackageManager packageManager = context.getPackageManager();
                        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        if (info != null) {
                            context.startActivity(intent);
                        } else {
                            String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                            view.loadUrl(fallbackUrl);
                        }
                        return true;
                    }
                } catch (URISyntaxException e) {
                    Log.e("MEME", "Can't resolve intent://", e);
                }
            } else {
                view.loadUrl(url);
                return false;
            }
            return true;

        }

        /**
         * 각각의 카드사에 해당하는 마켓 install 알림
         */
        private AlertDialog getCardInstallAlertDialog(final String coCardNm) {
            final Hashtable<String, String> cardNm = new Hashtable<>();
            cardNm.put("HYUNDAE", "현대 앱카드");
            cardNm.put("SAMSUNG", "삼성 앱카드");
            cardNm.put("LOTTE", "롯데 앱카드");
            cardNm.put("SHINHAN", "신한 앱카드");
            cardNm.put("KB", "국민 앱카드");
            cardNm.put("HANASK", "하나SK 통합안심클릭");
            //cardNm.put("SHINHAN_SMART",  "Smart 신한앱");

            final Hashtable<String, String> cardInstallUrl = new Hashtable<String, String>();
            cardInstallUrl.put("HYUNDAE", "market://details?id=com.hyundaicard.appcard");
            cardInstallUrl.put("SAMSUNG", "market://details?id=kr.co.samsungcard.mpocket");
            cardInstallUrl.put("LOTTE", "market://details?id=com.lotte.lottesmartpay");
            cardInstallUrl.put("SHINHAN", "market://details?id=com.shcard.smartpay");
            cardInstallUrl.put("KB", "market://details?id=com.kbcard.cxh.appcard");
            cardInstallUrl.put("HANASK", "market://details?id=com.ilk.visa3d");
            //cardInstallUrl.put("SHINHAN_SMART",  "market://details?id=com.shcard.smartpay");//여기 수정 필요!!2014.04.01

            return new AlertDialog.Builder(getApplicationContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("알림")
                    .setMessage(cardNm.get(coCardNm) + " 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.")
                    .setCancelable(false)
                    .setPositiveButton("설치", (dialog, which) -> {
                        String installUrl = cardInstallUrl.get(coCardNm);
                        Uri uri = Uri.parse(installUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        Log.d("MEME","<INIPAYMOBILE> Call : " + uri.toString());
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException anfe) {
//                            ToastHelper.showToastShort(cardNm.get(coCardNm) + "설치 url이 올바르지 않습니다");
                        }
                    })
                    .setNegativeButton("취소", (dialog, which) -> {
//                        ToastHelper.showToastShort("(-1)결제를 취소 하셨습니다.");
                        onBackPressed();
                    })
                    .create();
        }

        /**
         * id에 해당하는 다이어로그 생성 후 반환
         */
        private Dialog createDialog(int id, String cardName) {
            switch (id) {
                case DIALOG_ISP:
                    return new AlertDialog.Builder(getApplicationContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("알림")
                            .setMessage("모바일 ISP 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.")
                            .setPositiveButton("설치", (dialog, which) -> {
                                String ispUrl = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp";
                                wv.loadUrl(ispUrl);
                            })
                            .setNegativeButton("취소", (dialog, which) -> {
//                                ToastHelper.showToastShort("(-1)결제를 취소 하셨습니다.");
                                onBackPressed();
                            })
                            .create();

                case DIALOG_CARD_APP:
                    return getCardInstallAlertDialog(cardName);
            }
            return null;
        }


//            if (!url.startsWith("http://") && !url.startsWith("http://") && !url.startsWith("javascript:"))
//            {
//                Intent intent;
//
//                Uri uri = Uri.parse(view.getUrl());
//                intent = new Intent(MainActivity.this, PopUpActivity.class); // 새창을 여는 액티비티나, 팝업일때 이용하면 용이합니다.
//                intent.putExtra("url",uri);
//                startActivity(intent);
////
////                Uri uri = Uri.parse(intent.getDataString());
////                intent = new Intent(Intent.ACTION_VIEW, uri);
////
////                startActivity(intent);
//                }
//                return true;

    }




}