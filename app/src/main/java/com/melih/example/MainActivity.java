package com.melih.example;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.melih.holdtoload.HoldToLoadLayout;
import com.mobven.example.R;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final HoldToLoadLayout holdToLoadLayout = (HoldToLoadLayout) findViewById(R.id.holdToLoadLayout);

		if (holdToLoadLayout != null) {
			holdToLoadLayout.setStrokeWidth(10);
			holdToLoadLayout.setStrokeAlpha(255);
			holdToLoadLayout.setPlayReverseAnimation(true);
			holdToLoadLayout.setStopWhenFilled(false);
			holdToLoadLayout.setColorAnimator(Color.YELLOW, Color.RED);
			holdToLoadLayout.setStartAngle(30);

			/**
			 * Enabling this will suppress the use of stopWhenFilled and cause progress to stop at all times
			 */
			holdToLoadLayout.setHoldAtLastPosition(true);

			holdToLoadLayout.setFillListener(new HoldToLoadLayout.FillListener() {
				@Override
				public void onFull() {
					//					holdToLoadLayout.setStrokeColor(android.R.color.holo_green_dark);
					holdToLoadLayout.setDuration(1000);
				}

				@Override
				public void onEmpty() {

				}

				@Override
				public void onAngleChanged(float angle) {

				}
			});
		}
	}
}
