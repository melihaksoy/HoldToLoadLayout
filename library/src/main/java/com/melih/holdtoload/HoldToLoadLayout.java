package com.melih.holdtoload;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author Melih Aksoy
 *         Created by Melih on 19/01/16.
 */
public class HoldToLoadLayout extends FrameLayout {

	private static final String TAG = "HoldToLoadLayout";

	public static final int DEFAULT_DURATION = 1500;
	public static final int DEFAULT_ALPHA = 255;
	public static final int DEFAULT_COLOR = Color.GREEN;

	private Paint mPaint;
	private RectF mRectF;

	private ValueAnimator mForwardAnimator;
	private ValueAnimator mReverseAnimator;
	private ValueAnimator mForwardColorAnimator;
	private ValueAnimator mReverseColorAnimator;

	private FillListener mFillListener;

	private int mDuration;
	private View mChild;
	private int mStartingColor;
	private int mEndingColor;
	private int mColor;
	private int mAlpha;

	private long mDurationPerAngle;

	private float[] fromColorArr;
	private float[] toColorArr;
	private float[] hsvArr;

	private float mStartAngle;
	private float mAngle;
	private float mAnimatedValue;
	private float mStrokeWidth;

	private boolean isFilling;
	private boolean isPreparingProgressAnimator;
	private boolean isPreparingColorAnimator;
	private boolean isInitialized;
	private boolean animateColors;
	private boolean isHoldAtLastPosition;

	private boolean stopWhenFilled = true;
	private boolean isReverseAnimationEnabled = true;


	@SuppressLint("unused")
	public HoldToLoadLayout(Context context) {
		this(context, null);
	}

	@SuppressLint("unused")
	public HoldToLoadLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("unused")
	public HoldToLoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	@SuppressLint("unused")
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public HoldToLoadLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(attrs);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (getChildCount() > 1) {
			throw new IllegalStateException("HoldToLoadLayout can only host one child");
		}

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		int left = (width - mChild.getMeasuredWidth()) / 2;
		int top = (height - mChild.getMeasuredHeight()) / 2;
		int right = width - left;
		int bottom = height - top;

		mChild.layout(left, top, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		measureChild(mChild, widthMeasureSpec, heightMeasureSpec);

		int childWidth = mChild.getMeasuredWidth();
		int childHeight = mChild.getMeasuredHeight();

		if (widthMode == MeasureSpec.EXACTLY) {
			width = MeasureSpec.getSize(widthMeasureSpec);
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = Math.min(width, (int) (childWidth + (mStrokeWidth * 2)));
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			height = MeasureSpec.getSize(heightMeasureSpec);
		} else if (heightMode == MeasureSpec.AT_MOST) {
			height = Math.min(height, (int) (childHeight + (mStrokeWidth * 2)));
		}

		setMeasuredDimension(width, height);

		mRectF.set((width / 2) - (childWidth / 2) - mStrokeWidth,
				   (height / 2) - (childHeight / 2) - mStrokeWidth,
				   (width / 2) + (childWidth / 2) + mStrokeWidth,
				   (height / 2) + (childHeight / 2) + mStrokeWidth);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.drawArc(mRectF, mStartAngle, mAngle, true, mPaint);
		super.dispatchDraw(canvas);
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		mChild = child;
	}

	@Override
	public void addView(View child, int index) {
		super.addView(child, index);
		mChild = child;
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		mChild = child;
	}

	@Override
	public void addView(View child, ViewGroup.LayoutParams params) {
		super.addView(child, params);
		mChild = child;
	}

	@Override
	public void addView(View child, int width, int height) {
		super.addView(child, width, height);
		mChild = child;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isFilling = true;
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				isFilling = false;
				invalidate();
				break;
			default:
				break;
		}

		return true;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (stopWhenFilled && mAngle == 360) {
			return;
		}

		update();
	}

	private void init(AttributeSet attrs) {
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HoldToLoadLayout, 0, 0);

		String colorString = typedArray.getString(R.styleable.HoldToLoadLayout_hold_strokeColor);
		try {
			setStrokeColor(Color.parseColor(colorString));
		} catch (Exception e) {
			setStrokeColor(DEFAULT_COLOR);
		}

		setStartAngle(typedArray.getFloat(R.styleable.HoldToLoadLayout_hold_startAngle, Angle.TOP.getValue()));
		setStrokeWidth(typedArray.getDimensionPixelSize(R.styleable.HoldToLoadLayout_hold_strokeWidth, 0));
		setStrokeAlpha(typedArray.getInt(R.styleable.HoldToLoadLayout_hold_strokeAlpha, DEFAULT_ALPHA));
		setDuration(typedArray.getInt(R.styleable.HoldToLoadLayout_hold_duration, DEFAULT_DURATION));
		setStopWhenFilled(typedArray.getBoolean(R.styleable.HoldToLoadLayout_hold_stopWhenFilled, false));

		typedArray.recycle();

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(mColor);
		mPaint.setAlpha(mAlpha);
		mPaint.setStyle(Paint.Style.FILL);

		mDurationPerAngle = mDuration / 360;

		mForwardAnimator = new ValueAnimator();
		mForwardAnimator.setDuration(mDuration);

		mForwardColorAnimator = new ValueAnimator();
		mForwardColorAnimator.setDuration(mDuration);

		mReverseAnimator = new ValueAnimator();
		mReverseAnimator.setDuration(mDuration);

		mReverseColorAnimator = new ValueAnimator();
		mReverseColorAnimator.setDuration(mDuration);

		mRectF = new RectF();

		isInitialized = true;
	}

	private void update() {
		if (isInitialized) {
			if (isFilling) {
				startForwardProgressAnimator();
				if (animateColors) {
					startForwardColorAnimator();
				}
			} else {
				stopForwardProgressAnimator();
				if (isReverseAnimationEnabled && !isHoldAtLastPosition) {
					startReverseProgressAnimator();
					if (animateColors) {
						startReverseColorAnimator();
					}
				} else {
					if (animateColors) {
						stopForwardColorAnimator();
					}

					if (!isHoldAtLastPosition) {
						mAngle = 0;
					}
				}
			}
		}
	}

	private void stopForwardColorAnimator() {
		if (mForwardColorAnimator.isRunning()) {
			mForwardColorAnimator.cancel();
			mForwardColorAnimator.removeAllUpdateListeners();
		}
	}

	private void stopReverseColorAnimator() {
		if (mReverseColorAnimator.isRunning()) {
			mReverseColorAnimator.cancel();
			mReverseColorAnimator.removeAllUpdateListeners();
		}
	}

	private void stopReverseProgressAnimator() {
		if (mReverseAnimator.isRunning()) {
			mReverseAnimator.cancel();
			mReverseAnimator.removeAllUpdateListeners();
		}
	}

	private void stopForwardProgressAnimator() {
		if (mForwardAnimator.isRunning()) {
			mForwardAnimator.cancel();
			mForwardAnimator.removeAllUpdateListeners();
		}
	}

	private void startForwardProgressAnimator() {
		stopReverseProgressAnimator();

		if (!isPreparingProgressAnimator) {
			isPreparingProgressAnimator = true;

			if (!mForwardAnimator.isRunning()) {
				mForwardAnimator.setFloatValues(mAngle, 360);
				mForwardAnimator.setDuration((long) ((360 - mAngle) * mDurationPerAngle));
				mForwardAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@TargetApi(Build.VERSION_CODES.HONEYCOMB)
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						mAngle = (float) animation.getAnimatedValue();
						invalidate();

						if (mFillListener != null) {
							mFillListener.onAngleChanged(mAngle);
							mFillListener.onOffsetChanged(mAngle / 360);
							if (mAngle == 360) {
								mFillListener.onFull();
								mForwardAnimator.removeUpdateListener(this);
							}
						}
					}
				});

				mForwardAnimator.start();
			}

			isPreparingProgressAnimator = false;
		}
	}

	private void startReverseProgressAnimator() {
		stopForwardProgressAnimator();

		if (!isPreparingProgressAnimator) {
			isPreparingProgressAnimator = true;

			if (!mReverseAnimator.isRunning()) {
				mReverseAnimator.setFloatValues(mAngle, 0);
				mReverseAnimator.setDuration((long) (mAngle * mDurationPerAngle));
				mReverseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						mAngle = (float) animation.getAnimatedValue();
						invalidate();

						if (mFillListener != null) {
							mFillListener.onAngleChanged(mAngle);
							mFillListener.onOffsetChanged(mAngle / 360);
							if (mAngle == 0) {
								mFillListener.onEmpty();
								mReverseAnimator.removeUpdateListener(this);
							}
						}
					}
				});

				mReverseAnimator.start();
			}

			isPreparingProgressAnimator = false;
		}
	}

	private void startForwardColorAnimator() {
		stopReverseColorAnimator();

		if (!isPreparingColorAnimator) {
			isPreparingColorAnimator = true;

			if (!mForwardColorAnimator.isRunning()) {
				mForwardColorAnimator.setFloatValues(mAnimatedValue, 1);
				mForwardColorAnimator.setDuration((long) ((360 - mAngle) * mDurationPerAngle));
				mForwardColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						mAnimatedValue = (float) animation.getAnimatedValue();
						Log.d(TAG, "onAnimationUpdate: " + mAnimatedValue);

						hsvArr[0] = fromColorArr[0] + (toColorArr[0] - fromColorArr[0]) * mAnimatedValue;
						hsvArr[1] = fromColorArr[1] + (toColorArr[1] - fromColorArr[1]) * mAnimatedValue;
						hsvArr[2] = fromColorArr[2] + (toColorArr[2] - fromColorArr[2]) * mAnimatedValue;

						mPaint.setColor(Color.HSVToColor(hsvArr));
						mPaint.setAlpha(mAlpha);
					}
				});

				mForwardColorAnimator.start();
			}

			isPreparingColorAnimator = false;
		}
	}

	private void startReverseColorAnimator() {
		stopForwardColorAnimator();

		if (!isPreparingColorAnimator) {
			isPreparingColorAnimator = true;

			if (!mReverseColorAnimator.isRunning()) {
				mReverseColorAnimator.setFloatValues(mAnimatedValue, 0);
				mReverseColorAnimator.setDuration((long) (mAngle * mDurationPerAngle));
				mReverseColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						mAnimatedValue = (float) animation.getAnimatedValue();
						hsvArr[0] = fromColorArr[0] + (toColorArr[0] - fromColorArr[0]) * mAnimatedValue;
						hsvArr[1] = fromColorArr[1] + (toColorArr[1] - fromColorArr[1]) * mAnimatedValue;
						hsvArr[2] = fromColorArr[2] + (toColorArr[2] - fromColorArr[2]) * mAnimatedValue;

						mPaint.setColor(Color.HSVToColor(hsvArr));
						mPaint.setAlpha(mAlpha);
					}
				});

				mReverseColorAnimator.start();
			}

			isPreparingColorAnimator = false;
		}
	}

	/**
	 * Set {@code true} if filler should animate back to 0 from current position when user no longer
	 * holds down
	 *
	 * @param isReverseAnimationEnabled {@code true} if reverse animation should be enabled
	 */
	@SuppressLint("unused")
	public void setPlayReverseAnimation(boolean isReverseAnimationEnabled) {
		this.isReverseAnimationEnabled = isReverseAnimationEnabled;
	}

	/**
	 * Return if reverse animation enabled or not
	 *
	 * @return {@code true} if reverse animation is enabled, returns {@code true} by default if not set
	 */
	@SuppressLint("unused")
	public boolean isReverseAnimationEnabled() {
		return isReverseAnimationEnabled;
	}

	/**
	 * Set {@code true} if you want progress to hold after you lift your finger up.
	 * Enabling this will suppress the use of {@link #stopWhenFilled}.
	 *
	 * @param isHoldAtLastPosition {@code true} if reverse animation should be enabled
	 */
	@SuppressLint("unused")
	public void setHoldAtLastPosition(boolean isHoldAtLastPosition) {
		this.isHoldAtLastPosition = isHoldAtLastPosition;
	}

	/**
	 * Return progress will hold after you lift your finger up.
	 *
	 * @return {@code true} if reverse animation is enabled, returns {@code true} by default if not set
	 */
	@SuppressLint("unused")
	public boolean isHoldAtLastPosition() {
		return isHoldAtLastPosition;
	}

	/**
	 * If set {@code true}, stroke does not reverse when filled completely
	 *
	 * @param stopWhenFilled {@code true} if stroke should stop at max when filled
	 */
	@SuppressLint("unused")
	public void setStopWhenFilled(boolean stopWhenFilled) {
		this.stopWhenFilled = stopWhenFilled;
	}

	/**
	 * Return if stroke reverse when filled
	 *
	 * @return {@code true} if stops when filled, returns {@code true} by default if not set
	 */
	@SuppressLint("unused")
	public boolean stopWhenFilled() {
		return stopWhenFilled;
	}

	/**
	 * Enables animating color while stroke is filling or reversing
	 *
	 * @param startingColor int value of the color that animation starts with
	 * @param endingColor   int value of the color that animation ends with
	 */
	@SuppressLint("unused")
	public void setColorAnimator(int startingColor, int endingColor) {
		mStartingColor = startingColor;
		mEndingColor = endingColor;

		fromColorArr = new float[3];
		toColorArr = new float[3];
		hsvArr = new float[3];
		Color.colorToHSV(mStartingColor, fromColorArr);
		Color.colorToHSV(mEndingColor, toColorArr);
		animateColors = true;
	}

	/**
	 * Cancels animation color while stroke is filling or reversing
	 */
	@SuppressLint("unused")
	public void cancelColorAnimator() {
		animateColors = false;
	}

	/**
	 * Returns starting color.
	 *
	 * @return int value of the starting color, returns 0 if not set
	 */
	@SuppressLint("unused")
	public int getStartingColor() {
		return mStartingColor;
	}

	/**
	 * Returns ending color.
	 *
	 * @return int value of the ending color, returns 0 if not set
	 */
	@SuppressLint("unused")
	public int getEndingColor() {
		return mEndingColor;
	}

	/**
	 * Sets stroke width in pixels if given value is greater than 0
	 * This method does not dynamically updates stroke width
	 *
	 * @param strokeWidth stroke width in pixels
	 */
	@SuppressLint("unused")
	public void setStrokeWidth(float strokeWidth) {
		if (strokeWidth > 0) {
			mStrokeWidth = strokeWidth;
		}
	}

	/**
	 * Returns stroke width in pixels
	 *
	 * @return int value of stroke width, returns 2 by default if not set
	 */
	@SuppressLint("unused")
	public float getStrokeWidth() {
		return mStrokeWidth;
	}

	/**
	 * Set stroke color. If color animation is disabled, stroke will be painted with this color
	 * This method does not dynamically updates stroke color
	 *
	 * @param color int value of the stroke color
	 */
	@SuppressLint("unused")
	public void setStrokeColor(int color) {
		mColor = color;
	}

	/**
	 * Set stroke color. If color animation is disabled, stroke will be painted with this color
	 * This method does not dynamically updates stroke color
	 *
	 * @param color int value of the stroke color
	 */
	@SuppressLint("unused")
	public void setStrokeColor(String color) {
		mColor = Color.parseColor(color);
	}

	/**
	 * Returns stroke color
	 *
	 * @return int value of the stroke color, returns COLOR.BLACK by default if not set
	 */
	@SuppressLint("unused")
	public int getStrokeColor() {
		return mColor;
	}

	/**
	 * Set duration for fill & color animations
	 *
	 * @param durationInMillis int duration in milliseconds
	 * @throws IllegalArgumentException if {@code durationInMillis} is less than 0
	 */
	@SuppressLint("unused")
	public void setDuration(int durationInMillis) throws IllegalArgumentException {
		if (durationInMillis > 0) {
			mDuration = durationInMillis;
			mDurationPerAngle = mDuration / 360;
		} else {
			throw new IllegalArgumentException("Duration should be greater than 0");
		}
	}

	/**
	 * Returns fill & color animation duration
	 *
	 * @return duration in milliseconds
	 */
	@SuppressLint("unused")
	public int getDuration() {
		return mDuration;
	}

	/**
	 * Set alpha for the stroke
	 *
	 * @param alpha int value of alpha, must be between 0 and 255 inclusive
	 * @throws IllegalArgumentException if {@code durationInMillis} is less than 0 or greater than 255
	 */
	@SuppressLint("unused")
	public void setStrokeAlpha(int alpha) throws IllegalArgumentException {
		if (alpha >= 0 && alpha <= 255) {
			mAlpha = alpha;
		} else {
			throw new IllegalArgumentException("Alpha should be minimum 0 and maximum 255");
		}
	}

	/**
	 * Returns alpha of stroke
	 *
	 * @return int value of alpha, 255 by default if not set
	 */
	@SuppressLint("unused")
	public int getStrokeAlpha() {
		return mAlpha;
	}

	/**
	 * Set starting angle
	 *
	 * @param startAngle {@link Angle} value of starting angle
	 */
	@SuppressLint("unused")
	public void setStartAngle(Angle startAngle) {
		mStartAngle = startAngle.getValue();
	}

	/**
	 * Set starting angle
	 *
	 * @param startAngle float value of starting angle
	 */
	@SuppressLint("unused")
	public void setStartAngle(float startAngle) {
		mStartAngle = startAngle;
	}

	/**
	 * Returns starting angle
	 *
	 * @return starting angle, returns 270 by default if not set
	 */
	@SuppressLint("unused")
	public float getStartAngle() {
		return mStartAngle;
	}

	/**
	 * Returns if filling in progress
	 *
	 * @return {@code true} if filling, false otherwise
	 */
	@SuppressLint("unused")
	public boolean isFilling() {
		return isFilling;
	}

	/**
	 * Set fill listener
	 *
	 * @param fillListener {@link FillListener}
	 */
	@SuppressLint("unused")
	public void setFillListener(FillListener fillListener) {
		mFillListener = fillListener;
	}

	/**
	 * Removes fill listener
	 */
	@SuppressLint("unused")
	public void removeFillListener() {
		mFillListener = null;
	}

	/**
	 * Fill listener interface
	 */
	public interface FillListener {

		@SuppressLint("unused")
		void onFull();

		@SuppressLint("unused")
		void onEmpty();

		@SuppressLint("unused")
		void onAngleChanged(float angle);

		@SuppressLint("unused")
		void onOffsetChanged(float offset);
	}

	public enum Angle {
		TOP(270), RIGHT(0), BOTTOM(90), LEFT(180);
		private final float value;

		Angle(int value) {
			this.value = value;
		}

		public float getValue() {
			return value;
		}
	}
}
