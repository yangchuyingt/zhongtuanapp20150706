package com.teambuy.zhongtuan.actor.near;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.teambuy.zhongtuan.actor.SuperActor;
import com.teambuy.zhongtuan.listener.near.NearListener;

public class NearActor extends SuperActor {

	NearListener mListener;

	public NearActor(Context context, View v, NearListener listener) {
		super(context);
		setCurrentView(v);
		mListener = listener;
	}

	public NearActor(Context context) {
		super(context);
	}

	public void initViews() {
	}

	public void showFirstTab() {
//	$Ibtn("business").performClick();
//		$Ibtn("business").performClick();
	}

	public void changeTab(Fragment fragment, FragmentManager fm) {
		FragmentTransaction tr = fm.beginTransaction();
		tr.replace($("nearContent"), fragment);
		tr.commit();
	}
}
