package com.example.androidemoji;

import info.guardianproject.emoji.EmojiGroup;
import info.guardianproject.emoji.EmojiManager;
import info.guardianproject.emoji.EmojiPagerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;

public class EmojiSampleActivity extends Activity {

	private final static String TAG = "EmojiSample";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emoji_sample);
		
		EditText tv = (EditText)findViewById(R.id.textview_emoji_display);
		
		EmojiManager eManager = EmojiManager.getInstance(this);
		
		try
		{
			eManager.addJsonDefinitions("phantomsmiles.json", "phantom", "png");
			
			Collection<EmojiGroup> emojiGroups = eManager.getEmojiGroups();
			
			EmojiPagerAdapter emojiPagerAdapter = new EmojiPagerAdapter(this, tv, new ArrayList<EmojiGroup>(emojiGroups));
			ViewPager vPager = (ViewPager)this.findViewById(R.id.emojiPager);
			vPager.setAdapter(emojiPagerAdapter);
			
		
		}
		catch (JsonSyntaxException jse)
		{
			Log.e(TAG,"could not parse json", jse);
		}
		catch (IOException fe)
		{
			Log.e(TAG,"could not load emoji definition",fe);
		}	
		catch (Exception fe)
		{
			Log.e(TAG,"could not load emoji definition",fe);
		}	
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.emoji_sample, menu);
		return true;
	}

}
