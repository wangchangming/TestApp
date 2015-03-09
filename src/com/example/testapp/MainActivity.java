package com.example.testapp;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import libcore.io.DiskLruCache;
import com.example.model.AttitudeDesign;
import com.example.model.DesignDetail;
import com.example.testview.NetDialog;
import com.example.testview.RefreshTestView;
import com.example.testview.RefreshTestView.PullToRefreshListener;
import com.example.util.MainAdapter;
import com.example.util.MyDatabaseHelper;
import com.example.util.MytabOperate;
import com.example.util.Util;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

	private List<AttitudeDesign> attitudeDesignList = null;//用于存储个人信息
	private boolean conFlag = false;// 是否可以连接网络
	private SQLiteOpenHelper helper;//数据库类
	private MytabOperate mtab = null;//数据库操作类
	private Handler myHandler = null;//消息处理
	private ProgressDialog progressDialog = null;//进度对话框
	private Button mainHomeBtn;//点击主页按钮
	private RelativeLayout mainTopRl = null;
	private RefreshTestView refreshableView;//自定义布局
	private ListView listView;
	private MainAdapter adapter;//用于填充listview数据
	private Util util;
	private int topDistance = 0;// 列表项据顶部的距离
	private boolean clickFlag = true;// 列表子项是否可以被点击
	private boolean doMoreThread = true;// 加载更多的线程,为空时可以加载
	private View loadingLayout;// 列表底部显示信息
	// 图片硬盘缓存核心类。
	private DiskLruCache mDiskLruCache;
	private int currentPage = 1;// 接口请求的当前页面
	private View childView = null;// 所点击的列表子项
	private long mExitTime = 0;// 返回键响应时间
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent it = super.getIntent();
		attitudeDesignList = (List<AttitudeDesign>) it
				.getSerializableExtra("AttitudeDesign");// 接口数据
		if (attitudeDesignList == null || attitudeDesignList.size() == 0) {
			attitudeDesignList = new ArrayList<AttitudeDesign>();
		}
		conFlag = it.getBooleanExtra("conFlag", false);// 是否有网
		util = new Util(this);
		// 实例化数据库
		this.helper = new MyDatabaseHelper(this);
		MainActivity.this.mtab = new MytabOperate(
				MainActivity.this.helper.getWritableDatabase());
		if (!conFlag) {
			Dialog dialog = new NetDialog(MainActivity.this, R.style.MyDialog);
			dialog.show();
		}
		
		progressDialog = new ProgressDialog(MainActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("加载中......");
		progressDialog.setCancelable(true);
		try {
			// 获取图片缓存路径
			File cacheDir = util.getDiskCacheDir("thumb");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			// 创建DiskLruCache实例，初始化缓存数据
			mDiskLruCache = DiskLruCache.open(cacheDir, util.getAppVersion(),
					1, 30 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		listView = (ListView) findViewById(R.id.list_view);
		adapter = new MainAdapter(this, attitudeDesignList, mDiskLruCache);
		loadingLayout = LayoutInflater.from(this).inflate(
				R.layout.listview_bottom_info, null);
		listView.addFooterView(loadingLayout);
		listView.setAdapter(adapter);

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// if(MainActivity.this.popWin.isShowing()){
			// MainActivity.this.popWin.dismiss(); // 不显示
			// }
			if (mExitTime == 0
					|| (System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次，退出态度", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		} else {// 防止菜单键不能显示
			return false;
		}
	}

}
