package com.example.myzxinglibray.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.myzxinglibray.zxing.camera.CameraManager;
import com.google.zxing.ResultPoint;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by yqy on 2017/6/27.
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial transparency outside it, as well as the laser scanner animation and result points.
 *
 */

public class ViewfinderView  extends View{

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 100L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor =0x60000000;
    private final int resultColor=0xb0000000;
    private final int frameColor=0xff000000;
    private final int laserColor =0xffff0000;
    private final int resultPointColor =0xc0ffff00;
    private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;

    private int i = 0;// 添加的
    private Rect mRect;// 扫描线填充边界
    private GradientDrawable mDrawable;// 采用渐变图作为扫描线
    private int mDrawableColor=0xffff0000;
    private Drawable lineDrawable;// 采用图片作为扫描线

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint();
        Resources resources = getResources();
        scannerAlpha = 0;
        possibleResultPoints = new HashSet<ResultPoint>(5);

        mRect = new Rect();
        mDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] { mDrawableColor, mDrawableColor, mDrawableColor, mDrawableColor, mDrawableColor });

    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {

            // Draw a two pixel solid black border inside the framing rect
            paint.setColor(frameColor);
            canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
            canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
            canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
            canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

            // Draw a red "laser scanner" line through the middle to show decoding is active
            paint.setColor(laserColor);
            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;

            // 将扫描线修改为上下走的线
            if ((i += 5) < frame.bottom - frame.top) {
				/* 以下为用渐变线条作为扫描线 */
                // 渐变图为矩形
                mDrawable.setShape(GradientDrawable.RECTANGLE);
                // 渐变图为线型
                mDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                // 线型矩形的四个圆角半径
                mDrawable.setCornerRadii(new float[] { 8, 8, 8, 8, 8, 8, 8, 8 });
                // 位置边界
                mRect.set(frame.left + 10, frame.top + i, frame.right - 10, frame.top + 1 + i);
                // 设置渐变图填充边界
                mDrawable.setBounds(mRect);
                // 画出渐变线条
                mDrawable.draw(canvas);

				/* 以下为图片作为扫描线 */
//        mRect.set(frame.left - 6, frame.top + i - 6, frame.right + 6,
//                frame.top + 6 + i);
//        lineDrawable.setBounds(mRect);
//        lineDrawable.draw(canvas);

                // 刷新
                invalidate();
            } else {
                i = 0;
            }

            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

}
