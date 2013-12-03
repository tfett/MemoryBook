package com.memorybookshelf.memoryplayer;

import java.util.ArrayList;
import java.util.HashMap;

import com.memorybookshelf.memoryplayer.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PlayListActivity extends ListActivity {
	// Memories list
	public ArrayList<HashMap<String, String>> memoriesList = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);

		ArrayList<HashMap<String, String>> memoriesListData = new ArrayList<HashMap<String, String>>();

		MemoriesManager plm = new MemoriesManager();
		// get all memories from sdcard
		this.memoriesList = plm.getPlayList();

		// looping through playlist
		for (int i = 0; i < memoriesList.size(); i++) {
			// creating new HashMap
			HashMap<String, String> memory = memoriesList.get(i);

			// adding HashList to ArrayList
			memoriesListData.add(memory);
		}

		// Adding menuItems to ListView
		ListAdapter adapter = new SimpleAdapter(this, memoriesListData,
				R.layout.playlist_item, new String[] { "memoryTitle" },
				new int[] { R.id.memoryTitle });

		setListAdapter(adapter);

		// selecting single ListView item
		ListView lv = getListView();
		// listening to single listitem click
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting listitem index
				int memoryIndex = position;

				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						MemoryPlayerActivity.class);
				// Sending memoryIndex to PlayerActivity
				in.putExtra("memoryIndex", memoryIndex);
				setResult(100, in);
				// Closing PlayListView
				finish();
			}
		});

	}
}
