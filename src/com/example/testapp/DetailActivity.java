package com.example.testapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import com.example.model.AttitudeDesign;
import com.example.model.DesignDetail;
import com.example.testview.AsiViewPaperAdapter;
import com.example.util.StaticProperty;
import com.example.util.Util;
import com.example.wxapi.WXEntryActivity;
import libcore.io.DiskLruCache;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 详情展示
 * 
 * @author Savvy
 * 
 */
public class DetailActivity extends Activity implements OnClickListener {

	SharedPreferences share = null;
	private ViewPager detailViewPager;
	private AsiViewPaperAdapter pagerAdapter = null;
	private ArrayList<DesignDetail> moduleList = null;
	private AttitudeDesign attitudeDesign = null;
	private ArrayList<View> arrayList = null;
	private DiskLruCache mDiskLruCache;
	private Util util;

	private Button detailBackBtn;
	private Button detailShareBtn;
	private LinearLayout detailIndexLy;
	private TextView detailCurrentText;
	private TextView detailAllText;
	private View popView = null;
	private PopupWindow popWin = null;
	private RelativeLayout weixnPopBg;
	private ImageView weixinPopImg01;
	private ImageView weixinPopImg02;
	private int topDistance = 0;
	private String description = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		//
		Intent intent = super.getIntent();
		moduleList = (ArrayList<DesignDetail>) intent
				.getSerializableExtra("DesignDetail");// 服务器下载的数据
		attitudeDesign = (AttitudeDesign) intent
				.getSerializableExtra("AttitudeDesign");// listview点击过来的
		topDistance = intent.getIntExtra("topDistance", 0);
		// 创建sharedPreferences存储
		share = this.getSharedPreferences(StaticProperty.SAVEINFO,
				Activity.MODE_PRIVATE);

		// 创建一个操作处理类
		util = new Util(this);
		try {
			// 获取图片缓存路径
			File cacheDir = util.getDiskCacheDir("thumb");
			if (!cacheDir.exists()) {// 如果文件不存在，则创建
				cacheDir.mkdirs();
			}
			// 创建DiskLruCache实例，初始化缓存数据
			mDiskLruCache = DiskLruCache.open(cacheDir, util.getAppVersion(),
					1, 10 * 1024 * 1024);
		} catch (Exception e) {
			e.printStackTrace();
		}

		detailBackBtn = (Button) super.findViewById(R.id.detailBackBtn);
		detailBackBtn.setOnClickListener(this);
		detailShareBtn = (Button) super.findViewById(R.id.detailShareBtn);
		detailIndexLy = (LinearLayout) super.findViewById(R.id.detailIndexLy);
		detailCurrentText = (TextView) super
				.findViewById(R.id.detailCurrentText);
		detailCurrentText.setText(String.valueOf(1));
		detailAllText = (TextView) super.findViewById(R.id.detailAllText);

		// 判断从服务器下载的数据是否为空
		if (moduleList != null && moduleList.size() > 0) {
			detailAllText.setText(String.valueOf(moduleList.size() + 1));
		} else {
			detailAllText.setText(String.valueOf(1));
		}

		// 获取viewpage对象
		detailViewPager = (ViewPager) super.findViewById(R.id.detailViewPager);
		pagerAdapter = new AsiViewPaperAdapter(this, moduleList, mDiskLruCache,
				attitudeDesign, topDistance);
		detailViewPager.setAdapter(pagerAdapter);
		detailViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				detailCurrentText.setText(String.valueOf(arg0 + 1));
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		
		findViewById(R.id.detailShareBtn).setOnClickListener(this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			this.mDiskLruCache.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			this.mDiskLruCache.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.detailBackBtn:
			finish();
			break;
		case R.id.detailShareBtn:
			// 首先加载一个布局文件
			LayoutInflater flater = LayoutInflater.from(this);
			//获取布局文件
			popView = flater.inflate(R.layout.popwindow, null);
			//布局文件的点击事件
			popView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DetailActivity.this.popWin.dismiss();
				}
			});
			//将布局文件加入到弹出窗口中
			popWin = new PopupWindow(popView, share.getInt(
					StaticProperty.SCREENWIDTH, 480), share.getInt(
					StaticProperty.SCREENHEIGHT, 720), true);
			popWin.setFocusable(true);//弹出框获得焦点
			//点击屏幕其他地方或者返回键弹出窗口消失
			popWin.setBackgroundDrawable(new PaintDrawable());
			//微信分享布局
			weixnPopBg = (RelativeLayout) popView.findViewById(R.id.weixinPopBg);
			
			//创建一个背景颜色
			ColorDrawable dw = new ColorDrawable(-000000);
			//位弹出窗口设置背景色
			popWin.setBackgroundDrawable(dw);
			
			//获取两个分享的ID
			weixinPopImg01 = (ImageView) popView.findViewById(R.id.weixinPopImg01);
			weixinPopImg02 = (ImageView) popView.findViewById(R.id.weixinPopImg02);
			weixinPopImg01.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					popWin.dismiss();
					Intent intent = new Intent();
					intent.setClass(DetailActivity.this, WXEntryActivity.class);
					intent.putExtra("flag", 1);
					intent.putExtra("title", attitudeDesign.getGroup_name());
					intent.putExtra("theme", attitudeDesign.getGroup_theme());
					intent.putExtra("groupId", attitudeDesign.getGroup_id());
					intent.putExtra("photo", moduleList.get(0).getPhoto_url());
					startActivityForResult(intent, 1);
				}
			});
			weixinPopImg02.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			});
			popWin.showAtLocation(detailShareBtn, Gravity.CENTER, 0, 0);
			
			break;
		default:
			break;
		}
	}
}
