package com.suping.i2_watch;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.suping.i2_watch.util.AccessTokenKeeper;
import com.suping.i2_watch.util.FileUtil;
import com.suping.i2_watch.util.L;
import com.suping.i2_watch.util.ScreenShot;

public class SinaActivity extends Activity implements WeiboAuthListener,Response{
	  public static final String APP_KEY      = "1593767485";		   // 应用的APP_KEY
	    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";// 应用的回调页
	    public static final String SCOPE =  "email,direct_messages_read,direct_messages_write,"
	            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
	            + "follow_app_official_microblog," + "invitation_write"
;
		private SsoHandler mSsoHandler;
		private IWeiboShareAPI shareWeiboAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sina);
		
		
//		shouquan();
//		shareWeiboAPI = WeiboShareSDK.createWeiboAPI(this, APP_KEY);
//		shareWeiboAPI.registerApp();//注册到微薄客户端
		
		
		
	}
	
	/**
	 * 创建要分享的内容
	 * @return
	 */
	private TextObject getTextObj(){
		TextObject obj = new TextObject();
		obj.text = "红豆生南国，此物最相思。春来发几枝，愿君多采？。";
		return obj;
	
	}
	
	/**
	 * 分享
	 */
	private void shareText(){
		WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
		weiboMultiMessage.textObject = getTextObj();
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		request.transaction =String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMultiMessage;
		
		shareWeiboAPI.sendRequest(this, request);;
	}
	
	/**
	 * 使用OpenAPI进行分享
	 */
	private void shareByOpenApi(){
	}
	 
	
	
	/**
	 * 授权
	 */
	private void shouquan(){
		// 	创建微博授权类对象，将应用的信息保存：
		AuthInfo mAuthInfo = new AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE);
		//	创建SsoHandler对象
		mSsoHandler = new SsoHandler(this, mAuthInfo);
		//	调用SsoHandler# authorizeWeb方法 
		mSsoHandler.authorize(this);
		//		仅通过Web页面方式授权
		//	一、注册授权页面	
		/**
		<activity 
	     android:name="com.sina.weibo.sdk.component.WeiboSdkBrowser" 
	     android:configChanges="keyboardHidden|orientation"
	     android:windowSoftInputMode="adjustResize"
	     android:exported="false" >
		</activity>
		**/
		//二、all In one方式授权
//		注：此种授权方式会根据手机是否安装微博客户端来决定使用sso授权还是网页授权，如果安装有微博客户端 则调用微博客户端授权，否则调用Web页面方式授权 
		//重写onActivityResult()
		
		//三、通过手机短信授权登录
//		mSsoHandler.registerOrLoginByMobile("", this);
		//重写onActivityResult()
	}
	
	public void share(View v){
//		shareText();
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "test：红豆生南国国,此物最相思。愿君多采撷，春来发几枝。");
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
		
		
//		sendIntent.setType("image/*");
//		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "fen xiang ?");
//				startActivity(Intent.createChooser(sendIntent, getTitle())); 
	}
	
	public void share_text(View v){
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "test：红豆生南国国,此物最相思。愿君多采撷，春来发几枝。");
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, "思杭")); 
	}
	
	
	public void share_img(View v){	
		String filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"xyx.jpg";
		File file = new File(filename);
		  Uri imageUri = Uri.fromFile(file);
	    Intent shareIntent = new Intent();  
	    shareIntent.setAction(Intent.ACTION_SEND);  
	    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);  
	    shareIntent.setType("image/jpeg");  
	    startActivity(Intent.createChooser(shareIntent, "haha"));  
	}
	
	public void screen(View v){
		ImageView img = (ImageView) findViewById(R.id.img_screen);
		Bitmap bitmap = ScreenShot.takeScreenShot(this);
		String filename = FileUtil.getandSaveCurrentImage(this, bitmap);
		if (bitmap!=null) {
			img.setImageBitmap(bitmap);
		}
		
		File file = new File(filename);
		Uri imageUri = Uri.fromFile(file);
	    Intent shareIntent = new Intent();  
	    shareIntent.setAction(Intent.ACTION_SEND);  
	    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);  
	    shareIntent.setType("image/jpeg");  
	    startActivity(Intent.createChooser(shareIntent, "haha")); 
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (mSsoHandler != null) {
		        mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		    }

		
		
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		shareWeiboAPI.handleWeiboResponse(intent, this); //当前应用唤起微博分享后，返回当前应用
	}
	
	/** WeiboAuthListener  接口实现	***/
	@Override
	public void onCancel() {
		
	}

	@Override
	public void onComplete(Bundle b) {
		Oauth2AccessToken  mAccessToken  = Oauth2AccessToken.parseAccessToken(b);
//		    String  phoneNum =  mAccessToken.getPhoneNum();//用户输入的电话号码//短信授权
		if (mAccessToken.isSessionValid()) {
			//授权成功
		    AccessTokenKeeper.writeAccessToken(SinaActivity.this, mAccessToken); 
		}else{
			 String code = b.getString("code", "");
			 L.e("签名不正确 ：" + code);
		}
	}

	@Override
	public void onWeiboException(WeiboException arg0) {
	}
	/** WeiboAuthListener 接口实现	***/

	/**  IWeiboHandler.response  **/
	@Override
	public void onResponse(BaseResponse response) {
		switch (response.errCode) {
		case WBConstants.ErrorCode.ERR_OK://接收微客户端博请求的数据。
			L.i("接收微客户端博请求的数据  成功");
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			L.i("接收微客户端博请求的数据  取消");
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			L.i("接收微客户端博请求的数据  失败");
			break;

		default:
			break;
		}
	}
	/**  IWeiboHandler.response  **/
}
