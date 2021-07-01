package com.lalbab.app;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.lalbab.app.Fragment.CatSFragment;
import com.lalbab.app.UpdateVersion.UpdateHelper;
import com.squareup.picasso.Picasso;

import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.Fragment.Cart_fragment;
import com.lalbab.app.Fragment.Edit_profile_fragment;
import com.lalbab.app.Fragment.Home_fragment;
import com.lalbab.app.Fragment.My_order_fragment;
import com.lalbab.app.Fragment.My_requsts_fragment;
import com.lalbab.app.Fragment.Product_fragment_shop;
import com.lalbab.app.fcm.MyFirebaseRegister;
import com.lalbab.app.util.ConnectivityReceiver;
import com.lalbab.app.util.DatabaseHandler;
import com.lalbab.app.util.Session_management;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ConnectivityReceiver.ConnectivityReceiverListener,CatSFragment.OnFragmentInteractionListener,UpdateHelper.onUpdateCheckListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private TextView totalBudgetCount,tv_name,tv_number;
    private ImageView iv_profile;

    private DatabaseHandler dbcart;

    private Session_management sessionManagement;

    private Menu nav_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("sssssss", "------------------------");
        UpdateHelper.with(this).onUpdareCheck(this).check();
        Log.d("sssssss", "------------------------");

        BaseURL.loadLocale(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        dbcart = new DatabaseHandler(this);

        checkConnection();

        sessionManagement = new Session_management(MainActivity.this);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nav_menu = navigationView.getMenu();

        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);

        iv_profile = (ImageView) header.findViewById(R.id.iv_header_img);
        tv_name = (TextView) header.findViewById(R.id.tv_header_name);
        tv_number = (TextView) header.findViewById(R.id.tv_header_moblie);

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManagement.isLoggedIn()) {
                    Fragment fm = new Edit_profile_fragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                }else{
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
        });

        updateHeader();
        sideMenu();

        if (savedInstanceState == null) {
            Fragment fm = new Home_fragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentPanel, fm, "Home_fragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                try {

                    InputMethodManager inputMethodManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    Fragment fr = getFragmentManager().findFragmentById(R.id.contentPanel);

                    final String fm_name = fr.getClass().getSimpleName();
                    Log.e("backstack: ", ": " + fm_name);

                    if (fm_name.contentEquals("Home_fragment")) {

                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                        toggle.setDrawerIndicatorEnabled(true);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        toggle.syncState();

                    }else if(fm_name.contentEquals("My_order_fragment") ||
                            fm_name.contentEquals("Thanks_fragment")){
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                        toggle.setDrawerIndicatorEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        toggle.syncState();

                        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Fragment fm = new Home_fragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                        .addToBackStack(null).commit();
                            }
                        });
                    } else{

                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                        toggle.setDrawerIndicatorEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        toggle.syncState();

                        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                onBackPressed();
                            }
                        });
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });


        if(sessionManagement.getUserDetails().get(BaseURL.KEY_ID) != null && !sessionManagement.getUserDetails().get(BaseURL.KEY_ID).equalsIgnoreCase("")) {
            MyFirebaseRegister fireReg = new MyFirebaseRegister(this);
            fireReg.RegisterUser(sessionManagement.getUserDetails().get(BaseURL.KEY_ID));
        }
        String KEY_STATUS= sessionManagement.getUserDetails().get(BaseURL.KEY_STATUS);
        if (KEY_STATUS!= null){
         if (KEY_STATUS.equals("2")){
             isProvider();
         }
        }


    }

    private void isProvider() {

    }

    public void updateHeader(){
        if(sessionManagement.isLoggedIn()) {
            String getname = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
            String getimage = sessionManagement.getUserDetails().get(BaseURL.KEY_IMAGE);
            String getemail = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);

            /*
            Glide.with(this)
                    .load(BaseURL.IMG_PROFILE_URL + getimage)
                    .placeholder(R.mipmap.ic_launcher)
                  //  .crossFade()
                    .into(iv_profile);
            */

            Picasso.with(this).load(BaseURL.IMG_PROFILE_URL + getimage).into(iv_profile);


            tv_name.setText(getname);
            tv_number.setText(getemail);
        }
    }

    public void sideMenu(){

        String KEY_STATUS= sessionManagement.getUserDetails().get(BaseURL.KEY_STATUS);

        if(sessionManagement.isLoggedIn()){
            tv_number.setVisibility(View.VISIBLE);
            nav_menu.findItem(R.id.nav_logout).setVisible(true);
            nav_menu.findItem(R.id.nav_user).setVisible(true);

            if (KEY_STATUS!= null){
                if (KEY_STATUS.equals("2")){
                    nav_menu.findItem(R.id.nav_myshop).setVisible(true);
                    nav_menu.findItem(R.id.nav_myrequest).setVisible(true);
                    String title = getResources().getString(R.string.nav_provider);
                    nav_menu.findItem(R.id.nav_user).setTitle("" +title );
                }else {
                    nav_menu.findItem(R.id.nav_myshop).setVisible(false);
                    nav_menu.findItem(R.id.nav_myrequest).setVisible(false);
                    String title = getResources().getString(R.string.nav_user);
                    nav_menu.findItem(R.id.nav_user).setTitle("" +title );
                }
            }else {
                nav_menu.findItem(R.id.nav_myrequest).setVisible(false);
                nav_menu.findItem(R.id.nav_myshop).setVisible(false);
                String title = getResources().getString(R.string.nav_user);
                nav_menu.findItem(R.id.nav_user).setTitle("" +title );
            }

        }else{
            tv_number.setVisibility(View.GONE);
            tv_name.setText(getResources().getString(R.string.btn_login));
            tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            });
            nav_menu.findItem(R.id.nav_logout).setVisible(false);
            nav_menu.findItem(R.id.nav_user).setVisible(false);
        }
    }

    public void setFinish(){
        finish();
    }

    public void setCartCounter(String totalitem) {
        totalBudgetCount.setText(totalitem);
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem item = menu.findItem(R.id.action_cart);
      //  MenuItem c_password = menu.findItem(R.id.action_change_password);
        MenuItem search = menu.findItem(R.id.action_search);
        MenuItem ku = menu.findItem(R.id.lang_ku);
        MenuItem ar = menu.findItem(R.id.lang_ar);
        MenuItem en = menu.findItem(R.id.lang_en);

        item.setVisible(true);
      //  c_password.setVisible(false);
        search.setVisible(false);

        View count = item.getActionView();
        count.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                menu.performIdentifierAction(item.getItemId(), 0);
            }
        });
        totalBudgetCount = (TextView) count.findViewById(R.id.actionbar_notifcation_textview);

        totalBudgetCount.setText("" + dbcart.getCartCount());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.lang_ku) {
            if (!BaseURL.LANGUAGE.contains("ku")){
                BaseURL.setLocale("ku", this);
                recreate();
            }

        }else  if (id == R.id.lang_ar) {
            if (!BaseURL.LANGUAGE.contains("ar")){
                BaseURL.setLocale("ar", this);
                recreate();
            }


        }else  if (id == R.id.lang_en) {
            if (!BaseURL.LANGUAGE.contains("en")){
                BaseURL.setLocale("en", this);
                recreate();
            }
        }

        if (id == R.id.action_cart || id == R.id.bt_Confirmation) {

            if(dbcart.getCartCount() >0) {
                Fragment fm = new Cart_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }else{
                Toast.makeText(MainActivity.this, "No item in cart", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fm = null;
        Bundle args = new Bundle();

        if (id == R.id.nav_home) {
            Fragment fm_home = new Home_fragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentPanel, fm_home, "Home_fragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } else if (id == R.id.nav_myorders) {
            fm = new My_order_fragment();
        } else if (id == R.id.nav_myrequest) {
            fm = new My_requsts_fragment();

            Session_management sessionManagement = new Session_management(this);
            String id_catagore = sessionManagement.getUserDetails().get(BaseURL.KEY_ID_CATAGORE);

            String getid = id_catagore;
            String getcat_title = "Requests ";
            args.putString("cat_id", getid);
            args.putString("cat_title", getcat_title);
            fm.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;

        } else if (id == R.id.nav_myshop) {
            fm = new Product_fragment_shop();


            Session_management sessionManagement = new Session_management(this);
            String id_catagore = sessionManagement.getUserDetails().get(BaseURL.KEY_ID_CATAGORE);

            String getid = id_catagore;
            String getcat_title = "My Shop";
            args.putString("cat_id", getid);
            args.putString("cat_title", getcat_title);
            fm.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;

        } else if (id == R.id.nav_myprofile) {
            fm = new Edit_profile_fragment();
        } else if (id == R.id.nav_aboutus) {

            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_Contact) {

            Intent intent = new Intent(MainActivity.this, ContactUs.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_review) {
            reviewOnApp();
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_logout) {

            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sessionManagement.logoutSession();
                            finish();
                        }
                    });

        }

        if (fm != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi friends i am using ." + " http://play.google.com/store/apps/details?id=" + getPackageName() + " APP");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void reviewOnApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        AppController.getInstance().setConnectivityListener(this);

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(BaseURL.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(BaseURL.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;

        if (!isConnected) {
            message = ""+getResources().getString(R.string.no_internet);
            color = Color.RED;

            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinatorlayout), message, Snackbar.LENGTH_LONG)
                /*.setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })*/;

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(BaseURL.PREFS_NAME, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId)) {
            //txtRegId.setText("Firebase Reg Id: " + regId);
        }else {
            //txtRegId.setText("Firebase Reg Id is not received yet!");
        }
    }
    @Override
    public void onUpdateCheckListener(final String urlApp) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("New Versione Avaliable")
                .setMessage("Please update to new version to continue use")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,""+urlApp,Toast.LENGTH_SHORT).show();
                        String url = urlApp;
                        Intent ii = new Intent(Intent.ACTION_VIEW);
                        ii.setData(Uri.parse(url));
                        startActivity(ii);
                    }
                }).create();
        alertDialog.show();

    }

}
