package com.teambuy.zhongtuan.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.TeamBuyListener;
import com.teambuy.zhongtuan.utilities.ImageUtilities;

public class TeamBuyListAdapter extends SimpleCursorAdapter{
	static String[] from = new String[] { D.DB_PRODUCT_PICURL, D.DB_PRODUCT_CPMC,
			D.DB_PRODUCT_DJ2, D.DB_PRODUCT_SELLS };
	static int[] to = new int[] { R.id.img_product, R.id.tv_product_tittle,
			R.id.tv_product_price, R.id.tv_product_sell };
	Context context;
	Cursor cursor;

	ListView mListView;
	int state_tag = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;		// 断定是否应该刷新列表。

	public TeamBuyListAdapter(Context _context, Cursor c, String[] from, int[] to,TeamBuyListener _listener) {
		super(_context, R.layout.x_list_product, c, from, to, 0);
		context = _context;
		cursor=c;
	}
	public TeamBuyListAdapter (Context context,Cursor c){
		super(context, R.layout.x_list_product, c, from, to, 0);
		this.context = context;
		cursor=c;
		
	}
	
	@Override
	public View getView(int position, View view, ViewGroup group) {
		ViewHolder viewHolder;
		if(view==null){
			view=LayoutInflater.from(mContext).inflate(R.layout.x_list_product, group, false);
			viewHolder=new ViewHolder();
			viewHolder.picView=(ImageView) view.findViewById(R.id.img_product);
			viewHolder.nameView=(TextView) view.findViewById(R.id.tv_product_tittle);
			viewHolder.distanceView=(TextView) view.findViewById(R.id.tv_distance);
			viewHolder.detailView=(TextView) view.findViewById(R.id.webview_product_detail);
			viewHolder.priceView=(TextView) view.findViewById(R.id.tv_product_price);
			viewHolder.sellsView=(TextView) view.findViewById(R.id.tv_product_sell);
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
		setViewText(viewHolder.distanceView, "");
		setViewText(viewHolder.nameView, cursor.getString(cursor.getColumnIndex(D.DB_PRODUCT_CPMC)));
		setViewText(viewHolder.detailView, cursor.getString(cursor.getColumnIndex(D.DB_PRODUCT_CONTENT)));
		setViewText(viewHolder.priceView, cursor.getString(cursor.getColumnIndex(D.DB_PRODUCT_DJ2)));
		setViewText(viewHolder.sellsView, cursor.getString(cursor.getColumnIndex(D.DB_PRODUCT_SELLS)));
		return view;
	}

	@Override
	public void setViewImage(ImageView iv, final String url) {
		ImageUtilities.loadBitMap(url, iv);
	}

	@Override
	public void setViewText(TextView v, String text) {
		super.setViewText(v, text);
		int vid = v.getId();
		switch (vid) {
		case R.id.tv_product_price:
			v.setText("￥"+text);
			break;
		case R.id.tv_product_sell:
			v.setText("销量："+text);
			break;
		case R.id.tv_distance:
			v.setText("");
			break;
		case R.id.webview_product_detail:
			v.setText(text);
			break;
		default:
			break;
		}
	}
	public static class ViewHolder{
		ImageView picView;
		TextView nameView,distanceView,detailView,priceView,sellsView;
	}
}