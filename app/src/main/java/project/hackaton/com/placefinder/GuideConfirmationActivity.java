package project.hackaton.com.placefinder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by rafaelgontijo on 11/8/15.
 */
public class GuideConfirmationActivity extends Activity {

    private Button doneBt;
    private TextView roteiroTextView;
    private TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        doneBt = (Button) findViewById(R.id.activity_confirmation_done);
        doneBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String roteiro = getIntent().getStringExtra("roteiro");
        roteiroTextView = (TextView) findViewById(R.id.activity_confirmation_roteiro);
        roteiroTextView.setText("Roteiro: " + roteiro);

        String hora = getIntent().getStringExtra("hora");
        String data = getIntent().getStringExtra("data");
        dataTextView = (TextView) findViewById(R.id.activity_confirmation_data);
        dataTextView.setText("Data: " + data + " - " + hora);

    }
}
