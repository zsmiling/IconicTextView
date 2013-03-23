package com.atermenji.android.iconictextview;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;

import com.atermenji.android.iconictextview.icon.Icon;

public class IconicFontDrawable extends Drawable {

    private Context mContext;

    private Paint mIconPaint;
    private Paint mContourPaint;

    private Rect mPaddingBounds;
    private RectF mPathBounds;

    private Path mPath;

    private int mIconPadding;
    private int mContourWidth;

    private boolean mDrawContour;

    private Icon mIcon;
    private char[] mIconUtfChars;

    public IconicFontDrawable(Context context) {
        mContext = context.getApplicationContext();

        mIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mContourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mContourPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();

        mPathBounds = new RectF();
        mPaddingBounds = new Rect();
    }
    
    public IconicFontDrawable(Context context, final Icon icon) {
        this(context);
        updateIcon(icon);
    }
    
    public void setIcon(final Icon icon) {
        updateIcon(icon);
        invalidateSelf();
    }

    public void setIconColor(int color) {
        mIconPaint.setColor(color);
        invalidateSelf();
    }

    public void setIconPadding(int iconPadding) {
        mIconPadding = iconPadding + mContourWidth;
        invalidateSelf();
    }

    public void setContour(int contourColor, int contourWidth) {
        setContourColor(contourColor);
        setContourWidth(contourWidth);
        invalidateSelf();
    }

    public void setContourColor(int contourColor) {
        mContourPaint.setColor(contourColor);
        invalidateSelf();
    }

    public void setContourWidth(int contourWidth) {
        mContourWidth = contourWidth;
        mContourPaint.setStrokeWidth(mContourWidth);
        invalidateSelf();
    }

    public void drawContour(boolean drawStroke) {
        mDrawContour = drawStroke;

        if (mDrawContour) {
            mIconPadding += mContourWidth;
        } else {
            mIconPadding -= mContourWidth;
        }

        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mIcon != null) {
            final Rect viewBounds = getBounds();

            updatePaddingBounds(viewBounds);
            updateTextSize(viewBounds);
            offsetIcon(viewBounds);

            mPath.close();

            if (mDrawContour) {
                canvas.drawPath(mPath, mContourPaint);
            }

            canvas.drawPath(mPath, mIconPaint);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void setAlpha(int alpha) {
        mIconPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mIconPaint.setColorFilter(cf);
    }

    private void updateIcon(Icon icon) {
        mIcon = icon;
        mIconUtfChars = Character.toChars(icon.getIconUtfValue());
        mIconPaint.setTypeface(mIcon.getIconicTypeface().getTypeface(mContext));
    }

    private void updatePaddingBounds(Rect viewBounds) {
        if (mIconPadding >= 0
                && !(mIconPadding * 2 > viewBounds.width())
                && !(mIconPadding * 2 > viewBounds.height())) {
            mPaddingBounds.set(
                    viewBounds.left + mIconPadding,
                    viewBounds.top + mIconPadding,
                    viewBounds.right - mIconPadding,
                    viewBounds.bottom - mIconPadding);
        }
    }

    private void updateTextSize(Rect viewBounds) {
        float textSize = (float) viewBounds.height() * 2;
        mIconPaint.setTextSize(textSize);

        mIconPaint.getTextPath(mIconUtfChars, 0, mIconUtfChars.length,
                0, viewBounds.height(), mPath);
        mPath.computeBounds(mPathBounds, true);

        float deltaWidth = ((float) mPaddingBounds.width() / mPathBounds.width());
        float deltaHeight = ((float) mPaddingBounds.height() / mPathBounds.height());
        float delta = (deltaWidth < deltaHeight) ? deltaWidth : deltaHeight;
        textSize *= delta;

        mIconPaint.setTextSize(textSize);

        mIconPaint.getTextPath(mIconUtfChars, 0, mIconUtfChars.length,
                0, viewBounds.height(), mPath);
        mPath.computeBounds(mPathBounds, true);
    }

    private void offsetIcon(Rect viewBounds) {
        float startX = viewBounds.centerX() - (mPathBounds.width() / 2);
        float offsetX = startX -mPathBounds.left;

        float startY = viewBounds.centerY() - (mPathBounds.height() / 2);
        float offsetY = startY - (mPathBounds.top);

        mPath.offset(offsetX, offsetY);
    }
}