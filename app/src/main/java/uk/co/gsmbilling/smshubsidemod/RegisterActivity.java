package uk.co.gsmbilling.smshubsidemod;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import uk.co.gsmbilling.sms2uk.utils.ContextUtils;
import uk.co.gsmbilling.sms2uk.utils.ParseUtility;
import yalantis.com.sidemenu.sample.R;

/**
 * Created by Roman on 19.05.16.
 */
public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.register_input_name) EditText inputName;
    @BindView(R.id.register_input_surname)EditText inputSurname;
    @BindView(R.id.register_input_email)EditText inputEmail;
    @BindView(R.id.register_input_password)EditText inputPassword;
    @BindView(R.id.register_input_confirm_password)EditText inputConfirmPassword;
    @BindView(R.id.register_button)Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextUtils.isNetworkAvailable(this);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnFocusChange({R.id.register_input_name, R.id.register_input_surname, R.id.register_input_email,R.id.register_input_confirm_password,R.id.register_input_password})
    public void moveHint(EditText e){
        if (e.getText().toString().isEmpty()) {
            if (e.hasFocus())
                ObjectAnimator.ofFloat(e, "translationY", 0, 10).start();
            else
                ObjectAnimator.ofFloat(e, "translationY", 10, 0).start();

        }
    }

    @OnCheckedChanged(R.id.register_checkbox)
    public void checkbox(boolean checked){
        buttonRegister.setEnabled(checked);
    }

    @OnClick(R.id.register_terms_link)
    public void terms(){
        Intent intent = new Intent(RegisterActivity.this, TermsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.register_button)
    public void register(){
        if (validateName()&&validateSurname()&&validateEmail()&&validatePassword()){
            String name = inputName.getText().toString().trim();
            String surname = inputSurname.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            ParseUtility.register(buttonRegister, RegisterActivity.this, name, surname, email, password);
        }
    }

    @OnTextChanged(R.id.register_input_name)
    public void onNameChanged(){
        validateName();
    }
    @OnTextChanged(R.id.register_input_surname)
    public void onSurnameChanged(){
        validateSurname();
    }
    @OnTextChanged({R.id.register_input_password,R.id.register_input_confirm_password})
    public void onPasswordChanged(){
        validatePassword();
    }
    @OnTextChanged(R.id.register_input_email)
    public void onEmailChanged(){
        validateEmail();
    }
    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputName.setError(getString(R.string.register_error_empty));
            return false;
        } else {
        inputName.setError(null);
        return true;
        }
    }
    private boolean validateSurname(){
        if (inputSurname.getText().toString().trim().isEmpty()) {
            inputSurname.setError(getString(R.string.register_error_empty));
            return false;
        } else {
            inputSurname.setError(null);
            return true;
    }}
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
        String password = inputPassword.getText().toString();
        String confirm = inputConfirmPassword.getText().toString();
        if (password.trim().isEmpty()){
            inputPassword.setError(getString(R.string.register_error_empty));
            return false;
        } else if (confirm.trim().isEmpty()){
            inputConfirmPassword.setError(getString(R.string.register_error_empty));
            return false;
        } else if (!password.equals(confirm)) {
            inputConfirmPassword.setError(getString(R.string.register_error_password));
            return false;
        } else {
            inputConfirmPassword.setError(null);
            inputPassword.setError(null);
            return true;
    }}
}
