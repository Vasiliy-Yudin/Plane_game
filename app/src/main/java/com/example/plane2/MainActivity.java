package com.example.plane2;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    public static boolean game_must_be_over;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game_must_be_over=false;
        setContentView(R.layout.activity_main);
        Button play_button=(Button)findViewById(R.id.play_button);
        Button menu_button=(Button)findViewById(R.id.settings_button);
        play_button.setOnTouchListener(this);
        menu_button.setOnTouchListener(this);
    }

    public boolean onTouch(View button, MotionEvent motion) {
        switch(button.getId()) { // определяем какая кнопка
            case R.id.play_button:
                if (motion.getAction()==MotionEvent.ACTION_DOWN) {
                    startActivity(new Intent(this,GameActivity.class));

                    //startActivity(new Intent(this,GameOverActivity.class));
                }
                break;
            case R.id.settings_button:
                if (motion.getAction()==MotionEvent.ACTION_DOWN) {
                    SharedPreferences preferences = getSharedPreferences(GameActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    // add a list
                    String[] actions = {"MIN", "MEDIUM", "MAX"};
                    AlertDialog dialog /*= builder.create()*/;
                    builder.setTitle("Chose sensitivity");
                    builder.setItems(actions, new DialogInterface.OnClickListener() {
                        //@Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("SENSITIVITY",10+10*which);
                            editor.apply();
                            dialog.cancel();
                            /*switch (which) {
                                case 0:{
                                    dialog.cancel();
                                    GameActivity.setSensitivity(10);
                                    break;
                                }
                                case 1: {
                                    dialog.cancel();
                                    GameActivity.setSensitivity(20);
                                    break;
                                }
                                case 2: {
                                    dialog.cancel();
                                    GameActivity.setSensitivity(30);
                                    break;
                                }
                            }*/
                        }
                    });
                    // create and show the alert dialog
                    dialog = builder.create();
                    dialog.show();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        if(game_must_be_over)startActivity(new Intent(this,GameOverActivity.class));
        super.onStart();
    }
}