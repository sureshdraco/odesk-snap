package com.snapchat;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawPanel extends View {

    ArrayList<ArrayList> allpoints;
    ArrayList<ArrayList> allstrokes;
    ArrayList<Paint> allpaints;
    private Paint paint, paint_new;
    private ArrayList points;
    private ArrayList strokes;
    Boolean is_changed = false;
    PaintActivity activity;

    public DrawPanel(Context context, PaintActivity act) {
        super(context);
        activity = act;
        allpoints = new ArrayList<ArrayList>();
        allstrokes = new ArrayList<ArrayList>();
        allpaints = new ArrayList<Paint>();
        points = new ArrayList();
        strokes = new ArrayList();
        paint = createPaint(Color.WHITE, 8);
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        this.setBackgroundColor(Color.TRANSPARENT);
        if (allpoints.size() > 0) {

            for (int i = 0; i < allpoints.size(); i++) {
                paint_new = allpaints.get(i);
                for (Object obj : allstrokes.get(i)) {
                    drawStroke((ArrayList) obj, c);
                }
                drawStroke(allpoints.get(i), c);
            }

        }

        paint_new = paint;
        for (Object obj : strokes) {
            drawStroke((ArrayList) obj, c);
        }
        drawStroke(points, c);
    }

    public void change_color(String color) {
        allpoints.add(points);
        allstrokes.add(strokes);
        allpaints.add(paint);
//			 Log.e("arraylist", ""+allpaints.size()+"--"+allpoints.size()+"--"+allstrokes.size());
        points = new ArrayList();
        strokes = new ArrayList();
        paint = createPaint(Color.parseColor(color), 8);
        is_changed = false;
    }

    public void remove_color() {
        allpoints = new ArrayList<ArrayList>();
        allstrokes = new ArrayList<ArrayList>();
        allpaints = new ArrayList<Paint>();
//			 Log.e("arraylist", ""+allpaints.size()+"--"+allpoints.size()+"--"+allstrokes.size());
        points = new ArrayList();
        strokes = new ArrayList();
        paint = createPaint(Color.parseColor("#00000000"), 8);
        is_changed = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            points.add(new Point((int) event.getX(), (int) event.getY()));
            invalidate();
        }

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            this.strokes.add(points);
            points = new ArrayList();
        }
        activity.closedrawers();
        return true;
    }

    private void drawStroke(ArrayList stroke, Canvas c) {
        if (stroke.size() > 0) {
            Point p0 = (Point) stroke.get(0);
            for (int i = 1; i < stroke.size(); i++) {
                Point p1 = (Point) stroke.get(i);
                c.drawLine(p0.x, p0.y, p1.x, p1.y, paint_new);
                p0 = p1;
            }
        }
    }

    private void drawStroke1(ArrayList stroke, Canvas c) {
        if (stroke.size() > 0) {
            Point p0 = (Point) stroke.get(0);
            for (int i = 1; i < stroke.size(); i++) {
                Point p1 = (Point) stroke.get(i);
                c.drawLine(p0.x, p0.y, p1.x, p1.y, paint_new);
                p0 = p1;
            }
        }
    }

    private Paint createPaint(int color, float width) {
        Paint temp = new Paint();
        temp.setStyle(Paint.Style.STROKE);
        temp.setAntiAlias(true);
        temp.setColor(color);
        temp.setStrokeWidth(width);
        temp.setStrokeCap(Cap.ROUND);

        return temp;
    }

}
