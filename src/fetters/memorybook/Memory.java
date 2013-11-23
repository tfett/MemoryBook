package fetters.memorybook;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.view.*;
import android.content.*;
import android.view.View.*;
import android.speech.RecognizerIntent;
import android.widget.*;

public class Memory extends Activity implements OnClickListener {
	//pop ups 
	Button btnClosePopup; 
	ImageButton btnCreatePopup, add, help, edit;
	ImageView mic;	
	
	//For Voice
    protected static final int REQUEST_OK = 1;

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
        
        final FrameLayout frame01 = (FrameLayout) findViewById(R.id.frameLayout1);
        frame01.setOnClickListener(new View.OnClickListener() {         
            @Override
            public void onClick(View v) {
                final Context context = Memory.this;
    			Intent intent = new Intent(context, Memory.class); //need to change location
                startActivity(intent); 
            }
        });
        
        final FrameLayout frame02 = (FrameLayout) findViewById(R.id.frameLayout2);
        frame02.setOnClickListener(new View.OnClickListener() {         
            @Override
            public void onClick(View v) {
            	initiatePopupWindow(R.id.not_a, R.layout.not_avail, 375, 200); //need to change location and from popup
            }
        });
        
        final FrameLayout frame03 = (FrameLayout) findViewById(R.id.frameLayout3);
        frame03.setOnClickListener(new View.OnClickListener() {         
            @Override
            public void onClick(View v) {
            	initiatePopupWindow(R.id.not_a, R.layout.not_avail, 375, 200); //need to change location and from popup
            }
        });
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

    	} catch (Exception e) { 
    	e.printStackTrace(); 
    	} 
    }
    private OnClickListener cancel_button_click_listener = new OnClickListener() { 
    	public void onClick(View v) { 
    	pwindo.dismiss();

    	} 
    };
    
}