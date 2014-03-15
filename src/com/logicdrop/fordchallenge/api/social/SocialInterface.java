package com.logicdrop.fordchallenge.api.social;

import android.content.Context;
import android.content.Intent;

public interface SocialInterface {

	Intent postToFacebook(Context context, String report);
	
	Intent postToTwitter(Context context, String report);
	
	Intent postToEmail(String report);
}
