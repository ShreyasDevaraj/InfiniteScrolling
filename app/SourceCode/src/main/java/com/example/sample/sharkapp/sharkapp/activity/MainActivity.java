package com.example.sample.sharkapp.sharkapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.sample.sharkapp.R;
import com.example.sample.sharkapp.sharkapp.listeners.OnSwipeTouchListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        final Button getResults = (Button) findViewById(R.id.getResults);
        getResults.setOnTouchListener(new OnSwipeTouchListener(this) {

            public void onSwipeRight() {
                Log.d(TAG, "right swipe action detected, starting imageListActivity");
                Intent intent = new Intent(getApplicationContext(), ImageListActivity.class);
                startActivity(intent);
            }

        });
    }

}
