package uk.co.gsmbilling.smshubsidemod;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import uk.co.gsmbilling.sms2uk.utils.ContextUtils;
import uk.co.gsmbilling.sms2uk.utils.ParseUtility;
import yalantis.com.sidemenu.sample.R;

/**
 * Created by Roman on 20.05.16.
 */
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_input_email)       EditText inputEmail;
    @BindView(R.id.login_input_password)    EditText inputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextUtils.isNetworkAvailable(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnFocusChange({R.id.login_input_password, R.id.login_input_email})
    public void moveHint(EditText e){
        if (e.getText().toString().isEmpty()) {
            if (e.hasFocus())
                ObjectAnimator.ofFloat(e, "translationY", 0, 10).start();
            else
                ObjectAnimator.ofFloat(e, "translationY", 10, 0).start();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick(R.id.login_register_button)
    public void register(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_sign_in_button)
    public void login(){
        if (validateEmail() && validatePassword()) {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            if (ParseUtility.login(findViewById(R.id.login_sign_in_button), LoginActivity.this, email, password)) {
                Intent intent = new Intent(LoginActivity.this, AboutActivity.class);
                startActivity(intent);
                finish();
            }

        } else {
            Toast.makeText(LoginActivity.this, "Wrong login or passwor", Toast.LENGTH_LONG).show();

        }

    }

    @OnClick(R.id.login_forgot_button)
    public void forgot(){
        View forgotView = getLayoutInflater().inflate(R.layout.forgot_password_dialog,null);

        if (validateEmail()) {
            new AlertDialog.Builder(LoginActivity.this)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ParseUtility.resetPassword(LoginActivity.this, inputEmail.getText().toString());
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setView(forgotView)
                    .show();
        }
    }

    @OnTextChanged(R.id.login_input_email)
    public void onEmailChanged(){
        validateEmail();
    }

    @OnTextChanged(R.id.login_input_password)
    public void onPasswordChanged(){
        validatePassword();
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
    private boolean validatePassword(){
        if (inputPassword.getText().toString().trim().isEmpty()){
            inputPassword.setError(getString(R.string.register_error_empty));
            return false;
        } else {
            inputPassword.setError(null);
            return true;
        }}

}
