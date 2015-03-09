package com.example.util;

import java.util.List;

import libcore.io.DiskLruCache;
import com.example.model.AttitudeDesign;
import com.example.testapp.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainAdapter extends BaseAdapter {
	private List<AttitudeDesign> allValues;
	private Activity context;
	private ViewHolder holder;
	private Util util;
	// 图片硬盘缓存核心类。
	private DiskLruCache mDiskLruCache;
	
	private final int maxMemory = (int) Runtime.getRuntime().maxMemory();// 获取当前应用程序所分配的最大内存
	private final int cacheSize = maxMemory / 4;// 只分4分之一用来做图片缓存
	private LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(
			cacheSize) {
		protected int sizeOf(String key, Bitmap bitmap) {// 复写sizeof()方法
			// replaced by getByteCount() in API 12
			return bitmap.getRowBytes() * bitmap.getHeight() / 1024; // 这里是按多少KB来算
		}
	};

	public MainAdapter(Activity ctx, List<AttitudeDesign> allValues,DiskLruCache mDiskLruCache) {
		this.allValues = allValues;
		this.context = ctx;
		this.mDiskLruCache=mDiskLruCache;
		util = new Util(context);
	}
	@Override
	public int getCount() {
		return allValues.size();
	}

	@Override
	public Object getItem(int position) {
		return allValues.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AttitudeDesign attitudeDesign = allValues.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listview_main, null);
			holder = new ViewHolder();
			holder.lmBg = (ImageView) convertView.findViewById(R.id.lmBg);
			holder.lmHeadImg = (ImageView) convertView.findViewById(R.id.lmHeadImg);
			holder.lmImgImg = (ImageView) convertView.findViewById(R.id.lmImgImg);
			holder.lmNumberText = (TextView) convertView
					.findViewById(R.id.lmNumberText);
			holder.lmTypeText = (TextView) convertView
					.findViewById(R.id.lmTypeText);
			holder.lmHeadImg.setTag(attitudeDesign.getDsg_photo());//加入标识，防止乱序
			holder.lmBg.setTag(attitudeDesign.getGroup_photo());
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(attitudeDesign.isShowFlag()){
			holder.lmBg.setVisibility(View.VISIBLE);
			holder.lmHeadImg.setVisibility(View.VISIBLE);
			holder.lmImgImg.setVisibility(View.VISIBLE);
			holder.lmNumberText.setVisibility(View.VISIBLE);
			holder.lmTypeText.setVisibility(View.VISIBLE);
			holder.lmNumberText.setText(attitudeDesign.getGroup_photo_num() + "");
			holder.lmTypeText.setText(attitudeDesign.getGroup_name());
			loadCircleBitmap(attitudeDesign.getDsg_photo(), holder.lmHeadImg);
			loadBitmap(attitudeDesign.getGroup_photo(), holder.lmBg);
		}else{
			holder.lmBg.setImageResource(R.drawable.bg_back);
			holder.lmHeadImg.setVisibility(View.GONE);
			holder.lmHeadImg.setImageResource(R.drawable.bg_back);
			holder.lmImgImg.setVisibility(View.GONE);
			holder.lmNumberText.setVisibility(View.GONE);
			holder.lmTypeText.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	/**
	 * 
	 * @param urlStr
	 *            所需要加载的图片的url，以String形式传进来，可以把这个url作为缓存图片的key
	 * @param image
	 *            ImageView 控件
	 */
	private void loadBitmap(String urlStr, ImageView image, int width,
			int height) {
		// System.out.println(urlStr);
		AsyncImageLoader asyncLoader = new AsyncImageLoader(image, mLruCache,
				mDiskLruCache, width, height,util,false);// 什么一个异步图片加载对象
		Bitmap bitmap = asyncLoader.getBitmapFromMemoryCache(urlStr);// 首先从内存缓存中获取图片
		if(urlStr.equals(image.getTag())){
		if (bitmap != null) {
			image.setImageBitmap(bitmap);// 如果缓存中存在这张图片则直接设置给ImageView
		} else {
//			image.setImageResource(R.drawable.ic_launcher);// 否则先设置成默认的图片
			asyncLoader.execute(urlStr);// 然后执行异步任务AsycnTask 去网上加载图片
		}
		}
	}
	
	private void loadBitmap(String urlStr, ImageView image) {
		AsyncImageLoader asyncLoader = new AsyncImageLoader(image, mLruCache,
				mDiskLruCache, 0, 0,util,false);// 什么一个异步图片加载对象
		Bitmap bitmap = asyncLoader.getBitmapFromMemoryCache(urlStr);// 首先从内存缓存中获取图片
		if (bitmap != null) {
			image.setImageBitmap(bitmap);// 如果缓存中存在这张图片则直接设置给ImageView
		} else {
			image.setImageResource(R.drawable.main_image_bg);// 否则先设置成默认的图片
			asyncLoader.execute(urlStr);// 然后执行异步任务AsycnTask 去网上加载图片
		}
	}
	
	private void loadCircleBitmap(String urlStr, ImageView image) {
		AsyncImageLoader asyncLoader = new AsyncImageLoader(image, mLruCache,
				mDiskLruCache, 0, 0,util,true);// 什么一个异步图片加载对象
		Bitmap bitmap = asyncLoader.getBitmapFromMemoryCache(urlStr);// 首先从内存缓存中获取图片
		if (bitmap != null) {
			image.setImageBitmap(util.createCircleImage(bitmap));// 如果缓存中存在这张图片则直接设置给ImageView
		} else {
			image.setImageResource(R.drawable.main_head);// 否则先设置成默认的图片
			asyncLoader.execute(urlStr);// 然后执行异步任务AsycnTask 去网上加载图片
		}
	}
	// 列表组件的缓存
	static class ViewHolder {
		ImageView lmBg;
		ImageView lmHeadImg;
		ImageView lmImgImg;
		TextView lmNumberText;
		TextView lmTypeText;
	}

}
