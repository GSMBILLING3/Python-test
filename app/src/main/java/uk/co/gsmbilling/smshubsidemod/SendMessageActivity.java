package uk.co.gsmbilling.smshubsidemod;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kdotj.menu.FabMenu;
import com.kdotj.menu.FabMenuItem;

import java.util.ArrayList;

import uk.co.gsmbilling.sms2uk.utils.ApiUtility;
import uk.co.gsmbilling.sms2uk.utils.ContextUtils;
import uk.co.gsmbilling.sms2uk.utils.ParseUtility;
import yalantis.com.sidemenu.sample.R;

public class SendMessageActivity extends AppCompatActivity
        implements FabMenuItem.Callback{

    private static final String SHOWCASE_ID = "ShowcaseNewMessage";

    FabMenu fabMenu;


    private final int PICK_CONTACT = 1945;
    private final String TAG = "SendMessageActivity";
    private final String ALERT_TAG = "Show";
    private String responseCodeFromServer;
    private ArrayList<String> spinnerCountryCodesList;
    private SharedPreferences sharedPreferences;

    private String recepientNumber = "";
    private int countryCode = 44;
    public String userFriendNumber;
    private static String rememberTheMsg = "";
    private static boolean showDialogNoNumber = true;

    private ImageButton contactSelectBtn;
    private ImageButton sendMessageBtn;
    private ImageButton addNumberBtn;
    private EditText messageTextField;
    private EditText editTextDialogUserInput;
    private TextView recieverNumberTextField,charsCountTextField,noMessageHistoryText,sendToTitleToolbar, buyNumberLink, sendMessageAct_messageTitle, YourTelNumber, YourBalanceCount;
    private DrawerLayout drawer;
    private Toolbar supportToolbar;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
   // private Spinner countryCodeSpinner;
    private boolean hasHistory = false;
    private RecyclerView recyclerView;
    private BroadcastReceiver sendMsgsBroadcast;
    private LinearLayout sendToContentHolder;
    private CheckBox noNumberCheckbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextUtils.isNetworkAvailable(this);
        setContentView(R.layout.content_send_message);
        sharedPreferences = getSharedPreferences("uk.co.gsmSaves", MODE_PRIVATE);


        supportToolbar = (Toolbar) findViewById(R.id.sendActToolbar);
        setSupportActionBar(supportToolbar);

        sendToTitleToolbar = (TextView) findViewById(R.id.sendToTitleToolbar);

       // Log.i("LIST", "Jestem w OnCreate");
        sendMsgsBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String action = intent.getStringExtra("BCRSend");
                   // Log.i("Alive", "onReceive.action = " + action);
                    if (action.equals("SMS")) {
                        refreshMsgList();
                    }
                }
            }
        };

        //---------------------Views-----------------
        messageTextField = (EditText) findViewById(R.id.sendMessageAct_editMessageText);
        messageTextField.requestFocus();

        recieverNumberTextField = (TextView) findViewById(R.id.sendMessageAct_recieverTelNumber);
        charsCountTextField = (TextView) findViewById(R.id.sendMessageAct_charsCounter);
        contactSelectBtn = (ImageButton) findViewById(R.id.sendMessageAct_contactSelectBtn);
        sendMessageBtn = (ImageButton) findViewById(R.id.sendMessageAct_sendBtn);
        sendMessageBtn.setEnabled(false);
        addNumberBtn = (ImageButton) findViewById(R.id.sendMessageAct_addContactBtn);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        noMessageHistoryText = (TextView) findViewById(R.id.noMessageHistoryText);
        sendMessageAct_messageTitle = (TextView) findViewById(R.id.sendMessageAct_messageTitle);
        recyclerView = (RecyclerView) findViewById(R.id.messagesHistoryListView);
        sendToContentHolder = (LinearLayout) findViewById(R.id.sendToContentHolder);
        //------------------------Message History List-------------------
        spinnerCountryCodesList = ParseUtility.countryCodesList;


        Intent userIntentChecker = getIntent();
        if (userIntentChecker != null) {
            boolean isRefreshedtemp = true;
            userFriendNumber = userIntentChecker.getStringExtra("Number");
            String mesgFromForward = userIntentChecker.getStringExtra("currentMsg");

            if (userIntentChecker.hasExtra("isRefreshed")) {
                isRefreshedtemp = userIntentChecker.getBooleanExtra("isRefreshed", true);
            }
            if (userFriendNumber != null) {
                checkForNumberHistory(userFriendNumber, isRefreshedtemp);
            }
            if (mesgFromForward != null) {
                messageTextField.setText(mesgFromForward);
            }
        }
        //---------------------Message Field Watcher----------------

        final TextWatcher darkTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String textCounter = String.valueOf(s.length()) + " / 160";
                charsCountTextField.setText(textCounter);
                if (s.length() >= 2 && !s.equals("")) {
                    sendMessageBtn.setEnabled(true);

                } else {
                    sendMessageBtn.setEnabled(false);
                }
                if (s.length() > 160) {

                    messageTextField.setError("Sorry, maximum number of characters is 160");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        messageTextField.addTextChangedListener(darkTextWatcher);
        messageTextField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(160)});

        //---------------------Send Button Action----------------

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.contains(ALERT_TAG)) {
                    showDialogNoNumber = sharedPreferences.getBoolean(ALERT_TAG, false);
                }
           //     Log.i(ALERT_TAG, showDialogNoNumber + "");
                if (ParseUtility.getCurrentUserNumber() == null || ParseUtility.getCurrentUserNumber().equals("")) {
                    if (showDialogNoNumber) {
                        View noNumberAlertDView = getLayoutInflater().inflate(R.layout.send_msg_dialog_view, null);
                        noNumberCheckbox = (CheckBox) noNumberAlertDView.findViewById(R.id.noNumberCheckbox);
                        buyNumberLink = (TextView) noNumberAlertDView.findViewById(R.id.buyNumberLink);
                        buyNumberLink.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (noNumberCheckbox.isChecked()) {
                                    sharedPreferences.edit().putBoolean(ALERT_TAG, false).apply();
                                }
                                startActivity(new Intent(SendMessageActivity.this, AccountActivity.class));
                                finish();
                            }
                        });
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SendMessageActivity.this)
                                .setView(noNumberAlertDView)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (noNumberCheckbox.isChecked()) {
                                            sharedPreferences.edit().putBoolean(ALERT_TAG, false).apply();
                                        }
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (noNumberCheckbox.isChecked()) {
                                            sharedPreferences.edit().putBoolean(ALERT_TAG, false).apply();
                                        }
                                        dialog.dismiss();
                                        sendMessage();
                                    }
                                });
                        dialogBuilder.create();
                        dialogBuilder.show();
                    }
                } else {
                    //Log.i("NUMB", ParseUtility.getCurrentUserNumber());
                    sendMessage();
                }
            }
        });

        //---------------------Contact Button Action----------------

        contactSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactIntent, PICK_CONTACT);
               // Log.i("CONT", "I am in contacts");
            }
        });

        //--------------------Add number to send message-----------

        addNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder inputNumberDialogBuilder = new android.app.AlertDialog.Builder(SendMessageActivity.this);

                LayoutInflater layoutInflater = LayoutInflater.from(SendMessageActivity.this);
                View view = layoutInflater.inflate(R.layout.input_number_dialog_view, null);
                inputNumberDialogBuilder.setView(view);
                editTextDialogUserInput = (EditText) view.findViewById(R.id.editTextDialogUserInput);


                final TextWatcher inputNumberFieldWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.equals("")) {
                            editTextDialogUserInput.setError("Please enter the number and press 'OK'");
                        }
                        if (s.length() < 9) {
                            editTextDialogUserInput.setError("Please, enter correct number");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };
                editTextDialogUserInput.addTextChangedListener(inputNumberFieldWatcher);

                inputNumberDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String inputNumberStr = editTextDialogUserInput.getText().toString().trim();

                        if (!inputNumberStr.equals("") && inputNumberStr.length() <= 12 && inputNumberStr.length() >= 6) {
                            final String inputNumber = inputNumberStr;
                            recieverNumberTextField.setText(inputNumber);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if(ParseUtility.numberIsInDatabase(inputNumber)){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                sendMessageAct_messageTitle.setText(R.string.your_free_message);
                                            }
                                        });
                                    }
                                }
                            }).start();

                            if (checkIfRecipientExists(inputNumber))
                                checkForNumberHistory(String.valueOf(44) + inputNumber, false);
                            String name = ContextUtils.checkForName(SendMessageActivity.this, inputNumber);
                            if (name != null)
                                sendToTitleToolbar.setText("Send to: " + name);
                                if (inputNumber.substring(0,2).equals("44") && inputNumber.length() == 12){
                                    checkForNumberHistory(inputNumber, false);
                                }else if (inputNumber.charAt(0)=='0'){
                                    checkForNumberHistory(String.valueOf(44) + inputNumber.substring(1), false);
                                }else {
                                    checkForNumberHistory(String.valueOf(44) + inputNumber, false);
                                }
                        } else {
                            Toast.makeText(getApplicationContext(), "You entered incorrect number", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                inputNumberDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                android.app.AlertDialog inputNumberAlertDialog = inputNumberDialogBuilder.create();
                inputNumberAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                inputNumberAlertDialog.show();

            }
        });
    }

    public void onItemClicked(FabMenuItem fabMenuItem) {
        if(fabMenuItem.getItemId() == R.id.action_menu_1){
            Intent intent = new Intent(this, SendMessageActivity.class);
            startActivity(intent);
        }else if(fabMenuItem.getItemId() == R.id.action_menu_2){
            Intent intent = new Intent(this, MessageListActivity.class);
            startActivity(intent);
        } else if(fabMenuItem.getItemId() == R.id.action_menu_3){
            Intent intent = new Intent(this, OutListActivity.class);
            startActivity(intent);
        }else if(fabMenuItem.getItemId() == R.id.action_menu_4){
            View logoutView = getLayoutInflater().inflate(R.layout.logout_dialog_view, null);
            new AlertDialog.Builder(SendMessageActivity.this)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ParseUtility.logout();
                            Toast.makeText(SendMessageActivity.this, R.string.logged_out, Toast.LENGTH_SHORT).show();
                            //   Intent intent = new Intent(AboutActivity.this, LoginActivity.class);
                            //   startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setView(logoutView)
                    .show();

        } else if(fabMenuItem.getItemId() == R.id.action_menu_5){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else{
            Toast.makeText(this, "NO action detected", Toast.LENGTH_SHORT).show();
            fabMenu.toggleVisibility();
        }
    }


    private void sendMessage() {
        if (hasHistory) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SendMessageActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendMessageBtn.setEnabled(false);
                        }
                    });
                    String currentMsg = messageTextField.getText().toString();

                    if (countryCode != -1) {
                        SendMessageActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageTextField.setText("");
                            }
                        });
                        responseCodeFromServer = ApiUtility.sendMessage(recepientNumber, countryCode, currentMsg);
                    } else {
                       // Log.i(TAG, "run: country code invalid");
                    }
                    switch (responseCodeFromServer) {
                        case "SUCCESS":
                            SendMessageActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageTextField.setText("");
                                }
                            });
                            refreshMsgList();
                            break;
                        case "Error404":
                            break;
                        case "Not found":
                            break;
                        case "NO CREDITS":
                            SendMessageActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SendMessageActivity.this)
                                            .setTitle("Low balance")
                                            .setMessage("You have 0 balance. Do You want to top-up your account?")
                                            .setNegativeButton("Top-up later", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    messageTextField.setText(rememberTheMsg);
                                                    Toast.makeText(SendMessageActivity.this, "Message send request fail: 0 balance", Toast.LENGTH_SHORT).show();
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

                            break;
                    }
                    SendMessageActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendMessageBtn.setEnabled(true);
                        }
                    });
                }
            }).start();

        } else if (!recieverNumberTextField.getText().toString().equals("") || !recieverNumberTextField.getText().toString().equals("No number")) {

            recepientNumber = ParseUtility.checkNumberForSigns(recieverNumberTextField.getText().toString());
            if (recepientNumber.equals("9999999999")) {
                Toast.makeText(getApplicationContext(), "Please add number", Toast.LENGTH_LONG).show();
            } else {
                //String currentMsg = messageTextField.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SendMessageActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sendMessageBtn.setEnabled(false);
                            }
                        });
                        String currentMsg = messageTextField.getText().toString();
                        if (countryCode != -1) {
                            SendMessageActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageTextField.setText("");
                                }
                            });
                            responseCodeFromServer = ApiUtility.sendMessage(recepientNumber, countryCode, currentMsg);
                        } else {
                            SendMessageActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SendMessageActivity.this, "Please, choose country code", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        switch (responseCodeFromServer) {
                            case "SUCCESS":
                                SendMessageActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageTextField.setText("");
                                    }
                                });
                                Intent intent = getIntent();
                                intent.putExtra("Number", String.valueOf(44) + recepientNumber);
                                intent.putExtra("isRefreshed", false);
                                finish();
                                startActivity(intent);
                                break;
                            case "Error404":
                                break;
                            case "Not found":
                                break;
                            case "NO CREDITS":
                                SendMessageActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SendMessageActivity.this)
                                                .setTitle("Low balance")
                                                .setMessage("You have 0 balance. Do You want to top-up your account?")
                                                .setNegativeButton("Top-up later", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        Toast.makeText(SendMessageActivity.this, "Message send request fail: 0 balance", Toast.LENGTH_SHORT).show();
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

                                break;
                        }
                        SendMessageActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sendMessageBtn.setEnabled(true);
                            }
                        });
                    }
                }).start();
            }
        }
    }


    private boolean checkIfRecipientExists(String inputNumber) {
        boolean recipientExists = false;
        if (inputNumber.charAt(0) == '0') {
            inputNumber = String.valueOf(44) + inputNumber.substring(1);
        }else if (inputNumber.length() == 10){
            inputNumber = "44" + inputNumber;
        }
        for (int i = 0; i < ParseUtility.listOfNumbers.size(); i++) {
            if (inputNumber.equals(ParseUtility.listOfNumbers.get(i))) {
                recipientExists = true;
            }
        }
        return recipientExists;
    }

    @Override
    protected void onPause() {
        if (sendMsgsBroadcast != null) {
            this.unregisterReceiver(sendMsgsBroadcast);
            sendMsgsBroadcast = null;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        this.registerReceiver(this.sendMsgsBroadcast, new IntentFilter("gsmbilling.action.sendBroadcast"));
        super.onResume();
    }

    private void refreshMsgList() {
        ParseUtility.getMessages(ParseUtility.getCurrentUserNumber(), recepientNumber, 20,
                recyclerView, noMessageHistoryText, this, false);
       // Log.i(TAG, "refreshMsgList");
    }

    private void checkForNumberHistory(final String userFriendNumber, boolean isRefreshed) {
        if (!userFriendNumber.equals("") || !userFriendNumber.equals("SMSHub")) {
            LinearLayoutManager lm = new LinearLayoutManager(this);
            lm.setStackFromEnd(false);
            lm.setReverseLayout(true);
            recyclerView.setLayoutManager(lm);
            //Log.i("NUMB", "Number exist in DB: " + ParseUtility.numberIsInDatabase(userFriendNumber));
           new Thread(new Runnable() {
               @Override
               public void run() {
                   if (ParseUtility.numberIsInDatabase(userFriendNumber)){
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               sendMessageAct_messageTitle.setText(R.string.your_free_message);
                           }
                       });
                   }else{
                      // Log.i("NUMB", "Number exist in DB: NOT");
                   }
               }
           }).start();
            ParseUtility.getMessages(ParseUtility.getCurrentUserNumber(), userFriendNumber, 20,
                    recyclerView, noMessageHistoryText, this, isRefreshed);
            recepientNumber = userFriendNumber;
            hasHistory = true;
            recieverNumberTextField.setText(recepientNumber);
            if (recepientNumber.equals("")) {
                sendToTitleToolbar.setText("Send message: ");
            } else {
                String name = ContextUtils.checkForName(this, recepientNumber);
                sendToTitleToolbar.setText("Send to: " + ((name == null) ? "+" + recepientNumber : name));
            }
            contactSelectBtn.setEnabled(false);
            addNumberBtn.setEnabled(false);
            sendToContentHolder.setVisibility(View.GONE);
            recyclerView.scrollTo(0, 0);
        } else hasHistory = false;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_CONTACT:
                if (resultCode == RESULT_OK) {
                   // Log.i("CONT", "resultCode == RESULT_OK");
                    Uri returnUri = data.getData();
                    Cursor cursor = getContentResolver().query(returnUri, null, null, null, null);

                    if (cursor.moveToNext()) {
                        int columnIndex_ID = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                        // Log.i("CONT", "columnIndex_ID == "+ columnIndex_ID);
                        String contactID = cursor.getString(columnIndex_ID);
//                        Log.i("CONT", "contactID == "+ contactID);
                        int columnIndex_HASPHONENUMBER = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                        String stringHasPhoneNumber = cursor.getString(columnIndex_HASPHONENUMBER);
                        //Log.i("CONT", "stringHasPhoneNumber == "+ stringHasPhoneNumber);

                        if (stringHasPhoneNumber.equalsIgnoreCase("1")) {
                            Cursor cursorNum = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactID, null, null);

                            //Get the first phone number
                            if (cursorNum.moveToNext()) {

                                int columnIndex_number = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                String telNumber = cursorNum.getString(columnIndex_number);
                                //Log.i("CONT", "telNumber == "+ telNumber);
                                //Toast.makeText(getApplicationContext(), "Contact number " + contactID + " added", Toast.LENGTH_SHORT).show();
                                if (telNumber.charAt(0) == '+') {
                                    recieverNumberTextField.setText(telNumber.substring(1));
                                } else {
                                    recieverNumberTextField.setText(telNumber);
                                }
                                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                sendToTitleToolbar.setText("Send to: " + name);
                            }
                            cursor.close();
                        } else {
                            recieverNumberTextField.setText("No number");
                        }
                    }
                }
                if (requestCode == RESULT_CANCELED) {
                   // Log.i("CONT", "resultCode == RESULT_CANCELED");
                }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MessageListActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.support_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        LayoutInflater layoutInflater = LayoutInflater.from(SendMessageActivity.this);
        View view = layoutInflater.inflate(R.layout.delete_history_dialog_view, null);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "History deleted", Toast.LENGTH_SHORT).show();
                ParseUtility.delNumbMessageHistory(SendMessageActivity.this, ParseUtility.getCurrentUserNumber(), recepientNumber);


            }
        });
        alertDialog.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SendMessageActivity.this, "You didn't wont to delete : " + recepientNumber, Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        AlertDialog deleteDialog = alertDialog.create();
        switch (id) {
            case R.id.action_delete:
                if (hasHistory != false) {
                    if (!recepientNumber.equals("0") || !recepientNumber.equals("-1")) {
                        deleteDialog.show();
                        return true;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No history to delete", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
