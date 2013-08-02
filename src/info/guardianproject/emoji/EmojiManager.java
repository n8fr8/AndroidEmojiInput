package info.guardianproject.emoji;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.style.ImageSpan;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
public class EmojiManager {
	
	
	private static EmojiManager mInstance = null;

	private Map<Pattern, String> emoticons = new HashMap<Pattern, String>();
	private Map<String, EmojiGroup> categories = new HashMap<String, EmojiGroup>();
	
	private Context mContext;
	
	private EmojiManager (Context context)
	{
		mContext = context;
	}
	
	public void addJsonDefinitions (String assetPathJson, String basePath, String fileExt) throws IOException, JsonSyntaxException
	{
	
		Gson gson = new Gson();
		
		Reader reader = new InputStreamReader(mContext.getAssets().open(assetPathJson));
		
		Type collectionType = new TypeToken<ArrayList<Emoji>>(){}.getType();
		Collection<Emoji> emojis = gson.fromJson(reader, collectionType );
		
		for (Emoji emoji : emojis)
		{
			emoji.assetPath = basePath + '/' + emoji.name + '.' + fileExt;

			try
			{
				mContext.getAssets().open(emoji.assetPath);
				
				addPattern(':' + emoji.name + ':', emoji.assetPath);
				
				if (emoji.moji != null)
					addPattern(emoji.moji, emoji.assetPath);
				
				if (emoji.emoticon != null)
					addPattern(emoji.emoticon, emoji.assetPath);

				
				if (emoji.category != null)
					addEmojiToCategory (emoji.category, emoji);
			}
			catch (FileNotFoundException fe)
			{
				//should not be added as a valid emoji
			}
		}
		
		
	}
	
	public Collection<EmojiGroup> getEmojiGroups ()
	{
		return categories.values();
	}
	
	public String getAssetPath (Emoji emoji)
	{
		return emoji.name;
	}
	
	public synchronized void addEmojiToCategory (String category, Emoji emoji)
	{
		EmojiGroup emojiGroup = categories.get(category);
		
		if (emojiGroup == null)
		{
			emojiGroup = new EmojiGroup();
			emojiGroup.category = category;
			emojiGroup.emojis = new ArrayList<Emoji>();
		}
		
		emojiGroup.emojis.add(emoji);
		
		categories.put(category, emojiGroup);
	}
	
	public static synchronized EmojiManager getInstance (Context context)
	{       
		
		if (mInstance == null)
			mInstance = new EmojiManager(context);
		
		return mInstance;
	}

	
	private void addPattern(String pattern, String resource) {
		  
		emoticons.put(Pattern.compile(pattern,Pattern.LITERAL), resource);
		
	}
	
	private void addPattern(char charPattern, String resource) {
		  
		emoticons.put(Pattern.compile(charPattern+"",Pattern.UNICODE_CASE), resource);
	}
	
	
	public boolean addEmoji(Context context, Spannable spannable) throws IOException {
		boolean hasChanges = false;
		for (Entry<Pattern, String> entry : emoticons.entrySet()) 
		{
			Matcher matcher = entry.getKey().matcher(spannable);
			while (matcher.find()) {
				boolean set = true;
				for (ImageSpan span : spannable.getSpans(matcher.start(),
				        matcher.end(), ImageSpan.class))
					
				    if (spannable.getSpanStart(span) >= matcher.start()
				            && spannable.getSpanEnd(span) <= matcher.end())
				        spannable.removeSpan(span);
				    else {
				        set = false;
				        break;
				    }
				if (set) {
				    hasChanges = true;
				    spannable.setSpan(new ImageSpan(context, BitmapFactory.decodeStream(context.getResources().getAssets().open(entry.getValue()))),
				            matcher.start(), matcher.end(),
				            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return hasChanges;
	}


}
