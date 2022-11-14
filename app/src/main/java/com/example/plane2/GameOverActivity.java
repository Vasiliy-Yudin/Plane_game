package com.example.plane2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {
    private static final String TAG = GameOverActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        SharedPreferences preferences = getSharedPreferences(GameActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        if(preferences.getLong(GameActivity.FINISH_SCORE,0)>preferences.getLong(GameActivity.MAX_SCORE,0)){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(GameActivity.MAX_SCORE, preferences.getLong(GameActivity.FINISH_SCORE,0));
            editor.apply();
            TextView tv_h=(TextView)findViewById(R.id.new_highest_score_text);
            tv_h.setText("New highest score!");
        }

        /*
        if(preferences.contains(GameActivity.MAX_SCORE)){
            if(preferences.getLong(GameActivity.))
        }
        else{
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(GameActivity.MAX_SCORE, preferences.getLong(GameActivity.FINISH_SCORE,0));
            editor.apply();
        }*/
        TextView tv=(TextView)findViewById(R.id.score_text);
        tv.setText(String.valueOf(preferences.getLong(GameActivity.FINISH_SCORE,0)));

    }

    public void editActions(View view){
        MainActivity.game_must_be_over=false;
        Log.d(TAG, "editActions");
        finish();
    }

    @Override
    public void onBackPressed() {
        MainActivity.game_must_be_over=false;
        Log.d(TAG, "GameOver onBackPressed");
        finish();
    }
}
