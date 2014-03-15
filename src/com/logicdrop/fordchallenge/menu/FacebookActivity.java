package com.logicdrop.fordchallenge.menu;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.logicdrop.fordchallenge.R;

public class FacebookActivity extends FragmentActivity {

	private FacebookFragment mainFragment;
	private static String message;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook);
		
		message = (getIntent().getExtras() != null) ? getIntent().getExtras().getString("Message") : "No Message";
		
		if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
	        mainFragment = new FacebookFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(android.R.id.content, mainFragment)
	        .commit();
	    } else {
	        // Or set the fragment from restored state info
	        mainFragment = (FacebookFragment) getSupportFragmentManager()
	        .findFragmentById(android.R.id.content);
	    }
	}
	
	public static String getMessage() {
		return message;
	}
}