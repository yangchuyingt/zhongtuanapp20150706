package com.teambuy.zhongtuan.listener.me;

import android.widget.AdapterView.OnItemClickListener;

import com.teambuy.zhongtuan.listener.global.PullDownListener;

public interface OrderListListener extends OnItemClickListener,PullDownListener{
	void onLoadPicSuccess();
}
