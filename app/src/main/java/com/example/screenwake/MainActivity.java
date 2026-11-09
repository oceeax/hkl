package com.example.screenwake;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.content.Intent;

public class MainActivity extends Activity {
    private CountDownTimer timer;
    private PatternView patternView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 27) {
            setTurnScreenOn(true);
            setShowWhenLocked(true);
        }

        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        patternView = new PatternView();
        setContentView(patternView);

        timer = new CountDownTimer(300000L, 1000L) {
            @Override public void onTick(long millisUntilFinished) {
                if (patternView != null) patternView.setRemaining(millisUntilFinished);
            }
            @Override public void onFinish() {
                finish();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        super.onDestroy();
    }

    private class PatternView extends View {
        private final Paint p = new Paint(3);
        private long remaining = 300000L;

        PatternView() {
            super(MainActivity.this);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        void setRemaining(long value) {
            remaining = value;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas c) {
            super.onDraw(c);
            int w = getWidth(), h = getHeight();
            c.drawColor(Color.rgb(10, 17, 38));

            // 明亮的几何图案：即使远处也能看出屏幕正在显示
            float cx = w / 2f, cy = h * 0.43f;
            float radius = Math.min(w, h) * 0.30f;

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(3f, w * 0.008f));
            int[] colors = {
                Color.rgb(0, 220, 255), Color.rgb(120, 90, 255),
                Color.rgb(255, 80, 170), Color.rgb(255, 190, 50)
            };
            for (int i = 0; i < 4; i++) {
                p.setColor(colors[i]);
                c.drawCircle(cx, cy, radius * (0.48f + i * 0.18f), p);
            }

            p.setStrokeWidth(Math.max(2f, w * 0.004f));
            p.setColor(Color.rgb(105, 220, 255));
            for (int i = 0; i < 12; i++) {
                double a = Math.PI * 2 * i / 12.0;
                float x1 = cx + (float)Math.cos(a) * radius * 0.48f;
                float y1 = cy + (float)Math.sin(a) * radius * 0.48f;
                float x2 = cx + (float)Math.cos(a) * radius * 1.02f;
                float y2 = cy + (float)Math.sin(a) * radius * 1.02f;
                c.drawLine(x1, y1, x2, y2, p);
            }

            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.WHITE);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
            p.setTextSize(Math.max(24f, w * 0.075f));
            c.drawText("屏幕已点亮", cx, h * 0.78f, p);

            p.setColor(Color.rgb(80, 220, 255));
            p.setTextSize(Math.max(18f, w * 0.052f));
            long seconds = Math.max(0, (remaining + 999) / 1000);
            c.drawText("剩余 " + (seconds / 60) + ":" +
                    String.format(java.util.Locale.US, "%02d", seconds % 60),
                    cx, h * 0.84f, p);

            p.setColor(Color.rgb(185, 198, 225));
            p.setTextSize(Math.max(13f, w * 0.034f));
            c.drawText("5 分钟后自动退出", cx, h * 0.90f, p);
        }
    }
}
