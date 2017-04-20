package com.orange.customview.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.orange.customview.R;
import com.orange.customview.weather.TemperatureChartView;

public class WeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        /**
         * 天气预报界面测试
         */
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
