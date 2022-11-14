package com.example.plane2;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

public class Building {
    private static final String TAG = Building.class.getSimpleName();
    private static MainGameView gameView;
    private static Bitmap bmp;
    private int x, y;
    private static float speed;
    private static int half_bmp_w, half_bmp_h;
    public static int prev_rand;
    private static Random rand;
    public Building(){
        while(true){
            int temp=rand.nextInt(4);
            if(temp!=prev_rand){prev_rand=temp;break;}
        }
        x=prev_rand*half_bmp_w*2+half_bmp_w;
        y=-half_bmp_h-(int)speed;
    }

    public Building(int X,int Y){
        x=X;
        y=Y;
    }

    public static void setStaticValues(MainGameView gv, Bitmap b){
        rand=new Random();
        gameView=gv;
        bmp=b;
        half_bmp_w=b.getWidth()/2;
        half_bmp_h=b.getHeight()/2;
    }

    public static void setSpeed(float sp){speed=sp;}
    public static float getSpeed(){return speed;}

    synchronized public void update(){
        //Log.d(TAG,"speed= "+MainActivity.speed);
        y+=speed;
    }

    public void onDraw(Canvas c)
    {
        c.drawBitmap(bmp, x-half_bmp_w, y-half_bmp_h, null);
    }

    public int getY(){return y;}
    public int getX(){return x;}

    public static int getHalf_bmp_w(){return half_bmp_w;}
    public static int getHalf_bmp_h(){return half_bmp_h;}

    public void decreaseY(int times){
        y-=times*speed;
    }
}

