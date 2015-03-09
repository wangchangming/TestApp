package com.example.util;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.BackwardSupportUtil.BitmapFactory;

/**
 * 微信实现，工具类
 * 
 * @author Savvy
 * 
 */
public class WeixinUtil {
	private IWXAPI api;
	private Activity mActivity = null;
	private final int THUMB_SIZE = 150;

	public WeixinUtil(Activity activity, IWXAPI api) {
		mActivity = activity;
		this.api = api;
	}

	// 上传url图片
	public void uploadUrlPic(String url) {
		try {

			WXImageObject imgObj = new WXImageObject();
			imgObj.imageUrl = url;

			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = imgObj;

			Bitmap bmp = BitmapFactory.decodeStream(new URL(url).openStream());
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
					THUMB_SIZE, true);
			bmp.recycle();
			msg.thumbData = bmpToByteArray(thumbBmp, true);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("img");
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			api.sendReq(req);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 上传bitmap图片
	public void uploadPic(Bitmap bitmap) {

		WXImageObject imgObj = new WXImageObject(bitmap);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;

		Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE,
				THUMB_SIZE, true);

		bitmap.recycle();
		msg.thumbData = bmpToByteArray(thumbBmp, true); // 设置缩略图

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);

	}

	// 上传文本
	public void updateWXStatus(String text, String desc) {

		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = desc;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); //
		// transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;

		// 调用api接口发送数据到微信
		api.sendReq(req);
	}

	// 上传网址
	public void updateWXWebpage(String text, String desc, String url,
			int toCircle, Bitmap bmp) {
		try {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = url;
			WXMediaMessage msg = new WXMediaMessage(webpage);// 分享的内容
			msg.title = text;
			msg.description = desc;
//			Bitmap bmp = BitmapFactory.decodeStream(new URL(imgURL)
//					.openStream());
			
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
					THUMB_SIZE, true);
			bmp.recycle();
			msg.thumbData = bmpToByteArray(thumbBmp, true);
			// 构造一个Req
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");// 当前时间
			// transaction字段用于唯一标识一个请求
			req.message = msg;// 加载内容
			req.scene = (toCircle == 1) ? SendMessageToWX.Req.WXSceneSession
					: SendMessageToWX.Req.WXSceneTimeline;// 选择分享对象是好友还是朋友圈

			// 调用api接口发送数据到微信
			boolean sendReq = api.sendReq(req);// 发送微信

		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("!!!!!!!!!!!!!!发送"+sendReq);
	}

	private String buildTransaction(final String type) {

		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();

	}

	private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
