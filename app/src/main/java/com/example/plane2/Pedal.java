package com.example.plane2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Pedal {
    private boolean isPressed;

    private MainGameView gameView;
    private Bitmap bmp;
    //координаты левого верхнего угла
    private int x, y;
    private static final String TAG = Pedal.class.getSimpleName();

    public Pedal(MainGameView gv, Bitmap b, int x1, int y1) {
        gameView = gv;
        bmp = b;
        x = x1;
        y = y1;
        isPressed=false;
    }

    public void onDraw(Canvas c) {
        c.drawBitmap(bmp, x, y, null);
    }

    public boolean if_pressed(int x1,int y1){
        Log.d(TAG,"x1= "+x1+", y1= "+y1+"bool= "+ (x1>=x && x1<=x+bmp.getWidth() && y1>=y && y1<=y+bmp.getHeight()));
        return (x1>=x && x1<=x+bmp.getWidth() && y1>=y && y1<=y+bmp.getHeight());
    }
    public void set_pressed(boolean b){isPressed=b;}
    public boolean isPressed(){return isPressed;}
}