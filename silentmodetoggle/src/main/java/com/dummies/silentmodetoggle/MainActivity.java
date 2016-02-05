package com.dummies.silentmodetoggle;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        //Find the view with the id content in layout file
        FrameLayout contentView = (FrameLayout)findViewById(R.id.content);

        //create a click listener for the contentView that will toogle the phone ringer state, and then update de UI to reflect the new state
        contentView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                RingerHelper.performToggle(audioManager);
                updateUI();
            }
        });

        Log.d("SilentModeToogle","This is a test");
    }

    private void updateUI(){
        ImageView imageView = (ImageView) findViewById(R.id.phone_icon);
        int phoneImage = RingerHelper.isPhoneSilent(audioManager)? R.drawable.ringer_off : R.drawable.ringer_on ;
        imageView.setImageResource(phoneImage);


    }

    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
