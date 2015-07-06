package com.teambuy.zhongtuan.actor.near;

import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;

import com.teambuy.zhongtuan.actor.SuperActor;
import com.teambuy.zhongtuan.define.D;
import com.teambuy.zhongtuan.listener.near.ProductListener;
import com.teambuy.zhongtuan.model.Model;
import com.teambuy.zhongtuan.model.Product;
import com.teambuy.zhongtuan.model.Store;
import com.teambuy.zhongtuan.utilities.ImageUtilities;

public class ProductActor extends SuperActor {
	ProductListener mListener;
	Context mContext;
	public ProductActor(Context context){
		super(context);
		mContext = context;
		mListener = (ProductListener)context;
	}
	
	public void initViewWithProductId(String pid){
		Product p = Model.load(new Product(), pid);
		Store s = Model.load(new Store(), p.getShopid());
		initTitleBar(D.BAR_SHOW_LEFT, p.getCpmc());
		$tv("price").setText("￥"+p.getDj2());
		$tv("mPrice").setText("￥"+p.getDj0());
		$tv("name").setText(p.getCpmc());
		$tv("address").setText(s.getAddress());
		initWebView(p.getMid(),p.getContent());
//		$iv("pic").setImageBitmap(ImageUtilities.loadBitMap(p.getPicurl(), $iv("pic"), this));
		ImageUtilities.loadBitMap(p.getPicurl(), $iv("pic"));
	}
	
	/*================================== Helpers ==============================*/
	private void initWebView(String uri,String content){
		WebSettings webSettings = $wv("des").getSettings();
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);  
		$wv("des").loadDataWithBaseURL(uri,  "<style>img,body,html,dl,ul,li,div,p,dd{width:98%;height:auto;}</style>"+content, "text/html", "utf-8", "");
	}
}
