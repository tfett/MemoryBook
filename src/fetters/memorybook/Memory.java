package fetters.memorybook;

import java.util.ArrayList;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.view.*;
import android.content.*;
import android.view.View.*;
import android.speech.RecognizerIntent;
import android.widget.*;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class Memory extends Activity implements OnClickListener, OnInitListener {
	//pop ups 
	Button btnClosePopup; 
	ImageButton btnCreatePopup, add, help, edit;
	ImageView mic;	
	
	//For Voice
    protected static final int REQUEST_OK = 1;
    
    //tts
    ImageButton buttonSpeak;
    ImageButton buttonSpeak2;
    TextToSpeech tts ;
    
    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memories);

        add = (ImageButton) findViewById(R.id.add);
        add.setOnClickListener(onClickListener);
        edit = (ImageButton) findViewById(R.id.edit);
        edit.setOnClickListener(onClickListener);
        help = (ImageButton) findViewById(R.id.help);
        help.setOnClickListener(onClickListener);
        
        findViewById(R.id.mic).setOnClickListener(this);
        
        final FrameLayout frame01 = (FrameLayout) findViewById(R.id.frameLayout21);
        frame01.setOnClickListener(new View.OnClickListener() {         
            @Override
            public void onClick(View v) {
            	changeView(1);
            }
        });

        
        final FrameLayout frame02 = (FrameLayout) findViewById(R.id.frameLayout22);
        frame02.setOnClickListener(new View.OnClickListener() {         
            @Override
            public void onClick(View v) {
            	changeView(2);
            }
        });

        
        final FrameLayout frame03 = (FrameLayout) findViewById(R.id.frameLayout23);
        frame03.setOnClickListener(new View.OnClickListener() {         
            @Override
            public void onClick(View v) {
            	changeView(3);
            }
        });

        
        final FrameLayout frame04 = (FrameLayout) findViewById(R.id.frameLayout24);
        frame04.setOnClickListener(new View.OnClickListener() {         
            @Override
            public void onClick(View v) {
            	changeView(4);
            }
        });

        
        //tts     
        tts = new TextToSpeech(this, this);

		 
        
    }
	
	public void changeView (int i) {
		switch(i){
        case 1:
        	//change below code to Player and correct song 
        	
        	/*final Context context = Memory.this;
    		Intent intent = new Intent(context, Memory.class); //need to change location
            startActivity(intent); */
            Toast.makeText(this, "Option 1", Toast.LENGTH_LONG).show();
        break;
        case 2:
        	//change below code to Player and correct song 
        	
        	/*final Context context = Memory.this;
    		Intent intent = new Intent(context, Memory.class); //need to change location
            startActivity(intent); */
            Toast.makeText(this, "Option 2", Toast.LENGTH_LONG).show();
        break;
        case 3:
        	//change below code to Player and correct song 
        	
        	/*final Context context = Memory.this;
    		Intent intent = new Intent(context, Memory.class); //need to change location
            startActivity(intent); */
            Toast.makeText(this, "Option 3", Toast.LENGTH_LONG).show();
        break;
        case 4:
        	//change below code to Player and correct song 
        	
        	/*final Context context = Memory.this;
    		Intent intent = new Intent(context, Memory.class); //need to change location
            startActivity(intent); */ 
            Toast.makeText(this, "Option 4", Toast.LENGTH_LONG).show();
        break;
        
    }
		
		
	
	}
	
	//tts destroy
    @Override
    public void onDestroy()
    {
        // Do Not forget to Stop the TTS Engine when you do not require it        
            if (tts != null) 
            {
                tts.stop();
                tts.shutdown();
            }
            super.onDestroy(); 
    }
    
    public void onInit(int status) 
    {
    	
            if (status == TextToSpeech.SUCCESS) 
            {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) 
                    {
                           Toast.makeText(this, "This Language is not supported", Toast.LENGTH_LONG).show();
                    }
                    else 
                    {
                        //Toast.makeText(this, "Ready to Speak", Toast.LENGTH_LONG).show();
                        //speakTheText();
                    }
            } 
            else 
            {
                 Toast.makeText(this, "Can Not Speak", Toast.LENGTH_LONG).show();
            }
            
    }

    private void speakTheText(int i)
    {
    	String textToSpeak;
    	if (i == 1)
    		textToSpeak = this.getResources().getString(R.string.helpTxt);
    	else 
    		textToSpeak = this.getResources().getString(R.string.notAvail);
    	
        tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }
	
	
	//voice integration
    @Override
    public void onClick(View v) {
    Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
             i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
            	 try {
                 startActivityForResult(i, REQUEST_OK);
             } catch (Exception e) {
            	 	Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
            	 	
             }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_OK  && resultCode==RESULT_OK) {
            		ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            		if (thingsYouSaid.get(0).contains("Grandma") )
            		{
            			final Context context = this;
            			Intent intent = new Intent(context, Memory.class);
                        startActivity(intent); 
            		}
            }
        }
    
    
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.add:
                	initiatePopupWindow(R.id.not_a, R.layout.not_avail, 375, 200); 
                break;
                case R.id.help:
                	initiatePopupWindow(R.id.help_pu, R.layout.help, 375, 575); 
                break;
                case R.id.edit:
                	initiatePopupWindow(R.id.not_a, R.layout.not_avail, 375, 200);  
                break;

            }

      }
   };
   
    
   
   //Pop up 
    private PopupWindow pwindo;

    private void initiatePopupWindow(int resource, int layOut, int wid, int heig) { 
	    try { 
		    // We need to get the instance of the LayoutInflater 
		    LayoutInflater inflater = (LayoutInflater) Memory.this 
		    	.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		    
		    View layout = inflater.inflate(layOut,(ViewGroup)
		    	findViewById(resource)); 
		    
		    pwindo = new PopupWindow(layout, wid, heig, true); 
		    pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
		    btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup); 
		    btnClosePopup.setOnClickListener(cancel_button_click_listener);
		    
		    if (resource == R.id.not_a) {
		    	buttonSpeak = (ImageButton) layout.findViewById(R.id.ttsOnNA);
	        	buttonSpeak.setOnClickListener(speakHelpNA);
		    }
		    else if (resource == R.id.help_pu) {
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