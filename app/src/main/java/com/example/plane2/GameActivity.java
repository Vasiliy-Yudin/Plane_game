package com.example.plane2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = GameActivity.class.getSimpleName();
    public static boolean finished=true;
    private  SensorManager sensor_manager;
    private Sensor sensor;
    public static float speed=0;
    //sensitivity of accelerometer
    private int sensitivity;
    //sensor is working now
    public static boolean RegisterIsPresent=false;
    //prefernce's name
    public static final String APP_PREFERENCES = "last_settings";
    //should be finished
    private static final String FINISHED = "finished";
    //finish score
    public static final String FINISH_SCORE = "finish_score";
    //max score
    public static final String MAX_SCORE = "score";

    private SharedPreferences preferences;
    private MainGameView GV;
    //defining sensor event listener for accelerometer to define plane's speed
    SensorEventListener sensor_listener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(Math.abs(sensorEvent.values[0])>=0.2){
                if(Math.abs(sensorEvent.values[0])>0.08)
                    speed=-sensorEvent.values[0]*(float)sensitivity;
            }
            else speed=0;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public void onBackPressed() {
        if(RegisterIsPresent){ sensor_manager.unregisterListener(sensor_listener,sensor); RegisterIsPresent=false;}
        GV.killThread();
        GV.goBack(25);
        openQuitDialog();
    }

    private void openQuitDialog() {
        setContentView(R.layout.empty);
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // add a list
        String[] actions = {"Resume", "Restart", "Menu"};
        AlertDialog dialog /*= builder.create()*/;
        builder.setItems(actions, new DialogInterface.OnClickListener() {
            //@Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:{
                        if(!RegisterIsPresent){
                            sensor_manager.registerListener(sensor_listener,sensor,SensorManager.SENSOR_DELAY_GAME);
                            RegisterIsPresent=true;
                        }
                        dialog.cancel();
                        setContentView(GV);
                        Log.d(TAG,"resume");
                        break;
                    }
                    case 1: {
                        if(!RegisterIsPresent){
                            sensor_manager.registerListener(sensor_listener,sensor,SensorManager.SENSOR_DELAY_GAME);
                            RegisterIsPresent=true;
                        }
                        dialog.cancel();
                        makeNewGV();
                        setContentView(GV);
                        Log.d(TAG,"restart");break;
                    }
                    case 2: {
                        finish();
                        Log.d(TAG,"menu");break;
                    }
                }
            }
        });
        // create and show the alert dialog
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }


    private void makeNewGV(){GV=new MainGameView(this);}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        sensitivity=preferences.getInt("SENSITIVITY",10);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FINISHED,true);
        editor.apply();
        Log.d(TAG,"finished from onCreate= "+preferences.getBoolean(FINISHED,false));
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    protected void onStart(){
        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(preferences.contains(FINISHED)) {
            finished=preferences.getBoolean(FINISHED,true); Log.d(TAG,"finished: "+finished);
            if(!finished) {
                /*Log.d(TAG, "plane_x=" + preferences.getInt(PLANE_X, 0) + ", buildings: " +
                        preferences.getString(BUILDINGS_ARRAY, "---") + ", speed=" + preferences.getFloat(SPEED, 0) +
                        ", score=" + preferences.getLong(SCORE, -1));*/
                if(RegisterIsPresent){ sensor_manager.unregisterListener(sensor_listener,sensor); RegisterIsPresent=false;}
                //setContentView(R.layout.empty);
                GV.goBack(70);
                openQuitDialog();
            }
            else{
                sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                if(sensor_manager!=null) {
                    List<Sensor> sensors=sensor_manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
                    if (sensors.size()>0){
                        RegisterIsPresent=true;
                        sensor = sensors.get(0);
                    }
                }
                if(RegisterIsPresent)sensor_manager.registerListener(sensor_listener,sensor,SensorManager.SENSOR_DELAY_GAME);
                makeNewGV();
                setContentView(GV);
            }
        }
        Log.d(TAG,"onStart");
        super.onStart();
    }

    @Override
    protected void onDestroy(){
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        GV.killThread();
        if(RegisterIsPresent){
            sensor_manager.unregisterListener(sensor_listener);
            RegisterIsPresent=false;
        }
        Log.d(TAG,"finished from onPause = "+finished);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FINISHED,finished);
        editor.apply();
        Log.d(TAG,"finished from onPause: "+preferences.getBoolean(FINISHED,true));
        Log.d(TAG,"onPause");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        Log.d(TAG,"onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop(){
        Log.d(TAG,"onStop");
        super.onStop();
    }
}
