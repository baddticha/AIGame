package com.example.aigame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameLoop extends Thread {


    //These privates act as variables that can be used in different classes

    private static final double MAX_UPS = 60.0;
    private static final double UPS_PERIOD = 1E+3/MAX_UPS;
    private boolean isRunning = false;
    private SurfaceHolder surfaceHolder;
    private Game game;
    private double averageUPS;
    private double averageFPS;

    //Gameloop is a class that takes two parameters, 1. Game instance 2.surfaceholder
    public GameLoop(Game game, SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        this.game = game;
    }

    //This is building a method to get averageUPS
    public double getAverageUPS() {
        return averageUPS;
    }

    //This is building a method to get averageFPS
    public double getAverageFPS() {
        return averageFPS;
    }

    //This is building a method to start looping
    public void startLoop() {
        isRunning = true;
        start();
    }

    @Override
    public void run() {
        super.run();

        //Declare time and cycle count variables
        int updateCount = 0;
        int frameCount = 0;

        //These are variables used in the run method
        long startTime;
        long elapsedTime;
        long sleepTime;

        //loop starts here

        //Create a canvas object and name it canvas
        Canvas canvas;

        //Calculate start time for first loop entry
        startTime = System.currentTimeMillis();

        //loop to calculate average UPS and FPS starts here
        while (isRunning){

            //try to update and render game
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    game.update();
                    updateCount++;
                    game.draw(canvas);
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
                frameCount++;

            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }

            //Pause game loop to not exceed UPS
            elapsedTime = System.currentTimeMillis() - startTime;
            sleepTime= (long) (updateCount * UPS_PERIOD - elapsedTime);
            if(sleepTime>0){
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //Skip frames to keep up with target UPS
            while(sleepTime<0 && updateCount<MAX_UPS-1){
                game.update();
                updateCount++;
                elapsedTime = System.currentTimeMillis() - startTime;
                sleepTime= (long) (updateCount * UPS_PERIOD - elapsedTime);
            }

            //calculate average UPS and FPS
            elapsedTime = System.currentTimeMillis() - startTime;
            if(elapsedTime >=1000){
                averageUPS = updateCount / (1E-3 * elapsedTime);
                averageFPS = frameCount / (1E-3 * elapsedTime);
                updateCount = 0;
                frameCount = 0;
                startTime = System.currentTimeMillis();
            }


        }
    }
}
