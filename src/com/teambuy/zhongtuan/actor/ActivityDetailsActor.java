package com.teambuy.zhongtuan.actor;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.widget.ListView;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.adapter.ActivitiesListAdapter;
import com.teambuy.zhongtuan.background.NetAsync;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.ActivitiesListener;
import com.teambuy.zhongtuan.model.Activities;
import com.teambuy.zhongtuan.model.Model;
import com.teambuy.zhongtuan.utilities.JsonUtilities;

public class ActivityDetailsActor extends SuperActor {
	Context mContext;
	ActivitiesListener mListener;
	ActivitiesListAdapter adapter;
	ListView listView;
	public ActivityDetailsActor(Context context,ActivitiesListener listener){
		super(context);
		mListener = listener;
		mContext = context;
	}
	
	public void loadDetails(final String tgno) {
		NetAsync task_loadEvent = new NetAsync(D.API_CPMX_GETONEHD,mListener) {
			
			@Override
			public Object processDataInBackground(JsonElement elData) {
				Type type = new TypeToken<Activities[]>(){}.getType();
				Activities[] activitiesList = JsonUtilities.parseModelByType(elData, type);
				Model.delete(Activities.class);
				Model.save(activitiesList);
				return activitiesList;
			}
			
			@Override
			public void beforeRequestInBackground(List<NameValuePair> params) {
				params.add(new BasicNameValuePair(D.ARG_AC_AC, ZhongTuanApp.getInstance().getCityCode()));
				params.add(new BasicNameValuePair(D.ARG_AC_TGNO, tgno));		
			}
		};
		task_loadEvent.execute();
	}
	
	
	
}
