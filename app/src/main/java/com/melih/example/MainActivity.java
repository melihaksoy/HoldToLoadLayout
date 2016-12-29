package com.melih.example;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.melih.holdtoload.HoldToLoadLayout;
import com.mobven.example.R;

public class MainActivity extends AppCompatActivity {

	private HoldToLoadLayout mHoldToLoadLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mHoldToLoadLayout = (HoldToLoadLayout) findViewById(R.id.holdToLoadLayout);

		mHoldToLoadLayout.setStrokeWidth(10);
		mHoldToLoadLayout.setStrokeAlpha(255);
		mHoldToLoadLayout.setPlayReverseAnimation(true);
		mHoldToLoadLayout.setStopWhenFilled(false);
		mHoldToLoadLayout.setColorAnimator(Color.YELLOW, Color.RED);
		mHoldToLoadLayout.setStartAngle(HoldToLoadLayout.Angle.BOTTOM);

		/**
		 * Enabling this will suppress the use of stopWhenFilled and cause progress to stop at all times
		 */
		mHoldToLoadLayout.setHoldAtLastPosition(true);

		mHoldToLoadLayout.setFillListener(new HoldToLoadLayout.FillListener() {
			@Override
			public void onFull() {

			}

			@Override
			public void onEmpty() {

			}

			@Override
			public void onAngleChanged(float angle) {

			}

			@Override
			public void onOffsetChanged(float offset) {

			}
		});
	}

	@Override
	protected void onDestroy() {
		mHoldToLoadLayout.removeFillListener();
		super.onDestroy();
	}
}
