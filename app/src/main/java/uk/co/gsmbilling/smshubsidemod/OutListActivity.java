package uk.co.gsmbilling.smshubsidemod;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.kdotj.menu.FabMenu;
import com.kdotj.menu.FabMenuItem;

import java.util.ArrayList;

import uk.co.gsmbilling.sms2uk.utils.ParseUtility;
import yalantis.com.sidemenu.sample.R;

public class OutListActivity extends AppCompatActivity
        implements FabMenuItem.Callback{


    private SwipeRefreshLayout swipeRefreshLayoutContainer;
    private OutboxlistAdapter outboxlistAdapter;
    private RecyclerView recyclerView;
    FabMenu fabMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_outbox_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayoutContainer = (SwipeRefreshLayout) findViewById(R.id.swipeLayoutContainerMsgs);
        outboxlistAdapter = new OutboxlistAdapter(new ArrayList<Message>(), this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(outboxlistAdapter);
        if (swipeRefreshLayoutContainer != null) {
            swipeRefreshLayoutContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ParseUtility.getThreads2(outboxlistAdapter);
                    swipeRefreshLayoutContainer.setRefreshing(false);
                }
            });
            swipeRefreshLayoutContainer.setColorSchemeResources(
                    R.color.btnColorMainPressed,
                    R.color.btnColorMainUnpressed);
        }
            }



    @Override
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
            new AlertDialog.Builder(OutListActivity.this)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ParseUtility.logout();
                            Toast.makeText(OutListActivity.this, R.string.logged_out, Toast.LENGTH_SHORT).show();
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
}
