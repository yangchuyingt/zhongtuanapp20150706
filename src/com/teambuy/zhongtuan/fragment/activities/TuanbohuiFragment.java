package com.teambuy.zhongtuan.fragment.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.activity.activities.TBHDetails;
import com.teambuy.zhongtuan.actor.ActivitiesActor;
import com.teambuy.zhongtuan.listener.ActivitiesListener;
import com.teambuy.zhongtuan.model.Model;
import com.teambuy.zhongtuan.model.Tuanbohui;
import com.teambuy.zhongtuan.views.CustomProgressDialog;

public class TuanbohuiFragment extends Fragment implements ActivitiesListener{
	Context mContext;
	ActivitiesActor mActor;
	ActivitiesListener mListener;
	CustomProgressDialog mProgressDialog;
	private ListView list;
	public TuanbohuiFragment(Context context){
		mContext=context;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActor = new ActivitiesActor(getActivity(), this);
		mProgressDialog = CustomProgressDialog.createDialog(getActivity());
		mProgressDialog.show();
		mActor.loadTuanbohui();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.tuangouhui_fregment, container,false);
		list=(ListView) view.findViewById(R.id.list);
		mActor.initTBHView(list);
		return view;
	}
	
	@Override
	public void onResultError(String reqUrl, String errMsg) {
		mProgressDialog.dismiss();
		Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
		
	}
	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		mProgressDialog.dismiss();	
		mActor.updateTBHList(true);
		
	}
	@Override
	public void onTokenTimeout() {
		mProgressDialog.dismiss();
		ZhongTuanApp.getInstance().logout(getActivity());
		
	}
/*	@Override
	public void onLoadPicSuccess() {
		mActor.updateTBHList(false);
		
	}*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
		Tuanbohui activities=Model.load(new Tuanbohui(), String.valueOf(id));
		String _id=activities.getEid();
		String detail=activities.getDetail();		
		String picurl=activities.getPicurl();
		String piclarge=activities.getPiclarge();
		String mobmemo=activities.getMobmemo();
		

		Intent intent = new Intent(getActivity(),TBHDetails.class);		
		Bundle bundle = new Bundle();
		bundle.putString("id",_id);
		bundle.putString("detail",detail);
		bundle.putString("picurl",picurl);
		bundle.putString("piclarge",piclarge);
		bundle.putString("mobmemo",mobmemo);	
		intent.putExtras(bundle);
		startActivity(intent);
	}

}
