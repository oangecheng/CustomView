package com.orange.customview.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.orange.customview.R;

/**
 * Created by Orange on 2017/3/28.
 */


/**
 * 实现温度的圆滑曲线图白天为黄色曲线，晚上为蓝色曲线
 */
public class TemperatureChartView extends View {
    //集合数目
    private static final int NUM = 6;

    //白天温度集合
    private int tempDay[] = new int[6];
    //夜间温度集合
    private int tempNight[] = new int[6];
    //天气图标集合
    private int weatherImage[] = new int[6];


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
    //y轴方向曲线距离控件的距离
    private float timeSpace;


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
        timeSpace = 60 * density;
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
        drawChart(canvas, tempDay, YDay, 0);
        drawChart(canvas, tempNight, YNight, 1);

    }


    private void drawChart(Canvas canvas, int temp[], float y[], int type) {

        int alpha1 = 102;
        int alpha2 = 255;
        for (int i = 0; i < NUM; i++) {
            //画线
            if (i < NUM - 1) {
                drawScrollLine(canvas, i, y, type);
            }

            if (type == 0) {
                pointPaint.setColor(getResources().getColor(R.color.YELLOW_COMMON));
            } else {
                pointPaint.setColor(getResources().getColor(R.color.BLUE_COMMON));
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

            //绘制天气图标
            drawPicture(canvas, weatherImage);
        }

    }

    //设置x轴集合
    private void setXValues() {
        //获取控件高度
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

        //获取夜间最高气温和最低气温
        int minNight = tempNight[0];
        int maxNight = tempNight[0];
        for (int i : tempNight) {
            if (i < minNight) {
                minNight = i;
            }
            if (i > maxNight) {
                maxNight = i;
            }
        }


        int minTemp = minNight < minDay ? minNight : minDay;
        int maxTemp = maxNight > maxDay ? maxNight : maxDay;


        //将y轴划分为p份数
        float p = maxTemp - minTemp;
        //y轴一端到控件一端的距离
        float s = space + textSpace + textSize + radius + timeSpace;
        //y轴高度
        float y = height - 2 * s;

        //温度相同的时候
        if (p == 0) {
            for (int i = 0; i < NUM; i++) {
                YDay[i] = y / 2 + s;
                YNight[i] = y / 2 + s;
            }
        } else {
            float value = y / p;
            for (int i = 0; i < NUM; i++) {
                YDay[i] = height - value * (tempDay[i] - minTemp) - s;
                YNight[i] = height - value * (tempNight[i] - minTemp) - s;
            }
        }


    }

    /**
     * 绘制天气图标
     *
     * @param canvas
     * @param pic
     */
    private void drawPicture(Canvas canvas, int pic[]) {

        //获取两个点的水平间距
        int w = (int) (X[1] - X[0]);
        float rate = 0.6f;

        for (int i = 0; i < pic.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), pic[i]);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            //按比率压缩图片
            float scaleWidth = (w * rate) / width;
            float scaleHeight = (w * rate) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

            canvas.drawBitmap(bm, X[i] - 0.3f * w, 2 * textSpace, new Paint());
        }
    }


    /**
     * 绘制曲线
     *
     * @param canvas 画布
     * @param i      索引
     * @param y      y轴集合
     */
    private void drawScrollLine(Canvas canvas, int i, float y[], int type) {
        PointF start = new PointF(X[i], y[i]);
        PointF end = new PointF(X[i + 1], y[i + 1]);

        float wt = (start.x + end.x) / 2;
        PointF p3 = new PointF();
        PointF p4 = new PointF();
        p3.y = start.y;
        p3.x = wt;
        p4.y = end.y;
        p4.x = wt;

        Path path = new Path();
        path.moveTo(start.x, start.y);
        //三阶贝塞尔曲线
        path.cubicTo(p3.x, p3.y, p4.x, p4.y, end.x, end.y);
        if (type == 0) {
            canvas.drawPath(path, paintDay);
        } else {
            canvas.drawPath(path, paintNight);
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

        for (int j = 0; j < NUM; j++) {
            if (j == 0) {
                canvas.drawText("昨日", X[j], height - 2 * textSpace, textPaint);
            } else {
                canvas.drawText(6 + 3 * (j - 1) + ":00", X[j], height - 2 * textSpace, textPaint);
            }

        }
    }


    public void setTempDay(int[] tempDay) {
        this.tempDay = tempDay;
        invalidate();
    }

    public void setTempNight(int[] tempNight) {
        this.tempNight = tempNight;
        invalidate();
    }

    public void setWeatherImage(int[] weatherImage) {
        this.weatherImage = weatherImage;
        invalidate();
    }
}
