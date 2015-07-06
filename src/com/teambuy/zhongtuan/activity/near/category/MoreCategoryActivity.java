package com.teambuy.zhongtuan.activity.near.category;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.activity.BaseActivity;
import com.teambuy.zhongtuan.adapter.ExAdapter;
import com.teambuy.zhongtuan.background.NetAsync;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.global.NetAsyncListener;
import com.teambuy.zhongtuan.model.Model;
import com.teambuy.zhongtuan.model.ProductCategory;
import com.teambuy.zhongtuan.utilities.JsonUtilities;
import com.teambuy.zhongtuan.views.CustomProgressDialog;
/**
 * 周边生活点击更多分类按钮后的页面
 * @author lforxeverc
 * 2015-1-13
 */
public class MoreCategoryActivity extends BaseActivity implements NetAsyncListener{
	// title bar
	TextView tittle;
	Button back;
	// list view
	ExpandableListView listView;
	CustomProgressDialog mDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eplvcate);
		
		initTitleBar();		
		initHeaderView();
		initListView();
		loadProductCategory();
		
		
	}
	
	/********************** Helpers ****************************/
	/**
	 * 初始化顶部栏并设置返回监听
	 */
	private void initTitleBar(){
		/// title
		tittle=(TextView) findViewById(R.id.tv_header_tittle);
		tittle.setText("更多分类");		
		/// back button
		back=(Button) findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.header_back);
		back.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	/**
	 * 初始化可展开列表，并把所有列表默认打开
	 */
	private void initListView(){

		ExAdapter adapter=new ExAdapter(this);
		listView.setAdapter(adapter);
		//把所有项展开
		for(int i=0;i<adapter.getGroupCount();i++){
			listView.expandGroup(i);
		}
		listView.setOnGroupExpandListener(null);
	}
	/**
	 * 初始化该可展开列表的头，即顶部的更多分类按钮
	 */
	@SuppressLint("InflateParams")
	private void initHeaderView(){
		listView=(ExpandableListView) findViewById(R.id.exlv);
		View headerView=LayoutInflater.from(this).inflate(R.layout.more_category,null);
		listView.addHeaderView(headerView);
	}
	
	/**
	 * 更多分类按钮点击事件，跳转到全部团购页面
	 * @param v
	 */
	public void onAllCategoryClick(View v){
		Intent intent=new Intent(MoreCategoryActivity.this,AllTuangouActivity.class);
		startActivity(intent);
		
	}
	/**
	 * 网络访问服务器加载更多分类列表
	 */
	private void loadProductCategory() {
		mDialog=CustomProgressDialog.createDialog(this);
		mDialog.show();
		NetAsync task1 = new NetAsync(D.API_PRODUCT_CATEGORY, this) {
			@Override
			public Object processDataInBackground(JsonElement elData) {
				Type type = new TypeToken<ProductCategory[]>() {}.getType();
				ProductCategory[] productCategory=JsonUtilities.parseModelByType(elData, type);
				Model.delete(ProductCategory.class);
				Model.save(productCategory);
				return elData;
			}

			@Override
			public void beforeRequestInBackground(List<NameValuePair> params) {

			}
		};
		task1.execute();
	}

	@Override
	public void onResultError(String reqUrl, String errMsg) {
		mDialog.dismiss();
		
	}

	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		mDialog.dismiss();
		initListView();
		
	}

	@Override
	public void onTokenTimeout() {
		mDialog.dismiss();
		ZhongTuanApp.getInstance().logout(this);
		
	}
	 
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}

}
