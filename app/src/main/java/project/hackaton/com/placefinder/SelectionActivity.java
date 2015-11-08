package project.hackaton.com.placefinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by rafaelgontijo on 11/8/15.
 */
public class SelectionActivity extends Activity {

    private ImageView contrateBt;
    private ImageView assistenciaBt;
    private ImageView bookingBt;
    private ImageView tipsBt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        contrateBt = (ImageView) findViewById(R.id.activity_selection_contrate);
        contrateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectionActivity.this, ContratoSelectActivity.class);
                startActivity(intent);
            }
        });
    }
}
