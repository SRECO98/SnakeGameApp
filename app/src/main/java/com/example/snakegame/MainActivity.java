package com.example.snakegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    //List of snake points / snake length
    private final List<SnakePoints> snakePointsList = new ArrayList<>();
    private SurfaceView surfaceView;
    private TextView scoreTV;

    //Surface holder to draw snake on surface's canvas
    private SurfaceHolder surfaceHolder;
    //snake moving position. Values must be right,left,top,bottom.
    //By default snake move to right.
    private String movingPosition = "right";

    //score
    private int score = 0;
    //snake size / point size, this can be changed to make bigger snake.
    private static final int pointSize = 28;
    //default snake points at start
    private static final int defaultTalePoints = 3;
    //snake color
    private static final int snakeColor = Color.YELLOW;
    //snake moving speed. Value must be lie between 1 - 1000
    private static final int snakeMovingSpeed = 800;
    //Random points position cordinates on the surfaceView
    private int positionX, positionY;
    //timer to move snake / change snake position after a specific time(snakeMovingSpeed)
    private Timer timer;
    // canvas to draw snake and show on surface view
    private Canvas canvas = null;
    //point color / single point color of a snake
    private Paint pointColor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting surfaceView and score TextView  from xml file
        surfaceView = findViewById(R.id.surfaceView);
        scoreTV = findViewById(R.id.textViewScore);

        final AppCompatImageButton topBtn = findViewById(R.id.topBtn); //referencira se na sliku iz drawable file
        final AppCompatImageButton bottomBtn = findViewById(R.id.bottomBtn); //referencira se na sliku iz drawable file
        final AppCompatImageButton rightBtn = findViewById(R.id.rightBtn); //referencira se na sliku iz drawable file
        final AppCompatImageButton leftBtn = findViewById(R.id.leftBtn); //referencira se na sliku iz drawable file

        //adding callback to surfaceView
        surfaceView.getHolder().addCallback(this);

        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check is previous moving position is not bottom. Snake can't move.
                //For exm  if snake moving to bottom then snake can't directly start moving to top
                //Snake must take left or right first then top.
                if(!movingPosition.equals("bottom")){
                    movingPosition = "top";
                }
            }
        });

        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!movingPosition.equals("top")){
                    movingPosition = "bottom";
                }
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!movingPosition.equals("left")){
                    movingPosition = "right";
                }
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!movingPosition.equals("right")){
                    movingPosition = "left";
                }
            }
        });
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        //When surface is created then get SurfaceHolder from it and assign to surfaceHolder
        this.surfaceHolder = surfaceHolder;

        //ini data for snake/surfaceView
        init();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    private void init(){
        //Clear snake points / snake length
        snakePointsList.clear();
        //set default score as 0
        scoreTV.setText("0");

        //makes score 0
        score = 0;
        //setting default moving position
        movingPosition = "right";

        //default snake starting position on the screen
        int startPositionX = (pointSize) * defaultTalePoints;

        //making snake's default lenght / points
        for(int i = 0; i < defaultTalePoints; i++){
            //adding points to snake's tale
            SnakePoints snakePoints = new SnakePoints(startPositionX, pointSize);
            snakePointsList.add(snakePoints);

            //Increasing value for next point as snake's tale
            startPositionX = startPositionX - (pointSize * 2);
        }

        //add the random point on the screen to be eaten by snake.
        addPoint();
        //start moving snake / start the game
        moveSnake();

    }

    private void addPoint(){
        //Getting surfaceView height and width to add point on surface to be eaten by snake.
        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);

        int randomXPosition = new Random().nextInt(surfaceWidth / pointSize);
        int randomYPosition = new Random().nextInt(surfaceHeight / pointSize);

        //Check if randomXPosition is even or odd value. We need only even number.
        if((randomXPosition % 2) != 0){
            randomXPosition = randomXPosition + 1;
        }
        if((randomYPosition % 2) != 0){
            randomYPosition = randomYPosition + 1;
        }

        positionX = (pointSize * randomXPosition) + pointSize;
        positionY = (pointSize * randomYPosition) + pointSize;
    }

    private void moveSnake(){

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //getting head position  //getPositionX je metoda iz klase SnakePoints
                int headPositionX = snakePointsList.get(0).getPositionX();
                int headPositionY = snakePointsList.get(0).getPositionY();
                //check if snake eaten a point
                if(headPositionX == positionX && positionY == headPositionY){
                    //grow snake after eaten point
                    growSnake();
                    //add another random point on the screen
                    addPoint();
                }

                //check of which side snake is moving
                switch (movingPosition){
                    case "right":
                        //move snake's head to right.
                        //other points follow snake's head point to move the snake
                        snakePointsList.get(0).setPositionX(headPositionX + (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "left":
                        //move snake's head to left.
                        //other points follow snake's head point to move the snake
                        snakePointsList.get(0).setPositionX(headPositionX - (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "top":
                        //move snake's head to top.
                        //other points follow snake's head point to move the snake
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY - (pointSize * 2));
                        break;
                    case "bottom":
                        //move snake's head to bottom.
                        //other points follow snake's head point to move the snake
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY + (pointSize * 2));
                        break;
                }

                //Check if game over. Weather snake touch edges or snake itself.
                if(checkGameOver(headPositionX, headPositionY)){
                    //Stop timer / stop moving snake
                    timer.purge();
                    timer.cancel();

                    //show game over dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Your score = " + score);
                    builder.setTitle("GameOver");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Start again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //restart game / re-init data
                            init();
                        }
                    });
                    //times runs in background so we need to show dialog on main thread.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    });
                }else{
                    //Lock canvas on surfaceHolder to draw on it
                    canvas = surfaceHolder.lockCanvas();
                    //clear canvas with white color
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                    //change snake's head position. Other snake points will follow snake's head
                    canvas.drawCircle(snakePointsList.get(0).getPositionX(), snakePointsList.get(0).getPositionY(), pointSize, createPointColor());
                    //draw random point circle on the surface to be eaten by the snake
                    canvas.drawCircle(positionX, positionY, pointSize, createPointColor());
                    //follow points are Following snake's head, position 0 is head of snake, zato krecemo od jedan
                    for(int i = 1; i < snakePointsList.size(); i++){
                        int getTempPosotionX = snakePointsList.get(i).getPositionX();
                        int getTempPosotionY = snakePointsList.get(i).getPositionY();
                        //move points accross the head
                        snakePointsList.get(i).setPositionX(headPositionX);
                        snakePointsList.get(i).setPositionY(headPositionY);
                        canvas.drawCircle(snakePointsList.get(i).getPositionX(), snakePointsList.get(i).getPositionY(), pointSize, createPointColor());
                        //change head position
                        headPositionX = getTempPosotionX;
                        headPositionY = getTempPosotionY;
                    }
                    //unlock canvas to draw on surfaceView
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

            }
        }, (1000 - snakeMovingSpeed), (1000 - snakeMovingSpeed));
    }

    private void growSnake(){
        //Create new snake points
        SnakePoints snakePoints = new SnakePoints(0, 0);
        //add point to the snake's tale
        snakePointsList.add(snakePoints);
        //increase score of game
        score++;
        //setting score to TextView
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTV.setText(String.valueOf(score));
            }
        });
    }

    boolean checkGameOver(int headPositionX, int headPositionY){
        boolean gameOver = false;
        //check if snake's head touches edges
        if(  snakePointsList.get(0).getPositionX() < 0 ||
                snakePointsList.get(0).getPositionY() < 0 ||
                snakePointsList.get(0).getPositionX() >= surfaceView.getWidth() ||
                snakePointsList.get(0).getPositionY() >= surfaceView.getHeight()  )     {
            gameOver = true;
        }else{
            //check if snake's head touches snake itself.
            for(int i = 0; i < snakePointsList.size(); i++){
                if(     headPositionX == snakePointsList.get(i).getPositionX() &&
                        headPositionY == snakePointsList.get(i).getPositionY()  )   {
                    gameOver = true;
                }
            }
        }
        return gameOver;
    }

    private Paint createPointColor(){
        //check if color not defined before
        if(pointColor == null) {
            pointColor = new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true);// smoothness
        }
        return pointColor;
    }
}