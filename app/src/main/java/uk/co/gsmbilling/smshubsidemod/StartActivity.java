package uk.co.gsmbilling.smshubsidemod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseInstallation;
import com.parse.ParsePush;

import uk.co.gsmbilling.sms2uk.utils.ContextUtils;
import uk.co.gsmbilling.sms2uk.utils.ParseUtility;

/**
 * Created by Roman on 21.05.16.
 */
public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextUtils.isNetworkAvailable(this)) {
            try {
                ParseUtility.initialize(this);
                ParseInstallation.getCurrentInstallation().saveInBackground();
                ParsePush.subscribeInBackground("smshubv2");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ParseUtility.getCurrentUser() == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
