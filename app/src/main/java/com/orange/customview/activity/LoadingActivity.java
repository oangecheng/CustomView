package com.orange.customview.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.orange.customview.R;
import com.orange.customview.loading.LeafLoadingView;

import java.util.Random;

public class LoadingActivity extends AppCompatActivity {

    private static final int REFRESH_PROGRESS = 0x10;
    private int mProgress = 0;
    private LeafLoadingView mLeafLoadingView;
    private ImageView iv;


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
                    } else if (mProgress < 100) {
                        mProgress += 2;
                        // 随机1200ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                new Random().nextInt(1200));
                        mLeafLoadingView.setProgress(mProgress);

                    } else {
                        mProgress = 0;
                        mLeafLoadingView.setProgress(mProgress);
                        mHandler.sendEmptyMessage(REFRESH_PROGRESS);
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        /**
         * 加载动画测试
         */
        mLeafLoadingView = (LeafLoadingView) findViewById(R.id.loading);
        iv = (ImageView)findViewById(R.id.fengche);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fengche);
        animation.setFillAfter(true);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        iv.startAnimation(animation);
        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 3000);


    }
}
