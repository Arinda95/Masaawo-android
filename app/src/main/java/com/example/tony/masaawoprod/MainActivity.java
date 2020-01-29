package com.example.tony.masaawoprod;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout container;
    AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidNetworking.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        this.setTitle("");

        //Toolbar addition
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/BAQRoundedRegularOutline.otf");
        mTitle.setTypeface(custom_font);

        //inflate container
        container = (LinearLayout) findViewById(R.id.container);

        //Animated gradient transition
        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);

        //Side drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Side nav view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Number plate input
        final EditText numberPlate = (EditText) findViewById(R.id.numberPlateInput);
        //Search numberplate
        ImageButton search = (ImageButton) findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If app is connected to main server continue else error
                if (isOnline() == true){
                String plate = numberPlate.getText().toString();
                    if (plate.trim().length() > 0) {
                        String plateCap = plate.toUpperCase();
                        searchAndReport(plateCap);
                    } else {
                        // Prompt user to enter numberplate
                        emptySlideToBottom(MainActivity.this);
                    }
                }
                else if (isOnline() == false){
                    errorSlideToBottom(MainActivity.this);
                }
            }
        });

        //To terms and conditions
        TextView totnc = (TextView) findViewById(R.id.tnc);
        totnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Tuts = new Intent(MainActivity.this, MainActivityTermsAndConditions.class);
                startActivity(Tuts);
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_search) {
            // Handle the camera action
        }  else if (id == R.id.nav_tnc) {
            //Switch to Terms and conditions activity
            Intent TNC = new Intent(this, MainActivityTermsAndConditions.class);
            startActivity(TNC);
        } else if (id == R.id.nav_about_us) {
            //Switch to about us
            Intent AboutUs = new Intent(this, MainActivityAboutUs.class);
            startActivity(AboutUs);
        } else if (id == R.id.nav_how_to) {
            //Switch to tuts
            Intent Tuts = new Intent(this, HowToMainActivity.class);
            startActivity(Tuts);
        }
        else if (id == R.id.nav_exit) {
            //Exit app
            finish();
            System.exit(0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }

    public void safeSlideToBottom(Context context){
        final LinearLayout safeView = (LinearLayout) findViewById(R.id.safe);
        safeView.setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.safeTop)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.safeHeader)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.safeImage)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.safeSubText)).setVisibility(View.VISIBLE);
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch( audio.getRingerMode() ){
            case AudioManager.RINGER_MODE_NORMAL:
                final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
                mp.start();
                break;
            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= 26) {v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {v.vibrate(500);}
                break;
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                safeView.setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.safeTop)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.safeHeader)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.safeImage)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.safeSubText)).setVisibility(View.GONE);
            }
        }, 6000);
    }

    public void unsafeSlideToBottom(Context context){
        final LinearLayout unsafe = (LinearLayout) findViewById(R.id.unsafe);
        unsafe.setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.unsafeTop)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.unsafeHeader)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.unsafeImage)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.unsafeSubText)).setVisibility(View.VISIBLE);
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch( audio.getRingerMode() ){
            case AudioManager.RINGER_MODE_NORMAL:
                final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
                mp.start();
                break;
            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= 26) {v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {v.vibrate(500);}
                break;
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                unsafe.setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.unsafeTop)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.unsafeHeader)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.unsafeImage)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.unsafeSubText)).setVisibility(View.GONE);
            }
        }, 6000);
    }

    public void errorSlideToBottom(Context context){
        final LinearLayout error = (LinearLayout) findViewById(R.id.error);
        error.setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.errorTop)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.errorHeader)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.errorImage)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.errorSubText)).setVisibility(View.VISIBLE);
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch( audio.getRingerMode() ){
            case AudioManager.RINGER_MODE_NORMAL:
                final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
                mp.start();
                break;
            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= 26) {v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {v.vibrate(500);}
                break;
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                error.setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.errorTop)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.errorHeader)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.errorImage)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.errorSubText)).setVisibility(View.GONE);
            }
        }, 6000);
    }

    public void emptySlideToBottom(Context context){
        final LinearLayout empty = (LinearLayout) findViewById(R.id.empty);
        empty.setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.emptyTop)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.emptyHeader)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.emptyImage)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.emptySubText)).setVisibility(View.VISIBLE);
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch( audio.getRingerMode() ){
            case AudioManager.RINGER_MODE_NORMAL:
                final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
                mp.start();
                break;
            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= 26) {v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {v.vibrate(500);}
                break;
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                empty.setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.emptyTop)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.emptyHeader)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.emptyImage)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.emptySubText)).setVisibility(View.GONE);
            }
        }, 6000);
    }

    private void addtoLogger(final String numberPlate, final String result){
        AndroidNetworking.post("http://167.99.207.49:80/api/platequery")
                .addBodyParameter("phoneNumber", "undefinedAND")
                .addBodyParameter("numberPlate", numberPlate)
                .addBodyParameter("result", result)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    private void searchAndReport(final String plate){
        AndroidNetworking.get("http://167.99.207.49:80/api/search/"+plate+"")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // post response results ot logger db
                        String responseString = response.optString("response");
                        String safeIndicator = "safe";
                        String unsafeIndicator = "reported";
                        Log.d("response", responseString);
                        if (responseString.toLowerCase().contains(safeIndicator.toLowerCase())){
                            //call safe animation
                            safeSlideToBottom(MainActivity.this);
                            addtoLogger(plate, "0");
                        }
                        else if (responseString.toLowerCase().contains(unsafeIndicator.toLowerCase())){
                            //call show unsafe animation
                            unsafeSlideToBottom(MainActivity.this);
                            addtoLogger(plate, "1");
                        }
                        else {
                            //Error connecting to network
                            errorSlideToBottom(MainActivity.this);
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("Error ", ":" + error);
                    }
                });
    }

}

























