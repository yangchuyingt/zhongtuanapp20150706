package com.teambuy.zhongtuan.activity.near.category;

import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.activity.BaseActivity;
import com.teambuy.zhongtuan.adapter.BusinessAdapter;
import com.teambuy.zhongtuan.background.NetAsync;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.OnRefreshListener;
import com.teambuy.zhongtuan.listener.near.CategoryListener;
import com.teambuy.zhongtuan.listener.near.StoreListener;
import com.teambuy.zhongtuan.model.Model;
import com.teambuy.zhongtuan.model.Product;
import com.teambuy.zhongtuan.model.Store;
import com.teambuy.zhongtuan.utilities.ImageUtilities;
import com.teambuy.zhongtuan.utilities.JsonUtilities;
import com.teambuy.zhongtuan.views.CategorysView;
import com.teambuy.zhongtuan.views.CustomProgressDialog;
import com.teambuy.zhongtuan.views.RefreshListView;

public class NearBusinessActivity extends BaseActivity implements
		StoreListener, CategoryListener, OnRefreshListener, OnClickListener {
	CustomProgressDialog mDialog;
	BDLocation mLocation;
	RefreshListView listView;
	RelativeLayout rl;
	LocationClient mClient;
	TextView tittleBar;
	ImageView ivRefresh;
	Button backBtn, locationBtn;
	View headerView;
	BusinessAdapter mAdapter;
	CategorysView cateView;
	String dl, xl, pTag, cTag; 
	Cursor shopCursor; //商铺游标
	Animation animation; //底部定位时旋转的动画
	boolean isFirstTime = true; // should do some stuff once
	boolean isFromSearch = false; //是否从搜索界面跳转过来
	Boolean isRefresh=false;	//是否是下拉刷新
	int page = 0;	//加载页数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Debug.startMethodTracing("myTrace");
		setContentView(R.layout.copy_of_near_business_header);
		//获得上一个页面传过来的大类和小类
		dl = getIntent().getStringExtra("pTag");
		xl = getIntent().getStringExtra("cTag");
		isFromSearch = getIntent().getBooleanExtra("isSearch", false);
		//页面部分
		initViews();
		setList();
		
		this.loadBusinessByTag(dl, xl);
	}

	/**
	 * 初始化控件和监听
	 * @author lforxeverc 2014-12-22上午11:04:00
	 */
	private void initViews() {
		findViews();		
//		Model.delete(Store.class);		
		initListener();		
	}
	/**
	 * 初始化控件
	 * 2015-1-24
	 * lforxeverc
	 */
	private void findViews() {
		rl = (RelativeLayout) findViewById(R.id.rl);
		cateView = (CategorysView) findViewById(R.id.cateview);
		listView = (RefreshListView) findViewById(R.id.list_near_business_product);
		tittleBar = (TextView) findViewById(R.id.tv_header_tittle);
		backBtn = (Button) findViewById(R.id.back);
		locationBtn = (Button) findViewById(R.id.btn_location);
		backBtn.setBackgroundResource(R.drawable.header_back);
		ivRefresh = (ImageView) findViewById(R.id.iv);
		//获得百度定位client
		mClient = ZhongTuanApp.getInstance().getLocationClient();
		if(mClient!=null){
		mLocation = mClient.getLastKnownLocation();
		if(locationBtn!=null)//不知道为什么这里会报空指针异常，做一个判断
			try {
				
				locationBtn.setText(mLocation.getAddrStr());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mDialog = CustomProgressDialog.createDialog(this);
		setmAnimation();
	}

	/**
	 * 设置页面的监听器
	 * 2015-1-24
	 * lforxeverc
	 */
	private void initListener() {
		cateView.setOnCategoryListener(this);
		listView.setOnRefreshListener(this);
		listView.setEnablePullDownRefresh(true);
		listView.setEnableLoadingMore(true);
		backBtn.setOnClickListener(this);
		locationBtn.setOnClickListener(this);
		rl.setOnClickListener(this);
	}

	/**
	 * 给底下的定位符号设置动画
	 * 2015-1-24
	 * lforxeverc
	 */
	private void setmAnimation() {
		animation = new RotateAnimation(0f, 1440f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(1500);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				rl.setClickable(false);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				rl.setClickable(true);
				mLocation=mClient.getLastKnownLocation();
				locationBtn.setText(mLocation.getAddrStr());
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onResultError(String reqUrl, String errMsg) {
		mDialog.dismiss();
		Cursor cursor = ZhongTuanApp
				.getInstance()
				.getRDB()
				.query("STORE_LIST", null, null, null, null, null,
						"_distance asc");
		mAdapter.changeCursor(cursor);
		mAdapter.notifyDataSetChanged();
		listView.onRefreshFinish();	
		//把当前的cursor赋值给shopCursor，当页面销毁时把最后一个cursor给销毁
		shopCursor=cursor;
		Toast.makeText(this, "没有更多店铺啦！", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		if (mDialog != null) {
			mDialog.dismiss();
		}
		//数据回来后查询数据库并更新数据
		Cursor cursor = ZhongTuanApp
				.getInstance()
				.getRDB()
				.query("STORE_LIST", null, null, null, null, null,
						"_distance asc");
		mAdapter.changeCursor(cursor);
		mAdapter.notifyDataSetChanged();
		listView.onRefreshFinish();
		//增加页面
		page++;		
		//把当前的cursor赋值给shopCursor，当页面销毁时把最后一个cursor给销毁
		shopCursor=cursor;

	}

	@Override
	public void onTokenTimeout() {
		mDialog.dismiss();
		ZhongTuanApp.getInstance().logout(this);
	}

	/**
	 * 设置商家的列表适配器，使用BusinessAdapter
	 * 
	 * @author lforxeverc 2014-12-22上午11:17:02
	 */
	public void setList() {
		
		shopCursor = ZhongTuanApp
				.getInstance()
				.getRDB()
				.query("STORE_LIST", null, null, null, null, null,
						"_distance asc");
		String[] from = new String[] { "_shopname", "_distance", "_models" };
		int[] to = new int[] { R.id.tv_storename, R.id.tv_storedistance,
				R.id.btn_more_tg };
		mAdapter = new BusinessAdapter(this,
				R.layout.copy_of_listitem_nearbusiness, shopCursor, from, to);
		listView.setAdapter(mAdapter);

	}

	/**
	 * 根据页码加载数据，页码会在加载数据成功后自增
	 * 
	 * @param page 页数
	 * @author lforxeverc 2014-12-22上午11:17:54
	 */
	public void loadShops(final int page) {
		mDialog.show();
		NetAsync loadTask = new NetAsync(D.API_SHOP_GETALL, this) {
			@Override
			public Object processDataInBackground(JsonElement elData) {
				//当是下拉刷新时，把数据库清空，在保存新进来的数据
				if(isRefresh){Model.delete(Store.class);Model.delete(Product.class);}
				java.lang.reflect.Type type = new TypeToken<Map<String, JsonElement>>() {
				}.getType();
				Gson gson = new Gson();
				Map<String, JsonElement> element = gson.fromJson(elData, type);
				for (String temp : element.keySet()) {
					Map<String, JsonElement> shopElement = gson.fromJson(
							element.get(temp), type);
					if (shopElement == null) {
						return null;
					}
					for (String shop : shopElement.keySet()) {
						Store store = JsonUtilities.parseModelByType(
								shopElement.get(shop), Store.class);
						store.save();

						Product product = JsonUtilities.parseModelByType(
								shopElement.get(shop), Product.class);
						product.save();
					}

				}

				return elData;
			}

			@Override
			public void beforeRequestInBackground(List<NameValuePair> params) {
				params.add(new BasicNameValuePair("cityid", ZhongTuanApp
						.getInstance().getCityId()));
				params.add(new BasicNameValuePair("lngo", ZhongTuanApp
						.getInstance().getLngo()));
				params.add(new BasicNameValuePair("lato", ZhongTuanApp
						.getInstance().getLato()));
				params.add(new BasicNameValuePair("page", page + ""));
				params.add(new BasicNameValuePair("cpdl", dl));
				params.add(new BasicNameValuePair("cpxl", xl));
				//如果是从搜索页面过来的话需要添加搜索的key，否则不加
				if (isFromSearch) {
					params.add(new BasicNameValuePair("key", getIntent()
							.getStringExtra("key")));
				}

			}
		};
		loadTask.execute();
	}

	@Override
	public void loadBusinessByTag(String dl, String xl) {
		Model.delete(Store.class);
		this.dl = dl;
		this.xl = xl;
		page = 0;
		loadShops(page);
		switch (dl) {
		case D.CATE_PTAG_1:
			tittleBar.setText("商家");
			break;
		case D.CATE_PTAG_2:
			tittleBar.setText("美食");
			break;
		case D.CATE_PTAG_3:
			tittleBar.setText("电影");
			break;
		case D.CATE_PTAG_4:
			tittleBar.setText("娱乐");
			break;
		case D.CATE_PTAG_5:
			tittleBar.setText("酒店");
			break;
		case D.CATE_PTAG_6:
			tittleBar.setText("旅游");
			break;

		}
	}

	@Override
	public void OnPullDownRefresh() {
		//设置下拉刷新状态为true，把page设置为0，清空数据库
		isRefresh=true;
		page=0;
		loadShops(page);
	}

	@Override
	public void onLoadingMore() {
		//设置下拉刷新状态为false
		isRefresh=false;
		loadShops(page);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//关闭当前的店铺游标，防止内存泄露
		shopCursor.close();
		//关闭店铺对应的产品游标列表
		mAdapter.closeProductCrs();
		ImageUtilities.removeBitmaps();
		Debug.stopMethodTracing();
		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		//顶部返回按钮
		case R.id.back:
			finish();
			break;
		//定位点击事件，启动定位服务
		case R.id.btn_location:
		case R.id.rl:
			if (!mClient.isStarted()) {
				mClient.start();
			} 
				animation.reset();
				ivRefresh.startAnimation(animation);
			break;
		default:
			break;
		}

	}
	

}
