package com.teambuy.zhongtuan.adapter;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.activity.near.NearProductActivity;
//使用这个BusinessAdapter
public class BusinessAdapter extends SimpleCursorAdapter {

	Map<String, Cursor> productCrMap = new HashMap<>();
	Map<String, Boolean> openMark = new HashMap<>();
	SQLiteDatabase db=ZhongTuanApp.getInstance().getRDB();
	int mLayoutId;
	String[] mFrom;
	int[] mTo;
	
	public BusinessAdapter(Context context,int layoutId, Cursor c,String[] from,int[] to) {
		super(context, layoutId, c, from, to, 0);
		mLayoutId = layoutId;
		mFrom = from;
		mTo = to;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {	
		ViewHolder holder=null;
		String shopId=null;
		if(view==null){
			view=LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
			holder=new ViewHolder();
			holder.shopName=(TextView)view.findViewById(R.id.tv_storename);
			holder.distance=(TextView) view.findViewById(R.id.tv_storedistance);
			holder.sum=(TextView) view.findViewById(R.id.btn_more_tg);
			holder.lv=(ListView) view.findViewById(R.id.lv_product);
			view.setTag(holder);
		}
		holder=(ViewHolder) view.getTag();
		
		//给展开按钮设置商铺id唯一标识
		Cursor shopCr=getCursor();		
		shopCr.moveToPosition(position);							
		shopId=shopCr.getString(shopCr.getColumnIndex("_id"));
		holder.sum.setTag(shopId);

		initCell(holder,view);
				
		String[] from = new String[] { "_picurl","_cpmc", "_dj2","_sells"};
		int[] to = new int[] { R.id.img_product,R.id.tv_product_tittle,R.id.tv_product_price,R.id.tv_product_sells};
		
		//给产品list设置适配器
		Cursor productcCr = getProductCursor(shopId);
		holder.lv.setAdapter(new ProductAdapter(mContext,R.layout.business_in_listview,productcCr,from,to));
		
		//openMark标记来设置产品列表的高度
		if(openMark.containsKey(holder.sum.getTag().toString())){
			setListViewHeight(holder.lv, false);
		}else{
			setListViewHeight(holder.lv,true);
		}
		
		//设置产品列表监听器
		holder.lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position ,long row) {
				Intent intent=new Intent(mContext,NearProductActivity.class);
				intent.putExtra("productId",row+"");
				mContext.startActivity(intent);
			}
		});
		return view;
	}
	
	public void setViewText(TextView v, String text,final View parent) {
		switch (v.getId()) {
		//设置查看更多按钮的显示与隐藏和对应的值
		case R.id.btn_more_tg:		
			if(text==null){
				text=0+"";
			}
			//获得商铺的id，在getview（）里已设置商铺id
			final String id=v.getTag().toString();
			int sum=Integer.parseInt(text);
			sum = sum-2;
			if(!openMark.containsKey(id)&&sum>0){
				v.setVisibility(View.VISIBLE);	
				v.setText("查看其它" +sum + "个团购");
			}
			else{
				v.setVisibility(View.GONE);
				}				
				v.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ListView listView  = (ListView) parent.findViewById(R.id.lv_product);
				        setListViewHeight(listView, false);
				        openMark.put(id, true);
				        v.setVisibility(View.GONE);
				        listView.requestLayout();
					}
				});
			break;
		
		case R.id.tv_storedistance:
			v.setText("离我" + text + "m");
			break;
		default:
			v.setText(text);
		}
	}
	/**
	 * 设置listView高度，limit 为true，则限制最多显示两条.
	 * @param listView
	 * @param limit
	 */
	public void setListViewHeight(ListView listView,boolean limit) {   
		ListAdapter listAdapter = listView.getAdapter();    
        if (listAdapter == null)return;
        int totalHeight = 0;
        int listCount = limit?(listAdapter.getCount()>2?2:listAdapter.getCount()):listAdapter.getCount(); 
        for (int i = 0; i < listCount; i++) {   
            View listItem = listAdapter.getView(i, null, listView);   
            listItem.measure(0, 0);   
            totalHeight += listItem.getMeasuredHeight();   
        }   
        ViewGroup.LayoutParams params = listView.getLayoutParams();   
        params.height = (int) (totalHeight + (listView.getDividerHeight() * listCount));   
        listView.setLayoutParams(params);  
    }
	

	
	/**
	 * 获取产品cursor
	 * @param shopId
	 * @return
	 */
	private Cursor getProductCursor(String shopId){
		if(productCrMap.containsKey(shopId))	return productCrMap.get(shopId);
		Cursor cursor = db.query("PRODUCT_LIST", null, "_shopid=?", new String[]{shopId}, null, null, null,null);
		productCrMap.put(shopId, cursor); //缓存cursor。
		return cursor;
	}
	/**
	 * 设置listview的item的店名，距离以及团购数量
	 * @param holder
	 * @param parent
	 * 2015-1-24
	 * lforxeverc
	 */
	private void initCell(ViewHolder holder,View parent){
		Cursor cursor=getCursor();
		setViewText(holder.shopName, cursor.getString(cursor.getColumnIndex(mFrom[0])), parent);
		setViewText(holder.distance, cursor.getString(cursor.getColumnIndex(mFrom[1])), parent);
		setViewText(holder.sum, cursor.getString(cursor.getColumnIndex(mFrom[2])), parent);
		
	}
	
	public static class ViewHolder{
		TextView shopName,distance,sum;
		ListView lv;
	}
	/**
	 * 移除商铺对应的产品游标列表
	 * 2015-1-24
	 * lforxeverc
	 */
	public void closeProductCrs() {
		if(productCrMap.isEmpty()){return;}
		for(String key:productCrMap.keySet()){
			productCrMap.get(key).close();
		}
		productCrMap=null;
		openMark=null;
		System.gc();
	}

}
