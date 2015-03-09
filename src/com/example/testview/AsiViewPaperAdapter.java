package com.example.testview;

import java.util.ArrayList;

import com.example.model.AttitudeDesign;
import com.example.model.DesignDetail;
import com.example.testapp.R;
import com.example.testview.PullScrollView.OnTurnListener;
import com.example.util.AsyncImageLoader;
import com.example.util.StaticProperty;
import com.example.util.Util;

import libcore.io.DiskLruCache;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;


/**
 ** 作品详细，viewpaper适配器
 */
public class AsiViewPaperAdapter extends PagerAdapter {
	private Util util;
	SharedPreferences share = null;
	private Activity context = null;
	private AttitudeDesign attitudeDesign = null;
	private ArrayList<DesignDetail> arrayList = null;
	private int topDistance=0;
	private boolean doAnimation=true;
	private final int maxMemory = (int) Runtime.getRuntime().maxMemory();// 获取当前应用程序所分配的最大内存
	private final int cacheSize = maxMemory / 4;// 只分4分之一用来做图片缓存
	private LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(
			cacheSize) {
		protected int sizeOf(String key, Bitmap bitmap) {// 复写sizeof()方法
			// replaced by getByteCount() in API 12
			return bitmap.getRowBytes() * bitmap.getHeight() / 1024; // 这里是按多少KB来算
		}
	};

	// 图片硬盘缓存核心类。
	private DiskLruCache mDiskLruCache;

	public AsiViewPaperAdapter(Activity context,
			ArrayList<DesignDetail> arrayList, DiskLruCache mDiskLruCache,
			AttitudeDesign attitudeDesign,int topDistance) {
		this.context = context;
		this.arrayList = arrayList;
		this.mDiskLruCache = mDiskLruCache;
		this.attitudeDesign = attitudeDesign;
		this.topDistance=topDistance;
		// System.out.println("!!!!!!!!!!!!!position"+arrayList.size());
		util = new Util(context);
		share = context.getSharedPreferences(StaticProperty.SAVEINFO,
				Activity.MODE_PRIVATE);
	}

	@Override
	public Object instantiateItem(View container, int position) {
		// System.out.println("!!!!!!!!!!!!!position"+position+"总数"+position%arrayList.size());
		View view = new View(context);
		if (position == 0) {
			view = AsiViewPaperAdapter.this.context.getLayoutInflater()
					.inflate(R.layout.viewpaper_detail_index, null);
			PullScrollView apdScrollView = (PullScrollView) view
					.findViewById(R.id.apdScrollView);
			ImageView apdBgImg = (ImageView) view.findViewById(R.id.apdBgImg);
			updateImageSize(apdBgImg);//动态设置图片布局
			loadBitmap(attitudeDesign.getGroup_photo(), apdBgImg);
			apdScrollView.setHeader(apdBgImg);
			apdScrollView.setOnTurnListener(new OnTurnListener() {
				public void onTurn() {
				}
			});
			TextView apdName = (TextView) view.findViewById(R.id.apdName);
			apdName.setText(attitudeDesign.getDsg_name());
			TextView apdJob = (TextView) view.findViewById(R.id.apdJob);
			apdJob.setText(attitudeDesign.getDsg_title());
			TextView apdInfoText = (TextView) view
					.findViewById(R.id.apdInfoText);
			apdInfoText.setText(attitudeDesign.getGroup_theme());

			ImageView apdheadImage = (ImageView) view
					.findViewById(R.id.apdheadImage);
			loadCircleBitmap(attitudeDesign.getDsg_photo(), apdheadImage);
			Button apdPhone = (Button) view.findViewById(R.id.apdPhone);
			apdPhone.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Uri uri = Uri.parse("tel:01068647345");
					Intent it = new Intent();
					it.setAction(Intent.ACTION_CALL);
					it.setData(uri);
					context.startActivity(it);
				}
			});
			if (attitudeDesign.getDsg_code() != null
					&& !"".equals(attitudeDesign.getDsg_code())) {
				ImageView apdTwoDimensionImage = (ImageView) view
						.findViewById(R.id.apdTwoDimensionImage);
				loadBitmap(attitudeDesign.getDsg_code(), apdTwoDimensionImage);
			} else {
				apdPhone.setVisibility(View.GONE);
			}
			
			if(doAnimation){
				// 头像渐变动画
				AnimationSet set = new AnimationSet(true);
//				TranslateAnimation tran2 = new TranslateAnimation(
//						Animation.RELATIVE_TO_SELF, -10.0f, // X轴开始位置
//						Animation.RELATIVE_TO_SELF, 0.0f, // X轴移动的结束位置
//						Animation.RELATIVE_TO_SELF, 0.0f, // Y轴开始位置
//						Animation.RELATIVE_TO_SELF, 0.0f); // Y轴移动位置
//				tran2.setDuration(2000);// 设置动画持续时间
//				set2.addAnimation(tran2); // 增加动画
//				AlphaAnimation alpha2 = new AlphaAnimation(0, 1); // 由完全显示 -->
//																	// 完全透明
//				alpha2.setDuration(1000);// 设置动画持续时间
//				set2.addAnimation(alpha2); // 增加动画
//				set2.setFillAfter(true);
				ScaleAnimation scale0 = new ScaleAnimation(0.0f, 1.7f, 0.0f, 1.7f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				scale0.setDuration(100); // 3秒完成动画
				ScaleAnimation scale = new ScaleAnimation(1.7f, 1.0f, 1.7f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				scale.setDuration(400); // 3秒完成动画
				set.addAnimation(scale0); // 增加动画
				set.addAnimation(scale); // 增加动画
				apdheadImage.startAnimation(set); // 启动动画
//				
//				AnimationSet set2 = new AnimationSet(true);
//				TranslateAnimation tran = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, // X轴开始位置
//						Animation.RELATIVE_TO_SELF, 0.0f, // X轴移动的结束位置
//						Animation.RELATIVE_TO_SELF, 0.05f, // Y轴开始位置
//						Animation.RELATIVE_TO_SELF, 0.0f);
//				tran.setDuration(500); // 3秒完成动画
//				set2.addAnimation(tran); // 增加动画
//				apdBgImg.startAnimation(set2); // 启动动画
				doAnimation=false;
			}
		} else {
			if (position <= arrayList.size() + 1) {
				DesignDetail designDetail = arrayList.get((position - 1)
						% arrayList.size());

				view = AsiViewPaperAdapter.this.context.getLayoutInflater()
						.inflate(R.layout.viewpaper_detail, null);
				ImageView vdImg = (ImageView) view.findViewById(R.id.vdImg);
				updateImageSize(vdImg);//动态设置图片布局
				TextView vdText = (TextView) view.findViewById(R.id.vdText);
				loadBitmap(designDetail.getPhoto_url(), vdImg);
				vdText.setText(designDetail.getPhoto_infor());
			}
		}
		((ViewPager) container).addView(view);
		return view;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return arrayList.size() + 1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	private void loadBitmap(String urlStr, ImageView image) {
		AsyncImageLoader asyncLoader = new AsyncImageLoader(image, mLruCache,
				mDiskLruCache, 0, 0, util,false);// 什么一个异步图片加载对象
		Bitmap bitmap = asyncLoader.getBitmapFromMemoryCache(urlStr);// 首先从内存缓存中获取图片
		// System.out.println("!!!!!!!!!!!!!内存图片"+bitmap!=null);
		if (bitmap != null) {
			image.setImageBitmap(bitmap);// 如果缓存中存在这张图片则直接设置给ImageView
		} else {
			 image.setImageResource(R.drawable.main_image_bg);// 否则先设置成默认的图片
			asyncLoader.execute(urlStr);// 然后执行异步任务AsycnTask 去网上加载图片
		}
	}
	private void loadCircleBitmap(String urlStr, ImageView image) {
		AsyncImageLoader asyncLoader = new AsyncImageLoader(image, mLruCache,
				mDiskLruCache, 0, 0, util,true);// 什么一个异步图片加载对象
		Bitmap bitmap = asyncLoader.getBitmapFromMemoryCache(urlStr);// 首先从内存缓存中获取图片
		// System.out.println("!!!!!!!!!!!!!内存图片"+bitmap!=null);
		if (bitmap != null) {
			image.setImageBitmap(bitmap);// 如果缓存中存在这张图片则直接设置给ImageView
		} else {
			 image.setImageResource(R.drawable.main_head);// 否则先设置成默认的图片
			asyncLoader.execute(urlStr);// 然后执行异步任务AsycnTask 去网上加载图片
		}
	}


	private void updateImageSize(ImageView imageView) {
		int width=share.getInt(StaticProperty.SCREENWIDTH, 480);
//		int height=share.getInt(StaticProperty.SCREENHEIGHT, 720);
	LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,(int)(width*0.859375)); // 表示新组件的布局参数
	imageView.setLayoutParams(layoutParams);
	}
}
