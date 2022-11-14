package com.example.plane2;


import static android.graphics.Bitmap.createScaledBitmap;
import static java.lang.System.currentTimeMillis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;

public class MainGameView extends SurfaceView implements SurfaceHolder.Callback {
    Paint paint;
    private long score, last_gas, last_brake;
    private GameThread thread;
    private Plane plane;
    private ArrayList<Building> buildings;
    private int border_1, border_2, border_3;
    private Pedal gas_pedal, brake_pedal;
    private static final String TAG = MainGameView.class.getSimpleName();
    public MainGameView(Context context)
    {
        super(context);
        GameActivity.finished=false;
        thread=new GameThread(getHolder(),this);
        getHolder().addCallback(this);
        setFocusable(true);
        plane=new Plane(this, BitmapFactory.decodeResource(getResources(),R.drawable.plane_for_app));
        score=0;
        buildings=new ArrayList<Building>();
        last_gas=currentTimeMillis();
        last_brake=currentTimeMillis();
        paint=new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(38);
    }

    public boolean startThread(){
        Log.d(TAG,"start thread");
        thread=new GameThread(getHolder(),this);
        thread.setRunning(true);
        thread.start();
        return true;
    }

    public boolean killThread(){
        Log.d(TAG,"beggining of killing the thread, running= "+thread.getRunning());
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {return false;}
        }
        Log.d(TAG,"the thread killed");
        return true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        killThread();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        //thread.start_animation();
        startThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //если поставили палец
        if(event.getAction()== MotionEvent.ACTION_DOWN){
            Log.d(TAG,"Action_down: x= "+event.getX()+", y= "+event.getY());
            //если ни одна из педалей еще не нажата
            if(!(gas_pedal.isPressed()||brake_pedal.isPressed())){
                if(gas_pedal.if_pressed((int)event.getX(),(int)event.getY()))gas_pedal.set_pressed(true);
                else if(brake_pedal.if_pressed((int)event.getX(),(int)event.getY()))brake_pedal.set_pressed(true);}
        }
        //если подняли палец, значит обе педали не нажаты
        if(event.getAction()== MotionEvent.ACTION_UP){
            Log.d(TAG,"Action_up: x= "+event.getX()+", y= "+event.getY());
            gas_pedal.set_pressed(false);
            brake_pedal.set_pressed(false);
        }
        //return super.onTouchEvent(event);
        return true;
    }

    protected void update(){
        if(score==0){
            //Log.d(TAG,"GameActivity.finished from update= "+GameActivity.finished);
            //set values of plane because we know view parameters now
            plane.setValues(false);//????????????????  разберись с false, вроде вообще это не нужно
            //defining pedals
            Bitmap b_ped=BitmapFactory.decodeResource(getResources(),R.drawable.brake_pedal);
            Bitmap g_ped=BitmapFactory.decodeResource(getResources(), R.drawable.gas_pedal);
            b_ped=createScaledBitmap(b_ped, (getWidth()/11),((int)(getWidth()/11*b_ped.getHeight()/b_ped.getWidth())),false);
            g_ped=createScaledBitmap(g_ped, (getWidth()/11),((int)(getWidth()/11*g_ped.getHeight()/g_ped.getWidth())),false);
            brake_pedal=new Pedal(this,b_ped,8,getHeight()*3/4);
            gas_pedal=new Pedal(this,g_ped,getWidth()-g_ped.getWidth()-8,getHeight()*3/4);
            //defining building's static values
            Bitmap b=BitmapFactory.decodeResource(getResources(),R.drawable.building_for_app);
            b = createScaledBitmap(b, (getWidth()/4),((int)(getWidth()/4*b.getHeight()/b.getWidth())),false);
            Building.setStaticValues(this,b);
            /*if(score==0){ */Building.setSpeed(15);buildings.add(new Building());/* }*/
            //defining borders for collision checking
            border_1=getHeight()-plane.getHalf_bmp_h()*2*211/224-30-Building.getHalf_bmp_h();
            border_2=getHeight()-plane.getHalf_bmp_h()*2*167/224-30-Building.getHalf_bmp_h();
            border_3=getHeight()-plane.getHalf_bmp_h()*2*103/224-30+Building.getHalf_bmp_h();
        }
        //collision checking
        //***********if(isCollision())Collision();
        //add new building
        if(buildings.get(buildings.size()-1).getY()>=plane.getHalf_bmp_h()*9) buildings.add(new Building());
        //delete buildings which is out of view
        Iterator<Building> iter = buildings.iterator();
        while(iter.hasNext()){
            if(iter.next().getY()>=getHeight()){
                iter.remove();
                iter = buildings.iterator();
            }
            else break;
        }
        //update buildings coordinates
        iter=buildings.iterator();
        while(iter.hasNext()){
            iter.next().update();
        }
        //update plane coordinates
        plane.update();
        //score depends on speed
        score+=Building.getSpeed();
        //decrease speed every second while brake pedal is pressed
        if(brake_pedal.isPressed()) {
            if(currentTimeMillis()-last_brake>=1000){
                if(Building.getSpeed() >= 20){
                    Building.setSpeed(Building.getSpeed()-10/*(Building.getSpeed()-10)/5*/);
                    last_brake=currentTimeMillis();
                }
            }
        }
        //increase speed every second while gas pedal is pressed
        else if(gas_pedal.isPressed()){
            if(Building.getSpeed()<65)
                if(currentTimeMillis()-last_gas>=1000){
                    if(Building.getSpeed()<45) Building.setSpeed(Building.getSpeed()+1);
                    else Building.setSpeed(Building.getSpeed()+(float)0.5);
                    last_gas=currentTimeMillis();
                }
        }
        //automatic speed decrease if nothing is pressed
        else{
            if(currentTimeMillis()-last_gas>=500) {
                if (Building.getSpeed() >= 12){Building.setSpeed(Building.getSpeed() - 2);
                last_gas=currentTimeMillis();
                }
            }
        }
    }

    //collision checking
    public boolean isCollision(){
        Iterator<Building> iter=buildings.iterator();
        int x,y;
        while(iter.hasNext()){
            Building b=iter.next();
            x=b.getX();
            y=b.getY();
            //checking 2 areas of plane
            if(y>=border_1 && y<border_3){
                if(y<=border_2) {
                    if(x>=get1LeftBorder() && x<=get1RightBorder())return true;
                }
                else{
                    if(x>=get2LeftBorder() && x<=get2RightBorder())return true;
                }
            }
        }
        return false;
    }

    //end of the game
    public void Collision(){
        Log.d(TAG,"Collision");
        GameActivity.finished=true;
        Log.d(TAG,"GameActivity.finished="+GameActivity.finished);
        Activity tmp = (Activity)getContext();
        //********
        SharedPreferences preferences = tmp.getSharedPreferences(GameActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(GameActivity.FINISH_SCORE,score/100);
        editor.apply();
        //********
        MainActivity.game_must_be_over=true;
        tmp.finish();
    }

    public void goBack(int times){
        Iterator<Building> iter = buildings.iterator();
        while(iter.hasNext()){
            Building tmp=iter.next();
            tmp.decreaseY(times);
        }
    }

    //drawing everything on canvas
    @Override
    protected void onDraw(Canvas canvas)
    {
        //canvas.drawBitmap();
        canvas.drawColor(Color.BLACK);
        plane.onDraw(canvas);
        Iterator<Building> iter = buildings.iterator();
        while(iter.hasNext()){
            iter.next().onDraw(canvas);
        }
        gas_pedal.onDraw(canvas);
        brake_pedal.onDraw(canvas);
        canvas.drawText("score: "+Long.toString(score/100), getWidth()/20,getHeight()/30,paint);
    }

    public int get1LeftBorder(){ return plane.getX()-plane.getHalf_bmp_w()*47/92-Building.getHalf_bmp_w(); }
    public int get1RightBorder(){ return plane.getX()+plane.getHalf_bmp_w()*47/92+Building.getHalf_bmp_w(); }

    public int get2LeftBorder(){ return plane.getX()-plane.getHalf_bmp_w()-Building.getHalf_bmp_w(); }
    public int get2RightBorder(){ return plane.getX()+plane.getHalf_bmp_w()+Building.getHalf_bmp_w(); }

    public float getSp(){
        return Building.getSpeed();
    }

}