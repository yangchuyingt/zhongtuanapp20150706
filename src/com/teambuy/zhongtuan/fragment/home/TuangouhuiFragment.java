package com.teambuy.zhongtuan.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.activity.activities.ActivityDetails;
import com.teambuy.zhongtuan.activity.near.activityCatgory;
import com.teambuy.zhongtuan.actor.ActivitiesActor;
import com.teambuy.zhongtuan.adapter.ActivitiesListAdapter;
import com.teambuy.zhongtuan.listener.ActivitiesListener;
import com.teambuy.zhongtuan.model.Activities;
import com.teambuy.zhongtuan.model.Model;
import com.teambuy.zhongtuan.views.CustomProgressDialog;

public class TuangouhuiFragment extends Fragment implements ActivitiesListener, OnClickListener {
	Context mContext;
	ActivitiesListener mListener;
	ActivitiesListAdapter adapter;
	ActivitiesActor mActor;
	CustomProgressDialog mProgressDialog;
	ListView list;
	LayoutInflater inflater;
	Button testBtn,btn_right;
	TextView tittle;
	int page=0;
	public TuangouhuiFragment() {
		super();
	}
	
	public static final TuangouhuiFragment newInstance(String tag) {
		TuangouhuiFragment fragment = new TuangouhuiFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActor = new ActivitiesActor(getActivity(), this);
		mProgressDialog = CustomProgressDialog.createDialog(getActivity());
		mProgressDialog.show();
		mActor.loadEvent(page);
	}
	

	@Override
	public void onDestroy() {
		if(null != mProgressDialog)mProgressDialog.dismiss();
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tuangouhui_fregment, container,
				false);
		list = (ListView) view.findViewById(R.id.list);
		inflater = getActivity().getLayoutInflater();
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		View headerView = inflater.inflate(R.layout.list_header, layout);
		initheaderview(headerView);
		mActor.initTGHView(list, headerView);
		tittle=(TextView)view.findViewById(R.id.tv_header_tittle);
		tittle.setText("线下活动");
		btn_right=(Button) view.findViewById(R.id.setting);
		btn_right.setBackgroundResource(R.drawable.bg_search_left);
		
		list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				
			}
			
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				
			}
		});
		return view;
	}

	private void initheaderview(View view) {
		Button btn_house=(Button) view.findViewById(R.id.btn_house);
		Button btn_furniture=(Button) view.findViewById(R.id.btn_furniture);
		Button btn_wedding=(Button) view.findViewById(R.id.btn_wedding);
		Button btn_car=(Button) view.findViewById(R.id.btn_car);
		Button btn_education=(Button) view.findViewById(R.id.btn_education);
		Button btn_xq=(Button) view.findViewById(R.id.btn_xq);
		Button btn_party=(Button) view.findViewById(R.id.btn_party);
		Button btn_tbh=(Button) view.findViewById(R.id.btn_tbh);
		btn_house.setOnClickListener(this);
		btn_furniture.setOnClickListener(this);
		btn_wedding.setOnClickListener(this);
		btn_car.setOnClickListener(this);
		btn_education.setOnClickListener(this);
		btn_xq.setOnClickListener(this);
		btn_party.setOnClickListener(this);
		btn_tbh.setOnClickListener(this);
		
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
		Intent intent = new Intent(getActivity(), ActivityDetails.class);
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
		mProgressDialog.dismiss();


	}

	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		mProgressDialog.dismiss();
		mActor.updateList(true);
	}

	@Override
	public void onTokenTimeout() {
		mProgressDialog.dismiss();
		ZhongTuanApp.getInstance().logout(getActivity());

	}

	@Override
	public void onClick(View v) {
		Intent intent=new Intent(getActivity(), activityCatgory.class);
		switch (v.getId()) {
		case R.id.btn_house:
			intent.putExtra("type", "房产");
			startActivity(intent);
			break;
		case R.id.btn_furniture:
			intent.putExtra("type", "家居");
			startActivity(intent);
			break;
		case R.id.btn_wedding:
			intent.putExtra("type", "结婚");
			startActivity(intent);
			break;
		case R.id.btn_car:
			intent.putExtra("type", "汽车");
			startActivity(intent);
			break;
		case R.id.btn_education:
			intent.putExtra("type", "教育");
			startActivity(intent);
			break;
		case R.id.btn_xq:
			intent.putExtra("type", "相亲");
			startActivity(intent);
			break;
		case R.id.btn_party:
			intent.putExtra("type", "聚会");
			startActivity(intent);
			break;
		case R.id.btn_tbh:
			intent.putExtra("type", "团博会");
			startActivity(intent);
			break;
          
		default:
			break;
		}
		
	}

}
