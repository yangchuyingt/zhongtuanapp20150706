package com.teambuy.zhongtuan.activity.me.setting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.adapter.FeedbackAdapter;
import com.teambuy.zhongtuan.background.NetAsync;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.global.NetAsyncListener;
import com.teambuy.zhongtuan.model.Feedback;
import com.teambuy.zhongtuan.views.CustomProgressDialog;

public class FeedbackListActivity extends Activity implements NetAsyncListener{
	ListView feedbackLv;
	CustomProgressDialog mDialog;
	TextView title;
	ArrayList<Feedback> fbData=new ArrayList<Feedback>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_list);
		feedbackLv=(ListView) findViewById(R.id.feedback_lv);
		title=(TextView) findViewById(R.id.tv_header_tittle);
		
		title.setText("反馈意见");
		mDialog=CustomProgressDialog.createDialog(this);
		mDialog.show();
		NetAsync task=new NetAsync(D.API_GET_FEEDBACK, this) {
			
			@Override
			public Object processDataInBackground(JsonElement elData) {
			java.lang.reflect.Type type=new TypeToken<ArrayList<Feedback>>(){}.getType();
			Gson gson=new Gson();
			fbData=gson.fromJson(elData, type);
				return fbData;
			}
			
			@Override
			public void beforeRequestInBackground(List<NameValuePair> params) {
				
			}
		};
		task.execute();
	}
	@Override
	public void onResultError(String reqUrl, String errMsg) {
		mDialog.dismiss();
		Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
		
	}
	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		mDialog.dismiss();
		FeedbackAdapter adapter=new FeedbackAdapter(this, fbData);
		feedbackLv.setAdapter(adapter);
		
	}
	@Override
	public void onTokenTimeout() {
		mDialog.dismiss();
		ZhongTuanApp.getInstance().logout(this);
		
	}
	

}
