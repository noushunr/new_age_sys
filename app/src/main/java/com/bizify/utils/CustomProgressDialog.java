

package com.bizify.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizify.R;


public class CustomProgressDialog extends ProgressDialog {

	private Animation animation;
	ImageView iv_animation;
	TextView tv_text;
	public static ProgressDialog ctor(Context context) {
		CustomProgressDialog dialog = new CustomProgressDialog(context);
		
		/*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
		     // only for gingerbread and newer versions
		}*/
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		return dialog;
	}

	public CustomProgressDialog(Context context) {
		//super(context);
		super(context, R.style.MyProgress);
		setCancelable(false);
	}

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_custom_progress_dialogue);
		iv_animation = (ImageView) findViewById(R.id.iv_animation);
		tv_text= (TextView) findViewById(R.id.tv_text);
		//animate.setBackgroundResource(R.drawable.custom_animation);		
		animation=	 new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setDuration(500);
			}

	public void show(String text) {
		super.show();
		if (!text.isEmpty()) {
			tv_text.setText(text);
		}

		iv_animation.setImageResource(R.drawable.loader);
		iv_animation.startAnimation(animation);
	}

	public void showSuccess(String text){
		animation.cancel();
		dismiss();
		//iv_animation.getAnimation().cancel();
	//	tv_text.setText(text);
		//iv_animation.setImageResource(R.drawable.ic_tick);
		// 3 seconds delay
		/*new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
			dismiss();
			}
		}, 1000);*/
		
	}
	
	@Override
	public void dismiss() {
		try {
			super.dismiss();
		}catch (Exception e){

		}

	}
}
