package com.orange.customview.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.orange.customview.R;
import com.orange.customview.reader.BookPageFactory;
import com.orange.customview.reader.TxtReaderView;

import java.io.IOException;

public class ReaderActivity extends AppCompatActivity {

    private TxtReaderView mPageWidget;
    Bitmap mCurPageBitmap, mNextPageBitmap;
    Canvas mCurPageCanvas, mNextPageCanvas;
    BookPageFactory pagefactory;

    private  int w;
    private  int h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        txtReader();
    }

    /**
     * 翻页
     */
    private void txtReader(){

        mPageWidget = (TxtReaderView)findViewById(R.id.txt_reader);
        w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        mPageWidget.measure(w,h);

        w = 300;
        h = 400;

        int w1 =dipToPx(ReaderActivity.this, w);
        int h1 = dipToPx(ReaderActivity.this, h);

        mCurPageBitmap = Bitmap.createBitmap(dipToPx(ReaderActivity.this, w), dipToPx(ReaderActivity.this, h), Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap.createBitmap(dipToPx(ReaderActivity.this, w), dipToPx(ReaderActivity.this, h), Bitmap.Config.ARGB_8888);

        mCurPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);
        pagefactory = new BookPageFactory(dipToPx(ReaderActivity.this, w), dipToPx(ReaderActivity.this, h));

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.bg);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //按比率压缩图片
        float scaleWidth = ((float) w1) / width;
        float scaleHeight =((float) h1) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);


        pagefactory.setBgBitmap(bm);//设置背景图片

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
        mPageWidget.invalidate();

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
