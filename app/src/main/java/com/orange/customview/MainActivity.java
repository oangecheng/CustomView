package com.orange.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.orange.customview.widget.TemperatureChartView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TemperatureChartView temp = (TemperatureChartView)findViewById(R.id.temp);
        int t[] = new int[]{
          12, 15, 14, 16, 13, 14
        };

        temp.setTempDay(t);
        temp.invalidate();
    }
}
