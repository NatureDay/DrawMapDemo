package com.example.administrator.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * 多边形绘制View
 * Author: Administrator
 * Time: 2017/4/19 10:57
 */
public class PolygonView extends DrawView {

    protected static final int MAX_DISTANCE_FOR_FINISH = 20;

    protected static final int POINT_RADIUS_MAX = 20;
    protected static final int POINT_RADIUS_MIN = 10;

    private ArrayList<Point> mPoints;
    private Path mPath;
    private Paint mPointPaint;
    private Paint mLinePaint;
    private Paint mAreaPaint;
    /**
     * 用于标识是创建模式还是编辑模式
     */
    private boolean mIsEditMode = false;
    private onDrawFinishListener mOnDrawFinishListener;

    private Point mMovePoint;

    public interface onDrawFinishListener {
        void onDrawFinish(ArrayList<Point> points);
    }

    public PolygonView(Context context) {
        this(context, null);
    }

    public PolygonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPoints = new ArrayList<Point>();
        mPath = new Path();

        mPointPaint = new Paint();
        mPointPaint.setColor(Color.WHITE);
        mPointPaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(0xffffffff);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(10f);
        setEditMode(mIsEditMode);

        mAreaPaint = new Paint();
        mAreaPaint.setStyle(Paint.Style.FILL);
        mAreaPaint.setColor(0x4400ff00);
        mAreaPaint.setAntiAlias(true);
    }

    public ArrayList<Point> getPoints() {
        return mPoints;
    }

    public void setPoints(ArrayList<Point> points) {
        this.mPoints = points;
        invalidate();
    }

    public void setOnDrawFinishListener(onDrawFinishListener onDrawFinishListener) {
        this.mOnDrawFinishListener = onDrawFinishListener;
    }

    public boolean isIsEditMode() {
        return mIsEditMode;
    }

    public void setEditMode(boolean mode) {
        this.mIsEditMode = mode;
        if (mIsEditMode) {
            PathEffect effects = new DashPathEffect(new float[]{30, 40, 30, 40}, 0);
            mLinePaint.setPathEffect(effects);
        } else {
            mLinePaint.setPathEffect(null);
        }
    }

    @Override
    protected void onSingleClick(int x, int y) {
        Point point = null;
        if (mPoints.size() >= 3) {
            Point firstPoint = mPoints.get(0);
            if (Math.abs(firstPoint.x - x) < MAX_DISTANCE_FOR_FINISH &&
                    Math.abs(firstPoint.y - y) < MAX_DISTANCE_FOR_FINISH) {
                if (mOnDrawFinishListener != null) {
                    mOnDrawFinishListener.onDrawFinish(mPoints);
                }
                point = new Point(firstPoint.x, firstPoint.y);
            } else {
                point = new Point(x, y);
            }
        } else {
            point = new Point(x, y);
        }
        mPoints.add(point);
        invalidate();
    }

    @Override
    protected void onMove(int originX, int originY, int x, int y) {
        if (mMovePoint == null) {
            for (Point point : mPoints) {
                if (Math.abs(point.x - originX) < MAX_DISTANCE_FOR_CLICK &&
                        Math.abs(point.y - originY) < MAX_DISTANCE_FOR_CLICK) {
                    mMovePoint = point;
                    break;
                }
            }
        }
        mMovePoint.x = x;
        mMovePoint.y = y;
        invalidate();
    }

    @Override
    protected void onMoveFinish() {
        mMovePoint = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPoints.isEmpty()) return;
        int size = mPoints.size();
        /**
         * 画面
         */
        if (size >= 3) {
            mPath.reset();
            for (int i = 0; i < size; i++) {
                Point point = mPoints.get(i);
                if (i == 0) {
                    mPath.moveTo(point.x, point.y);
                } else {
                    mPath.lineTo(point.x, point.y);
                }
            }
            canvas.drawPath(mPath, mAreaPaint);
        }
        /**
         * 画线
         */
        if (size >= 2) {
            mPath.reset();
            for (int i = 0; i < size; i++) {
                Point point = mPoints.get(i);
                if (i == 0) {
                    mPath.moveTo(point.x, point.y);
                } else {
                    mPath.lineTo(point.x, point.y);
                }
            }
            canvas.drawPath(mPath, mLinePaint);
        }
        /**
         * 画点
         */
        if (!mIsEditMode) {
            for (int i = 0; i < size; i++) {
                Point point = mPoints.get(i);
                canvas.drawCircle(point.x, point.y, POINT_RADIUS_MAX, mPointPaint);
            }
        } else {
            for (int i = 0; i < size; i++) {
                Point point = mPoints.get(i);
                if (i % 2 == 0) {
                    canvas.drawCircle(point.x, point.y, POINT_RADIUS_MAX, mPointPaint);
                } else {
                    canvas.drawCircle(point.x, point.y, POINT_RADIUS_MIN, mPointPaint);
                }
            }
        }
    }

}
