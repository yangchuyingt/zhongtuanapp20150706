package com.teambuy.zhongtuan.listener.login;

import android.text.TextWatcher;

import com.teambuy.zhongtuan.listener.global.NetAsyncListener;
import com.teambuy.zhongtuan.listener.global.ValidateListener;
import com.teambuy.zhongtuan.listener.global.YzmListener;


public interface RegisterListener extends NetAsyncListener, YzmListener ,ValidateListener ,TextWatcher{

}
