package uk.co.gsmbilling.smshubsidemod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import uk.co.gsmbilling.sms2uk.utils.ContextUtils;
import yalantis.com.sidemenu.sample.R;

public class TermsActivity extends AppCompatActivity {

    WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextUtils.isNetworkAvailable(this);
        setContentView(R.layout.activity_terms);

        Toolbar toolbar = (Toolbar) findViewById(R.id.terms_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.loadUrl("file:///android_res/raw/terms.html");


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MessageListActivity.class));
        finish();
    }
}
