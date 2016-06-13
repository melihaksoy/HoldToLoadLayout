package com.melih.example;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.melih.holdtoload.HoldToLoadLayout;
import com.mobven.example.R;

public class MainActivity extends AppCompatActivity implements HoldToLoadLayout.FillListener {

	private HoldToLoadLayout mHoldToLoadLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mHoldToLoadLayout = (HoldToLoadLayout) findViewById(R.id.holdToLoadLayout);
		mHoldToLoadLayout.setStrokeShape(HoldToLoadLayout.StrokeShape.CIRCLE);
		mHoldToLoadLayout.setStrokeWidth(0);
		mHoldToLoadLayout.setDrawToFront(true);
		mHoldToLoadLayout.setStrokeAlpha(150);
		mHoldToLoadLayout.setColorAnimator(Color.RED, Color.BLACK);
		mHoldToLoadLayout.setPlayReverseAnimation(true);
		mHoldToLoadLayout.setTransparentInside(false);
		mHoldToLoadLayout.setStartAngle(270);

		mHoldToLoadLayout.setFillListener(this);
	}

	@Override
	public void onFull() {

	}

	@Override
	public void onEmpty() {

	}

	@Override
	public void onAngleChanged(float angle) {

	}
}
