package com.teambuy.zhongtuan.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.listener.SaleListener;
import com.teambuy.zhongtuan.utilities.ImageUtilities;

public class SellListAdapter extends SimpleCursorAdapter {


	Context context;
	SaleListener listener;
	private LruCache<String, Bitmap> memorycache;

	/*public SellListAdapter(Context _context, Cursor c, String[] from, int[] to,SaleListener _listener) {
		super(_context, R.layout.shop_product_adapterview, c, from, to, 0);
		context = _context;
		listener = _listener;
		
	}*/
	public SellListAdapter(Context _context, Cursor c, String[] from, int[] to,SaleListener _listener,LruCache<String, Bitmap> memorycache) {
		super(_context, R.layout.shop_product_adapterview, c, from, to, 0);
		context = _context;
		listener = _listener;
		this.memorycache=memorycache;
	}

	@Override
	public void setViewImage( ImageView iv, String url) {
	
		if(memorycache==null||memorycache.get(url)==null){
			ImageUtilities.loadBitMap(memorycache,url, iv);
		}else{
			iv.setImageBitmap(memorycache.get(url));
		}
	
		
	}
	@Override
	public void setViewText(TextView v, String text) {
		super.setViewText(v, text);
		switch (v.getId()) {
		case R.id.tv_item_nowprice:
			v.setText("￥"+text);
			break;
		case R.id.tv_item_beforeprice:
			v.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			v.setText("￥"+text);
			break;
		case R.id.iv_sendtb:
			if(TextUtils.isEmpty(text)||TextUtils.equals(text, "0")){
				v.setVisibility(View.GONE);
			}else{
				v.setVisibility(View.VISIBLE);
				v.setText("送团币");
			}
			break;
		default:
			break;
		}
	}

	
	
}