package com.diancan;

import com.diancan.Utils.DisplayUtil;
import com.diancan.http.ImageDownloader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class RecipeImgActivity extends Activity {
	ImageView mImageView;
	ImageDownloader imageDownloader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipe_image);
		mImageView = (ImageView)findViewById(R.id.img_large);
		
		Intent intent = getIntent();
		String urlString = intent.getStringExtra("url");
		int centerX = intent.getIntExtra("centerx", 0);
		int centerY = intent.getIntExtra("centery", 0);
		byte[] by = intent.getByteArrayExtra("byte");
		
		Drawable[] layers = new Drawable[1];
		if(by.length!=0){
			Bitmap bm = BitmapFactory.decodeByteArray(by, 0, by.length);
			BitmapDrawable bDrawable = new BitmapDrawable(bm);
			layers[0] = bDrawable;
		}
		else{
			layers[0] = getResources().getDrawable(R.drawable.imagewaiting);
		}
		imageDownloader = new ImageDownloader(layers);
		imageDownloader.download(urlString, mImageView);
		
		float prox = (centerX*1.0f)/DisplayUtil.PIXWIDTH;
		float proy = (centerY*1.0f)/DisplayUtil.PIXHEIGHT;
		
		Animation animation2 = new ScaleAnimation(0f, 1f, 0f, 1f,
				Animation.RELATIVE_TO_SELF, prox,Animation.RELATIVE_TO_SELF, proy);
		animation2.setDuration(500);
		
		mImageView.setVisibility(View.VISIBLE);
		mImageView.startAnimation(animation2);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

}
