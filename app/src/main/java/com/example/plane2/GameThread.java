package com.example.plane2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameThread extends Thread{
    private static final String TAG = GameThread.class.getSimpleName();
    private boolean running/* = false*/;
    private SurfaceHolder surfaceHolder;
    private MainGameView gameView;
    public GameThread(SurfaceHolder surfaceH,MainGameView gameV){
        super();
        surfaceHolder=surfaceH;
        gameView=gameV;
        running=false;
    }
    /**Задание состояния потока*/
    public void setRunning(boolean run)
    {
        running = run;
    }
    public boolean getRunning(){
        return running;
    }
    /** Действия, выполняемые в потоке */

    public void start_animation(){
        Canvas canvas;
        Log.d(TAG,"Starting game loop");
        for(int i=3;i>0;--i){
            canvas=null;
            try{
                canvas= this.surfaceHolder.lockCanvas();
                synchronized(surfaceHolder){
                    Paint p=new Paint();
                    p.setTextSize(50);
                    p.setColor(Color.RED);
                    canvas.drawText(String.valueOf(i),gameView.getWidth()/2,gameView.getHeight()/2,p);
                    sleep(800);
                }
            }
            catch (Exception e) { }
            finally{
                if(canvas!=null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    @Override
    public void run() {
        Canvas canvas;
        Log.d(TAG,"Starting game loop");
        while(running){
            canvas=null;
            try{
                gameView.update();
                if(!gameView.isCollision()) {
                    canvas= this.surfaceHolder.lockCanvas();
                    synchronized(surfaceHolder){
                        gameView.onDraw(canvas);//Вызываем метод для рисования
                    }
                }
                else {sleep(700);running=false; gameView.Collision();}
            }
            catch (Exception e) { }
            finally{
                if(canvas!=null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
        Log.d(TAG,"Game loop executed ");
    }
}