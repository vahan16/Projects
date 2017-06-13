package com.example.vahan.simplecursonadapter;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.graphics.PorterDuff;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vahan on 6/7/17.
 */

public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF000000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;
    private boolean erase=false;

    List<DrawnPath> l_dp = new ArrayList<>();
    int currentPosition = -1;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
//        drawPaint.setStrokeWidth(12);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        brushSize = getResources().getInteger(R.integer.small_size);
        lastBrushSize = brushSize;
        drawPaint.setStrokeWidth(brushSize);

    }



    public void undo(){
        if(currentPosition >= 0){
            canvasBitmap.eraseColor(Color.TRANSPARENT);
            for(int i = 0 ; i < currentPosition ; ++i)
                l_dp.get(i).redraw();
            currentPosition--;
            invalidate();
        }
    }

    public void redo(){
        if(currentPosition < l_dp.size() - 1) {
            currentPosition++;
            l_dp.get(currentPosition).redraw();
            invalidate();
        }
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setErase(boolean isErase){
        erase=isErase;
        if(erase) {
            drawPaint.setColor(Color.WHITE);
        }
        else drawPaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }

    public float getLastBrushSize(){
        return lastBrushSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:

                drawCanvas.drawPath(drawPath, drawPaint);
                if(l_dp.size() > 0)
                    l_dp = l_dp.subList(0, currentPosition + 1);
                l_dp.add(new DrawnPath(brushSize, drawPath, paintColor));
                currentPosition++;

                drawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(String newColor){
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    private class DrawnPath{

        public float brushSize;
        public Path path;
        public int color;

        public DrawnPath(float brushSize, Path path, int color){

            this.brushSize = brushSize;
            this.path = new Path(path);
            this.color = color;

        }

        public void redraw(){
            float previous_width = drawPaint.getStrokeWidth();
            drawPaint.setStrokeWidth(brushSize);
            drawPaint.setColor(color);
            drawCanvas.drawPath(path, drawPaint);
            drawPaint.setColor(paintColor);
            drawPaint.setStrokeWidth(previous_width);

        }



    }
}
