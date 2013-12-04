package com.memorybookshelf.memoryplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import com.memorybookshelf.memoryplayer.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MemoryPlayerActivity extends Activity implements
		OnCompletionListener, SeekBar.OnSeekBarChangeListener, OnClickListener,
		TextToSpeech.OnInitListener {

	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private SeekBar memoryProgressBar;
	private TextView memoryTitleLabel;
	private TextView memoryCurrentDurationLabel;
	private TextView memoryTotalDurationLabel;
	// Media Player
	private MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private MemoriesManager memoryManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentMemoryIndex = 0;
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private ArrayList<HashMap<String, String>> memoriesList = new ArrayList<HashMap<String, String>>();

	ImageView mic;
	ImageButton help;

	Button btnClosePopup;

	// tts
	ImageButton buttonSpeak;
	ImageButton buttonSpeak2;
	TextToSpeech tts;

	// For Voice
	protected static final int REQUEST_OK = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		memoryProgressBar = (SeekBar) findViewById(R.id.memoryProgressBar);
		memoryTitleLabel = (TextView) findViewById(R.id.memoryTitle);
		memoryCurrentDurationLabel = (TextView) findViewById(R.id.memoryCurrentDurationLabel);
		memoryTotalDurationLabel = (TextView) findViewById(R.id.memoryTotalDurationLabel);

		// Handles Clicks to common items
		findViewById(R.id.mic).setOnClickListener(this);
		help = (ImageButton) findViewById(R.id.help);
		help.setOnClickListener(onClickListener);

		// tts
		tts = new TextToSpeech(this, this);

		// Mediaplayer
		mp = new MediaPlayer();
		memoryManager = new MemoriesManager();
		utils = new Utilities();

		// Listeners
		memoryProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important

		// Getting all memories list
		memoriesList = memoryManager.getPlayList();

		// By default play first memory
		// playMemory(1); // code to play second memory

		/**
		 * Play button click event plays a memory and changes button to pause
		 * image pauses a memory and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check for already playing
				if (mp.isPlaying()) {
					if (mp != null) {
						mp.pause();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);
					}
				} else {
					// Resume memory
					if (mp != null) {
						mp.start();
						// Changing button image to pause button
						btnPlay.setImageResource(R.drawable.btn_pause);
					}
				}

			}
		});

		/**
		 * Forward button click event Forwards memory specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get current memory position
				int currentPosition = mp.getCurrentPosition();
				// check if seekForward time is lesser than memory duration
				if (currentPosition + seekForwardTime <= mp.getDuration()) {
					// forward memory
					mp.seekTo(currentPosition + seekForwardTime);
				} else {
					// forward to end position
					mp.seekTo(mp.getDuration());
				}
			}
		});

		/**
		 * Backward button click event Backward memory to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get current memory position
				int currentPosition = mp.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if (currentPosition - seekBackwardTime >= 0) {
					// forward memory
					mp.seekTo(currentPosition - seekBackwardTime);
				} else {
					// backward to starting position
					mp.seekTo(0);
				}

			}
		});

		/**
		 * Next button click event Plays next memory by taking
		 * currentMemoryIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check if next memory is there or not
				if (currentMemoryIndex < (memoriesList.size() - 1)) {
					playMemory(currentMemoryIndex + 1);
					currentMemoryIndex = currentMemoryIndex + 1;
				} else {
					// play first memory
					playMemory(0);
					currentMemoryIndex = 0;
				}

			}
		});

		/**
		 * Back button click event Plays previous memory by currentMemoryIndex -
		 * 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (currentMemoryIndex > 0) {
					playMemory(currentMemoryIndex - 1);
					currentMemoryIndex = currentMemoryIndex - 1;
				} else {
					// play last memory
					playMemory(memoriesList.size() - 1);
					currentMemoryIndex = memoriesList.size() - 1;
				}

			}
		});

		/**
		 * Button Click event for Repeat button Enables repeat flag to true
		 * */
		btnRepeat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isRepeat) {
					isRepeat = false;
					Toast.makeText(getApplicationContext(), "Repeat is OFF",
							Toast.LENGTH_SHORT).show();
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				} else {
					// make repeat to true
					isRepeat = true;
					Toast.makeText(getApplicationContext(), "Repeat is ON",
							Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isShuffle = false;
					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}
			}
		});

		/**
		 * Button Click event for Shuffle button Enables shuffle flag to true
		 * */
		btnShuffle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isShuffle) {
					isShuffle = false;
					Toast.makeText(getApplicationContext(), "Shuffle is OFF",
							Toast.LENGTH_SHORT).show();
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				} else {
					// make repeat to true
					isShuffle = true;
					Toast.makeText(getApplicationContext(), "Shuffle is ON",
							Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isRepeat = false;
					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}
			}
		});

		/**
		 * Button Click event for Play list click event Launches list activity
		 * which displays list of memories
		 * */
		btnPlaylist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(),
						PlayListActivity.class);
				startActivityForResult(i, 100);
			}
		});

	}

	public void changeView(int i) {
		final Context context = MemoryPlayerActivity.this;
		Intent intent;// = new Intent(context, Memory.class);
		// Intent intent = new Intent(context, Memory.class); // need
		// to
		// change
		// location
		switch (i) {

		case 1:
			// change below code to Player and correct memory

			/*
			 * final Context context = Memory.this; Intent intent = new
			 * Intent(context, Memory.class); //need to change location
			 * startActivity(intent);
			 */
			intent = new Intent(context, Memory.class);
			startActivity(intent);
			break;
		case 2:
			// change below code to Player and correct memory

			/*
			 * final Context context = Memory.this; Intent intent = new
			 * Intent(context, Memory.class); //need to change location
			 * startActivity(intent);
			 */
			intent = new Intent(context, Relations.class);
			startActivity(intent);
			break;
		}
		// startActivity(intent);
	}

	/**
	 * Function to play a memory
	 * 
	 * @param memoryIndex
	 *            - index of memory
	 * */
	public void playMemory(int memoryIndex) {
		// Play Memory
		try {
			mp.reset();
			mp.setDataSource(memoriesList.get(memoryIndex).get("memoryPath"));
			mp.prepare();
			mp.start();
			// Displaying Memory title
			String memoryTitle = memoriesList.get(memoryIndex).get(
					"memoryTitle");
			memoryTitleLabel.setText(memoryTitle);

			// Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.btn_pause);

			// set Progress bar values
			memoryProgressBar.setProgress(0);
			memoryProgressBar.setMax(100);

			// Updating progress bar
			//updateProgressBar(); 
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = mp.getDuration();
			long currentDuration = mp.getCurrentPosition();

			// Displaying Total Duration time
			memoryTotalDurationLabel.setText(""
					+ utils.milliSecondsToTimer(totalDuration));
			// Displaying time completed playing
			memoryCurrentDurationLabel.setText(""
					+ utils.milliSecondsToTimer(currentDuration));

			// Updating progress bar
			int progress = (int) (utils.getProgressPercentage(currentDuration,
					totalDuration));
			// Log.d("Progress", ""+progress);
			memoryProgressBar.setProgress(progress);

			// Running this thread after 100 milliseconds
			mHandler.postDelayed(this, 100);
		}
	};

	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {

	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(),
				totalDuration);

		// forward or backward to certain seconds
		mp.seekTo(currentPosition);

		// update timer progress again
		updateProgressBar();
	}

	/**
	 * On Memory Playing completed if repeat is ON play same memory again if
	 * shuffle is ON play random memory
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {

		// check for repeat is ON or OFF
		if (isRepeat) {
			// repeat is on play same memory again
			playMemory(currentMemoryIndex);
		} else if (isShuffle) {
			// shuffle is on - play a random memory
			Random rand = new Random();
			currentMemoryIndex = rand
					.nextInt((memoriesList.size() - 1) - 0 + 1) + 0;
			playMemory(currentMemoryIndex);
		}
		/*
		 * code to repeat from the beginning when playing memory is done else {
		 * // no repeat or shuffle ON - play next memory if (currentMemoryIndex
		 * < (memoriesList.size() - 1)) { playMemory(currentMemoryIndex + 1);
		 * currentMemoryIndex = currentMemoryIndex + 1; } else { // play first
		 * memory playMemory(0); currentMemoryIndex = 0; } }
		 */
	}

	@Override
	public void onDestroy() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
		mp.release();
	}

	public void onInit(int status) {

		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Toast.makeText(this, "This Language is not supported",
						Toast.LENGTH_LONG).show();
			} else {
				// Toast.makeText(this, "Ready to Speak",
				// Toast.LENGTH_LONG).show();
				// speakTheText();
			}
		} else {
			Toast.makeText(this, "Can Not Speak", Toast.LENGTH_LONG).show();
		}

	}

	private void speakTheText(int i) {
		String textToSpeak;
		if (i == 1)
			textToSpeak = this.getResources().getString(R.string.helpTxt);
		else
			textToSpeak = this.getResources().getString(R.string.notAvail);

		tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
	}

	// voice integration
	@Override
	public void onClick(View v) {
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
		try {
			startActivityForResult(i, REQUEST_OK);
		} catch (Exception e) {
			Toast.makeText(this, "Error initializing speech to text engine.",
					Toast.LENGTH_LONG).show();

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {
			currentMemoryIndex = data.getExtras().getInt("memoryIndex");
			// play selected memory
			playMemory(currentMemoryIndex);
		} else if (requestCode == REQUEST_OK && resultCode == RESULT_OK) {
			ArrayList<String> thingsYouSaid = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (thingsYouSaid.get(0).contains("grand mother")
					|| thingsYouSaid.get(0).contains("granny")
					|| thingsYouSaid.get(0).contains("memories")
					|| thingsYouSaid.get(0).contains("cauthorn")) {
				final Context context = this;
				Intent intent = new Intent(context, Memory.class);
				startActivity(intent);
			} else if (thingsYouSaid.get(0).contains("help")
					|| thingsYouSaid.get(0).contains("show help")) {
				initiatePopupWindow(R.id.help_pu, R.layout.help, 600, 800);
			} else if (thingsYouSaid.get(0).contains("add")
					|| thingsYouSaid.get(0).contains("new")
					|| thingsYouSaid.get(0).contains("delete")
					|| thingsYouSaid.get(0).contains("remove")
					|| thingsYouSaid.get(0).contains("edit")) {
				initiatePopupWindow(R.id.not_a, R.layout.not_avail, 400, 200);
			} else if (thingsYouSaid.get(0).contains("relation")
					|| thingsYouSaid.get(0).contains("relations")
					|| thingsYouSaid.get(0).contains("home")
					|| thingsYouSaid.get(0).contains("menu")) {
				final Context context = this;
				Intent intent = new Intent(context, Relations.class);
				startActivity(intent);
			}
		}
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add:
				initiatePopupWindow(R.id.not_a, R.layout.not_avail, 400, 200);
				break;
			case R.id.help:
				initiatePopupWindow(R.id.help_pu, R.layout.help, 600, 800);
				break;
			case R.id.edit:
				initiatePopupWindow(R.id.not_a, R.layout.not_avail, 400, 200);
				break;

			}

		}
	};

	// change view
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.relations, menu);
		return true;
	}

	// Pop up attempt 2
	private PopupWindow pwindo;

	private void initiatePopupWindow(int resource, int layOut, int wid, int heig) {
		try {
			// We need to get the instance of the LayoutInflater
			LayoutInflater inflater = (LayoutInflater) MemoryPlayerActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View layout = inflater.inflate(layOut,
					(ViewGroup) findViewById(resource));

			pwindo = new PopupWindow(layout, wid, heig, true);
			pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
			btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
			btnClosePopup.setOnClickListener(cancel_button_click_listener);

			if (resource == R.id.not_a) {
				buttonSpeak = (ImageButton) layout.findViewById(R.id.ttsOnNA);
				buttonSpeak.setOnClickListener(speakHelpNA);
			} else if (resource == R.id.help_pu) {
				buttonSpeak = (ImageButton) layout.findViewById(R.id.ttsOn);
				buttonSpeak.setOnClickListener(speakHelp);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OnClickListener cancel_button_click_listener = new OnClickListener() {
		public void onClick(View v) {
			pwindo.dismiss();
			// need command to interupt as well
			tts.stop();

		}
	};

	private OnClickListener speakHelp = new OnClickListener() {
		public void onClick(View v) {
			speakTheText(1);
		}
	};

	private OnClickListener speakHelpNA = new OnClickListener() {
		public void onClick(View v) {
			speakTheText(2);
		}
	};

}
