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
	private AttitudeDesign attitudeDesign=null;
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
	private int topDistance=0;
	private String description=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		Intent intent = getIntent();
		moduleList = (ArrayList<DesignDetail>) intent
				.getSerializableExtra("DesignDetail");// 接口数据
		attitudeDesign=(AttitudeDesign) intent
				.getSerializableExtra("AttitudeDesign");
		topDistance=intent.getIntExtra("topDistance", 0);
		share = this.getSharedPreferences(StaticProperty.SAVEINFO,
				Activity.MODE_PRIVATE);
		util = new Util(this);
		try {
			// 获取图片缓存路径
			File cacheDir = util.getDiskCacheDir("thumb");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			// 创建DiskLruCache实例，初始化缓存数据
			mDiskLruCache = DiskLruCache.open(cacheDir, util.getAppVersion(),
					1, 10 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}

		detailBackBtn = (Button) super.findViewById(R.id.detailBackBtn);
		detailShareBtn = (Button) super.findViewById(R.id.detailShareBtn);
		detailIndexLy = (LinearLayout) super.findViewById(R.id.detailIndexLy);
		detailCurrentText = (TextView) super
				.findViewById(R.id.detailCurrentText);
		detailCurrentText.setText(String.valueOf(1));
		detailAllText = (TextView) super.findViewById(R.id.detailAllText);
//		System.out.println(detailAllText+"!!!!!!!!!!!!!!!!");
		if (moduleList != null && moduleList.size() > 0) {
			detailAllText.setText(String.valueOf(moduleList.size()+1));
		} else {
			detailAllText.setText(String.valueOf(1));
		}
		detailBackBtn.setOnClickListener(this);
		detailShareBtn.setOnClickListener(this);
		detailViewPager = (ViewPager) super.findViewById(R.id.detailViewPager);
		pagerAdapter = new AsiViewPaperAdapter(this, moduleList, mDiskLruCache,attitudeDesign,topDistance);
		detailViewPager.setAdapter(pagerAdapter);
		detailViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int arg0) {
				System.out.println("翻页完成!!!!!!!!!!onPageSelected" + arg0);
				detailCurrentText.setText(String.valueOf(arg0 + 1));
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// System.out.println("翻页中!!!!!!!!!!onPageScrolled" + arg0 +
				// "��"
				// + arg1 + "��" + arg2);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				System.out.println("翻页状态改变!!!!!!!!!!onPageScrollStateChanged"
						+ arg0);

			}
		});
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
			LayoutInflater inflater = LayoutInflater
			.from(DetailActivity.this);
	DetailActivity.this.popView = inflater.inflate(
			R.layout.popwindow, null); // 找到了布局文件中的View
	DetailActivity.this.popView
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DetailActivity.this.popWin.dismiss(); // 不显示
				}
			});
	DetailActivity.this.popWin = new PopupWindow(
			DetailActivity.this.popView, share.getInt(StaticProperty.SCREENWIDTH, 480),
			share.getInt(StaticProperty.SCREENHEIGHT, 720), true);
	DetailActivity.this.popWin.setFocusable(true); // 弹出框获取焦点
	DetailActivity.this.popWin
			.setBackgroundDrawable(new PaintDrawable()); // 加入点击屏幕其他部分和返回键都能实现使其消失
	DetailActivity.this.weixnPopBg = (RelativeLayout) DetailActivity.this.popView
			.findViewById(R.id.weixinPopBg);
	  ColorDrawable dw = new ColorDrawable(-00000);
	  popWin.setBackgroundDrawable(dw);
//	weixnPopBg.getBackground().setAlpha(200);// 设置背景透明0-255
	DetailActivity.this.weixinPopImg01 = (ImageView) DetailActivity.this.popView
			.findViewById(R.id.weixinPopImg01); // 取得弹出界面中的组件
	DetailActivity.this.weixinPopImg02 = (ImageView) DetailActivity.this.popView
			.findViewById(R.id.weixinPopImg02);
	DetailActivity.this.weixinPopImg01
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DetailActivity.this.popWin.dismiss(); // 不显示
					Intent intent = new Intent();
					intent.setClass(DetailActivity.this,
							WXEntryActivity.class);
					intent.putExtra("flag", 1);
					intent.putExtra("title", attitudeDesign.getGroup_name());
					intent.putExtra("theme", attitudeDesign.getGroup_theme());
					intent.putExtra("groupId", attitudeDesign.getGroup_id());
					intent.putExtra("photo", moduleList.get(0).getPhoto_url());
					startActivityForResult(intent, 1);
				}
			});
	DetailActivity.this.weixinPopImg02
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DetailActivity.this.popWin.dismiss(); // 不显示
					Intent intent = new Intent();
					intent.setClass(DetailActivity.this,
							WXEntryActivity.class);
					intent.putExtra("flag", 2);
					intent.putExtra("title", attitudeDesign.getGroup_name());
					intent.putExtra("theme", attitudeDesign.getGroup_theme());
					intent.putExtra("groupId", attitudeDesign.getGroup_id());
					if(moduleList!=null&&moduleList.size()>0){
					intent.putExtra("photo", moduleList.get(0).getPhoto_url());
					}
					startActivityForResult(intent, 1);
				}
			});
	DetailActivity.this.popWin.showAtLocation(
			DetailActivity.this.detailShareBtn, Gravity.CENTER, 0, 0);
			break;
		default:
			break;
		}

	}
}
