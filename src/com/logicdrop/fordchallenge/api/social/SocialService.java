package com.logicdrop.fordchallenge.api.social;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class SocialService implements SocialInterface
{
	public SocialService(){}

	@Override
	public Intent postToFacebook(Context context, String report)
	{
		return socialIntent(context, report, "facebook");
	}

	@Override
	public Intent postToTwitter(Context context, String report)
	{
		return socialIntent(context, report, "twitter");
	}

	@Override
	public Intent postToEmail(String report)
	{
		Intent email = new Intent(Intent.ACTION_SEND);
		email.setType("text/plain");
		email.putExtra(Intent.EXTRA_SUBJECT, "Ford Traffic Report");
		email.putExtra(Intent.EXTRA_TEXT, report);
		//return createChooser due to multiple possible email clients
		return Intent.createChooser(email, "Post to email");
	}
	
	private Intent socialIntent(Context context, String report, String serviceType)
	{
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		PackageManager pm = context.getPackageManager();
		
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Ford Traffic Report");
		shareIntent.putExtra(Intent.EXTRA_TEXT, report);
		
		//Get possible handlers of the ACTION_SEND Intent
		List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
		
		for (final ResolveInfo app : activityList)
		{
			if (app.activityInfo.name.contains(serviceType)) //Find specified social service
			{
				final ActivityInfo activity = app.activityInfo;
				final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
				shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				shareIntent.setComponent(name);
				break;
			}
		}
		return shareIntent;
	}
}
