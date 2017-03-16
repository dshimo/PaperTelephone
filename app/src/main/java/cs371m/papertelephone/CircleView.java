package cs371m.papertelephone;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {

    private int radius;
    private Paint mPaint;

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        radius = 10;
        mPaint = new Paint();
        mPaint.setAntiAlias(false);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(canvas.getWidth()/2, getHeight()/2, radius, mPaint);
    }

    public void setRadius(int r) {
        radius = r;
        invalidate();
    }

    public int getRadius() {
        return radius;
    }
}
