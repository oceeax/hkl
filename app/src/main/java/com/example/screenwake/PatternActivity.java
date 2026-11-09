package com.example.screenwake;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.view.*;
import android.graphics.*;
import java.util.Locale;

public class PatternActivity extends Activity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (Build.VERSION.SDK_INT >= 27) { setTurnScreenOn(true); setShowWhenLocked(true); }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new PatternView());
    }

    private class PatternView extends View {
        Paint p = new Paint(3);
        long start = System.currentTimeMillis();
        PatternView() { super(PatternActivity.this); }
        @Override protected void onDraw(Canvas c) {
            int w=getWidth(), h=getHeight();
            c.drawColor(Color.rgb(10,17,38));
            float x=w/2f, y=h*.42f, r=Math.min(w,h)*.27f;
            int[] cs={Color.CYAN,0xff785aff,0xffff50aa,0xffffbe32};
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(5);
            for(int i=0;i<4;i++){p.setColor(cs[i]);c.drawCircle(x,y,r*(.5f+i*.18f),p);}
            p.setColor(Color.CYAN); p.setStrokeWidth(2);
            for(int i=0;i<12;i++){double a=i*Math.PI/6;c.drawLine(x+(float)Math.cos(a)*r*.5f,y+(float)Math.sin(a)*r*.5f,x+(float)Math.cos(a)*r,y+(float)Math.sin(a)*r,p);}
            p.setStyle(Paint.Style.FILL);p.setColor(Color.WHITE);p.setTextAlign(Paint.Align.CENTER);
            p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(Math.max(26,w*.07f));
            c.drawText("屏幕已点亮",x,h*.78f,p);
            long left=Math.max(0,300-(System.currentTimeMillis()-start)/1000);
            p.setColor(Color.CYAN);p.setTextSize(Math.max(20,w*.05f));
            c.drawText(String.format(Locale.CHINA,"剩余 %d:%02d",left/60,left%60),x,h*.85f,p);
            if(left==0) finishLater(); else postInvalidateDelayed(1000);
        }
        private void finishLater(){ post(() -> finish()); }
    }
}
