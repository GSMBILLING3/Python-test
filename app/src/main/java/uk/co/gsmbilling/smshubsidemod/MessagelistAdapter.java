package uk.co.gsmbilling.smshubsidemod;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import uk.co.gsmbilling.sms2uk.utils.ContextUtils;
import yalantis.com.sidemenu.sample.R;

/**
 * Created by GSM-1 on 2016-05-21.
 */
public class MessagelistAdapter extends RecyclerView.Adapter<MessagelistAdapter.MyViewHolder> {
    private List<Message> messagesList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mobilenumberID, text_of_message, data_delivery;
        public RelativeLayout background;
        public String number;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            background = (RelativeLayout) view.findViewById(R.id.thread_background);
            mobilenumberID = (TextView) view.findViewById(R.id.thread_person);
            text_of_message = (TextView) view.findViewById(R.id.thread_text);
            data_delivery = (TextView) view.findViewById(R.id.thread_date);
        }

        @Override
        public void onClick(View v) {
//            TextView numberStr = (TextView) v.findViewById(R.id.thread_person);
//            String number = numberStr.getText().toString();
            Intent i = new Intent(context, SendMessageActivity.class);
            i.putExtra("Number", number);
            context.startActivity(i);
        }
    }

    public MessagelistAdapter(List<Message> messagesList, Context context) {
        this.context = context;
        this.messagesList = messagesList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(context)
                .inflate(R.layout.thread_list_item, parent, false);
        final RecyclerView rv = (RecyclerView) parent;
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.background.setBackground(context.getResources().getDrawable(R.drawable.message_bubble));
        } else {
            holder.background.setBackground(context.getResources().getDrawable(R.drawable.message_bubble2));

        }
        Message message = messagesList.get(position);
        if (message.isFromCurrentUser()) {
            holder.mobilenumberID.setText(message.getRecipientNumber());
        } else
            holder.mobilenumberID.setText(message.getSenderNumber());
        holder.text_of_message.setText(message.getText());

        String number = holder.mobilenumberID.getText().toString();
        holder.number = number;
        String name = null;
        for (String num : new String[]{number, number.substring(2), "+" + number, "0" + number.substring(2)}) {
           // Log.i("contact", num);
            name = ContextUtils.checkForName(context, number);
            if (name != null)
                break;
        }
        if (name != null)
            holder.mobilenumberID.setText(name);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM, HH:mm");
        holder.data_delivery.setText(sdf.format(message.getCreatedAt()));

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public void refreshMessageList(List<Message> freshList) {
        messagesList.clear();
        messagesList.addAll(freshList);
        notifyDataSetChanged();
    }
}
