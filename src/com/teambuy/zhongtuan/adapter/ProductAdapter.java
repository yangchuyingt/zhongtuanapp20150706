package com.teambuy.zhongtuan.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.utilities.ImageUtilities;

public class ProductAdapter extends SimpleCursorAdapter{
	Cursor cursor;
	String[] from = new String[] { "_picurl","_cpmc", "_dj2","_sells"};
	int[] to = new int[] { R.id.img_product,R.id.tv_product_tittle,R.id.tv_product_price,R.id.tv_product_sells};
	public ProductAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to, 0);
		cursor=c;
	}


	@Override
	public void setViewImage(final ImageView iv, final String url) {
		ImageUtilities.loadBitMap(url, iv);
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder;
		if(view==null){
			view=LayoutInflater.from(mContext).inflate(R.layout.business_in_listview, parent, false);
			viewHolder=new ViewHolder();
			viewHolder.picView=(ImageView) view.findViewById(R.id.img_product);
			viewHolder.nameView=(TextView) view.findViewById(R.id.tv_product_tittle);
			viewHolder.priceView=(TextView) view.findViewById(R.id.tv_product_price);
			viewHolder.sellsView=(TextView) view.findViewById(R.id.tv_product_sells);
			view.setTag(viewHolder);
		}
		else{
			viewHolder=(ViewHolder) view.getTag();
		}
		if(cursor.isClosed()){
			cursor=getCursor();
		}
		cursor.moveToPosition(position);
		setViewImage(viewHolder.picView, cursor.getString(cursor.getColumnIndex(D.DB_PRODUCT_PICURL)));
		setViewText(viewHolder.nameView, cursor.getString(cursor.getColumnIndex(D.DB_PRODUCT_CPMC)));
		setViewText(viewHolder.priceView, cursor.getString(cursor.getColumnIndex(D.DB_PRODUCT_DJ2)));
		setViewText(viewHolder.sellsView, cursor.getString(cursor.getColumnIndex(D.DB_PRODUCT_SELLS)));
		return view;
		
	}
	@Override
	public void setViewText(TextView v, String text) {
		switch (v.getId()) {
		case R.id.tv_product_tittle:
			v.setText(text);
			break;
		case R.id.tv_product_price:
			v.setText("￥"+text);
			break;
		case R.id.tv_product_sells:
			v.setText("销量:"+text);		
			break;

		default:
			break;
		}
		
	}
	public static class ViewHolder{
		ImageView picView;
		TextView nameView,priceView,sellsView;
	}
	
}

