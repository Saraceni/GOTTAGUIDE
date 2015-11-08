package project.hackaton.com.placefinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by rafaelgontijo on 11/8/15.
 */
public class ContratoSelectActivity extends Activity {

    private Button preferenciasButton;
    private Button predefinidoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrato_select);

        preferenciasButton = (Button) findViewById(R.id.activity_contrato_select_prefs);

        predefinidoButton = (Button)findViewById(R.id.activity_contrato_select_rots);
        predefinidoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContratoSelectActivity.this, CitySelectedActivity.class);
                startActivity(intent);
            }
        });
    }
}
