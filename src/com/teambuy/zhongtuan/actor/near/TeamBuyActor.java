package com.teambuy.zhongtuan.actor.near;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.database.Cursor;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.actor.SuperActor;
import com.teambuy.zhongtuan.adapter.TeamBuyListAdapter;
import com.teambuy.zhongtuan.background.NetAsync;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.TeamBuyListener;
import com.teambuy.zhongtuan.model.Model;
import com.teambuy.zhongtuan.model.Product;
import com.teambuy.zhongtuan.model.ProductCategory;
import com.teambuy.zhongtuan.utilities.DBUtilities;
import com.teambuy.zhongtuan.utilities.JsonUtilities;

public class TeamBuyActor extends SuperActor{
	private TeamBuyListener mListener;
	private TeamBuyListAdapter adapter;
	private Context mContext;

	public TeamBuyActor(Context context,TeamBuyListener listener) {
		super(context);
		mContext = context;
		mListener = listener;
	}

	public void initViews(){
		String[] from = new String[] {D.DB_PRODUCT_PICURL, D.DB_PRODUCT_CPMC, D.DB_PRODUCT_DJ2,D.DB_PRODUCT_SELLS};
		int[] to = new int[] {R.id.img_product,R.id.tv_product_tittle,R.id.tv_product_price,R.id.tv_product_sell};
		Cursor cr = DBUtilities.getProductList();
		adapter = new TeamBuyListAdapter(mContext, cr, from, to, mListener);
		$lv("teambuyList").setAdapter(adapter);
		$lv("teambuyList").setOnItemClickListener(mListener);
	}
	/**
	 * 加载团购商品列表
	 * @param page 页数
	 * 2015-1-21
	 * lforxeverc
	 */
	public void loadProducts(final int page,final Boolean refresh) {
		NetAsync task_loadProduct = new NetAsync(D.API_CPMX_GETALLCPMX,mListener) {
			
			@Override
			public Object processDataInBackground(JsonElement elData) {
				Type type = new TypeToken<Product[]>(){}.getType();
				Product[] products = JsonUtilities.parseModelByType(elData, type);
				//如果是刷新，则删除数据库
				if(refresh) Model.delete(Product.class);
				Model.save(products);
				return null;
			}
			
			@Override
			public void beforeRequestInBackground(List<NameValuePair> params) {
				params.add(new BasicNameValuePair(D.ARG_TEAMBUY_CITYID, ZhongTuanApp.getInstance().getCityId()));
				params.add(new BasicNameValuePair(D.ARG_TEAMBUY_PAGE, page+""));
				params.add(new BasicNameValuePair(D.ARG_TEAMBUY_PX, "0"));				
			}
		};
		task_loadProduct.execute();
	}

	// 刷新列表
	public void updateList(boolean isReloadData) {
		if (isReloadData) {
			Cursor newCr = DBUtilities.getProductList();
			adapter.changeCursor(newCr);
		}
		if(null!=adapter)adapter.notifyDataSetChanged();
	}
	
	/**
	 * 网络访问服务器加载更多分类列表
	 */
	public void loadProductCategory() {
		NetAsync task1 = new NetAsync(D.API_PRODUCT_CATEGORY, mListener) {
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

}
