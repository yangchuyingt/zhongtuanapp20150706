package com.teambuy.zhongtuan.activity.near;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.activity.BaseActivity;
import com.teambuy.zhongtuan.activity.me.unpay.PicAndWordActivity;
import com.teambuy.zhongtuan.activity.near.pay.OrderActivity;
import com.teambuy.zhongtuan.actor.BusinessActor;
import com.teambuy.zhongtuan.adapter.EvaluationAdapter;
import com.teambuy.zhongtuan.background.NetAsync;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.global.NetAsyncListener;
import com.teambuy.zhongtuan.listener.near.BusinessListener;
import com.teambuy.zhongtuan.model.Collection;
import com.teambuy.zhongtuan.model.Evaluation;
import com.teambuy.zhongtuan.model.Model;
import com.teambuy.zhongtuan.model.Product;
import com.teambuy.zhongtuan.utilities.FileUtilities;
import com.teambuy.zhongtuan.utilities.ImageUtilities;
import com.teambuy.zhongtuan.utilities.JsonUtilities;
import com.teambuy.zhongtuan.views.CustomProgressDialog;

//使用中
public class NearProductActivity extends BaseActivity implements
		NetAsyncListener, BusinessListener, OnClickListener {
	TextView tittleTv, shopname, productname, sellsum, p1, address, tel1, tel2,
			prime_price, price, taocan, tips, ratingTv;
	Button backBtn, detailBtn, evaluationBtn, shareBtn;
	CheckBox collectCb;
	RatingBar ratingbar;
	ImageView shopView, telIcon;
	String shopName, productName, addressString, telStr1, telStr2, sellSum,
			primePrice, nowPrice, shopId, productId, picUrl, cpmemo, sysm,
			detail, rating, collectId;
	CustomProgressDialog mDialog;
	BusinessActor mActor;
	Cursor shopCursor, productCursor,collectCr,evaluationCursor;
	SQLiteDatabase db;
	ListView listview;
	View headerView, footerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lv_near_detail);
		initViews();
		initData();
		initListener();
		loadEvaluation();
		loadCollection();
	}

	/**
	 * 设置监听器
	 * 
	 * @author lforxeverc 2014-12-22上午11:33:16
	 */
	private void initListener() {
		backBtn.setOnClickListener(this);
		detailBtn.setOnClickListener(this);
		evaluationBtn.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		tel1.setOnClickListener(this);
		telIcon.setOnClickListener(this);
		collectCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean state) {
				collect(state ? D.API_ADD_COLLECT : D.API_CANCEL_COLLECT);
			}

		});

	}

	/**
	 * 此方法流程，先从数据库查产品，若无，网络加载商品信息，根据商品的shopid查找数据库中的信息，若无，网络加载信息
	 * 根据传入的产品id查询数据库（由于前一个页面已经加载了数据
	 * ，所以不需要网络访问，直接取数据），如果从收藏页面进来则有可能数据库没有该商品，需要访问网络获取
	 * 
	 * @author lforxeverc 2014-12-22上午11:25:51
	 */
	private void initData() {
		// 从上一个页面获得产品ID，并查询PRODUCT_LIS数据表
		productId = getIntent().getStringExtra("productId");
		db = ZhongTuanApp.getInstance().getRDB();
		if(productCursor!=null) productCursor.close();
		productCursor = db.query("PRODUCT_LIST", null, "_id=?",
				new String[] { productId }, null, null, null);
		// 数据库有数据则直接查询数据库中的商品
		if (productCursor.getCount() == 1) {
			productCursor.moveToFirst();
			productName = productCursor.getString(productCursor
					.getColumnIndex("_cpmc"));
			picUrl = productCursor.getString(productCursor
					.getColumnIndex("_picurl"));
			primePrice = productCursor.getString(productCursor
					.getColumnIndex("_dj0"));
			nowPrice = productCursor.getString(productCursor
					.getColumnIndex("_dj2"));
			sellSum = productCursor.getString(productCursor
					.getColumnIndex("_sells"));
			shopId = productCursor.getString(productCursor
					.getColumnIndex("_shopid"));
			cpmemo = productCursor.getString(productCursor
					.getColumnIndex("_cpmemo"));
			sysm = productCursor.getString(productCursor
					.getColumnIndex("_sysm"));
			productCursor.close();

			// 根据产品的店铺ID从数据库查询店铺，如果数据库不存在该数据则需要网络访问加载该数据
			shopCursor = db.query("STORE_LIST", null, "_id=?",
					new String[] { shopId }, null, null, null);
			if (shopCursor.moveToFirst()) {
				shopName = shopCursor.getString(shopCursor
						.getColumnIndex("_shopname"));
				addressString = shopCursor.getString(shopCursor
						.getColumnIndex("_address"));
				telStr1 = shopCursor.getString(shopCursor
						.getColumnIndex("_tel"));
				rating = shopCursor.getString(shopCursor
						.getColumnIndex("_zfen"));
				shopCursor.close();
				ImageUtilities.loadBitMap(picUrl, shopView);
				shopname.setText(shopName);
				productname.setText(productName);
				p1.setText(shopName);
				sellsum.setText("销量：" + sellSum);
				address.setText(addressString);
				tel1.setText(telStr1);
				price.setText(nowPrice);
				prime_price.setText(primePrice);
				taocan.setText(cpmemo);
				tips.setText(sysm);
				ratingbar.setRating(Float.parseFloat(rating));
				ratingTv.setText(rating);

			} else {
				// 商家不在数据库中，需要网络请求加载
				loadStore();
			}
		} else {
			// 产品在数据库中不存在，需要网络请求加载
			loadProduct();
		}
	}

	/**
	 * 初始化views
	 * 
	 * @author lforxeverc 2014-12-22上午10:41:41
	 */
	private void initViews() {
		listview = (ListView) findViewById(R.id.lv_pj);
		ListView header = new ListView(this);
		headerView = this.getLayoutInflater().inflate(R.layout.product_details,
				header, false);
		footerView = this.getLayoutInflater().inflate(
				R.layout.footer_evaluation, header, false);
		listview.addHeaderView(headerView);
		listview.addFooterView(footerView);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				R.layout.textview, new String[] { "评论加载中..." });
		listview.setAdapter(adapter);
		tittleTv = (TextView) findViewById(R.id.tv_header_tittle);
		backBtn = (Button) findViewById(R.id.back);
		detailBtn = (Button) findViewById(R.id.detailbtn);
		shopView = (ImageView) findViewById(R.id.shopview);
		shopname = (TextView) findViewById(R.id.shopname);
		productname = (TextView) findViewById(R.id.productname);
		sellsum = (TextView) findViewById(R.id.sellsum);
		p1 = (TextView) findViewById(R.id.p1);
		address = (TextView) findViewById(R.id.address);
		telIcon = (ImageView) findViewById(R.id.telicon);
		tel1 = (TextView) findViewById(R.id.tel1);
		tel2 = (TextView) findViewById(R.id.tel2);
		prime_price = (TextView) findViewById(R.id.prime_price);
		price = (TextView) findViewById(R.id.price);
		taocan = (TextView) findViewById(R.id.taocan);
		tips = (TextView) findViewById(R.id.tips);
		evaluationBtn = (Button) findViewById(R.id.evaluationbtn);
		shareBtn = (Button) findViewById(R.id.share);
		collectCb = (CheckBox) findViewById(R.id.setting);
		ratingbar = (RatingBar) findViewById(R.id.ratingbar);
		ratingTv = (TextView) findViewById(R.id.barsum);
		tel2.setVisibility(View.GONE);
		mDialog = CustomProgressDialog.createDialog(this);
		tittleTv.setText("商品详情");
		backBtn.setBackgroundResource(R.drawable.header_back);

	}

	/**
	 * 购买按钮事件点击事件
	 * 
	 * @param v
	 * @author lforxeverc 2014-12-22上午11:34:30
	 */
	public void onBuyNowClick(View v) {
		Intent intent = new Intent(NearProductActivity.this,
				OrderActivity.class);
		intent.putExtra("shop", shopId);
		intent.putExtra("cpid", productId);
		intent.putExtra("cpmc", productName);
		intent.putExtra("cppic", picUrl);
		intent.putExtra("cpdj", nowPrice);
		startActivity(intent);

	}

	@Override
	public void onResultError(String reqUrl, String errMsg) {
		mDialog.dismiss();
		switch (reqUrl) {
		// 获取商家接口
		case D.API_SHOP_GETASHOP:
			break;
		// 获取评价接口
		case D.API_GET_EVALUATION:
			ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
					R.layout.textview, new String[] { "暂无评论" });
			listview.setAdapter(adapter);
			listview.removeFooterView(footerView);
			break;
		// 收藏接口
		case D.API_ADD_COLLECT:
			break;
		// 取消收藏
		case D.API_CANCEL_COLLECT:
			break;
		// 获得商品
		case D.API_CPMX_GETACPMX:
			Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
			break;
		}

	}

	@Override
	public void onResultSuccess(String reqUrl, Object data) {
		switch (reqUrl) {
		// 获得商家
		case D.API_SHOP_GETASHOP:
			mDialog.dismiss();
			shopCursor.close();
			shopCursor = db.query("STORE_LIST", null, "_id=?",
					new String[] { shopId }, null, null, null);
			shopCursor.moveToFirst();
			shopName = shopCursor.getString(shopCursor
					.getColumnIndex("_shopname"));
			addressString = shopCursor.getString(shopCursor
					.getColumnIndex("_address"));
			telStr1 = shopCursor.getString(shopCursor.getColumnIndex("_tel"));
//			shopCursor.close();
			ImageUtilities.loadBitMap(picUrl, shopView);
			shopname.setText(shopName);
			productname.setText(productName);
			p1.setText(shopName);
			sellsum.setText(sellSum);
			address.setText(addressString);
			tel1.setText(telStr1);
			price.setText(nowPrice);
			prime_price.setText(primePrice);
			break;
		// 获得评价
		case D.API_GET_EVALUATION:
			evaluationCursor = db.query("EVALUATION_LIST", null, null,
					null, null, null, null, "2");
			if (evaluationCursor.moveToFirst()) {
				EvaluationAdapter adapter = new EvaluationAdapter(this,
						R.layout.listitem_pj, evaluationCursor, new String[] {
								"_username", "_dateandtime", "_recmemo" },
						new int[] { R.id.cusname, R.id.time, R.id.pingjia }, 0);
				listview.setAdapter(adapter);
			}
			break;
		// 添加收藏
		case D.API_ADD_COLLECT:
			Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
			break;
		// 取消收藏
		case D.API_CANCEL_COLLECT:
			Toast.makeText(this, "取消收藏成功", Toast.LENGTH_SHORT).show();
			deleteCollection();

			break;
		// 检查是否收藏（用于界面显示）
		case D.API_CHECK_COLLECT:
			setCheck();
			break;
		// 获得商品
		case D.API_CPMX_GETACPMX:
			mDialog.dismiss();
			initData();

		}

	}

	/**
	 * 取消收藏后删除数据库内的信息 lforxeverc 2014-12-25下午3:30:39
	 */
	private void deleteCollection() {
		db.delete("COLLECTION_LIST", "_lbid = ?", new String[] { productId });

	}

	/**
	 * 根据数据库查看商品是否收藏 lforxeverc 2014-12-25下午3:22:53
	 */
	private void setCheck() {
		collectCr = db.query("COLLECTION_LIST", null, "_lbid=?",
				new String[] { productId }, null, null, null);
		collectCr.moveToFirst();
		// 如果有数据，表明已收藏
		if (collectCr.getCount() == 0) {
			collectCb.setChecked(false);
		} else {
			collectCb.setChecked(true);
		}

	}

	@Override
	public void onTokenTimeout() {
		mDialog.dismiss();
		ZhongTuanApp.getInstance().logout(this);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {

		// 返回事件
		case R.id.back:
			finish();
			break;

		// 图文详情按钮点击事件
		case R.id.detailbtn:
			Intent intent = new Intent(NearProductActivity.this,
					PicAndWordActivity.class);
			intent.putExtra("cpje", nowPrice);
			intent.putExtra("cpdj", nowPrice);
			intent.putExtra("pprice", primePrice);
			intent.putExtra("shop", shopId);
			intent.putExtra("cpid", productId);
			intent.putExtra("cppic", picUrl);
			intent.putExtra("cpmc", productName);
			// 状态码1表示没有订单号，状态码0代表有订单号（为图文详情支付做准备）
			intent.putExtra("tag", "1");
			startActivity(intent);
			break;

		// 顶部分享按钮
		case R.id.share:
			showShare();
			break;

		// 显示评价点击事件
		case R.id.evaluationbtn:
			Intent intentEva = new Intent(NearProductActivity.this,
					EvaluationListActivity.class);
			intentEva.putExtra("shop", shopId);
			intentEva.putExtra("cpid", productId);
			startActivity(intentEva);
			break;

		// 拨打电话事件
		case R.id.tel1:
		case R.id.telicon:
			Intent telIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ telStr1));
			startActivity(telIntent);
			break;
		default:
			break;
		}

	}

	/**
	 * 
	 * 分享按钮功能，用于展示分享页面,待完善 2015-1-14
	 * 
	 * @author lforxeverc
	 * 
	 */
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.ic_launcher,
				getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://app.teambuy.com.cn/down/");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("中团APP下载链接 :http://app.teambuy.com.cn/down/");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 oks.setImagePath(FileUtilities.getImagePath()+"ic.png");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://app.teambuy.com.cn/down/");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("中团APP下载链接  \n想省钱，找中团");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("中团APP下载链接  \n想省钱，找中团");
		// 启动分享GUI
		oks.show(this);
	}

	/**
	 * 收藏事件
	 * @param url
	 * @author lforxeverc 2014-12-22上午11:37:01
	 */
	private void collect(final String url) {
		final SharedPreferences pre = getSharedPreferences(
				"zhongtuan_preference", Context.MODE_PRIVATE);
		NetAsync collectTask = new NetAsync(url, this) {
			@Override
			public Object processDataInBackground(JsonElement elData) {
				// TODO Auto-generated method stub
				return elData;
			}

			@Override
			public void beforeRequestInBackground(List<NameValuePair> params) {
				params.add(new BasicNameValuePair("uflb", "cpmx-cp"));
				params.add(new BasicNameValuePair("lbid", productId));
				if (url.equals(D.API_ADD_COLLECT)) {
					params.add(new BasicNameValuePair("lngo", pre.getString(
							"lgn", "")));
					params.add(new BasicNameValuePair("lato", pre.getString(
							"lat", "")));
				}
			}
		};
		collectTask.execute();
	}

	/**
	 * 查询该商品是否收藏 lforxeverc 2014-12-25下午2:52:27
	 */
	private void loadCollection() {
		NetAsync collTask = new NetAsync(D.API_CHECK_COLLECT, this) {

			@Override
			public Object processDataInBackground(JsonElement elData) {
				Collection data = JsonUtilities.parseModelByType(elData,
						Collection.class);
				data.save();
				return elData;
			}

			@Override
			public void beforeRequestInBackground(List<NameValuePair> params) {
				params.add(new BasicNameValuePair("uflb", "cpmx-cp"));
				params.add(new BasicNameValuePair("lbid", productId));
			}
		};
		collTask.execute();
	}

	/**
	 * 根据店铺id加载店铺信息
	 * 
	 * 2015-1-14 lforxeverc
	 */
	private void loadStore() {
		mDialog.show();
		mActor = new BusinessActor(this, this);
		mActor.loadStoreById(Long.parseLong(shopId));

	}

	/**
	 * 根据商品ID加载商品
	 * 
	 * 2015-1-14 lforxeverc
	 */
	private void loadProduct() {
		mDialog.show();
		NetAsync task = new NetAsync(D.API_CPMX_GETACPMX, this) {

			@Override
			public Object processDataInBackground(JsonElement elData) {
				Type type = new TypeToken<Product>() {
				}.getType();
				Product product = JsonUtilities.parseModelByType(elData, type);
				product.save();
				return null;
			}

			@Override
			public void beforeRequestInBackground(List<NameValuePair> params) {
				params.add(new BasicNameValuePair("modid", productId));

			}
		};
		task.execute();
	}

	/**
	 * 加载商品评价（两条）
	 * 
	 * @author lforxeverc 2014-12-22上午11:27:33
	 */
	private void loadEvaluation() {
		NetAsync loadEvaluation = new NetAsync(D.API_GET_EVALUATION, this) {

			@Override
			public Object processDataInBackground(JsonElement elData) {
				Type type = new TypeToken<Evaluation[]>() {
				}.getType();
				Evaluation[] evaluation = JsonUtilities.parseModelByType(
						elData, type);
				Model.delete(Evaluation.class);
				Model.save(evaluation);
				return null;
			}

			@Override
			public void beforeRequestInBackground(List<NameValuePair> params) {
				params.add(new BasicNameValuePair("shopid", shopId));
				params.add(new BasicNameValuePair("cpid", productId));
				params.add(new BasicNameValuePair("page", "0"));

			}
		};
		loadEvaluation.execute();
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
		if(shopCursor!=null) 
			shopCursor.close();
		if(productCursor!=null) 
			productCursor.close();
		if(collectCr!=null) 
			collectCr.close();
		if(evaluationCursor!=null) 
			evaluationCursor.close();
		ImageUtilities.removeBitmaps();
	}
}
