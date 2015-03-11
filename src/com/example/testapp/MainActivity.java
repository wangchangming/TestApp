package com.example.testapp;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
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

	private List<AttitudeDesign> attitudeDesignList = null;// 用于存储个人信息
	private boolean conFlag = false;// 是否可以连接网络
	private SQLiteOpenHelper helper;// 数据库类
	private MytabOperate mytab = null;// 数据库操作类
	private Handler myHandler = null;// 消息处理
	private ProgressDialog progressDialog = null;// 进度对话框
	private Button mainHomeBtn;// 点击主页按钮
	private RelativeLayout mainTopRl = null;
	private RefreshTestView refreshableView;// 自定义布局
	private ListView listView;
	private MainAdapter adapter;// 用于填充listview数据
	private Util util;
	private int topDistance = 0;// 列表项据顶部的距离

	/**
	 * 列表子项是否可以被点击
	 */
	private boolean clickFlag = true;//

	/**
	 * 加载更多的线程,为空时可以加载
	 */
	private boolean doMoreThread = true;//

	/**
	 * 列表底部显示信息
	 */
	private View loadingLayout;//
	// 图片硬盘缓存核心类。
	private DiskLruCache mDiskLruCache;

	/**
	 * 接口请求的当前页面
	 */
	private int currentPage = 1;//
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
		MainActivity.this.mytab = new MytabOperate(
				MainActivity.this.helper.getWritableDatabase());
		// 没有网的情况下弹出对话框
		if (!conFlag) {
			Dialog dialog = new NetDialog(MainActivity.this, R.style.MyDialog);
			dialog.show();
		}

		mainHomeBtn = (Button) super.findViewById(R.id.mainHomeBtn);
		mainHomeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						TrailerActivity.class);
				startActivity(intent);
			}
		});
		// 创建handler,用于处理接收消息
		myHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					attitudeDesignList.removeAll(attitudeDesignList);
					List<AttitudeDesign> attitudeDesignList2 = (List<AttitudeDesign>) msg.obj;
					attitudeDesignList.addAll(attitudeDesignList2);
					MainActivity.this.adapter.notifyDataSetChanged();
					refreshableView.finishRefreshing();
					break;
				case 2:
					Toast.makeText(MainActivity.this, "网络异常，请稍后再试！",
							Toast.LENGTH_SHORT).show();
					refreshableView.finishRefreshing();
					break;
				case 3:// 有网络连接时处理的操作
					Map<String, Object> map = (Map<String, Object>) msg.obj;// 接收消息内容
					// 这是从服务器上下载下来的
					final List<DesignDetail> moduleList = (List<DesignDetail>) map
							.get("detailList");
					// 这是从listview点击过来的
					final AttitudeDesign attitudeDesign = (AttitudeDesign) map
							.get("attitudeDesign");
					View view = (View) map.get("view");
					// 获取被点击的位置
					final int position = (Integer) map.get("position");
					TranslateAnimation tran = doStartAnimation(view);
					// 动画完成之后进行跳转
					tran.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							MainActivity.this.progressDialog.dismiss();
							for (int i = 0; i < attitudeDesignList.size(); i++) {
								if (i != position) {
									attitudeDesignList.get(i)
											.setShowFlag(false);
								} else {
									attitudeDesignList.get(i).setShowFlag(true);
								}
							}
							adapter.notifyDataSetChanged();
						}

						@Override
						public void onAnimationRepeat(Animation animation) {

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							clickFlag = true;
							Intent intent = new Intent();
							intent.setClass(MainActivity.this,
									DetailActivity.class);
							intent.putExtra("DesignDetail",
									(Serializable) moduleList);// 服务器下载的
							intent.putExtra("AttitudeDesign",
									(Serializable) attitudeDesign);// listview点击过来的
							intent.putExtra("topDistance", topDistance);
							startActivityForResult(intent, 1);
							overridePendingTransition(R.anim.main_in,
									R.anim.main_out);
						}
					});
					break;
				case 4:// 无网络连接时处理的操作
					Map<String, Object> map4 = (Map<String, Object>) msg.obj;
					final List<DesignDetail> moduleList4 = (List<DesignDetail>) map4
							.get("detailList");
					final AttitudeDesign attitudeDesign4 = (AttitudeDesign) map4
							.get("attitudeDesign");
					View view4 = (View) map4.get("view");
					final int position4 = (Integer) map4.get("position");
					TranslateAnimation tran4 = doStartAnimation(view4);
					// 动画完成之后进行跳转
					tran4.setAnimationListener(new AnimationListener() {
						public void onAnimationEnd(Animation animation) {
							clickFlag = true;
							Intent intent = new Intent();
							intent.setClass(MainActivity.this,
									DetailActivity.class);
							intent.putExtra("DesignDetail",
									(Serializable) moduleList4);
							intent.putExtra("AttitudeDesign",
									(Serializable) attitudeDesign4);
							intent.putExtra("topDistance", topDistance);
							startActivityForResult(intent, 1);
							overridePendingTransition(R.anim.main_in,
									R.anim.main_out);
						}

						public void onAnimationRepeat(Animation animation) {

						}

						public void onAnimationStart(Animation animation) {
							MainActivity.this.progressDialog.dismiss();
							for (int i = 0; i < attitudeDesignList.size(); i++) {
								if (i != position4) {
									attitudeDesignList.get(i)
											.setShowFlag(false);
								} else {
									attitudeDesignList.get(i).setShowFlag(true);
								}
							}
							adapter.notifyDataSetChanged();
						}
					});
					break;
				case 5:
					List<AttitudeDesign> attitudeDesignList5 = (List<AttitudeDesign>) msg.obj;
					if(attitudeDesignList5.size()!=0){
						attitudeDesignList.addAll(attitudeDesignList5);
						MainActivity.this.adapter.notifyDataSetChanged();
						doMoreThread =true;
					}else{
						//移除页脚视图
						listView.removeFooterView(loadingLayout);
					}
					refreshableView.finishRefreshing();
					break;
				case 6:
					new NetDialog(MainActivity.this, R.style.MyDialog).show();
					refreshableView.finishRefreshing();
					break;
				default:
					break;
				}
			}

		};

		// 取得下拉下载的头部
		mainTopRl = (RelativeLayout) super.findViewById(R.id.mainTopRl);

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

		refreshableView = (RefreshTestView) findViewById(R.id.refreshable_view);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				if (util.isConn()) {// 当网络连接时，启动线程，下载数据，更新listview
					currentPage = 1;
					doMoreThread = true;
					MoreThread refreshThread = new MoreThread(1);
					refreshThread.start();
				} else {// 当网络断开时，发送一个空消息
					MainActivity.this.myHandler.sendEmptyMessage(6);
				}
			}
		}, 0);

		// 获取listview
		listView = (ListView) findViewById(R.id.list_view);
		// 创建listview的适配器
		adapter = new MainAdapter(this, attitudeDesignList, mDiskLruCache);
		// 在listview的底部加载一个布局文件，用于上拉到底部实现加载效果
		loadingLayout = LayoutInflater.from(this).inflate(
				R.layout.listview_bottom_info, null);
		listView.addFooterView(loadingLayout);
		// 向listview中添加适配器
		listView.setAdapter(adapter);
		
		/**
		 * listview 的滑动事件
		 */
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem + visibleItemCount == totalItemCount){
					if(doMoreThread){
						doMoreThread = false;
						currentPage++;
						MoreThread moreThread = new MoreThread(5);//加载更多线程
						moreThread.start();
					}
				}
			}
		});

		/**
		 * listview的监听事件
		 */
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 判断点击的是哪一个item
				childView = view;
				if (clickFlag) {
					clickFlag = false;
					if (position < attitudeDesignList.size()) {
						// 获取被点击的item
						final AttitudeDesign attitudeDesign = attitudeDesignList
								.get(position);
						final View viewTest = view;
						final int positionTest = position;
						final int groupId = attitudeDesign.getGroup_id();
						Thread thread = new Thread() {// 创建一个线程
							@Override
							public void run() {
								// 服务器网址
								String url = "http://223.4.147.79:8080/AttitudeDesign/photo";
								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("gid", String
										.valueOf(groupId)));
								List<DesignDetail> detailList = null;
								String result = null;
								try {
									result = MainActivity.this.util.getResult(
											url, params);
									detailList = MainActivity.this.util
											.getDesignDetail(result);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// 判断获取的数据是否为空
								if (detailList != null && detailList.size() > 0) {
									for (DesignDetail designDetail : detailList) {
										if (MainActivity.this.mytab
												.findDesignDetailByUrl(designDetail
														.getPhoto_url())) {
											MainActivity.this.mytab
													.insertDesignDetail(
															designDetail,
															groupId);

										}
									}
								}

								Message locationMsg = MainActivity.this.myHandler
										.obtainMessage();
								locationMsg.what = 3;
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("detailList", detailList);// 这是从服务器上下载的
								map.put("attitudeDesign", attitudeDesign);// 这是listview点击item过来的
								map.put("view", viewTest);// 点击监听器的view
								map.put("position", positionTest);// 点击的位置
								locationMsg.obj = map;
								MainActivity.this.myHandler
										.sendMessage(locationMsg);
							}

						};// 线程结束
							// 检测网络连接
						conFlag = util.isConn();
						if (conFlag) {// 如果网络已连接
							progressDialog.show();// 显示加载进度框
							thread.start();
						} else {// 如果网络断开
								// 通过ID去数据库里面查询
							List<DesignDetail> detailList = MainActivity.this.mytab
									.findDesignDetailById(String
											.valueOf(groupId));
							// 创建消息
							Message locationMsg = MainActivity.this.myHandler
									.obtainMessage();
							// 创建一个hashmap
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("detailList", detailList);// 这是从服务器上下载的
							map.put("attitudeDesign", attitudeDesign);// 这是listview点击item过来的
							map.put("view", viewTest);// 点击监听器的view
							map.put("position", positionTest);// 点击的位置
							locationMsg.what = 4;
							locationMsg.obj = map;
							MainActivity.this.myHandler
									.sendMessage(locationMsg);
						}
					}
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		for (int i = 0; i < attitudeDesignList.size(); i++) {
			attitudeDesignList.get(i).setShowFlag(true);
		}
		adapter.notifyDataSetChanged();

		AnimationSet set = new AnimationSet(true);
		TranslateAnimation tran = new TranslateAnimation(0, 0, -topDistance
				- mainTopRl.getHeight(), 0);
		tran.setDuration(500);// 设置动画持续时间
		tran.setFillAfter(true);
		set.addAnimation(tran); // 增加动画
		set.setFillAfter(true);
		childView.startAnimation(set); // 启动动画
		childView.findViewById(R.id.lmTypeText).setVisibility(View.VISIBLE);
		childView.findViewById(R.id.lmImgImg).setVisibility(View.VISIBLE);
		childView.findViewById(R.id.lmNumberText).setVisibility(View.VISIBLE);

		// 头像渐变动画
		AnimationSet set2 = new AnimationSet(true);
		TranslateAnimation tran2 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.5f, // X轴开始位置
				Animation.RELATIVE_TO_SELF, 0.0f, // X轴移动的结束位置
				Animation.RELATIVE_TO_SELF, 5.5f, // Y轴开始位置
				Animation.RELATIVE_TO_SELF, 0.0f); // Y轴移动位置
		tran2.setDuration(600);// 设置动画持续时间
		set2.addAnimation(tran2); // 增加动画
		AlphaAnimation alpha2 = new AlphaAnimation(0, 1); // 由完全显示 -->
															// 完全透明
		alpha2.setDuration(500);// 设置动画持续时间
		set2.addAnimation(alpha2); // 增加动画
		set2.setFillAfter(true);
		childView.findViewById(R.id.lmHeadImg).startAnimation(set2); // 启动动画
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

	public TranslateAnimation doStartAnimation(View view) {
		// 隐藏listview中item的状态栏
		view.findViewById(R.id.lmTypeText).setVisibility(View.GONE);
		view.findViewById(R.id.lmImgImg).setVisibility(View.GONE);
		view.findViewById(R.id.lmNumberText).setVisibility(View.GONE);
		topDistance = view.getTop();// 记录移动前item与底部的距离
		// 头像渐变动画（两种动画效果的叠加）
		AnimationSet set = new AnimationSet(true);
		// 这里改变的是位置（位移）
		TranslateAnimation tran = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, // X轴开始位置
				Animation.RELATIVE_TO_SELF, 0.5f, // X轴结束位置
				Animation.RELATIVE_TO_SELF, 0.0f,// Y轴开始位置
				Animation.RELATIVE_TO_SELF, 5.5f);// Y轴结束位置
		// 这是动画持续时间
		tran.setDuration(500);
		set.addAnimation(tran);// 加入动画
		// 这里是改变动画的透明度
		AlphaAnimation alpha = new AlphaAnimation(1, 0);// 由完全显示到完全透明
		alpha.setDuration(500);// 设置动画持续时间
		set.addAnimation(alpha);// 增加动画
		set.setFillAfter(true);
		view.findViewById(R.id.lmHeadImg).startAnimation(set); // 启动动画

		// 接下来是背景位移动画
		AnimationSet set2 = new AnimationSet(true);
		// 位移动画
		TranslateAnimation trans2 = new TranslateAnimation(0, 0, 0,
				-topDistance - mainTopRl.getHeight());
		trans2.setDuration(600);// 设置动画持续时间
		trans2.setFillAfter(true);
		set2.addAnimation(trans2);
		set2.setFillAfter(true);
		view.startAnimation(set2);
		return trans2;
	}

	class MoreThread extends Thread {
		int number = 0;

		public MoreThread(int number) {
			this.number = number;
		}

		@Override
		public void run() {
			String url = "http://223.4.147.79:8080/AttitudeDesign/list";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("page", String
					.valueOf(currentPage)));
			params.add(new BasicNameValuePair("num", "20"));
			String result = null;
			List<AttitudeDesign> attitudeDesignList2 = null;
			try {
				result = MainActivity.this.util.getResult(url, params);
				attitudeDesignList2 = MainActivity.this.util
						.getAttitudeDesign(result);
			} catch (Exception e) {
				e.printStackTrace();
				number = 2;
			}

			Message LocationMsg = MainActivity.this.myHandler.obtainMessage(
					number, attitudeDesignList2);
			MainActivity.this.myHandler.sendMessage(LocationMsg);
		}

	}

}
