package com.example.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.model.AttitudeDesign;
import com.example.model.DesignDetail;
import com.example.testapp.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class Util {
	private Context context;
	public Util(Context context){
		this.context = context;
	}
	
	//检测网络连接
	public boolean isConn(){
		boolean bisConnFlag = false;
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
		}
		return bisConnFlag;
	}
	// 分享微信(分享网页)
	public void shareWeiXin(Context context, IWXAPI wxApi, int number) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "这里填写链接url";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "这里填写标题";
		msg.description = "这里填写内容";
		// 这里替换一张自己工程里的图片资源
		Bitmap thumb = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);
		msg.setThumbImage(thumb);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = number == 0 ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}
	
	// 网络获取图片
	public static Bitmap getBitmap(String biturl) {
		Bitmap bitmap = null;

		try {
			URL url = new URL(biturl);
			URLConnection conn = url.openConnection();
			InputStream in = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(new BufferedInputStream(in));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
		
	/**
	 * 设置显示头像图片大小
	 * 
	 * @param bm
	 *            图片源
	 * @param newWidth
	 *            图片宽度
	 * @param newHeight
	 *            图片高度
	 * @return
	 */
	public static Bitmap scaleImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 设置想要的大小
		int newWidth1 = newWidth;
		int newHeight1 = newHeight;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth1) / width;
		float scaleHeight = ((float) newHeight1) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}
	
	// 取得接口数据，post
	public String getResult(String url, List<NameValuePair> params)
			throws ParseException, IOException {
		String result = null;
		HttpPost post = new HttpPost(url);
		post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		HttpResponse response = new DefaultHttpClient().execute(post);
		if (response.getStatusLine().getStatusCode() == 200) {
			result = EntityUtils.toString(response.getEntity(),"UTF-8");
		}
		return result;
	}
	
	// 解析AttitudeDesign接口数据
	public List<AttitudeDesign> getAttitudeDesign(String result)
			throws Exception {
		List<AttitudeDesign> all = new ArrayList<AttitudeDesign>();
		JSONObject allData = new JSONObject(result); // 全部的内容变为一个项
		// int status=allData.getInt("status"); // 取出网络状态
		JSONObject jSONObject=allData.getJSONObject("data"); 
		JSONArray jsonArr = jSONObject.getJSONArray("group"); // 取出数组
		for (int x = 0; x < jsonArr.length(); x++) {
			AttitudeDesign attitudeDesign = new AttitudeDesign();
			JSONObject jsonObj = jsonArr.getJSONObject(x);
			if(jsonObj.has("dsg_code")){//二维码部分不存在
			attitudeDesign.setDsg_code(jsonObj.getString("dsg_code"));
			}
			attitudeDesign.setDsg_email(jsonObj.getString("dsg_email"));
			attitudeDesign.setDsg_level(jsonObj.getInt("dsg_level"));
			attitudeDesign.setDsg_name(jsonObj.getString("dsg_name"));
			attitudeDesign.setDsg_photo(jsonObj.getString("dsg_photo"));
			attitudeDesign.setDsg_sex(jsonObj.getInt("dsg_sex"));
			attitudeDesign.setDsg_tel(jsonObj.getInt("dsg_tel"));
			attitudeDesign.setDsg_title(jsonObj.getString("dsg_title"));
			attitudeDesign.setGroup_id(jsonObj.getInt("group_id"));
			attitudeDesign.setGroup_name(jsonObj.getString("group_name"));
			attitudeDesign.setGroup_photo(jsonObj.getString("group_photo"));
			attitudeDesign
					.setGroup_photo_num(jsonObj.getInt("group_photo_num"));
			attitudeDesign.setGroup_theme(jsonObj.getString("group_theme"));
			attitudeDesign.setShowFlag(true);
			all.add(attitudeDesign);
		}
		return all;
	}
		
	/** 
     * 获取当前应用程序的版本号。 
     */  
    public int getAppVersion() {  
        try {  
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),  
                    0);  
            return info.versionCode;  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
        }  
        return 1;  
    } 
    
	// 解析photo接口数据
	public List<DesignDetail> getDesignDetail(String result) throws Exception {
		List<DesignDetail> all = new ArrayList<DesignDetail>();
		JSONObject allData = new JSONObject(result); // 全部的内容变为一个项
		JSONObject allData2 = allData.getJSONObject("data"); 
		JSONArray jsonArr = allData2.getJSONArray("photo"); // 取出数组
		for (int x = 0; x < jsonArr.length(); x++) {
			DesignDetail designPhoto = new DesignDetail();
			JSONObject jsonObj = jsonArr.getJSONObject(x);
			designPhoto.setPhoto_url(jsonObj.getString("photo_url"));
			designPhoto.setPhoto_infor(jsonObj.getString("photo_infor"));
			all.add(designPhoto);
		}
		return all;
	}
    
    /** 
     * 使用MD5算法对传入的key进行加密并返回。 
     */  
    public String StringToMD5(String key) {  
        String cacheKey;  
        try {  
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");  
            mDigest.update(key.getBytes());  
            cacheKey = bytesToHexString(mDigest.digest());  
        } catch (NoSuchAlgorithmException e) {  
            cacheKey = String.valueOf(key.hashCode());  
        }  
        return cacheKey;  
    }  
    //将md5加密的二进制转化为字符串
    private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
    
    /** 
     * 根据传入的uniqueName获取硬盘缓存的路径地址。 
     */  
    public File getDiskCacheDir( String uniqueName) {  
        String cachePath;  
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())  
                || !Environment.isExternalStorageRemovable()) {  
            cachePath = context.getExternalCacheDir().getPath();  
        } else {  
            cachePath = context.getCacheDir().getPath();  
        }  
        return new File(cachePath + File.separator + uniqueName);  
    }  
    
    /**
	 * 建立HTTP请求，并获取Bitmap对象。
	 * 
	 * @param imageUrl
	 *            图片的URL地址
	 * @return 解析后的Bitmap对象
	 */
	public boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
			out = new BufferedOutputStream(outputStream, 8 * 1024);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	//bitmao转为圆形图片
		 public Bitmap createCircleImage(Bitmap source)  
		    {  
		        final Paint paint = new Paint();  
		        paint.setAntiAlias(true);  
		        int min=source.getWidth()<=source.getHeight()?source.getWidth():source.getHeight();
		        Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);  
		        /** 
		         * 产生一个同样大小的画布 
		         */  
		        Canvas canvas = new Canvas(target);  
		        /** 
		         * 首先绘制圆形 
		         */  
		        canvas.drawCircle(min / 2, min / 2, min / 2, paint);  
		        /** 
		         * 使用SRC_IN 
		         */  
		        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  
		        /** 
		         * 绘制图片 
		         */  
		        canvas.drawBitmap(source, 0, 0, paint); 
		        return target;  
		    }  

}
