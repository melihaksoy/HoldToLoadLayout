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

		HoldToLoadLayout holdToLoadLayout = (HoldToLoadLayout) findViewById(R.id.);

		if (holdToLoadLayout != null) {
			holdToLoadLayout.setStrokeWidth(10);
			holdToLoadLayout.setStrokeAlpha(255);
			holdToLoadLayout.setPlayReverseAnimation(true);
			holdToLoadLayout.setStopWhenFilled(false);
			holdToLoadLayout.setColorAnimator(Color.YELLOW, Color.RED);
			holdToLoadLayout.setStartAngle(30);

			holdToLoadLayout.setFillListener(new HoldToLoadLayout.FillListener() {
				@Override
				public void onFull() {

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
