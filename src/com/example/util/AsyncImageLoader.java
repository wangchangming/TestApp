package com.example.util;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class AsyncImageLoader extends AsyncTask<String, Void, Bitmap> {

	private ImageView image;  
    private LruCache<String, Bitmap> lruCache;  //图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
    private DiskLruCache mDiskLruCache;  //图片硬盘缓存核心类。 
    private int width;
    private int height;
    private Util util;
    private boolean circle;
    /** 
     * 构造方法，需要把ImageView控件和LruCache 对象传进来 
     * @param image 加载图片到此 {@code}ImageView 
     * @param lruCache 缓存图片的对象 
     */  
    public AsyncImageLoader(ImageView image, LruCache<String, Bitmap> lruCache,DiskLruCache mDiskLruCache,int width,int height,Util util,boolean circle) {  
        super();  
        this.image = image;  
        this.lruCache = lruCache;  
        this.mDiskLruCache=mDiskLruCache;
        this.width=width;
        this.height=width;
        this.util=util;
        this.circle=circle;
    }  
  
    @Override  
    protected Bitmap doInBackground(String... params) {  
//        Bitmap bitmap = null;  
//        bitmap = Util.getBitmap(params[0]); 
//        if(width!=0&height!=0){
//        	bitmap=Util.scaleImg(bitmap, width, height);
//        }
//        addBitmapToMemoryCache(params[0], bitmap);  
//        return bitmap;  
    	String imageUrl = params[0];  
         FileDescriptor fileDescriptor = null;  
         FileInputStream fileInputStream = null;  
         Snapshot snapShot = null;  
         try {  
             // 生成图片URL对应的key  
             final String key = util.StringToMD5(imageUrl); 
//             System.out.println(imageUrl+"缓存1!!!!!!!!!!!!!!!!!!!!!");
             // 查找key对应的缓存  
             snapShot = mDiskLruCache.get(key);  
             if (snapShot == null) { 
//            	  System.out.println(imageUrl+"没有硬盘缓存!!!!!!!!!!!!!!!!!!!!!");
                 // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存  
                 DiskLruCache.Editor editor = mDiskLruCache.edit(key);  
                 if (editor != null) {  
                     OutputStream outputStream = editor.newOutputStream(0);  
                     if (util.downloadUrlToStream(imageUrl, outputStream)) {
//                    	 System.out.println(imageUrl+"缓存成功!!!!!!!!!!!!!!!!!!!!!");
                         editor.commit();  
                     } else {  
//                    	 System.out.println(imageUrl+"缓存失败!!!!!!!!!!!!!!!!!!!!!");
                         editor.abort();  
                     }  
                 }  
                 // 缓存被写入后，再次查找key对应的缓存  
                 snapShot = mDiskLruCache.get(key);  
             }else{
//            	 System.out.println(imageUrl+"有硬盘缓存!!!!!!!!!!!!!!!!!!!!!");
             }
             if (snapShot != null) {  
//            	 System.out.println(imageUrl+"加载3!!!!!!!!!!!!!!!!!!!!!");
                 fileInputStream = (FileInputStream) snapShot.getInputStream(0);  
                 fileDescriptor = fileInputStream.getFD();  
             }  
             // 将缓存数据解析成Bitmap对象  
             Bitmap bitmap = null;  
             if (fileDescriptor != null) {  
                 bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);  
             }  
             if (bitmap != null) {  
            	   // 将Bitmap对象添加到内存缓存当中  
            	 if(circle){
            		 bitmap=util.createCircleImage(bitmap);
            	 }
            	  addBitmapToMemoryCache(params[0], bitmap);  
             }  
             return bitmap;  
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
         return null;  
    }  
  
    @Override  
    protected void onPostExecute(Bitmap bitmap) {  
    	if(bitmap!=null){
        image.setImageBitmap(bitmap); 
    	}
    }  
        //调用LruCache的put 方法将图片加入内存缓存中，要给这个图片一个key 方便下次从缓存中取出来  
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {  
        if (key!=null&&bitmap!=null&&getBitmapFromMemoryCache(key) == null) {  
            lruCache.put(key, bitmap);  
        }  
    }  
        //调用Lrucache的get 方法从内存缓存中去图片  
    public Bitmap getBitmapFromMemoryCache(String key) {  
        return lruCache.get(key);  
    }  
    
    
    /** 
     * 将缓存记录同步到journal文件中。 
     */  
    public void fluchCache() {  
        if (mDiskLruCache != null) {  
            try {  
                mDiskLruCache.flush();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
	
}
