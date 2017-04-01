package com.orange.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.orange.customview.loading.LeafLoadingView;
import com.orange.customview.reader.BookPageFactory;
import com.orange.customview.reader.TxtReaderView;
import com.orange.customview.weather.TemperatureChartView;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private static final int REFRESH_PROGRESS = 0x10;
    private int mProgress = 0;
    private LeafLoadingView mLeafLoadingView;
    private ImageView iv;

    private TxtReaderView mPageWidget;
    Bitmap mCurPageBitmap, mNextPageBitmap;
    Canvas mCurPageCanvas, mNextPageCanvas;
    BookPageFactory pagefactory;


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_PROGRESS:
                    if (mProgress < 40) {
                        mProgress += 2;
                        // 随机800ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                new Random().nextInt(800));
                        mLeafLoadingView.setProgress(mProgress);
                    } else if (mProgress<100){
                        mProgress += 2;
                        // 随机1200ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                new Random().nextInt(1200));
                        mLeafLoadingView.setProgress(mProgress);

                    }else {
                        mProgress = 0;
                        mLeafLoadingView.setProgress(mProgress);
                        mHandler.sendEmptyMessage(REFRESH_PROGRESS);
                    }
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mPageWidget = (TxtReaderView)findViewById(R.id.txt_reader);


        mCurPageBitmap = Bitmap.createBitmap(dipToPx(MainActivity.this, 150), dipToPx(MainActivity.this, 200), Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap.createBitmap(dipToPx(MainActivity.this, 150), dipToPx(MainActivity.this, 200), Bitmap.Config.ARGB_8888);

        mCurPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);
        pagefactory = new BookPageFactory(dipToPx(MainActivity.this, 150), dipToPx(MainActivity.this, 200));

        pagefactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.bg));//设置背景图片

        try {
            pagefactory.openbook(Environment.getExternalStorageDirectory().getAbsolutePath()+"/1000.txt");//打开文件
            pagefactory.onDraw(mCurPageCanvas);//将文字绘于手机屏幕
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            Toast.makeText(this, "电子书不存在,请将《test.txt》放在SD卡根目录下",
                    Toast.LENGTH_SHORT).show();
        }

        mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);

        mPageWidget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                // TODO Auto-generated method stub

                boolean ret=false;
                if (v == mPageWidget) {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        //停止动画。与forceFinished(boolean)相反，Scroller滚动到最终x与y位置时中止动画。
                        mPageWidget.abortAnimation();
                        //计算拖拽点对应的拖拽角
                        mPageWidget.calcCornerXY(e.getX(), e.getY());
                        //将文字绘于当前页
                        pagefactory.onDraw(mCurPageCanvas);
                        if (mPageWidget.DragToRight()) {
                            //是否从左边翻向右边
                            try {
                                //true，显示上一页
                                pagefactory.prePage();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            if(pagefactory.isfirstPage())return false;
                            pagefactory.onDraw(mNextPageCanvas);
                        } else {
                            try {
                                //false，显示下一页
                                pagefactory.nextPage();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            if(pagefactory.islastPage())return false;
                            pagefactory.onDraw(mNextPageCanvas);
                        }
                        mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
                    }

                    ret = mPageWidget.doTouchEvent(e);
                    return ret;
                }
                return false;
            }

        });
    }




//        /**
//         * 加载动画测试
//         */
//        mLeafLoadingView = (LeafLoadingView) findViewById(R.id.loading);
//        iv = (ImageView)findViewById(R.id.fengche);
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fengche);
//        animation.setFillAfter(true);
//        LinearInterpolator lin = new LinearInterpolator();
//        animation.setInterpolator(lin);
//        iv.startAnimation(animation);
//        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 3000);
//
//
//        /**
//         * 天气预报界面测试
//         */
//        TemperatureChartView temp = (TemperatureChartView)findViewById(R.id.temp);
//        int t1[] = new int[]{
//          12, 15, 14, 16, 13, 14
//        };
//        int t2[] = new int[]{
//                10, 13, 12, 14, 11, 13
//        };
//        int imageId[] = new int[]{
//                R.mipmap.logo1038_03,R.mipmap.logo1038_06,R.mipmap.logo1038_14,
//                R.mipmap.logo1038_19,R.mipmap.logo1038_25,R.mipmap.logo1038_09
//        };
//
//        temp.setTempDay(t1);
//        temp.setTempNight(t2);
//        temp.setWeatherImage(imageId);
//    }


    private int dipToPx(Context context, int dip) {
        return (int) (dip * getScreenDensity(context) + 0.5f);
    }

    private float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getMetrics(dm);
            return dm.density;
        } catch (Exception e) {
            return DisplayMetrics.DENSITY_DEFAULT;
        }
    }
}
