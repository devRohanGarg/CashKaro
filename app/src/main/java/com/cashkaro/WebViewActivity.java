package com.cashkaro;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cashkaro.utils.Utils;

import im.delight.android.webview.AdvancedWebView;

public class WebViewActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    private AdvancedWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mWebView.setListener(this, this);
        mWebView.loadUrl(getIntent().getExtras().getString("URL", "http://cashkaro.com/"));

        if (getSupportActionBar() != null)
            getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        Snackbar s = Snackbar.make(mWebView, "Loading the deal for you!", Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        TextView st = (TextView) s.getView().findViewById(android.support.design.R.id.snackbar_text);
        st.setTypeface(Utils.regularFont);
        s.show();
    }

    @Override
    public void onPageFinished(String url) {
        Snackbar s = Snackbar.make(mWebView, "Your deal is here!", Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        TextView st = (TextView) s.getView().findViewById(android.support.design.R.id.snackbar_text);
        st.setTypeface(Utils.regularFont);
        s.show();
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        Snackbar s = Snackbar.make(mWebView, "Oops! Something went wrong.", Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        TextView st = (TextView) s.getView().findViewById(android.support.design.R.id.snackbar_text);
        st.setTypeface(Utils.regularFont);
        s.show();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}
