package com.teambuy.zhongtuan.activity.activities;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.activity.BaseActivity;
import com.teambuy.zhongtuan.background.NetAsync;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.ActivitiesListener;
import com.teambuy.zhongtuan.listener.global.NetAsyncListener;
import com.teambuy.zhongtuan.utilities.ImageUtilities;

public class TBHDetails extends BaseActivity implements ActivitiesListener,NetAsyncListener{
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	ImageView imageView;
	TextView detailsView,daysumView, headerView,mobmemoView;
	String name,phone,address,need;
	Button submit;
	Button tbhSignUpBtn;
	CheckBox box1;
	String id,reapp,tgno;
	EditText nameText,phoneText,addressText,needText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tbh_details);
		initView();
		initData();
	}
	
	

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		id=bundle.getString("id");
		String detail = bundle.getString("detail");
		String mobmemo = bundle.getString("mobmemo");
		String picurl = bundle.getString("picurl");
//		imageView.setImageBitmap(ImageUtilities.loadBitMap(picurl,imageView, new ImageUpdateListener() {
//			@Override
//			public void onLoadImageSuccess(Bitmap bitmap) {
//				imageView.setImageBitmap(bitmap);
//			}
//		}));
		ImageUtilities.loadBitMap(picurl, imageView);
		detailsView.setText(detail);
		headerView.setText("团博会");
		mobmemoView.setText(mobmemo);
		
		box1.setOnCheckedChangeListener(new OnCheckedChangeListener() {		
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					mobmemoView.setVisibility(View.VISIBLE);
				} else {
					mobmemoView.setVisibility(View.GONE);
				}
			}
		});
		
	}



	private void initView() {
		imageView=(ImageView)findViewById(R.id.largeimage);
		detailsView=(TextView) findViewById(R.id.details);
		daysumView=(TextView) findViewById(R.id.time_distance_sum);
		mobmemoView=(TextView) findViewById(R.id.mobmemo);
		box1=(CheckBox) findViewById(R.id.up_down_btn);		
		headerView=(TextView) findViewById(R.id.title);
	}



	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		
	}

	@Override
	public void onResultError(String reqUrl, String errMsg) {
			
	}

	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		
	}

	@Override
	public void onTokenTimeout() {
		ZhongTuanApp.getInstance().logout(this);
	}
	
public void onTBHSignUpBtnClick(View v){
		
		final AlertDialog dialog=new AlertDialog.Builder(this).create();
		dialog.show();		
		
		Window window=dialog.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setContentView(R.layout.webview_dialog);
		dialog.setCanceledOnTouchOutside(false);
		
		nameText=(EditText) window.findViewById(R.id.cusname);
		phoneText=(EditText) window.findViewById(R.id.tel);
		addressText=(EditText) window.findViewById(R.id.cusaddress);
		needText=(EditText) window.findViewById(R.id.need);
		submit=(Button) window.findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {				
				name=nameText.getText().toString();
				phone=phoneText.getText().toString();
				address=addressText.getText().toString();
				need=needText.getText().toString();
				if(!name.equals("")&&!phone.equals("")){
					signUp();
					dialog.dismiss();
					
				}
				else{
					nameText.setHint("不能为空");
					nameText.setHintTextColor(Color.RED);
					phoneText.setHint("不能为空");
					phoneText.setHintTextColor(Color.RED);
				}
				
			}
		});
		
		
	}
	public void signUp(){
		
		NetAsync task=new NetAsync(D.API_TBH_SIGN_UP,this) {
			
			@Override
			public Object processDataInBackground(JsonElement elData) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void beforeRequestInBackground(List<NameValuePair> params) {
				params.add(new BasicNameValuePair(D.ARG_AC_TGNO,id));
				params.add(new BasicNameValuePair(D.ARG_AC_TRUENAME,name));
				params.add(new BasicNameValuePair(D.ARG_AC_TEL,phone));
				params.add(new BasicNameValuePair(D.ARG_AC_ADDRESS,address));
				params.add(new BasicNameValuePair(D.ARG_AC_MEMO,need));
				params.add(new BasicNameValuePair(D.ARG_AC_REAPP,reapp));
				params.add(new BasicNameValuePair(D.ARG_AC_AC,ZhongTuanApp.getInstance().getCityCode()));
				
			}
		};
		task.execute();
	}
	public void onClickBack(View  v){
		finish();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageUtilities.removeBitmaps();
	}

}
