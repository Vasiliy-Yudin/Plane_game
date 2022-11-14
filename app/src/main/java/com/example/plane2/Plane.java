package com.example.plane2;

import static android.graphics.Bitmap.createScaledBitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Plane {
    private MainGameView gameView;
    private Bitmap bmp;
    private int x, y, speed;
    private int half_bmp_w, half_bmp_h;
    private static final String TAG = Plane.class.getSimpleName();
    public Plane(MainGameView gv,Bitmap b){
        gameView=gv;
        bmp=b;
        x=0;
        y=0;
        speed=0;
    }

    public Plane(MainGameView gv,Bitmap b,int X){
        gameView=gv;
        bmp=b;
        x=X;
        y=0;
        speed=0;
    }

    //if restored than we shouldn't redefine X
    public void setValues(boolean restored){
        bmp=createScaledBitmap(bmp, (int)(gameView.getWidth()/5.5),
                (int)(gameView.getWidth()/5.5*bmp.getHeight()/bmp.getWidth()), false);
        half_bmp_w=bmp.getWidth()/2;
        half_bmp_h=bmp.getHeight()/2;
        if(!restored)x=gameView.getWidth()/2;
        y=gameView.getHeight()-half_bmp_h-30;
    }

    synchronized public void update(){
        /*if(first_time){
            bmp=createScaledBitmap(bmp, (int)(gameView.getWidth()/5.5),
                    (int)(gameView.getWidth()/5.5*bmp.getHeight()/bmp.getWidth()), false);
            half_bmp_w=bmp.getWidth()/2;
            half_bmp_h=bmp.getHeight()/2;
            x=gameView.getWidth()/2;
            y=gameView.getHeight()-half_bmp_h-30;
            first_time=false;
        }*/
        speed=(int)GameActivity.speed;
        //Log.d(TAG,"speed= "+MainActivity.speed);
        if((x-half_bmp_w+speed>=3)&&(x+half_bmp_w+speed<=gameView.getWidth()-3)){
            x+=speed;
        }
    }

    public void onDraw(Canvas c)
    {
        c.drawBitmap(bmp, x-half_bmp_w, y-half_bmp_h, null);
    }

    public int getHalf_bmp_w(){return half_bmp_w;}
    public int getHalf_bmp_h(){return half_bmp_h;}

    public int getX(){return x;}
}
