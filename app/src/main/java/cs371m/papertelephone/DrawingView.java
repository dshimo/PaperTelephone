package cs371m.papertelephone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = Color.BLACK;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private boolean timeLeft;

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    /**
     * Method based on instructions provided here:
     * https://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-touch-interaction--mobile-19202
     */
    private void setupView() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        timeLeft = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        if (timeLeft) {
            canvas.drawPath(drawPath, drawPaint);
        }
    }

    public void setPaintColor(int c) {
        paintColor = c;
        if (timeLeft)
            drawPaint.setColor(paintColor);
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setBrushWidth(int w) {
        if (timeLeft)
            drawPaint.setStrokeWidth(w);
    }

    public int getBrushWidth() {
        return (int) drawPaint.getStrokeWidth();
    }

    public void setTimeLeft(boolean val) {
        timeLeft = val;
    }

    public boolean getTimeLeft() {
        return timeLeft;
    }

    /**
     * Method based on instructions provided here:
     * https://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-touch-interaction--mobile-19202
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (timeLeft) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(touchX, touchY);
                    drawPaint.setStyle(Paint.Style.FILL);
                    drawCanvas.drawCircle(touchX, touchY, getBrushWidth() / 2, drawPaint);
                    drawPaint.setStyle(Paint.Style.STROKE);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }
            invalidate();
        }
        return true;
    }
}
