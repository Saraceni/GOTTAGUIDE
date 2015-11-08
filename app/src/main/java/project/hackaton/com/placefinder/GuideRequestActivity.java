package project.hackaton.com.placefinder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by rafaelgontijo on 11/8/15.
 */
public class GuideRequestActivity extends Activity {

    private static final String TIME_PATTERN = "HH:mm";

    private TextView timeLabel;
    private TextView dateLabel;
    private TextView destinationLabel;
    private Button buttonLabel;
    private Button confirmButton;
    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_guide);

        dateLabel = (TextView) findViewById(R.id.activity_request_guide_date);
        timeLabel = (TextView) findViewById(R.id.activity_request_guide_time);
        destinationLabel = (TextView) findViewById(R.id.activity_request_guide_destination);
        buttonLabel = (Button) findViewById(R.id.activity_request_guide_btn);
        buttonLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        confirmButton = (Button) findViewById(R.id.activity_request_guide_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                dialog = ProgressDialog.show(GuideRequestActivity.this, "Confirmando Reserva",
                        "Aguarde um momento por favor...", true);
                new ConfirmBookingInBackground().execute();

            }
        });

        String destination = getIntent().getStringExtra("destination");
        destinationLabel.setText(destination);

        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        update();
    }

    private void update() {
        dateLabel.setText(dateFormat.format(calendar.getTime()));
        timeLabel.setText(timeFormat.format(calendar.getTime()));
    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            calendar.set(year, month, day);
            update();
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(), "timePicker");
        }
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            update();

        }
    }

    private class ConfirmBookingInBackground extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {

            try
            {
                Thread.sleep(3500);
            }
            catch(Exception exc)
            {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            //Toast.makeText(GuideRequestActivity.this, "Reserva efetuada com sucesso!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(GuideRequestActivity.this, GuideConfirmationActivity.class);
            intent.putExtra("roteiro", destinationLabel.getText().toString());
            intent.putExtra("data", dateLabel.getText().toString());
            intent.putExtra("hora", timeLabel.getText().toString());
            dialog.dismiss();
            startActivity(intent);
            finish();
        }
    }


}
