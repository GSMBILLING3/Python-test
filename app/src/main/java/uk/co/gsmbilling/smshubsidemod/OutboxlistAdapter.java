package uk.co.gsmbilling.smshubsidemod;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import uk.co.gsmbilling.sms2uk.utils.ParseUtility;
import yalantis.com.sidemenu.sample.R;

/**
 * Created by GSM-1 on 2016-05-21.
 */
public class OutboxlistAdapter extends RecyclerView.Adapter<OutboxlistAdapter.MyViewHolder> {
    private List<Message> messagesList2;
    private Context context;
    private String text_sms_1;
    private String sms_id;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mobilenumberID_v, text_of_message_v, data_delivery_v, text_id_v;
        public RelativeLayout background;
        public ImageView overflow;
        public String number;
        public String text_sms;



        public MyViewHolder(View view) {
            super(view);
    //        view.setOnClickListener(this);
            background = (RelativeLayout) view.findViewById(R.id.thread_background);
            mobilenumberID_v = (TextView) view.findViewById(R.id.thread_person_v);
            text_of_message_v = (TextView) view.findViewById(R.id.thread_text_v);
            data_delivery_v = (TextView) view.findViewById(R.id.thread_date_v);
            text_id_v = (TextView) view.findViewById(R.id.sms_id_v);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
        }
        }


    public OutboxlistAdapter(List<Message> messagesList2, Context context) {
        this.context = context;
        this.messagesList2 = messagesList2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(context)
                .inflate(R.layout.thread_list_item_v, parent, false);
        final RecyclerView roman = (RecyclerView) parent;
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        Message outbox_message = messagesList2.get(position);
        holder.mobilenumberID_v.setText(outbox_message.getRecipientNumber());
        String number = holder.mobilenumberID_v.getText().toString();
        holder.number = number;
        holder.text_of_message_v.setText(outbox_message.getText());
        holder.text_id_v.setText(outbox_message.getObjectId());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM, HH:mm");
        holder.data_delivery_v.setText(sdf.format(outbox_message.getCreatedAt()));

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sms_id = holder.text_id_v.getText().toString();
                text_sms_1 = holder.text_of_message_v.getText().toString();
                showPopupMenu(holder.overflow);

            }
        });
    }

    @Override
    public int getItemCount() {
        return messagesList2.size();
    }

    public void refreshoutList12(List<Message> freshList) {
        messagesList2.clear();
        messagesList2.addAll(freshList);
        notifyDataSetChanged();
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_pop_up, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {


        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.delete_m:

                    ParseUtility.delOutboxMessage(sms_id);
                    Toast.makeText(context, "Swipe to refresh: "+ sms_id, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.foward_m:

                    Intent i = new Intent(context, SendMessageActivity.class);
                    i.putExtra("currentMsg", text_sms_1);
                    context.startActivity(i);

                    return true;
                default:
            }
            return false;
        }
    }
}
