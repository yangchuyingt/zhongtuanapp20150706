package com.teambuy.zhongtuan.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.activity.HomeActivity;
import com.teambuy.zhongtuan.activity.SuperActivity;
import com.teambuy.zhongtuan.actor.login.LoginActor;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.login.LoginListener;
import com.teambuy.zhongtuan.model.Collection;
import com.teambuy.zhongtuan.model.Evaluation;
import com.teambuy.zhongtuan.model.Model;
import com.teambuy.zhongtuan.model.OrderTG;
import com.teambuy.zhongtuan.model.OrderTM;
import com.teambuy.zhongtuan.model.TeMaiEvaluation;
import com.teambuy.zhongtuan.model.UserAddress;
import com.teambuy.zhongtuan.utilities.LogUtilities;
import com.teambuy.zhongtuan.views.CustomProgressDialog;

public class LoginActivity extends SuperActivity implements LoginListener{
	private LoginActor actor;
	private CustomProgressDialog progress;
	private String lognum;
	private String beforphoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,true);
		actor = new LoginActor(this);
		setContentView(R.layout.login_main);
		actor.initView();
	}
	
	/* ==================== Button Click Events ================= */

	/**
	 * 登录按钮被点击
	 * @param v
	 */
	public void onClickLoginBtn(View v){
		progress=CustomProgressDialog.createDialog(this);
		progress.show();
		beforphoneNumber = ZhongTuanApp.getInstance().getAppSettings().phoneNumber;
		lognum = actor.getReversText(actor.$et("phone"));
		actor.Login();
	}
	
	/**
	 * 忘记密码按钮被点击
	 * @param v
	 */
	public void onClickForgetPwdBtn(View v){
		LogUtilities.Log(D.DEBUG, "click forget",D.DEBUG_DEBUG);
		Intent intent = new Intent(this, GetPhoneNumber.class);
		startActivity(intent);
	}
	
	/**
	 * 注册按钮被点击
	 * @param v
	 */
	public void onClickRegistBtn(View v){
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}
	
	/*============================ Status Events ============================*/

	/**
	 * 登录失败
	 */
	@Override
	public void onResultError(String reqUrl, String errMsg) {
		progress.dismiss();
		Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 登录成功
	 */
	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		progress.dismiss();
		//清除必要的数据库数据
		cleardatabase();
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void cleardatabase() {
		if(!TextUtils.equals(lognum, beforphoneNumber)){
			Model.delete(OrderTM.class);
			Model.delete(OrderTG.class);
			Model.delete(Collection.class);
			Model.delete(UserAddress.class);
			Model.delete(Evaluation.class);
			Model.delete(TeMaiEvaluation.class);
		}
		
	}

	/**
	 * 校验失败
	 */
	@Override
	public void onValidateFailed(String errMsg) {
		progress.dismiss();
		Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 
	 */
	@Override
	public void onTokenTimeout() {
//		ZhongTuanApp.getInstance().logout(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}


	
}