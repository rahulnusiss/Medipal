package f06.medipal.measurement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Date;

import f06.medipal.R;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Measurement;

public class NewMeasurementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_new);

        //Systolic
        CheckBox checkBoxSystolic = (CheckBox) findViewById(R.id.checkBoxSystolic);
        SeekBar seekBarSystolic = (SeekBar) findViewById(R.id.seekBarSystolic);
        checkBoxSystolic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                seekBarSystolic.setEnabled(isChecked);
            }
        });
        seekBarSystolic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final TextView textViewBarSystolicVal = (TextView) findViewById(R.id.textViewBarSystolicVal);
                textViewBarSystolicVal.setText(String.format("%s(mmHg)", 50 + progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Diastolic
        CheckBox checkBoxDiastolic = (CheckBox) findViewById(R.id.checkBoxDiastolic);
        SeekBar seekBarDiastolic = (SeekBar) findViewById(R.id.seekBarDiastolic);
        checkBoxDiastolic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                seekBarDiastolic.setEnabled(isChecked);
            }
        });
        seekBarDiastolic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final TextView textViewBarDiastolicVal = (TextView) findViewById(R.id.textViewBarDiastolicVal);
                textViewBarDiastolicVal.setText(String.format("%s(mmHg)", 50 + progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Pulse
        CheckBox checkBoxPulse = (CheckBox) findViewById(R.id.checkBoxPulse);
        SeekBar seekBarPulse = (SeekBar) findViewById(R.id.seekBarPulse);
        checkBoxPulse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                seekBarPulse.setEnabled(isChecked);
            }
        });
        seekBarPulse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final TextView textViewBarPulseVal = (TextView) findViewById(R.id.textViewBarPulseVal);
                textViewBarPulseVal.setText(String.format("%s", 50 + progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Temperature
        CheckBox checkBoxTemperature = (CheckBox) findViewById(R.id.checkBoxTemperature);
        SeekBar seekBarTemperature = (SeekBar) findViewById(R.id.seekBarTemperature);
        checkBoxTemperature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                seekBarTemperature.setEnabled(isChecked);
            }
        });
        seekBarTemperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final TextView textViewBarTemperatureVal = (TextView) findViewById(R.id.textViewBarTemperatureVal);
                textViewBarTemperatureVal.setText(String.format("%s(°C)", 35 + progress / 10.0));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Weight
        CheckBox checkBoxWeight = (CheckBox) findViewById(R.id.checkBoxWeight);
        SeekBar seekBarWeight = (SeekBar) findViewById(R.id.seekBarWeight);
        checkBoxWeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                seekBarWeight.setEnabled(isChecked);
            }
        });
        seekBarWeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final TextView textViewBarWeightVal = (TextView) findViewById(R.id.textViewBarWeightVal);
                textViewBarWeightVal.setText(String.format("%s(kg)", 30 + progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void onOK(View v) {
        SeekBar seekBarSystolic = (SeekBar) findViewById(R.id.seekBarSystolic);
        SeekBar seekBarDiastolic = (SeekBar) findViewById(R.id.seekBarDiastolic);
        SeekBar seekBarPulse = (SeekBar) findViewById(R.id.seekBarPulse);
        SeekBar seekBarTemperature = (SeekBar) findViewById(R.id.seekBarTemperature);
        SeekBar seekBarWeight = (SeekBar) findViewById(R.id.seekBarWeight);

        Intent returnIntent = new Intent();
        DBDAO<Measurement> dao = new DBDAO<Measurement>(getBaseContext(), Measurement.class);
        Measurement item = new Measurement(
                50 + seekBarSystolic.getProgress(),
                50 + seekBarDiastolic.getProgress(),
                50 + seekBarPulse.getProgress(),
                35 + seekBarTemperature.getProgress() / 10.0,
                30 + seekBarWeight.getProgress(),
                new Date()
        );
        dao.save(item);

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onCancel(View v) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
