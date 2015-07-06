package com.teambuy.zhongtuan.activity.near.category;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.activity.BaseActivity;
import com.teambuy.zhongtuan.activity.near.NearProductActivity;
import com.teambuy.zhongtuan.actor.near.TeamBuyActor;
import com.teambuy.zhongtuan.adapter.TeamBuyListAdapter;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.OnRefreshListener;
import com.teambuy.zhongtuan.listener.TeamBuyListener;
import com.teambuy.zhongtuan.utilities.DBUtilities;
import com.teambuy.zhongtuan.utilities.ImageUtilities;
import com.teambuy.zhongtuan.views.CustomProgressDialog;
import com.teambuy.zhongtuan.views.RefreshListView;

public class AllTuangouActivity extends BaseActivity implements
		TeamBuyListener, OnRefreshListener {
	RefreshListView listView;
	TextView tittleBar;
	Button back;
	CustomProgressDialog mDialog;
	TeamBuyActor mTeamBuyActor;
	TeamBuyListAdapter adapter;
	private int page = 0;
	Cursor productCr;
	Boolean isRefresh=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.near_business_header);
		initViews();
		loadData();

	}

	/**
	 * 初始化view
	 */
	private void initViews() {
		tittleBar = (TextView) findViewById(R.id.tv_header_tittle);
		tittleBar.setText("全部团购");

		listView = (RefreshListView) findViewById(R.id.list_near_business_product);
		listView.setEnableLoadingMore(true);
		listView.setEnablePullDownRefresh(true);
		listView.setOnRefreshListener(this);
		listView.setOnItemClickListener(this);

		back = (Button) findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.header_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

	}

	/**
	 * 加载数据
	 */
	private void loadData() {
		mDialog = CustomProgressDialog.createDialog(this);
		mDialog.show();
		mTeamBuyActor = new TeamBuyActor(this, this);
		mTeamBuyActor.loadProducts(page,true);

		productCr = DBUtilities.getProductList();
		adapter = new TeamBuyListAdapter(this, productCr);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, NearProductActivity.class);
		intent.putExtra("productId", arg3 + "");
		startActivity(intent);

	}

	@Override
	public void onResultError(String reqUrl, String errMsg) {
		mDialog.dismiss();
		Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		isRefresh=false;
		switch (reqUrl) {
		case D.API_CPMX_GETALLCPMX:
			mDialog.dismiss();
			listView.onRefreshFinish();
			Cursor cr = DBUtilities.getProductList();
			adapter.changeCursor(cr);
			adapter.notifyDataSetChanged();
			page++;
			productCr=cr;
			break;
		case D.API_PRODUCT_CATEGORY:
			break;
		default:
			break;
		}

	}

	@Override
	public void onTokenTimeout() {
		mDialog.dismiss();
		ZhongTuanApp.getInstance().logout(this);

	}

	@Override
	public void OnPullDownRefresh() {
		mDialog.show();
		page=0;
		isRefresh=true;
		mTeamBuyActor.loadProducts(page,isRefresh);

	}

	@Override
	public void onLoadingMore() {
		mDialog.show();		
		mTeamBuyActor.loadProducts(page,isRefresh);

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(productCr!=null){
			productCr.close();
		}
		ImageUtilities.removeBitmaps();
	}
	
}
