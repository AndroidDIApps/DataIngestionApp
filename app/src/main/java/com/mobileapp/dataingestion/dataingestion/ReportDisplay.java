package com.mobileapp.dataingestion.dataingestion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ReportDisplay extends Activity {
    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_display);
        this.webView= (WebView) findViewById(R.id.webReport);
        try{
            if(!AppData.isOnline(ReportDisplay.this))
            {
                Toast.makeText(ReportDisplay.this,AppData.NOINTERNETMESSAGE,Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = getIntent();
            String reportLink = intent.getExtras().getString("ReportLink");
            if(reportLink!=null && reportLink!="") {
                startWebView(reportLink);
            }
            else
            {
                Toast.makeText(ReportDisplay.this,AppData.INVALIDREPORTLINK,Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(ReportDisplay.this,AppData.GENERICMESSAGE,Toast.LENGTH_SHORT).show();
        }
    }

    private void startWebView(String url) {
        //Create new webview Client to show progress dialog
        //When opening a url or click on link
        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;
            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(ReportDisplay.this);
                    progressDialog.setMessage("Loading Please wait...");
                    progressDialog.show();
                }
            }
            public void onPageFinished(WebView view, String url) {
                try{
                    if (progressDialog!=null ||progressDialog.isShowing()) {
                        progressDialog.dismiss();
                       // progressDialog = null;
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });
        // Javascript enabled on webview
        webView.getSettings().setJavaScriptEnabled(true);
        // Other webview options

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);

        webView.getSettings().setBuiltInZoomControls(true);

        //Load url in webview
        webView.loadUrl(url);
    }

    // Open previous opened link from history on webview when back button pressed

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }

}



