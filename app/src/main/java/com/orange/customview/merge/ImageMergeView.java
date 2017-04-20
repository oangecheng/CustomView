package com.orange.customview.merge;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


import com.orange.customview.R;

/**
 * Created by Orange on 2017/4/11.
 */

public class ImageMergeView extends View {

    private Paint paint;
    private Context context;

    private Bitmap carrierBitmap;
    private Bitmap secretBitmap;

    private int width;
    private int height;

    private Point carrierTopLeft;
    private Point carrierBottomRight;

    private Point secretTopLeft;
    private Point secretBottomRight;


    private int wSpeed;
    private int hSpeed;

    private Rect carrierRect;
    private Rect secretRect;

    public ImageMergeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        this.context = context;

    }

    private void init() {
        paint = new Paint();

        carrierBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        secretBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.leaf);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        wSpeed = width / 300;
        hSpeed = height / 300;

        carrierTopLeft = new Point(0, height * 9 / 10);
        carrierBottomRight = new Point(width / 10, height);

        secretTopLeft = new Point(width * 9 / 10, 0);
        secretBottomRight = new Point(width, height / 10);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        carrierRect = new Rect(carrierTopLeft.x, carrierTopLeft.y, carrierBottomRight.x, carrierBottomRight.y);
        secretRect = new Rect(secretTopLeft.x, secretTopLeft.y, secretBottomRight.x, secretBottomRight.y);

        if (carrierBitmap != null && secretBitmap != null) {
            canvas.drawBitmap(carrierBitmap, null, carrierRect, paint);
            canvas.drawBitmap(secretBitmap, null, secretRect, paint);
        }

        carrierTopLeft.x += wSpeed;
        carrierTopLeft.y -=hSpeed;

        carrierBottomRight.x += wSpeed;
        carrierBottomRight.y -= hSpeed;



        secretTopLeft.x -= wSpeed;
        secretTopLeft.y += hSpeed;

        secretBottomRight.x -= wSpeed;
        secretBottomRight.y += hSpeed;

        if (carrierTopLeft.x == width*9/10){

//            carrierTopLeft.x = 0;
//            carrierTopLeft.y =height * 9 / 10;
//
//            carrierBottomRight.x = width / 10;
//            carrierBottomRight.y = height;
//
//            secretTopLeft.x = width * 9 / 10;
//            secretTopLeft.y =0;
//
//            secretBottomRight.x = width;
//            secretBottomRight.y = height / 10;



        }


        postInvalidateDelayed(1, 0, 0, width, height);

    }


    public void setCarrierBitmap(Bitmap carrierBitmap) {
        this.carrierBitmap = carrierBitmap;
        invalidate();
    }

    public void setSecretBitmap(Bitmap secretBitmap) {
        this.secretBitmap = secretBitmap;
        invalidate();
    }
}
