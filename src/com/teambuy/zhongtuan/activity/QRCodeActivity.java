package com.teambuy.zhongtuan.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.views.QRPreView;

public class QRCodeActivity extends BaseActivity {

	Camera mCamera;
	QRPreView preview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.near_scan);
		mCamera = Camera.open();
		preview = new QRPreView(this, mCamera);
		FrameLayout fl_preview = (FrameLayout)findViewById(R.id.fl_preview);
		fl_preview.addView(preview);
	}

	@Override
	protected void onResume() {
		super.onResume();		
	}

	@Override
	protected void onPause() {
		super.onPause();
		mCamera.release();
	}
	
	

}
