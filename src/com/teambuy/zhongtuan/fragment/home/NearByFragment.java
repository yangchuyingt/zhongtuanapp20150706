package com.teambuy.zhongtuan.fragment.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.activity.near.MainActivity;
import com.teambuy.zhongtuan.activity.near.NearProductActivity;
import com.teambuy.zhongtuan.activity.near.SearchActivity;
import com.teambuy.zhongtuan.activity.near.category.AllTuangouActivity;
import com.teambuy.zhongtuan.activity.near.category.MoreCategoryActivity;
import com.teambuy.zhongtuan.activity.near.category.NearBusinessActivity;
import com.teambuy.zhongtuan.actor.near.TeamBuyActor;
import com.teambuy.zhongtuan.adapter.GuidePagerAdapter;
import com.teambuy.zhongtuan.adapter.TeamBuyListAdapter;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.OnRefreshListener;
import com.teambuy.zhongtuan.listener.TeamBuyListener;
import com.teambuy.zhongtuan.listener.near.NearListener;
import com.teambuy.zhongtuan.listener.near.OnLocationClickListener;
import com.teambuy.zhongtuan.model.Model;
import com.teambuy.zhongtuan.model.Product;
import com.teambuy.zhongtuan.utilities.DBUtilities;
import com.teambuy.zhongtuan.utilities.ImageUtilities;
import com.teambuy.zhongtuan.views.CustomProgressDialog;
import com.teambuy.zhongtuan.views.RefreshListView;

public class NearByFragment extends Fragment implements NearListener,
		TeamBuyListener, OnRefreshListener, OnClickListener, OnPageChangeListener, OnTouchListener {
	public NearByFragment() {
		super();
	}

	TextView  title,tv2, tv3, tv4, tv5, tv6, tv7;
	ImageView iv2, iv3, iv4, iv5, iv6, iv7;
	Button searchBtn, allBtn1,allBtn2;
	ViewPager adsViewPager;
//	RadioGroup adsRadioGroup;
	RefreshListView listview;
	//View ads1;on
	CustomProgressDialog mProgressDialog;
	RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6, rl7, rl8;

//	Fragment fBusiness, fTeambuy, fCategory, tbFragment;
//	FragmentManager fm;

	OnRefreshListener mRefreshListener;
	OnLocationClickListener onLocationClickListener;

//	NearActor mActor;
	TeamBuyActor mTeambuyactor;
	TeamBuyListAdapter adapter;
//	LayoutInflater inflater;
	ArrayList<View> adsList;
	List<Map<String,String>> categoryData;
	Cursor productCr;
	private ArrayList<ImageView > dotArray;
	private int beforposition=0;
	public static final int CHANGE_VIEW_PAGER=0;
//	public static final NearByFragment newInstance(String tag) {
//		NearByFragment fragment = new NearByFragment();
//		return fragment;
//	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		onLocationClickListener = (OnLocationClickListener) activity;
		mRefreshListener = this;
		

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//初始化分类的List（包含图片url，名称，id）
		categoryData=new ArrayList<Map<String,String>>();
		
		//listview的头和尾绘制
		View nearView = inflater.inflate(R.layout.f_near_main, container, false);
		View headerView = inflater.inflate(R.layout.list_near_header, null);
		View footerView = inflater.inflate(R.layout.list_near_footer, null);
		
		//找到界面上的UI控件
		findTittleViews(nearView);		
		findHeaderViews(headerView);
		
		//底部更多团购按钮
		allBtn1 = (Button) footerView.findViewById(R.id.btn_near_buynow);
		
		// 从数据库取出15条数据并显示
		productCr = DBUtilities.getProductList(15);
		adapter = new TeamBuyListAdapter(getActivity(), productCr);
		listview.addHeaderView(headerView);
		listview.addFooterView(footerView);
		listview.setAdapter(adapter);	


		// 初始化广告栏
		adsViewPager = (ViewPager) headerView.findViewById(R.id.adsView);
		adsList = new ArrayList<View>();
		/*ads1 = inflater.inflate(R.layout.ads1, (ViewGroup) this.adsViewPager,false);
		View ads2 = inflater.inflate(R.layout.ads1, (ViewGroup) this.adsViewPager,false);
		View ads3 = inflater.inflate(R.layout.ads1, (ViewGroup) this.adsViewPager,false);
		View ads4 = inflater.inflate(R.layout.ads1, (ViewGroup) this.adsViewPager,false);
		View ads5 = inflater.inflate(R.layout.ads1, (ViewGroup) this.adsViewPager,false);*/
		int [] dr=new int[]{R.drawable.ads1,R.drawable.ads1,R.drawable.ads1,R.drawable.ads1,R.drawable.ads1};
		for (int i = 0; i < 5; i++) {
			ImageView img=new ImageView(getActivity());
			img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			img.setImageResource(dr[i]);
			adsList.add(img);
		}
		
		adsViewPager.setAdapter(new GuidePagerAdapter(adsList));
       // adsViewPager.setCurrentItem(0);
        adsViewPager.setOnPageChangeListener(this);
        adsViewPager.setOnTouchListener(this);
        initListeners();		
		initCategroyViews();
		myhandle.postDelayed(new Runviewpage(), 3);
		//TODO viewpager 自动轮播；
		return nearView;
	}
	/**
	 * 为所有按钮设置监听器
	 * 2015-1-21
	 * lforxeverc
	 */
	private void initListeners() {
		listview.setOnItemClickListener(this);
		listview.setOnRefreshListener(this);
		listview.setEnablePullDownRefresh(true);
		searchBtn.setOnClickListener(this);
//		scanBtn.setOnClickListener(this);
		//adsViewPager.setOnClickListener(this);
		//ads1.setOnClickListener(this);
		allBtn1.setOnClickListener(this);
		allBtn2.setOnClickListener(this);
		rl1.setOnClickListener(this);
		rl2.setOnClickListener(this);
		rl3.setOnClickListener(this);
		rl4.setOnClickListener(this);
		rl5.setOnClickListener(this);
		rl6.setOnClickListener(this);
		rl7.setOnClickListener(this);
		rl8.setOnClickListener(this);
	}
	
	/**
	 * 找到分类里面的所有UI控件
	 * @param headerView
	 * 2015-1-21
	 * lforxeverc
	 */
	private void findHeaderViews(View headerView) {
		//每个类别里的textview和imageview以及外层的relativelayout
		rl1 = (RelativeLayout) headerView.findViewById(R.id.rl1);
		rl2 = (RelativeLayout) headerView.findViewById(R.id.rl2);
		rl3 = (RelativeLayout) headerView.findViewById(R.id.rl3);
		rl4 = (RelativeLayout) headerView.findViewById(R.id.rl4);
		rl5 = (RelativeLayout) headerView.findViewById(R.id.rl5);
		rl6 = (RelativeLayout) headerView.findViewById(R.id.rl6);
		rl7 = (RelativeLayout) headerView.findViewById(R.id.rl7);
		rl8 = (RelativeLayout) headerView.findViewById(R.id.rl8);
		iv2 = (ImageView) headerView.findViewById(R.id.iv2);
		iv3 = (ImageView) headerView.findViewById(R.id.iv3);
		iv4 = (ImageView) headerView.findViewById(R.id.iv4);
		iv5 = (ImageView) headerView.findViewById(R.id.iv5);
		iv6 = (ImageView) headerView.findViewById(R.id.iv6);
		iv7 = (ImageView) headerView.findViewById(R.id.iv7);
		tv2 = (TextView) headerView.findViewById(R.id.tv2);
		tv3 = (TextView) headerView.findViewById(R.id.tv3);
		tv4 = (TextView) headerView.findViewById(R.id.tv4);
		tv5 = (TextView) headerView.findViewById(R.id.tv5);
		tv6 = (TextView) headerView.findViewById(R.id.tv6);
		tv7 = (TextView) headerView.findViewById(R.id.tv7);
		allBtn2=(Button) headerView.findViewById(R.id.btn_all_tb);
		adsViewPager=(ViewPager) headerView.findViewById(R.id.adsView);
		ImageView dot1=(ImageView) headerView.findViewById(R.id.dot1);
		ImageView dot2=(ImageView) headerView.findViewById(R.id.dot2);
		ImageView dot3=(ImageView) headerView.findViewById(R.id.dot3);
		ImageView dot4=(ImageView) headerView.findViewById(R.id.dot4);
		ImageView dot5=(ImageView) headerView.findViewById(R.id.dot5);
		dotArray = new ArrayList<ImageView>();
		dotArray.add(dot1);
		dotArray.add(dot2);
		dotArray.add(dot3);
		dotArray.add(dot4);
		dotArray.add(dot5);
		
	}
	/**
	 * 找到顶部头内的控件
	 * @param nearView
	 * 2015-1-21
	 * lforxeverc
	 */
	private void findTittleViews(View nearView) {
		//顶部搜索按钮
		searchBtn = (Button) nearView.findViewById(R.id.setting);
		searchBtn.setBackgroundResource(R.drawable.bg_search_left);
		title=(TextView) nearView.findViewById(R.id.tv_header_tittle);
		title.setText("周边生活");
//		//顶部二维码按钮
//		scanBtn = (Button) nearView.findViewById(R.id.btn_scan);
		//产品列表
		listview = (RefreshListView) nearView.findViewById(R.id.nearContent);
		
		
	}
	
	/**
	 * 从数据库获得服务器返回的数据并设置对应的筛选图标和文字
	 * 2015-1-21
	 * lforxeverc
	 */
	private void initCategroyViews() {
		getDataFromDB();				
		initCateBtns();		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTeambuyactor = new TeamBuyActor(getActivity(), this);
		mTeambuyactor.loadProductCategory();
		mTeambuyactor.loadProducts(0,true);
		mProgressDialog = CustomProgressDialog.createDialog(getActivity());
		mProgressDialog.show();
	}

	/**
	 * 点击商品列表跳转到商品详情页
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
		Product product = Model.load(new Product(), String.valueOf(id));
		String _id = product.getMid();
		Intent intent = new Intent(getActivity(), NearProductActivity.class);
		intent.putExtra("productId", _id);
		startActivity(intent);
	}

	@Override
	public void onResultError(String reqUrl, String errMsg) {
		mProgressDialog.dismiss();
		//下拉刷新超时隐藏下拉头
		if(listview!=null){
			
			listview.onRefreshFinish();
		}
	}

	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		// 判断，跳转到其他页面后数据返回奔溃
		if (listview != null) {
			switch (reqUrl) {
			// 产品列表数据回来以后更新列表
			case D.API_CPMX_GETALLCPMX:
				listview.onRefreshFinish();
				mProgressDialog.dismiss();
				Cursor cr = DBUtilities.getProductList(15);
				if (adapter != null) {
					adapter.changeCursor(cr);
					adapter.notifyDataSetChanged();
				}
				productCr=cr;
				break;
			//分类数据返回后设置顶部分类图标和文字
			case D.API_PRODUCT_CATEGORY:
				initCategroyViews();			
				break;
			}
		}
	}
	
	/**
	 * 从数据库获得分类数据（id、图片url，文字标题等信息）
	 * 2015-1-21
	 * lforxeverc
	 */
	private void getDataFromDB() {
		Cursor cursor=ZhongTuanApp.getInstance().getRDB().query("PRODUCTCCATEGORY_LIST", null, "_cup = ?", new String[]{"0"}, null, null, null);
		cursor.moveToFirst();
		for(int i=0;i<cursor.getCount();i++){
			Map<String,String> item=new HashMap<String, String>();
			item.put("icon",cursor.getString(cursor.getColumnIndex("_icon")));
			item.put("name",cursor.getString(cursor.getColumnIndex("_lbname")));
			item.put("id",cursor.getString(cursor.getColumnIndex("_id")));
			categoryData.add(item);
			cursor.moveToNext();
		}
		cursor.close();
		
	}
	/**
	 * 根据获取的list数据设置顶部分类按钮的图片、文字以及relativelayout的tag（id）
	 * 2015-1-21
	 * lforxeverc
	 */
	private void initCateBtns() {
		TextView[] cateTv={tv2, tv3, tv4, tv5, tv6, tv7};
		ImageView[] cateIv={iv2, iv3, iv4, iv5, iv6, iv7};
		RelativeLayout[] cateRl={rl2, rl3, rl4, rl5, rl6, rl7};
		if(categoryData.isEmpty()){return; }
		for(int i=0;i<cateTv.length;i++){
			Map<String,String> item=categoryData.get(i);
			ImageUtilities.loadBitMap(item.get("icon"),cateIv[i]);
			cateTv[i].setText(item.get("name"));
			cateRl[i].setTag(item.get("id"));
		}
	}

	@Override
	public void onTokenTimeout() {
		mProgressDialog.dismiss();
		ZhongTuanApp.getInstance().logout(getActivity());
	}

	@Override
	public void OnPullDownRefresh() {
		//下拉刷新时加载第0页数据
		mTeambuyactor.loadProducts(0,true);

	}

	@Override
	public void onLoadingMore() {
		//未使用上拉加载更多
	}

	@Override
	public void onResume() {
		super.onResume();
		//加载分类
		mTeambuyactor.loadProductCategory();
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		//顶部搜索栏
		case R.id.setting:
			Intent intent = new Intent(getActivity(), SearchActivity.class);
			startActivity(intent);
			break;
			
//		case R.id.btn_scan:
//		//二维码：
//			break;
		
		//查看全部团购按钮
		case R.id.btn_all_tb:
		case R.id.btn_near_buynow:
			Intent fIntent = new Intent(getActivity(), AllTuangouActivity.class);
			startActivity(fIntent);
			break;
		
		// 附近商家点击事件
		case R.id.rl1:
			Intent businessIntent = new Intent(getActivity(), NearBusinessActivity.class);
			businessIntent.putExtra("pTag", "");
			businessIntent.putExtra("cTag", "");
			startActivity(businessIntent);
			break;
		//触发同一个事件
		case R.id.rl2:
		case R.id.rl3:
		case R.id.rl4:
		case R.id.rl5:
		case R.id.rl6:
		case R.id.rl7:
			String pTag="";
			//判断是否为空
			if(view.getTag()!=null){
				pTag=view.getTag().toString();
			}
			Intent tagIntent = new Intent(getActivity(), NearBusinessActivity.class);
			tagIntent.putExtra("pTag", pTag);
			tagIntent.putExtra("cTag", "");
			startActivity(tagIntent);
			break;
		//更多分类按钮点击事件
		case R.id.rl8:
			Intent moreIntent = new Intent(getActivity(), MoreCategoryActivity.class);
			startActivity(moreIntent);
			break;
		case R.id.ads1:
			Intent aIntent=new Intent(getActivity(),MainActivity.class);
			startActivity(aIntent);
			break;

		default:
			break;
		}

	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (productCr!=null) {
			productCr.close();
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		//System.out.println("arg0:"+arg0+",arg1:"+arg1+",arg2:"+arg2);
		
	}

	@Override
	public void onPageSelected(int arg0) {
		dotArray.get(arg0).setImageResource(R.drawable.dot_focus);
		dotArray.get(beforposition).setImageResource(R.drawable.dot_normal);
		beforposition=arg0;
		
	}
 /* public void runviewPage(){
	  new Thread(){
		  public void run() {
				 // SystemClock.sleep(3000);
				  myhandle.sendEmptyMessage(CHANGE_VIEW_PAGER);
			  
		  };
	  }.start();
  }*/
	private class Runviewpage implements Runnable{

	@Override
	public void run() {
		myhandle.sendEmptyMessage(CHANGE_VIEW_PAGER);
	}
		
	}
  private int currentitem=0;
  Handler myhandle=new Handler(){
	public void handleMessage(android.os.Message msg) {
		switch (msg.what) {
		case CHANGE_VIEW_PAGER:
			currentitem=(adsViewPager.getCurrentItem()+1)%dotArray.size();
			adsViewPager.setCurrentItem(currentitem);
			postDelayed(new Runviewpage(), 5000);
			break;

		default:
			break;
		}
	};  
  };

@Override
public boolean onTouch(View v, MotionEvent event) {
	switch (event.getAction()) {
	case MotionEvent.ACTION_DOWN:
		myhandle.removeCallbacksAndMessages(null);
		break;
	case MotionEvent.ACTION_UP:
		myhandle.postDelayed(new Runviewpage(), 3000);
		break;
	default:
		break;
	}
	return false;
}
}
