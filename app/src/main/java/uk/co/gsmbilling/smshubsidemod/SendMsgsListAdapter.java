package uk.co.gsmbilling.smshubsidemod;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import yalantis.com.sidemenu.sample.R;

/**
 * Created by GSM-2 on 2016-05-30.
 */
public class SendMsgsListAdapter extends RecyclerView.Adapter<SendMsgsListAdapter.MyViewHolderRe> {

    private SharedPreferences sharedPreferences;
    private List<Message> messagesList;
    private Context context;
    private int msgsInListCounter = 0;
    private String recepientNumb;
    private boolean firstScroll;
    public  boolean firstCheck = true;
    public static String lastIDObject;

    public SendMsgsListAdapter(List<Message> messagesList, Context context, String recepientNumb, boolean isRefreshed) {
        this.context = context;
        this.messagesList = messagesList;
        firstScroll = isRefreshed;
       // Log.i("LIST","isRefreshed ");
        this.recepientNumb = recepientNumb;
        sharedPreferences = context.getSharedPreferences("uk.co.gsmSaves", Context.MODE_PRIVATE);

    }

    private void checkForLastMessage() {

        if (sharedPreferences.contains(recepientNumb)){
                if (sharedPreferences.getString(recepientNumb,null).equals(messagesList.get(0).getObjectId())) {
                   // Log.i("LIST", sharedPreferences.getString(recepientNumb, null) + " equals(true) " + messagesList.get(0).getObjectId());
                    lastIDObject = "Nothing";
                }else{
                  //  Log.i("LIST", sharedPreferences.getString(recepientNumb,null) + " equals(false) " + messagesList.get(0).getObjectId());
                    lastIDObject = sharedPreferences.getString(recepientNumb,null);
                    sharedPreferences.edit().putString(recepientNumb,messagesList.get(0).getObjectId()).apply();
                }
        }else{

               // Log.i("LIST", "sharedPreferences.NOT contains(recipient)" + " - " + messagesList.get(0).getObjectId());
                lastIDObject = messagesList.get(0).getObjectId();
                sharedPreferences.edit().putString(recepientNumb, messagesList.get(0).getObjectId()).apply();
                firstScroll = false;
        }
    }


    @Override
    public MyViewHolderRe onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(context)
                .inflate(R.layout.message_bubble_list_item_right, parent, false);
        if (firstCheck) {
            checkForLastMessage();
            firstCheck = false;
        }
        return new MyViewHolderRe(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolderRe holder, int position) {

        Message currentMsg = messagesList.get(position);
        if (currentMsg.isFromCurrentUser()){
            holder.background.setBackgroundResource(R.drawable.message_bubble_right);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,5,0,5);
            layoutParams.gravity = Gravity.RIGHT;
            holder.background.setLayoutParams(layoutParams);
            holder.background.setGravity(Gravity.RIGHT);
            holder.text_of_message.setTextColor(Color.parseColor("#1b5265"));
            holder.data_delivery.setTextColor(Color.parseColor("#28A9BC"));

            holder.text_of_message.setText(currentMsg.getText());
            SimpleDateFormat messageDateRight = new SimpleDateFormat("dd.MM - HH:mm");
            holder.data_delivery.setText(messageDateRight.format(currentMsg.getCreatedAt()));

            }else{


                holder.background.setBackgroundResource(R.drawable.message_bubble_left);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.LEFT;
                layoutParams.setMargins(0,5,0,5);
                holder.background.setLayoutParams(layoutParams);

                holder.text_of_message.setTextColor(Color.parseColor("#E1F5FE"));
                holder.text_of_message.setGravity(Gravity.LEFT);

                holder.data_delivery.setTextColor(Color.parseColor("#C6F0DA"));
                holder.data_delivery.setGravity(Gravity.LEFT);

                holder.text_of_message.setText(currentMsg.getText());
                SimpleDateFormat messageDateRight = new SimpleDateFormat("dd.MM - HH:mm");
                holder.data_delivery.setText(messageDateRight.format(currentMsg.getCreatedAt()));
            }
           //  Log.i("LIST", "Before firstScroll status is : " + firstScroll);

            if (lastIDObject.equals(currentMsg.getObjectId()) && firstScroll){
              //  Log.i("LIST", "I found my position firstScroll: " + firstScroll);
                holder.divideLineMsgs.setVisibility(View.VISIBLE);
                holder.newMsgsCounter.setText(position + " New messages");
                holder.newMsgsCounter.setVisibility(View.VISIBLE);
                firstScroll = false;
            }else{
                holder.divideLineMsgs.setVisibility(View.GONE);
                holder.newMsgsCounter.setVisibility(View.GONE);
            }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class MyViewHolderRe extends RecyclerView.ViewHolder {
        TextView text_of_message, data_delivery,newMsgsCounter;
        LinearLayout background;
        View divideLineMsgs;

        public MyViewHolderRe(View view) {
            super(view);
            background = (LinearLayout) view.findViewById(R.id.message_bubble_list_item_right);
            text_of_message = (TextView) view.findViewById(R.id.messagesHistoryTextItemRight);
            data_delivery = (TextView) view.findViewById(R.id.messagesHistoryDateTextItemRight);
            divideLineMsgs = view.findViewById(R.id.divideLineMsgs);
            newMsgsCounter = (TextView) view.findViewById(R.id.newMsgsCounter);
        }
    }

    }
