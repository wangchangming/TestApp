package com.example.testapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * 介绍页面
 * @author Savvy
 *
 */
public class TrailerActivity extends Activity implements OnClickListener{

	private RelativeLayout trailerBack;
	private Button trailerPhoneBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trailer);
		
		trailerBack=(RelativeLayout)super.findViewById(R.id.trailerBack);
		trailerBack.setOnClickListener(this);
		trailerPhoneBtn=(Button)super.findViewById(R.id.trailerPhoneBtn);
		trailerPhoneBtn.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
	switch(v.getId()){
	case R.id.trailerBack:
		finish();
	break;
	case R.id.trailerPhoneBtn:
		Uri uri = Uri.parse("tel:10086");
		Intent it = new Intent();
		it.setAction(Intent.ACTION_CALL);
		it.setData(uri);
		TrailerActivity.this.startActivity(it);
	break;
	default:
		break;
	}
		
	}

}
