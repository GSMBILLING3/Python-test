package uk.co.gsmbilling.smshubsidemod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import uk.co.gsmbilling.sms2uk.utils.ApiUtility;
import uk.co.gsmbilling.sms2uk.utils.ContextUtils;
import uk.co.gsmbilling.sms2uk.utils.ParseUtility;
import yalantis.com.sidemenu.sample.R;

/**
 * Created by GSM-1 on 2016-07-26.
 */
public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "payment";
    String number = "Hi roman";


    FloatingActionButton fab_pay1, fab_pay5, fab_pay10, fab_pay15, fab_action;
    Animation FabOpen, FabClose, FabL360, FabR360;
    private Button numberButton;
    boolean isOpen = false;
    private String didMessage;

    private static final int REQUEST_CODE_PAYMENT1 = 1;
    private static final int REQUEST_CODE_PAYMENT5 = 5;
    private static final int REQUEST_CODE_PAYMENT10 = 10;
    private static final int REQUEST_CODE_PAYMENT15 = 15;
    private static volatile int moneyCount = 0;
    private String infoMessage = "Not assigned";

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId("VyO9Xx-X9DTQ42SJu4mFVDwLpC6WpWf_01q3GBxBurSjjrx9iLcWqBmysW7mhzQzhsn2nPpJTBCXwjO")
            .acceptCreditCards(true)
            .languageOrLocale("EN")
            .rememberUser(true)
            .merchantName("GSM Billing");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextUtils.isNetworkAvailable(this);
        setContentView(R.layout.activity_account);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_exit);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this, AboutActivity.class));
            }
        });

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        fab_action = (FloatingActionButton) findViewById(R.id.fab_action);
        fab_pay1 = (FloatingActionButton) findViewById(R.id.fab_pay1);
        fab_pay5 = (FloatingActionButton) findViewById(R.id.fab_pay5);
        fab_pay10 = (FloatingActionButton) findViewById(R.id.fab_pay10);
        fab_pay15 = (FloatingActionButton) findViewById(R.id.fab_pay15);

        fab_pay1.setOnClickListener(this);
        fab_pay5.setOnClickListener(this);
        fab_pay10.setOnClickListener(this);
        fab_pay15.setOnClickListener(this);


        setNumberSection();
        setAnimationBtn();
    }


    private void setNumberSection() {
        numberButton = (Button) findViewById(R.id.number_button);
        TextView numberTitle = (TextView) findViewById(R.id.account_number_title);
        TextView numberLabel = (TextView) findViewById(R.id.account_number_label);
        if (ParseUtility.getCurrentUserNumber() == null || ParseUtility.getCurrentUserNumber().isEmpty()) {
            numberTitle.setVisibility(View.GONE);
            numberLabel.setText("You don't have a virtual number yet.");
            numberButton.setText("Buy a number for £1");
            numberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertBuilderDID = new AlertDialog.Builder(AccountActivity.this)
                            .setView(R.layout.buy_number_dialog_view)
                            .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final String status = ApiUtility.assignDid("44", ParseUtility.getCurrentUser().getObjectId());
                                            if (status.contains("SHORTCODE")) {
                                                ParseUtility.getCurrentUser().fetchInBackground();

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            JSONObject json = new JSONObject(status);
                                                            Toast.makeText(AccountActivity.this, "Your new number is " + json.getString("SHORTCODE"), Toast.LENGTH_SHORT).show();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });

                                                startActivity(new Intent(AccountActivity.this, MessageListActivity.class));
                                            } else if (status.contains("Insufficient balance")) {
                                                AccountActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AccountActivity.this)
                                                                .setTitle("Low balance")
                                                                .setMessage("You don't have enough balance. Do You want to top-up your account?")
                                                                .setNegativeButton("Top-up later", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                        Toast.makeText(AccountActivity.this, "You don't have enough credit to do this.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .setPositiveButton("Top-up now", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                        startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                                                                        finish();
                                                                    }
                                                                });
                                                        dialogBuilder.create();
                                                        dialogBuilder.show();
                                                    }
                                                });
                                            } else {
                                                dialog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                     //   Log.i("acc", status);
                                                        Toast.makeText(AccountActivity.this, status, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                        }
                                    }).start();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertBuilderDID.create().show();

                }
            });
        } else {
            numberTitle.setVisibility(View.VISIBLE);
            numberTitle.setText("Your virtual number:");
            numberLabel.setText(String.valueOf(ParseUtility.getCurrentUserNumber()));
            numberButton.setText("Deactivate number");
            numberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ParseUtility.getCurrentUserNumber() == null) {
                        Toast.makeText(AccountActivity.this, "You have no number. Please top-up", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder alertBuilderDID = new AlertDialog.Builder(AccountActivity.this)
                                .setView(R.layout.delete_number_dialog_view)
                                .setPositiveButton("Deactivate", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String status = ApiUtility.deactivateDid(ParseUtility.getCurrentUserNumber());
                                               // Log.i("LEG", "I am in runnable");
                                                if (status.contains("Success")) {
                                                 //   Log.i("LEG", "status.contains(\"Success\")");
                                                    didMessage = "Number was deactivated";
                                                 //   Log.i("LEG", "status: " + didMessage);
                                                    ParseUtility.getCurrentUser().fetchInBackground();
                                                } else {
                                                    didMessage = "Can't deactivate number, please try again";
                                                }
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(AccountActivity.this, didMessage, Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(AccountActivity.this, MessageListActivity.class));
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertBuilderDID.create().show();
                    }
                }

            });
        }
    }

    private void setAnimationBtn() {
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        FabL360 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_left360);
        FabR360 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_right360);

        fab_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOpen) {
                    fab_action.startAnimation(FabR360);

                    fab_pay1.startAnimation(FabClose);
                    fab_pay5.startAnimation(FabClose);
                    fab_pay10.startAnimation(FabClose);
                    fab_pay15.startAnimation(FabClose);

                    fab_pay1.setClickable(false);
                    fab_pay5.setClickable(false);
                    fab_pay10.setClickable(false);
                    fab_pay15.setClickable(false);

                    isOpen = false;
                } else {
                    fab_action.startAnimation(FabL360);

                    fab_pay1.startAnimation(FabOpen);
                    fab_pay5.startAnimation(FabOpen);
                    fab_pay10.startAnimation(FabOpen);
                    fab_pay15.startAnimation(FabOpen);

                    fab_pay1.setClickable(true);
                    fab_pay5.setClickable(true);
                    fab_pay10.setClickable(true);
                    fab_pay15.setClickable(true);

                    isOpen = true;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        PayPalPayment putMoney = null;
        int REQUEST_CODE_PAYMENT_TEMP = 0;

        switch (v.getId()) {
            case R.id.fab_pay1:
                putMoney = new PayPalPayment(new BigDecimal("1.00"), "GBP",
                        "Top on for SMS Hub, virtual number" + ParseUtility.getCurrentUserNumber(),
                        PayPalPayment.PAYMENT_INTENT_SALE);
                REQUEST_CODE_PAYMENT_TEMP = REQUEST_CODE_PAYMENT1;
                break;
            case R.id.fab_pay5:
                putMoney = new PayPalPayment(new BigDecimal("5.00"), "GBP",
                        "Top on for SMS Hub, virtual number" + ParseUtility.getCurrentUserNumber(),
                        PayPalPayment.PAYMENT_INTENT_SALE);
                REQUEST_CODE_PAYMENT_TEMP = REQUEST_CODE_PAYMENT5;
                break;
            case R.id.fab_pay10:
                putMoney = new PayPalPayment(new BigDecimal("10.00"), "GBP",
                        "Top on for SMS Hub, virtual number" + ParseUtility.getCurrentUserNumber(),
                        PayPalPayment.PAYMENT_INTENT_SALE);
                REQUEST_CODE_PAYMENT_TEMP = REQUEST_CODE_PAYMENT10;
                break;
            case R.id.fab_pay15:
                putMoney = new PayPalPayment(new BigDecimal("15.00"), "GBP",
                        "Top on for SMS Hub, virtual number" + ParseUtility.getCurrentUserNumber(),
                        PayPalPayment.PAYMENT_INTENT_SALE);
                REQUEST_CODE_PAYMENT_TEMP = REQUEST_CODE_PAYMENT15;
                break;
        }

        if (putMoney != null && REQUEST_CODE_PAYMENT_TEMP != 0) {
            Intent intent = new Intent(AccountActivity.this, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, putMoney);
            startActivityForResult(intent, REQUEST_CODE_PAYMENT_TEMP);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");

        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");

        } else if (resultCode == Activity.RESULT_OK) {

            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("payment", confirm.toJSONObject().toString(4));
                } catch (JSONException e) {
                    Log.e("payment", "an extremely unlikely failure occurred: ", e);
                }

                  /*  ParseUser user = ParseUser.getCurrentUser();
                    user.put("balance", ParseUtility.getBalance() + moneyCount);
                    if (!ApiUtility.assignDid("44", user.getObjectId()).equals("Already assigned")){
                        Toast.makeText(AccountActivity.this, "Congratulations! You have new number", Toast.LENGTH_LONG).show();

                    }else{
                        if (ApiUtility.renewDid(user.getString("virtualNumber"),user.getObjectId()).equals("Success")){
                            Toast.makeText(AccountActivity.this, "Number balance is updated", Toast.LENGTH_LONG).show();
                        }else{

                        }
                    }
                    user.saveInBackground();*/
                final ProgressDialog progressDialog = new ProgressDialog(AccountActivity.this);
                progressDialog.setTitle("Balance update");
                progressDialog.setMessage("Updating....Wait for it");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        switch (requestCode) {

                            case REQUEST_CODE_PAYMENT1:
                                moneyCount = 1;
                                break;
                            case REQUEST_CODE_PAYMENT5:
                                moneyCount = 5;
                                break;
                            case REQUEST_CODE_PAYMENT10:
                                moneyCount = 10;
                                break;
                            case REQUEST_CODE_PAYMENT15:
                                moneyCount = 15;
                                break;
                        }
                        ParseUser user = ParseUser.getCurrentUser();
                        user.put("balance", ParseUtility.getBalance() + moneyCount);
                        String accstatus = ApiUtility.assignDid("44", user.getObjectId());
                        try {
                            JSONObject json = new JSONObject(accstatus);
                            number = json.getString("SHORTCODE");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("payment", accstatus);
                        Log.i("payment", accstatus.substring(12, accstatus.length() - 3));
                        if (!accstatus.substring(12, accstatus.length() - 3).equals("Already assigned")) {
                            //Toast.makeText(AccountActivity.this, "Congratulations! You have new number", Toast.LENGTH_LONG).show();
                            infoMessage = "Congratulations! You have new number";
                            Log.e("payment", "Congratulations! You have new number");

                        } else {
                            accstatus = ApiUtility.renewDid(user.getString("virtualNumber"), user.getObjectId());
                            Log.i("payment", accstatus);
                            Log.i("payment", accstatus.substring(12, accstatus.length() - 3));
                            if (accstatus.substring(12, accstatus.length() - 3).equals("Success")) {
                                //Toast.makeText(AccountActivity.this, "Number balance is updated", Toast.LENGTH_LONG).show();
                                infoMessage = "Number balance is updated.";
                                Log.i("payment", "Number balance is updated");
                            } else {
                                infoMessage = "Error on server side. Please connect with administrator";
                            }
                        }
                        user.put("virtualNumber", number);
                        user.saveInBackground();
                        progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               /* Toast.makeText(AccountActivity.this, infoMessage, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(AccountActivity.this, MessageListActivity.class));
                                finish();*/
                                AlertDialog.Builder alerDialog = new AlertDialog.Builder(AccountActivity.this)
                                        .setTitle("Update status")
                                        .setMessage(infoMessage);
                                alerDialog.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(AccountActivity.this, MessageListActivity.class));
                                        finish();
                                    }
                                });
                                alerDialog.setNegativeButton("Top-up more", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alerDialog.create();
                                alerDialog.show();

                            }
                        });
                    }
                }).start();
            } else {
                Toast.makeText(AccountActivity.this, "Problems with PayPal. Please try again", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
        finish();
    }

    public void Tutorial(View pressed) {
        startActivity(new Intent(this, Tutorial_Test_Activity.class));
    }



}