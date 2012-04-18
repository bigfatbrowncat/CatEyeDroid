package ilsoft.raw.viewer;

import android.app.Activity;
import android.os.Bundle;

public class RawViewerActivity extends Activity {
    
	private RawView rawView;
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_viewer_activity);
        rawView = (RawView)findViewById(R.id.surfaceView1);
    }
}