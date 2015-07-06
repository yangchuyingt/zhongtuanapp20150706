package com.teambuy.zhongtuan.activity.activities;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.activity.BaseActivity;
import com.teambuy.zhongtuan.background.NetAsync;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.global.NetAsyncListener;
import com.teambuy.zhongtuan.views.CustomProgressDialog;

public class ActivitySignUpActivity extends BaseActivity implements OnClickListener,NetAsyncListener {
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	String nameStr, telStr, addressStr, needStr, tgno, reapp;
	EditText nameEt, telEt, addressEt, needEt;
	Button submitBtn, back;
	TextView tittle;
	CustomProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		tgno = getIntent().getStringExtra("tgno");
		reapp = getIntent().getStringExtra("reapp");

		back = (Button) findViewById(R.id.back);
		submitBtn = (Button) findViewById(R.id.submit);
		tittle = (TextView) findViewById(R.id.tv_header_tittle);
		nameEt = (EditText) findViewById(R.id.et_name);
		telEt = (EditText) findViewById(R.id.et_tel);
		addressEt = (EditText) findViewById(R.id.et_address);
		needEt = (EditText) findViewById(R.id.et_etc);

		back.setBackgroundResource(R.drawable.header_back);
		tittle.setText("活动报名");
		back.setOnClickListener(this);
		submitBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.submit:
			signUp();
			break;

		default:
			break;
		}

	}
	public void signUp(){
		nameStr=nameEt.getText().toString();
		telStr=telEt.getText().toString();
		addressStr=addressEt.getText().toString();
		needStr=needEt.getText().toString();
		
		if(nameStr.equals("")){
			Toast.makeText(this, "姓名不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		if(telStr.equals("")){
			Toast.makeText(this, "电话号码错误！", Toast.LENGTH_SHORT).show();
			return;
		}
		mDialog=CustomProgressDialog.createDialog(this);
		mDialog.show();
		NetAsync task=new NetAsync(D.API_ACTIVITY_SIGN_UP,this) {
			
			@Override
			public Object processDataInBackground(JsonElement elData) {
				return null;
			}
			
			@Override
			public void beforeRequestInBackground(List<NameValuePair> params) {
				params.add(new BasicNameValuePair(D.ARG_AC_TGNO,tgno));
				params.add(new BasicNameValuePair(D.ARG_AC_TRUENAME,nameStr));
				params.add(new BasicNameValuePair(D.ARG_AC_TEL,telStr));
				params.add(new BasicNameValuePair(D.ARG_AC_ADDRESS,addressStr));
				params.add(new BasicNameValuePair(D.ARG_AC_MEMO,needStr));
				params.add(new BasicNameValuePair(D.ARG_AC_REAPP,reapp));
				params.add(new BasicNameValuePair(D.ARG_AC_AC,ZhongTuanApp.getInstance().getCityCode()));
				
			}

		};
		task.execute();
	}

	@Override
	public void onResultError(String reqUrl, String errMsg) {
		mDialog.dismiss();
		
	}

	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		mDialog.dismiss();
		setResult(1);
		finish();
		
	}

	@Override
	public void onTokenTimeout() {
		mDialog.dismiss();
		ZhongTuanApp.getInstance().logout(this);
		
	}
}
