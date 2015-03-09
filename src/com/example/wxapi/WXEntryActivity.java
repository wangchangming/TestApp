package com.example.wxapi;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.testapp.R;
import com.example.util.Util;
import com.example.util.WeixinUtil;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;
	private String appId = "wxf236f8d87506709f";
	// 正式码wxf236f8d87506709f，测试码wx9d6b74e312d417ba

	private DiskLruCache mDiskLruCache;// 硬盘缓存
	private Util util;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wxentry);
		api = WXAPIFactory.createWXAPI(this, appId, false);
		api.registerApp(appId);
		api.handleIntent(getIntent(), WXEntryActivity.this);
		// super.onCreate(savedInstanceState);
		Intent it = super.getIntent();
		int flag = it.getIntExtra("flag", 0);
		String title = it.getStringExtra("title");
		String theme = it.getStringExtra("theme");
		int groupId = it.getIntExtra("groupId", 1);
		String photoURL = it.getStringExtra("photo");
		boolean imageFlag = false;
		Bitmap bitmap = null;
		if (photoURL != null && !"".equals(photoURL)) {
			// 取出硬盘缓存中的图片
			util = new Util(this);
			try {
				// 获取图片缓存路径
				File cacheDir = util.getDiskCacheDir("thumb");
				if (!cacheDir.exists()) {
					cacheDir.mkdirs();
				}
				// 创建DiskLruCache实例，初始化缓存数据
				mDiskLruCache = DiskLruCache.open(cacheDir,
						util.getAppVersion(), 1, 10 * 1024 * 1024);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 生成图片URL对应的key
			final String key = util.StringToMD5(photoURL);
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			Snapshot snapShot = null;
			try {
				snapShot = mDiskLruCache.get(key);
				if (snapShot == null) {
					// System.out.println(imageUrl+"没有硬盘缓存!!!!!!!!!!!!!!!!!!!!!");
					// 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (util.downloadUrlToStream(photoURL, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					// 缓存被写入后，再次查找key对应的缓存
					snapShot = mDiskLruCache.get(key);
				}
				if (snapShot != null) {
					fileInputStream = (FileInputStream) snapShot
							.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}
				// 将缓存数据解析成Bitmap对象
				if (fileDescriptor != null) {
					bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
				}
				if (bitmap != null) {
					// 将Bitmap对象添加到内存缓存当中
					imageFlag = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileDescriptor == null && fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
					}
				}
			}
		}
		if (flag != 0) {
			// System.out.println("########################"+flag);
			if (!imageFlag) {
				android.content.res.Resources res = getResources();
				InputStream is = res.openRawResource(R.drawable.ic_launcher);
				bitmap = BitmapFactory.decodeStream(is);
			}
			new WeixinUtil(WXEntryActivity.this, api).updateWXWebpage(title,
					theme,
					"http://223.4.147.79:8080/AttitudeDesign/view/view.jsp?gid="
							+ groupId, flag, bitmap);
		}
		finish();
	}

	@Override
	public void onReq(BaseReq arg0) {

	}

	@Override
	public void onResp(BaseResp resp) {
		// System.out.println("!!!!!!!!!!!!!!!!resp.errCode:" + resp.errCode +
		// ",resp.errStr:"
		// + resp.errStr);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			// System.out.println("!!!!!!!!!!!!!!!!分享成功");
			WXEntryActivity.this.setResult(2, WXEntryActivity.this.getIntent());
			api.unregisterApp();
			// 分享成功
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			// System.out.println("!!!!!!!!!!!!!!!!分享取消");
			// 分享取消
			WXEntryActivity.this.setResult(2, WXEntryActivity.this.getIntent());
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			// System.out.println("!!!!!!!!!!!!!!!!分享拒绝");
			// 分享拒绝
			WXEntryActivity.this.setResult(3, WXEntryActivity.this.getIntent());
			break;
		}
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mDiskLruCache != null) {
			try {
				this.mDiskLruCache.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDiskLruCache != null) {
			try {
				this.mDiskLruCache.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
