package uk.co.gsmbilling.smshubsidemod;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.sample.R;
import uk.co.gsmbilling.smshubsidemod.fragment.ContentFragment;
import yalantis.com.sidemenu.util.ViewAnimator;


public class MainActivity extends ActionBarActivity implements ViewAnimator.ViewAnimatorListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private ContentFragment contentFragment;
    private ViewAnimator viewAnimator;
    private int res = R.drawable.content_music;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentFragment = ContentFragment.newInstance(R.drawable.content_music);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, contentFragment)
                .commit();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        linearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        setActionBar();
        createMenuList();
        viewAnimator = new ViewAnimator<>(this, list, contentFragment, drawerLayout, this);
    }

    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem(ContentFragment.CLOSE, R.drawable.icn_close);
        list.add(menuItem0);
        SlideMenuItem menuItem = new SlideMenuItem(ContentFragment.NEW, R.drawable.ic_new_sms);
        list.add(menuItem);
        SlideMenuItem menuItem2 = new SlideMenuItem(ContentFragment.CHAT, R.drawable.ic_chat);
        list.add(menuItem2);
        SlideMenuItem menuItem3 = new SlideMenuItem(ContentFragment.OUTBOX, R.drawable.ic_outbox);
        list.add(menuItem3);
        SlideMenuItem menuItem4 = new SlideMenuItem(ContentFragment.ACCOUNT, R.drawable.ic_log_out);
        list.add(menuItem4);
        SlideMenuItem menuItem5 = new SlideMenuItem(ContentFragment.OUT, R.drawable.ic_account);
        list.add(menuItem5);
        SlideMenuItem menuItem6 = new SlideMenuItem(ContentFragment.SUPPORT, R.drawable.ic_support_guy);
        list.add(menuItem6);
        SlideMenuItem menuItem7 = new SlideMenuItem(ContentFragment.DELETE, R.drawable.ic_delete_user);
        list.add(menuItem7);
        SlideMenuItem menuItem8 = new SlideMenuItem(ContentFragment.RATE, R.drawable.ic_star);
        list.add(menuItem8);
        SlideMenuItem menuItem9 = new SlideMenuItem(ContentFragment.TOP_ON, R.drawable.ic_pounds_bag);
        list.add(menuItem9);

    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                linearLayout.removeAllViews();
                linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


 /*   public void onItemClicked (SlideMenuItem SlideMenuItem){

        if(SlideMenuItem.getItemId() == menuItem0){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if(SlideMenuItem.getItemId() == R.id.menuItem){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if(SlideMenuItem.getItemId() == R.id.menuItem2){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if(SlideMenuItem.getItemId() == R.id.menuItem3){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
    }*/


    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        switch (slideMenuItem.getName()) {
            case ContentFragment.CLOSE:
                //  return screenShotable;
            case ContentFragment.NEW:
                //Intent intent = new Intent(this, SendMessageActivity.class);
                break;
            case ContentFragment.CHAT:
                //Intent intent = new Intent(this, MessageListActivity.class);
                break;
            case ContentFragment.OUTBOX:
                //Intent intent = new Intent(this, OutListActivity.class);
                break;
            case ContentFragment.ACCOUNT:
                startActivity(new Intent(this, Main2Activity.class));
                break;
            case ContentFragment.OUT:

/*                View logoutView = getLayoutInflater().inflate(R.layout.logout_dialog_view, null);
                new AlertDialog.Builder(this)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseUtility.logout();
                                Toast.makeText(this, R.string.logged_out, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setView(logoutView)
                        .show();*/

                break;
            case ContentFragment.SUPPORT:
                //startActivity(new Intent(this, ContactActivity.class));
                break;
            case ContentFragment.DELETE:

/*                View logoutView = getLayoutInflater().inflate(R.layout.delete_account_dialog_view, null);
                new AlertDialog.Builder(this)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseUtility.deleteUser(AboutActivity.this);
                                Intent intent = new Intent(AboutActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setView(logoutView)
                        .show();*/
                break;

            case ContentFragment.RATE:

                final Uri uri = Uri.parse("market://details?id=uk.co.gsmbilling.sms2uk");
                final Intent googleIntent = new Intent(Intent.ACTION_VIEW, uri);
                if (getPackageManager().queryIntentActivities(googleIntent, 0).size() > 0) {
                    startActivity(googleIntent);
                }

                break;
            case ContentFragment.TOP_ON:
                //startActivity(new Intent(this, AccountActivity.class));
                break;

            default:

        }
        return replaceFragment(screenShotable, position);
    }



    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int topPosition) {
        this.res = this.res == R.drawable.content_music ? R.drawable.content_films : R.drawable.content_music;
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);

        findViewById(R.id.content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();
        ContentFragment contentFragment = ContentFragment.newInstance(this.res);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, contentFragment).commit();
        return contentFragment;
    }

/*    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        switch (slideMenuItem.getName()) {
            case ContentFragment.CLOSE:
                return screenShotable;
            default:
                return replaceFragment(screenShotable, position);
        }
    }*/

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();
    }

    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
    }

}
