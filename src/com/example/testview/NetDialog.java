package com.example.testview;

import com.example.testapp.R;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * 网络连接弹出框
 * @author wching
 *
 */
public class NetDialog extends Dialog {

	Context context;
	Button dialogSetBut = null;
	Button dialogCloseBut = null;
	public NetDialog(Context context) {
		super(context);
		this.context = context;
	}
	
	public NetDialog(Context context, int theme){
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_net);
		dialogSetBut = (Button) findViewById(R.id.dialogSetBut);
		dialogCloseBut = (Button) findViewById(R.id.dialogCloseBut);
		this.dialogSetBut.setOnClickListener(clickListener);
		this.dialogCloseBut.setOnClickListener(clickListener);
	}
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dialogSetBut:
				Intent intent = null;
				// 判断手机系统的版本 即API大于10 就是3.0或以上版本
				if (android.os.Build.VERSION.SDK_INT > 10) {
					intent = new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				} else {
					intent = new Intent();
					ComponentName component = new ComponentName(
							"com.android.settings",
							"com.android.settings.WirelessSettings");
					intent.setComponent(component);
					intent.setAction("android.intent.action.VIEW");
				}
				context.startActivity(intent);
				NetDialog.this.dismiss();
				break;
			case R.id.dialogCloseBut:
				NetDialog.this.dismiss();
				break;
			default:
				break;
			}
		}
	};

}
