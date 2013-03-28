package com.diancan;

import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.ImageDownloader;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.keep.AccessTokenKeeper;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends Activity implements OnClickListener {
	Button backButton;
	ImageView userImageView;
	TextView usernameView;
	Button weiboButton;
	Button qqButton;
	Button tencentButton;
	Button doubanButton;
	Button renrenButton;
	
	LinearLayout userInfoLayout;
	LinearLayout buttonsLayout;
	
	Weibo mWeibo;
	AppDiancan appDiancan;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfoactivity);
		backButton = (Button)findViewById(R.id.bt_back);
		backButton.setOnClickListener(this);
		userImageView = (ImageView)findViewById(R.id.user_image);
		usernameView = (TextView)findViewById(R.id.username);
		weiboButton = (Button)findViewById(R.id.weibologin_btn);
		weiboButton.setOnClickListener(this);
		qqButton = (Button)findViewById(R.id.qqlogin_btn);
		qqButton.setOnClickListener(this);
		tencentButton = (Button)findViewById(R.id.tencentlogin_btn);
		tencentButton.setOnClickListener(this);
		doubanButton = (Button)findViewById(R.id.doubanlogin_btn);
		doubanButton.setOnClickListener(this);
		renrenButton = (Button)findViewById(R.id.renrenlogin_btn);
		renrenButton.setOnClickListener(this);
		
		userInfoLayout = (LinearLayout)findViewById(R.id.infoLayout);
		buttonsLayout = (LinearLayout)findViewById(R.id.buttonsLayout);
		
		appDiancan = (AppDiancan)getApplicationContext();
		initElement();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_back:
			this.finish();
			break;
		case R.id.weibologin_btn:
			weiboLogin();
			break;
		case R.id.qqlogin_btn:
			qqLogin();
			break;
		case R.id.tencentlogin_btn:
			tencentLogin();
			break;
		case R.id.doubanlogin_btn:
			doubanLogin();
			break;
		case R.id.renrenlogin_btn:
			renrenLogin();
			break;
		default:
			break;
		}
	}
	private void initElement(){
		
		if(appDiancan.accessToken.isSessionValid()){
			Drawable[] layers={getResources().getDrawable(R.drawable.noheadimage)};
			ImageDownloader imgDownloader=new ImageDownloader(layers);
			imgDownloader.download(appDiancan.accessToken.getThumbnail(), userImageView);
			usernameView.setText(appDiancan.accessToken.getName());
			
			userInfoLayout.setVisibility(View.VISIBLE);
			buttonsLayout.setVisibility(View.GONE);
		}
		else{
			userImageView.setImageDrawable(null);
			usernameView.setText("");
			userInfoLayout.setVisibility(View.GONE);
			buttonsLayout.setVisibility(View.VISIBLE);
		}
	}
	
	private void weiboLogin(){
		String CONSUMER_KEY = "1399451403";
	    String REDIRECT_URL = "http://taochike.sinaapp.com/rest/1/taochike/thirdlogin/weibo";
	    String resultUrlString = "http://taochike.sinaapp.com/rest/1/taochike/thirdlogin/callback";
	    Weibo.URL_OAUTH2_ACCESS_AUTHORIZE = "https://open.weibo.cn/oauth2/authorize";
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL,resultUrlString);
		mWeibo.authorize(this, new AuthDialogListener());
	}
	
	private void tencentLogin(){
		String CONSUMER_KEY = "801329761";
	    String REDIRECT_URL = "http://taochike.sinaapp.com/rest/1/taochike/thirdlogin/tqq";
	    String resultUrlString = "http://taochike.sinaapp.com/rest/1/taochike/thirdlogin/callback";
	    Weibo.URL_OAUTH2_ACCESS_AUTHORIZE = "https://open.t.qq.com/cgi-bin/oauth2/authorize";
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL,resultUrlString);
		
		mWeibo.authorize(this, new AuthDialogListener());
	}
	
	private void qqLogin(){
		String CONSUMER_KEY = "100397130";
	    String REDIRECT_URL = "http://taochike.sinaapp.com/rest/1/taochike/thirdlogin/qzone";
	    String resultUrlString = "http://taochike.sinaapp.com/rest/1/taochike/thirdlogin/callback";
	    Weibo.URL_OAUTH2_ACCESS_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL,resultUrlString);
		
		mWeibo.authorize(this, new AuthDialogListener());
	}
	
	private void doubanLogin(){
		String CONSUMER_KEY = "0c6305f3fefbe2f7252911dbfcf3a8e5";
	    String REDIRECT_URL = "http://taochike.sinaapp.com/rest/1/taochike/thirdlogin/douban";
	    String resultUrlString = "http://taochike.sinaapp.com/rest/1/taochike/thirdlogin/callback";
	    Weibo.URL_OAUTH2_ACCESS_AUTHORIZE = "https://www.douban.com/service/auth2/auth";
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL,resultUrlString);
		
		mWeibo.authorize(this, new AuthDialogListener());
	}
	
	private void renrenLogin(){
		String CONSUMER_KEY = "fdd705c8e4ab47a2b9e979f0e3dcfe69";
	    String REDIRECT_URL = "http://taochike.sinaapp.com/rest/1/taochike/thirdlogin/renren";
	    String resultUrlString = "http://taochike.sinaapp.com/rest/1/taochike/thirdlogin/callback";
	    Weibo.URL_OAUTH2_ACCESS_AUTHORIZE = "https://graph.renren.com/oauth/authorize";
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL,resultUrlString);
		
		mWeibo.authorize(this, new AuthDialogListener());
	}

	class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            String name = values.getString("name");
            String thumbnail = values.getString("thumbnail");
            String Authorization = values.getString("Authorization");
            String openid = values.getString("openid");
            int snstype= Integer.parseInt(values.getString("snstype"));
            
            appDiancan.accessToken = new Oauth2AccessToken(token, expires_in);
            appDiancan.accessToken.setAuthorization(Authorization);
            appDiancan.accessToken.setName(name);
            appDiancan.accessToken.setThumbnail(thumbnail);
            appDiancan.accessToken.setOpenid(openid);
            appDiancan.accessToken.setSnstype(snstype);
            
            if (appDiancan.accessToken.isSessionValid()) {
            	
                initElement();
                AccessTokenKeeper.keepAccessToken(UserInfoActivity.this,appDiancan.accessToken);
                
            }
        }

        @Override
        public void onError(WeiboDialogError e) {
            Toast.makeText(getApplicationContext(),
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "Auth cancel",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(),
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }
}
