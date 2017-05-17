package com.example.administrator.myapplication;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 根据touch事件判断出点击事件
 * Author: Administrator
 * Time: 2017/4/19 11:31
 */
public abstract class DrawView extends View {

    protected static final int MAX_INTERVAL_FOR_CLICK = 250;

    protected static final int MAX_DISTANCE_FOR_CLICK = 20;

    private int mDownX = 0;
    private int mDownY = 0;
    private int mTempX = 0;
    private int mTempY = 0;

    private boolean mIsWaitUpEvent = false;

    private boolean mIsMoveEvent = false;

    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Runnable mTimerForUpEvent = new Runnable() {
        public void run() {
            if (mIsWaitUpEvent) {
                mIsWaitUpEvent = false;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (!mIsWaitUpEvent && event.getAction() != MotionEvent.ACTION_DOWN) {
//            return super.onTouchEvent(event);
//        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                mIsWaitUpEvent = true;
                mIsMoveEvent = false;
                postDelayed(mTimerForUpEvent, MAX_INTERVAL_FOR_CLICK);
                return true;
            case MotionEvent.ACTION_MOVE:
                mTempX = (int) event.getX();
                mTempY = (int) event.getY();
                if (Math.abs(mTempX - mDownX) > MAX_DISTANCE_FOR_CLICK
                        || Math.abs(mTempY - mDownY) > MAX_DISTANCE_FOR_CLICK) {
                    mIsWaitUpEvent = false;
                    removeCallbacks(mTimerForUpEvent);
                    mIsMoveEvent = true;
                    onMove(mDownX, mDownY, mTempX, mTempY);
                    return true;
                }
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_UP:
                mTempX = (int) event.getX();
                mTempY = (int) event.getY();
                mIsWaitUpEvent = false;
                removeCallbacks(mTimerForUpEvent);
                if (mIsMoveEvent) {
                    mIsMoveEvent = false;
                    onMoveFinish();
                }
                if (Math.abs(mTempX - mDownX) < MAX_DISTANCE_FOR_CLICK
                        && Math.abs(mTempY - mDownY) < MAX_DISTANCE_FOR_CLICK) {
                    onSingleClick(mDownX, mDownY);
                    return super.onTouchEvent(event);
                }
            case MotionEvent.ACTION_CANCEL:
                if (mIsMoveEvent) {
                    mIsMoveEvent = false;
                    onMoveFinish();
                }
                mIsWaitUpEvent = false;
                removeCallbacks(mTimerForUpEvent);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * click事件
     *
     * @param x 点击处的x坐标
     * @param y 点击处的y坐标
     */
    protected abstract void onSingleClick(int x, int y);

    /**
     * move事件
     *
     * @param originX 按下时的x坐标
     * @param originY 按下时的y坐标
     * @param x       移动时最新的x坐标
     * @param y       移动时最新的y坐标
     */
    protected abstract void onMove(int originX, int originY, int x, int y);

    /**
     * move停止事件
     */
    protected abstract void onMoveFinish();
}
