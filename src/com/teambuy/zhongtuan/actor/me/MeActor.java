package com.teambuy.zhongtuan.actor.me;

import android.content.Context;
import android.view.View;

import com.teambuy.zhongtuan.R;
import com.teambuy.zhongtuan.ZhongTuanApp;
import com.teambuy.zhongtuan.actor.SuperActor;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.me.MeListener;
import com.teambuy.zhongtuan.model.Model;
import com.teambuy.zhongtuan.model.User;
import com.teambuy.zhongtuan.utilities.ImageUtilities;

public class MeActor extends SuperActor {
	Context mContext;
	MeListener mListener;

	public MeActor(Context context,View v,MeListener listener) {
		super(context);
		setCurrentView(v);
		mContext = context;
		mListener = listener;
	}

	/* ====================== logic process ==================== */
	/**
	 * 初始化页面
	 */
	public void initView() {
		initTitleBar(D.BAR_SHOW_RIGHT, "我的中团");
		String uid = ZhongTuanApp.getInstance().getAppSettings().uid;
		User user = Model.load(new User(), uid);
		$tv("tv_me_name").setText(user.getNickname());
		$btn("setting").setOnClickListener(mListener);
		ImageUtilities.loadBitMap(user.getAvatar(), $iv("iv_avatar"));
		$btn("setting").setBackgroundResource(R.drawable.img_me_setting);
	}



	/* ======================= helpers ========================== */
	
}
