package com.teambuy.zhongtuan.actor.me.setting;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.teambuy.zhongtuan.actor.SuperActor;
import com.teambuy.zhongtuan.background.NetAsync;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.me.FeedBackListener;
import com.teambuy.zhongtuan.utilities.JsonUtilities;

public class FeedbackActor extends SuperActor {
	FeedBackListener mListener;

	public FeedbackActor(Context context) {
		super(context);
		mListener = (FeedBackListener)context;
	}
	
	public void initview(){
		initTitleBar(D.BAR_SHOW_LEFT, "客户反馈");
		
	}

	public void postFeedback() {
		NetAsync task_opst = new NetAsync(D.API_MY_FEEDBACK,mListener) {
			
			@Override
			public Object processDataInBackground(JsonElement elData) {
				Type type = new TypeToken<Map<String,String>>(){}.getType();
				Map<String,String> mResult = JsonUtilities.parseModelByType(elData, type);
				String tip = mResult.get("errmsg");
				return tip;
			}
			
			@Override
			public void beforeRequestInBackground(List<NameValuePair> params) {
				params.add(new BasicNameValuePair(D.ARG_FEEDBACK, getFeedBackContent()));
				params.add(new BasicNameValuePair("title", getFeedBackTitle()));
			}
		};
		task_opst.execute();
	}
	
	/*=================================== Helpers =================================*/
	public String getFeedBackContent(){
		return $et("content").getText().toString().trim();
	}
	public String getFeedBackTitle(){
		return $et("et_title").getText().toString().trim();
	}
}
