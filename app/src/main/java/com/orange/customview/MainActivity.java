package com.orange.customview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.orange.customview.loading.LeafLoadingView;
import com.orange.customview.weather.TemperatureChartView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private static final int REFRESH_PROGRESS = 0x10;
    private int mProgress = 0;
    private LeafLoadingView mLeafLoadingView;
    private ImageView iv;


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_PROGRESS:
                    if (mProgress < 40) {
                        mProgress += 5;
                        // 随机800ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                new Random().nextInt(800));
                        mLeafLoadingView.setProgress(mProgress);
                    } else if (mProgress<100){
                        mProgress += 5;
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

        mLeafLoadingView = (LeafLoadingView) findViewById(R.id.loading);
        iv = (ImageView)findViewById(R.id.fengche);


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fengche);
        animation.setFillAfter(true);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        iv.startAnimation(animation);

        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 3000);


        TemperatureChartView temp = (TemperatureChartView)findViewById(R.id.temp);
        int t1[] = new int[]{
          12, 15, 14, 16, 13, 14
        };
        int t2[] = new int[]{
                10, 13, 12, 14, 11, 13
        };
        int imageId[] = new int[]{
                R.mipmap.logo1038_03,R.mipmap.logo1038_06,R.mipmap.logo1038_14,
                R.mipmap.logo1038_19,R.mipmap.logo1038_25,R.mipmap.logo1038_09
        };

        temp.setTempDay(t1);
        temp.setTempNight(t2);
        temp.setWeatherImage(imageId);
    }
}
