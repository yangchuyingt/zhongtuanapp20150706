package com.teambuy.zhongtuan.activity.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.activity.BaseActivity;
import com.teambuy.zhongtuan.actor.ActivityDetailsActor;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.global.NetAsyncListener;
import com.teambuy.zhongtuan.utilities.ImageUtilities;
import com.teambuy.zhongtuan.views.CustomProgressDialog;

public class ActivityDetails extends BaseActivity implements NetAsyncListener {
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	CustomProgressDialog mDialog;
	ActivityDetailsActor mActor;
	String name, phone, address, need;
	Button submit;
	EditText nameText, phoneText, addressText, needText;

	ImageView imageview;
	TextView tittle, sum, header;
	WebView content1, content2, content4, content3;
	Button signUpBtn, back;
	CheckBox box1, box2;
	String id, reapp, tgno;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		initView();
		initData();

	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initData() {
		Bundle bundle = getIntent().getExtras();
		id = bundle.getString("id");
		String detail = bundle.getString("detail");
		String memo = bundle.getString("memo");
		String picpro = bundle.getString("picpro");
		String picbrand = bundle.getString("picbrand");
		String www = bundle.getString("www");
		reapp = bundle.getString("reapp");
		tgno = bundle.getString("tgno");
		String picurl = bundle.getString("picurl");
//		imageview.setImageBitmap(ImageUtilities.loadBitMap(picurl, imageview,
//				new ImageUpdateListener() {
//					@Override
//					public void onLoadImageSuccess(Bitmap bitmap) {
//						imageview.setImageBitmap(bitmap);
//					}
//				}));
		ImageUtilities.loadBitMap(picurl, imageview);
		tittle.setText(detail);
		back.setBackgroundResource(R.drawable.header_back);
		header.setText("活动详情");
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		WebSettings settings1 = content1.getSettings();
		settings1.setJavaScriptEnabled(true);
		settings1.setSupportZoom(true);
		settings1.setBuiltInZoomControls(true);
		settings1.setUseWideViewPort(true);
		settings1.setLoadWithOverviewMode(true);
		content1.loadDataWithBaseURL(D.BASIC_URL, memo, "text/html", "utf-8",
				null);
		WebSettings settings2 = content2.getSettings();
		settings2.setJavaScriptEnabled(true);
		settings2.setSupportZoom(true);
		settings2.setBuiltInZoomControls(true);
		settings2.setUseWideViewPort(true);
		settings2.setLoadWithOverviewMode(true);
		content2.loadDataWithBaseURL(D.BASIC_URL, picpro, "text/html", "utf-8",
				null);
		WebSettings settings3 = content3.getSettings();
		settings3.setJavaScriptEnabled(true);
		settings3.setSupportZoom(true);
		settings3.setBuiltInZoomControls(true);
		settings3.setUseWideViewPort(true);
		settings3.setLoadWithOverviewMode(true);
		content3.loadDataWithBaseURL(D.BASIC_URL, picbrand, "text/html",
				"utf-8", null);
		WebSettings settings4 = content4.getSettings();
		settings4.setJavaScriptEnabled(true);
		settings4.setSupportZoom(true);
		settings4.setBuiltInZoomControls(true);
		settings4.setUseWideViewPort(true);
		settings4.setLoadWithOverviewMode(true);
		content4.loadDataWithBaseURL(D.BASIC_URL, www, "text/html", "utf-8",
				null);
		box1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					content1.setVisibility(View.VISIBLE);
				} else {
					content1.setVisibility(View.GONE);
				}
			}
		});
		box2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					content2.setVisibility(View.VISIBLE);
				} else {
					content2.setVisibility(View.GONE);
				}
			}
		});

	}

	private void initView() {
		imageview = (ImageView) findViewById(R.id.imageview);
		tittle = (TextView) findViewById(R.id.tittlemsg);
		sum = (TextView) findViewById(R.id.sum);
		tittle = (TextView) findViewById(R.id.tittlemsg);
		content1 = (WebView) findViewById(R.id.content1);
		content2 = (WebView) findViewById(R.id.content2);
		content3 = (WebView) findViewById(R.id.content3);
		content4 = (WebView) findViewById(R.id.content4);
		header = (TextView) findViewById(R.id.tv_header_tittle);
		back = (Button) findViewById(R.id.back);
		signUpBtn = (Button) findViewById(R.id.signupbtn);
		box1 = (CheckBox) findViewById(R.id.checkbox1);
		box2 = (CheckBox) findViewById(R.id.checkbox2);

	}

	@Override
	public void onResultError(String reqUrl, String errMsg) {
		Toast.makeText(this, "报名失败", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		Toast.makeText(this, "报名成功！", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onTokenTimeout() {
		Toast.makeText(this, "超时！", Toast.LENGTH_SHORT).show();
		ZhongTuanApp.getInstance().logout(this);
		

	}

	public void onSignUpBtnClick(View v) {
		Intent intent = new Intent(ActivityDetails.this,
				ActivitySignUpActivity.class);
		intent.putExtra("tgno", tgno);
		intent.putExtra("reapp", reapp);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		// 报名失败啦哈哈
		case RESULT_CANCELED:
			break;
		// 报名成功
		case 1:
			Toast.makeText(this, "报名成功！", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageUtilities.removeBitmaps();
	}

}
