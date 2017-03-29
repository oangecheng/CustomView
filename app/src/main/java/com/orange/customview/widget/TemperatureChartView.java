package com.orange.customview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.orange.customview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orange on 2017/3/28.
 */

public class TemperatureChartView extends View {
    //集合数目
    private static final int NUM = 6;
    //光滑变量
    private static final float smoothness = 0.5f;


    //白天温度集合
    private int tempDay[] = new int[6];
    //白天贝塞尔曲线控制点

    List<PointF> basselPoints = new ArrayList<>();
    private float controlDayX[] = new float[12];
    private float controlDayY[] = new float[12];

    //白天y轴集合
    private float YDay[] = new float[6];
    //夜间y轴集合
    private float YNight[] = new float[6];
    //x轴集合
    private float X[] = new float[6];

    //控件高度
    private float height;


    //白天和夜间画笔
    private Paint paintDay;
    private Paint paintNight;

    //画圆点的画笔
    private Paint pointPaint;
    //画文字的画笔
    private Paint textPaint;

    //原点坐标

    private int zeroX, zeroY;
    //屏幕密度
    private float density;
    //圆点直径
    private float radius;
    //当天的圆点直径
    private float radiusToday;
    //字体大小
    private int textSize;

    //控件距离左边的空白
    private float space;
    //文字距离点的距离
    private float textSpace;


    public TemperatureChartView(Context context) {
        this(context, null);
    }

    public TemperatureChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化画笔和原点坐标
     */
    private void init() {

        //初始化字体大小
        float textDensity = getResources().getDisplayMetrics().scaledDensity;
        textSize = (int) (14 * textDensity);

        //获取屏幕密度
        density = getResources().getDisplayMetrics().density;
        radius = 3 * density;
        radiusToday = 5 * density;
        space = 3 * density;
        textSpace = 10 * density;
        float strokeWidth = 2 * density;

        //初始化白天画笔
        paintDay = new Paint();
        paintDay.setColor(getResources().getColor(R.color.YELLOW_COMMON));
        paintDay.setStrokeWidth(strokeWidth);
        paintDay.setStyle(Paint.Style.STROKE);
        paintDay.setAntiAlias(true);
        //初始化夜间画笔
        paintNight = new Paint();
        paintNight.setColor(getResources().getColor(R.color.BLUE_COMMON));
        paintNight.setStrokeWidth(strokeWidth);
        paintNight.setStyle(Paint.Style.STROKE);
        paintNight.setAntiAlias(true);
        //初始化点画笔
        pointPaint = new Paint();
        pointPaint.setColor(getResources().getColor(R.color.YELLOW_COMMON));
        pointPaint.setAntiAlias(true);
        //初始化字体画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(getResources().getColor(R.color.WHITE_COMMON));
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (height == 0) {
            setXValues();
        }

        setYValue();
        drawDayChart(canvas, tempDay, YDay, 0);

    }

    //设置x轴集合
    private void setXValues() {
        height = getHeight();
        float width = getWidth();
        float w = width / 12;
        for (int i = 0; i < NUM; i++) {
            X[i] = (2 * i + 1) * w;
        }
    }

    //设置y轴集合
    private void setYValue() {
        //获取白天最高气温和最低气温
        int minDay = tempDay[0];
        int maxDay = tempDay[0];
        for (int i : tempDay) {
            if (i < minDay) {
                minDay = i;
            }
            if (i > maxDay) {
                maxDay = i;
            }
        }
        //将y轴划分为p份数
        float p = maxDay - minDay;
        //y轴一端到控件一端的距离
        float s = space + textSpace + textSize + radius;
        //y轴高度
        float y = height - 2 * s;

        if (p == 0) {
            for (int i = 0; i < NUM; i++) {
                YDay[i] = y / 2 + s;
            }
        } else {
            float value = y / p;
            for (int i = 0; i < NUM; i++) {
                YDay[i] = height - value * (tempDay[i] - minDay) - s;
            }
        }


    }

    private void drawScrollLine(Canvas canvas, int i,float y[] ){
        PointF start = new PointF(X[i],y[i]);
        PointF end = new PointF(X[i+1],y[i+1]);

        float wt = (start.x+end.x)/2;
        PointF p3 = new PointF();
        PointF p4 = new PointF();
        p3.y = start.y;
        p3.x = wt;
        p4.y = end.y;
        p4.x = wt;

        Path path = new Path();
        path.moveTo(start.x, start.y);
        path.cubicTo(p3.x, p3.y, p4.x, p4.y, end.x, end.y);
        canvas.drawPath(path, paintDay);

    }


    private void drawDayChart(Canvas canvas, int temp[], float y[], int type) {

        int alpha1 = 102;
        int alpha2 = 255;
        for (int i = 0; i < NUM; i++) {
            //画线
            if (i < NUM-1) {
                Path path = new Path();
//                path.moveTo(X[i],y[i]);
//                path.lineTo(X[i+1]-density,y[i+1]);
//                path.quadTo(X[i]-density*10,y[i]-density*10,X[i+1],y[i+1]);
//                canvas.drawLine(X[i],y[i],X[i+1],y[i+1],paintDay);
                drawScrollLine(canvas, i, y);
            }

            //画点
            if (i == 1) {
                pointPaint.setAlpha(alpha2);
                canvas.drawCircle(X[i], y[i], radiusToday, pointPaint);
            } else {
                pointPaint.setAlpha(alpha1);
                canvas.drawCircle(X[i], y[i], radius, pointPaint);
            }

            //画字
            textPaint.setAlpha(alpha2);
            drawText(canvas, textPaint, i, temp, y, type);

        }

    }

    /**
     * 绘制文字
     *
     * @param canvas    画布
     * @param textPaint 画笔
     * @param i         索引
     * @param temp      温度集合
     * @param y         y轴集合
     * @param type      折线种类：0，白天；1，夜间
     */
    private void drawText(Canvas canvas, Paint textPaint, int i, int[] temp, float[] y, int type) {
        switch (type) {
            case 0:
                // 显示白天气温
                canvas.drawText(temp[i] + "°", X[i], y[i] - radius - textSpace, textPaint);
                break;
            case 1:
                // 显示夜间气温
                canvas.drawText(temp[i] + "°", X[i], y[i] + textSpace + textSize, textPaint);
                break;
        }
    }


    public void setTempDay(int[] tempDay) {
        this.tempDay = tempDay;
    }
}
