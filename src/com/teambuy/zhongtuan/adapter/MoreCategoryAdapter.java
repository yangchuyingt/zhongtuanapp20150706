package com.teambuy.zhongtuan.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.activity.near.category.NearBusinessActivity;

public class MoreCategoryAdapter extends SimpleCursorAdapter {
	HashMap<Integer, Cursor> cursorMap = new HashMap<>();
	//mFrom包含两个内容，第一个为大类名称 第二个为大类ID
	String[] mFrom;
	int[] mTo;
	int layoutId;
	final Context mContext;
	Cursor tempCr;
	List<HashMap<String,String>> tagMap=new ArrayList<HashMap<String, String>>();
	Map<String,List<HashMap<String,String>>> dataMap=new HashMap<String, List<HashMap<String,String>>>();
	
	public MoreCategoryAdapter(Context context, int layout, Cursor cr,
			String[] from, int[] to, int flags) {
		super(context,layout,cr,
			from, to,flags);
		mFrom = from;
		mTo = to;
		mContext = context;
		tempCr=cr;
		layoutId=layout;
		tempCr.moveToFirst();
		for(int i=0;i<tempCr.getCount();i++){
			 String dl=tempCr.getString(tempCr.getColumnIndex("_cup"));
			 String xl=tempCr.getString(tempCr.getColumnIndex("_id"));
			 String lbname=tempCr.getString(tempCr.getColumnIndex("_lbname"));
			if(!dl.equals("0")){
				if(!dataMap.containsKey(dl)){
					List<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();
					HashMap<String,String> item=new HashMap<String,String>();
					item.put("xl",xl);
					item.put("lbname",lbname);
					list.add(item);
					dataMap.put(dl, list);
				}
				else{
					List<HashMap<String,String>> list=dataMap.get(dl);
					HashMap<String,String> item=new HashMap<String,String>();
					item.put("xl",xl);
					item.put("lbname",lbname);
					list.add(item);
				}
			
			}
			else{
				HashMap<String,String> item=new HashMap<String, String>();
				item.put("dl",xl);
				item.put("lbname",lbname);
				tagMap.add(item);
			}
			tempCr.moveToNext();
		}
		for(int i=0;i<tagMap.size();i++){
			if(!dataMap.containsKey(tagMap.get(i).get("dl"))){
				tagMap.remove(i);
			}
		}
		
		
	}
	@Override
	public int getCount() {
		
		return dataMap.size();
	}
	

	/**
	 * 设置TextView
	 * @param v
	 * @param text
	 */
	public void setViewText(TextView v, String text) {
		v.setText(text);
	}
	
	@Override
	public View getView(int position, View view, ViewGroup container) {
		ViewHolder holder=null;
		if(view==null){
			view=LayoutInflater.from(mContext).inflate(layoutId, container, false);
			holder=new ViewHolder();
			holder.category_tag=(TextView) view.findViewById(mTo[0]);
			holder.gridView=(GridView) view.findViewById(mTo[1]);
			view.setTag(holder);
		}
		else{
			holder=(ViewHolder)view.getTag();
		}
		final String dl = tagMap.get(position).get("dl");
		final String lbname = tagMap.get(position).get("lbname");
		if(dataMap.containsKey(dl))
		{	
			holder.category_tag.setText(lbname);
			final List<HashMap<String,String>> list=dataMap.get(dl);
		SimpleAdapter adapter=new SimpleAdapter(mContext, list,R.layout.copy_of_more_category_button, new String[]{"lbname"}, new int[]{R.id.btn1});
		holder.gridView.setAdapter(adapter);
		holder.gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = new Intent(mContext,NearBusinessActivity.class);
				intent.putExtra("pTag",dl);
				intent.putExtra("cTag", list.get(position).get("xl"));
				mContext.startActivity(intent);
				((Activity)mContext).finish();
			}
		});
		gridViewAdjustment(holder.gridView);
		
		}
		return view;
	}


	/**
	 * 根据内容的多少调整GridView的高度
	 * @author 刘永元
	 * @param v 待调整的GridView
	 * @return void
	 */
	private void gridViewAdjustment(GridView v){
		ListAdapter listAdapter=v.getAdapter();
		int totalHeight = 0;   
        int listCount = listAdapter.getCount();
        int itemHight=0;
        
        for (int i = 0; i < listCount;i++) {
            View listItem = listAdapter.getView(i, null, v);   
            listItem.measure(0, 0); 
            itemHight=listItem.getMeasuredHeight();
            totalHeight += itemHight;
        }
       
        if(listCount%3!=0){
        	listCount=listCount/3;
        	totalHeight = totalHeight/3+30*(listCount-1)+itemHight;
        }
        else if(listCount!=0){       	
        	listCount=listCount/3;
        	totalHeight = totalHeight/3+30*(listCount-1);
        }
        
		ViewGroup.LayoutParams params=v.getLayoutParams();
		params.height=(int) (totalHeight);
		v.setLayoutParams(params);
	}
	

	
	/**
	 * View Holder
	 * @author 刘永元
	 *
	 */
	public static class ViewHolder {
		public GridView gridView;
		public TextView category_tag;
	}


}
