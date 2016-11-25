package uk.co.gsmbilling.smshubsidemod;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.gsmbilling.sms2uk.utils.MailUtils;
import yalantis.com.sidemenu.sample.R;


public class ContactActivity extends AppCompatActivity {

    EditText inputName;
    EditText inputEmail;
    EditText inputFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);

        inputName = (EditText) findViewById(R.id.editName);
        inputEmail = (EditText) findViewById(R.id.editEmail);
        inputFeedback = (EditText) findViewById(R.id.editFeedback);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_exit);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactActivity.this, AboutActivity.class));
            }
        });
    }

    @OnClick(R.id.sendbutton)
    public void emailRizApps() {

        if (validateEmail() && validateName() && validateMessageField()) {
            String msg = String.format(
                    "From: %s \n" +
                            "Name: %s\n\n" +
                            "Message:\n%s",
                    inputEmail.getText().toString(),
                    inputName.getText().toString(),
                    inputFeedback.getText().toString());
            MailUtils.sendMail(
                    "test@gsmbilling.co.uk",
                    "SMSHUB user report, " + new SimpleDateFormat("dd-MM-yy HH:mm").format(new Date()),
                    inputName.getText().toString(),
                    msg,
                    this);
            Toast.makeText(ContactActivity.this, "Mail sent", Toast.LENGTH_SHORT).show();
            startActivity(getIntent());
            finish();
           // Log.i("test", "1");
        }

    }
    private boolean validateEmail(){
        String email = inputEmail.getText().toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputEmail.setError(getString(R.string.register_error_email));
            return false;
        } else {
            inputEmail.setError(null);
            return true;
        }}

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputName.setError(getString(R.string.register_error_empty));
            return false;
        } else {
            inputName.setError(null);
            return true;
        }
    }

    private boolean validateMessageField() {
        if (inputFeedback.getText().toString().trim().isEmpty()) {
            inputFeedback.setError(getString(R.string.register_error_empty));
            return false;
        } else {
            inputFeedback.setError(null);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
        finish();
    }
}