package rival.com.changecolorindicater.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import rival.com.changecolorindicater.R;


/**
 * Created by rival on 2015/11/18.
 */

public class ChangeColorIconWithText extends View {


    private int mode_chose = MODE_COLOR;
    private int mode_change = MODE_CLIP;
    private int direction;

    private static final int MODE_COLOR = 0;
    private static final int MODE_IMAGE = 1;

    private static final int MODE_ALPHA = 0;
    private static final int MODE_CLIP = 1;

    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_RIGHT = 1;


    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;


    /**
     * 颜色
     */

    private int mColor = 0xFF45C01A;

    /**
     * 偏移量 0.0-1.0
     */

    private float mPositionOffset = 0f;

    /**
     * 图标
     */

    private Bitmap mIconBitmap;
    private Bitmap mIconBitmap_chose;

    /**
     * 限制绘制icon的范围
     */

    private Rect mIconRect;

    /**
     * icon底部文本
     */

    private String mText = "默认";
    private int mTextSize = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());

    private Paint mTextPaint;
    private Rect mTextBound = new Rect();

    public ChangeColorIconWithText(Context context) {
        this(context, null);
    }

    public ChangeColorIconWithText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ChangeColorIconWithText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ChangeColorIconWithText);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {

            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ChangeColorIconWithText_mode_chose:
                    mode_chose = a.getInt(attr, MODE_COLOR);
                    break;
                case R.styleable.ChangeColorIconWithText_mode_change:
                    mode_change=a.getInt(attr, MODE_ALPHA);
                    break;
                case R.styleable.ChangeColorIconWithText_color_chose:
                    mColor = a.getColor(attr, 0xFF45C01A);
                    break;
                case R.styleable.ChangeColorIconWithText_image_default:
                    BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
                    mIconBitmap = drawable.getBitmap();
                    break;
                case R.styleable.ChangeColorIconWithText_image_chose:
                    BitmapDrawable drawable1 = (BitmapDrawable) a.getDrawable(attr);
                    mIconBitmap_chose = drawable1.getBitmap();
                    break;
                case R.styleable.ChangeColorIconWithText_text:
                    mText = a.getString(attr);
                    break;
                case R.styleable.ChangeColorIconWithText_text_size:
                    mTextSize = (int) a.getDimension(attr, TypedValue
                            .applyDimension(TypedValue.COMPLEX_UNIT_SP, 10,
                                    getResources().getDisplayMetrics()));
                    break;
            }
        }

        a.recycle();

        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0xff555555);
        //得到text绘制范围
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);

    }

    public void setPositionOffset(float positionOffset, int direction) {
        setDirection(direction);
        if (positionOffset >= 0.0f && positionOffset <= 1.0f) {
            this.mPositionOffset = positionOffset;
            invalidateView();
        }
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //在UI线程
            invalidate();
        } else {
            //在工作线程
            postInvalidate();

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mIconBitmap.getHeight() + mTextBound.height() + getPaddingTop() + getPaddingBottom());
        }

        //得到绘制icon的宽
        int bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mTextBound.height());

        int left = getMeasuredWidth() / 2 - bitmapWidth / 2;
        int top = (getMeasuredHeight() - mTextBound.height()) / 2 - bitmapWidth / 2;
        //设置icon中的绘制范围
        mIconRect = new Rect(left, top, left + bitmapWidth, top + bitmapWidth);

    }

    private Paint paint = new Paint();
    //  private Paint paint_X = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {

        int alpha = (int) Math.ceil((255 * mPositionOffset));

        if (mode_change == MODE_ALPHA) {
            paint.setAlpha(255 - alpha);
        }

        canvas.drawBitmap(mIconBitmap, null, mIconRect, paint);
        setupTargetBitmap(alpha);
        drawSourceText(canvas, alpha);
        drawTargetText(canvas, alpha);


        if (mode_change == MODE_ALPHA) {
            Log.e("test",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            canvas.drawBitmap(mBitmap, 0, 0, null);
        } else {
            clipAndDrawIcon(canvas);
        }

    }


    private void clipAndDrawIcon(Canvas canvas) {

        int left, right;


        if (direction == DIRECTION_RIGHT) {
            left = (int) (mIconRect.left + (mIconRect.right - mIconRect.left) * (1 - mPositionOffset));
            right = mIconRect.right;
        } else {
            left = mIconRect.left;
            right = (int) (mIconRect.right - (mIconRect.right - mIconRect.left) * (1 - mPositionOffset));
        }

        canvas.save();
        canvas.clipRect(left, mIconRect.top, right, mIconRect.bottom);
        //   paint_X.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.restore();
    }

    private void setupTargetBitmap(int alpha) {
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        if (mode_chose == MODE_COLOR) {
            mPaint.setColor(mColor);
            if (mode_change == MODE_ALPHA) {
                mPaint.setAlpha(alpha);
            }
            mCanvas.drawRect(mIconRect, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mPaint.setAlpha(255);
            mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
        } else {
            Palette palette = Palette.generate(mIconBitmap_chose);
            mColor = palette.getVibrantColor(0x000000);
            if (mode_change == MODE_ALPHA) {
                mPaint.setAlpha(alpha);
            }
            mCanvas.drawBitmap(mIconBitmap_chose, null, mIconRect, mPaint);
        }

    }

    private void drawSourceText(Canvas canvas, int alhpa) {

        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0xff333333);
        if (mode_change == MODE_ALPHA) {
            mTextPaint.setAlpha(255 - alhpa);
        }
        if (alhpa>253){
            mTextPaint.setAlpha(0);
        }
        mTextPaint.setAntiAlias(true);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2
                        - mTextBound.width() / 2, mIconRect.bottom + mTextBound.height(),
                mTextPaint);
    }

    private void drawTargetText(Canvas canvas, int alpha) {

        mTextPaint.setColor(mColor);
        if (mode_change == MODE_ALPHA) {
            mTextPaint.setAlpha(alpha);
            canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2
                            - mTextBound.width() / 2, mIconRect.bottom + mTextBound.height(),
                    mTextPaint);
        } else {

            clipAndDrawText(canvas);
        }

    }

    private void clipAndDrawText(Canvas canvas) {
        int left;
        int right;

        left = mIconRect.left + mIconRect.width() / 2
                - mTextBound.width() / 2;
        right = mIconRect.left + mIconRect.width() / 2
                + mTextBound.width() / 2;


        if (direction == DIRECTION_RIGHT) {
            left = (int) (left + mTextBound.width() * (1 - mPositionOffset));
        } else {
            right = (int) (right - mTextBound.width() * (1 - mPositionOffset));
        }

        canvas.save();
        //TODO
        canvas.clipRect(left, mIconRect.bottom, right+5, mIconRect.bottom + mTextBound.height()+10);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2
                        - mTextBound.width() / 2, mIconRect.bottom + mTextBound.height(),
                mTextPaint);
        canvas.restore();
    }

    public void setIconColor(int color) {
        mColor = color;
    }

    public void setIcon(int resId) {
        this.mIconBitmap = BitmapFactory.decodeResource(getResources(), resId);
        if (mIconRect != null)
            invalidateView();
    }

    public void setIcon(Bitmap iconBitmap) {
        this.mIconBitmap = iconBitmap;
        if (mIconRect != null)
            invalidateView();
    }

    public void setDirection(int direction) {
        if (direction == DIRECTION_LEFT || direction == DIRECTION_RIGHT) {
            this.direction = direction;
        }
    }

    private static final String INSTANCE_STATE = "instance_state";

    private static final String STATE_ALPHA = "state_alpha";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(STATE_ALPHA, mPositionOffset);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mPositionOffset = bundle.getFloat(STATE_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}

