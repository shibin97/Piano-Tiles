package com.example.shibin.application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class MyView extends View {

    private Paint tPaint, sPaint;
    private int score = 0;
    private float progress = 5;
    public static final String TAG = "TAG";
    private boolean callOnDraw = false;
    float counter1 = 0;
    int cellWidth, cellHeight, height, width;
    int array[][] = new int[5][4];
    float x, y;
    private boolean flag = true;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createColorArray();
    }

    private void createColorArray() {
        for (int i = 0; i < 5; i++) {
            Random random = new Random();
            int k = random.nextInt(4);
            while (i > 0 && array[i - 1][k] == Color.BLACK) {
                k = random.nextInt(4);
            }
            for (int j = 0; j < 4; j++) {
                if (j == k) {

                    if (i == 4) {
                        array[i][j] = Color.rgb(0, 255, 255);

                    } else {
                        array[i][j] = Color.BLACK;
                    }
                } else {
                    array[i][j] = Color.WHITE;
                }

            }
        }

        tPaint = new Paint();
        tPaint.setTextSize(150);
        tPaint.setColor(Color.RED);
        tPaint.setFakeBoldText(true);
        tPaint.setShadowLayer(5f, 8f, 8f, Color.DKGRAY);

        sPaint = new Paint();
        sPaint.setTextSize(75);
        sPaint.setColor(Color.RED);
        sPaint.setFakeBoldText(true);
        sPaint.setShadowLayer(5f, 8f, 8f, Color.DKGRAY);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        paint.setColor(Color.RED);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                canvas.drawRect((cellWidth * j), cellHeight * (i - 1) + counter1, (cellWidth * (j + 1)) - 10, cellHeight * i + counter1, getPaint(array[i][j]));
                if (flag) {
                    if (array[4][j] == Color.rgb(0, 255, 255)) {
                        canvas.drawText("S", j * cellWidth + 100, 3 * cellHeight + 80, sPaint);
                        canvas.drawText("T", j * cellWidth + 100, 3 * cellHeight + 160, sPaint);
                        canvas.drawText("A", j * cellWidth + 100, 3 * cellHeight + 240, sPaint);
                        canvas.drawText("R", j * cellWidth + 100, 3 * cellHeight + 320, sPaint);
                        canvas.drawText("T", j * cellWidth + 100, 3 * cellHeight + 400, sPaint);
                    }
                }
            }
        }

        canvas.drawText("" + score, 500, 200, tPaint);

        if (counter1 < cellHeight) {
            counter1 += progress;

        } else {
            counter1 = 0;
            if (killCode()) {
                counter1 = -cellHeight;
                return;
            } else {
                modifyArray();
            }
        }

        if (callOnDraw) {
            invalidate();
        }

    }

    private void modifyArray() {
        for (int i = 3; i >= 0; i--) {
            for (int j = 0; j < 4; j++) {
                array[i + 1][j] = array[i][j];
            }
        }

        Random random = new Random();
        int k = random.nextInt(4);
        while (array[1][k] == Color.BLACK) {
            k = random.nextInt(4);
        }
        for (int i = 0; i < 4; i++) {
            if (i == k) {
                array[0][i] = Color.BLACK;

            } else {
                array[0][i] = Color.WHITE;
            }
        }

    }

    boolean killCode() {

        for (int j = 0; j < 4; j++) {
            if (array[4][j] == Color.BLACK) {
                showDialog();
                return true;
            }

        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        height = parentHeight;
        width = parentWidth;
        cellHeight = parentHeight / 4;
        cellWidth = parentWidth / 4;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private Paint getPaint(int color) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        return paint;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        x = event.getX();
        y = event.getY();
        progress += 0.5;

        Log.e(TAG, "onTouchEvent: I" + ((int) y / cellHeight) + 1 + "onTouchEvent: j" + (int) x / cellWidth);
        if (array[(((int) y + cellHeight) / cellHeight)][(int) x / cellWidth] == Color.rgb(0, 255, 255)) {
            invalidate();
            flag = false;
            callOnDraw = true;
        }

        if (callOnDraw) {
            if (array[(((int) y + cellHeight - (int) counter1) / cellHeight)][(int) x / cellWidth] == Color.BLACK) {
                array[(((int) y + cellHeight - (int) counter1) / cellHeight)][(int) x / cellWidth] = Color.rgb(177, 217, 244);
                MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.button11);
                mp.start();
                score += 1;
            }

            if (array[(((int) y + cellHeight - (int) counter1) / cellHeight)][(int) x / cellWidth] == Color.WHITE) {
                array[(((int) y + cellHeight - (int) counter1) / cellHeight)][(int) x / cellWidth] = Color.RED;
                callOnDraw = false;
                showDialog();

                MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.button2);
                mp.start();

            }
        }
        return super.onTouchEvent(event);
    }

    void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to retry")
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        getContext().startActivity(intent);
                    }

                });
        builder.create().show();
    }
}






