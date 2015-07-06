package com.teambuy.zhongtuan.activity.near;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.activity.BaseActivity;
import com.teambuy.zhongtuan.activity.activities.ActivityDetails;
import com.teambuy.zhongtuan.actor.ActivitiesActor;
import com.teambuy.zhongtuan.listener.ActivitiesListener;
import com.teambuy.zhongtuan.model.Activities;
import com.teambuy.zhongtuan.model.Model;

public class activityCatgory extends BaseActivity implements ActivitiesListener {
    private ListView list;
	private ActivitiesActor mActor;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.tuangouhui_fregment);
    	list = (ListView) findViewById(R.id.list);
    	TextView title=(TextView) findViewById(R.id.tv_header_tittle);
    	String type=getIntent().getStringExtra("type");
    	if(!TextUtils.isEmpty(type)){
    		title.setText(type);
    	}
    	mActor = new ActivitiesActor(this, this);
    	mActor.initTGHView(list, null);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Activities activities = Model
				.load(new Activities(), String.valueOf(id));
		String _id = activities.getId();
		String detail = activities.getDetail();
		String memo = activities.getMemo();
		String picbrand = activities.getPicbrand();
		String picpro = activities.getPicpro();
		String www = activities.getWww();
		String reapp = activities.getReapp();
		String picurl = activities.getPicurl();
		String tgno = activities.getTgno();
		Intent intent = new Intent(this, ActivityDetails.class);
		Bundle bundle = new Bundle();
		bundle.putString("id", _id);
		bundle.putString("detail", detail);
		bundle.putString("memo", memo);
		bundle.putString("picbrand", picbrand);
		bundle.putString("picpro", picpro);
		bundle.putString("www", www);
		bundle.putString("reapp", reapp);
		bundle.putString("picurl", picurl);
		bundle.putString("tgno", tgno);

		intent.putExtras(bundle);
		startActivity(intent);
		
	}

	@Override
	public void onResultError(String reqUrl, String errMsg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTokenTimeout() {
		// TODO Auto-generated method stub
		
	}
    
}
