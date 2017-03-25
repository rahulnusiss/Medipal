package f06.medipal.medicine;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import f06.medipal.R;
import f06.medipal.constants.Dosage;
import f06.medipal.constants.Remind;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Category;
import f06.medipal.model.Medicine;

import f06.medipal.reminder.MedicineNotificationService;

import f06.medipal.model.Reminder;
import f06.medipal.utils.DatePickerFragment;
import f06.medipal.utils.TimePickerFragment;

import static f06.medipal.dao.DBDAO.formatter;

public class NewMedicineActivity extends AppCompatActivity implements TimePickerFragment.OnTimePickedListener {

    private static final String TAG = NewMedicineActivity.class.getSimpleName();

    private Medicine _item;
    private EditText _nameText;
    private EditText _descriptionText;
    private EditText _quantityText;
    private EditText _consumequantityText;
    private EditText _dateissuedText;
    private Spinner _dosageSpinner;
    private EditText _expirefactorText;
    private EditText _starttimeText;
    private EditText _thresholdText;
    private EditText _frequencyText;
    private EditText _intervalText;
    private Button _addMedicineButton;
    private Button _cancelButton;
    private CheckBox _checkboxRemind;
    private DatePickerDialog datePickerDialog;
    private String reminderFlag;
    private Spinner spinnerCategoryType;

    @Override
    public void onTimePicked(Date pickedDatetime) {
        // Receive the result and show it in text view.
        _starttimeText.setText(formatter.format(pickedDatetime));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_new);
        final Calendar c = Calendar.getInstance();
        _nameText = (EditText) findViewById(R.id.input_name);
        _descriptionText = (EditText) findViewById(R.id.input_description);
        _quantityText = (EditText) findViewById(R.id.input_quantity);
        _consumequantityText = (EditText) findViewById(R.id.input_consumequantity);
        _dateissuedText = (EditText) findViewById(R.id.input_dateissued);
        _dosageSpinner = (Spinner) findViewById(R.id.spinner_dosage);
        _expirefactorText = (EditText) findViewById(R.id.input_expirefactor);
        _starttimeText = (EditText) findViewById(R.id.input_starttime);
        _thresholdText = (EditText) findViewById(R.id.input_threshold);
        _frequencyText = (EditText) findViewById(R.id.input_frequency);
        _intervalText = (EditText) findViewById(R.id.input_interval);
        _checkboxRemind = (CheckBox) findViewById(R.id.checkBoxRemind);
        _addMedicineButton = (Button) findViewById(R.id.buttonCreate);
        _cancelButton = (Button) findViewById(R.id.buttonCancel);
        spinnerCategoryType = (Spinner) findViewById(R.id.spinnerCategory);

        DBDAO<Category> cdao = new DBDAO<Category>(getBaseContext(), Category.class);
        ArrayList<Category> categories = cdao.getRecords();
        ArrayList<String> data = new ArrayList<String>();
        for (Category item : categories) {
            data.add(item.Category);
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data.toArray(new String[data.size()]));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryType.setAdapter(spinnerArrayAdapter);

        ArrayAdapter<String> dosageArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Dosage.Dosages);
        dosageArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _dosageSpinner.setAdapter(dosageArrayAdapter);

        //Retrieve Date from EditText
        _dateissuedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(NewMedicineActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, day);
                        Date date = cal.getTime();
                        _dateissuedText.setText(formatter.format(date));
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        _starttimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "date_picker");
            }
        });

        _checkboxRemind.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        findViewById(R.id.container_frequency).setVisibility(isChecked ? View.VISIBLE : View.GONE);
                        findViewById(R.id.container_interval).setVisibility(isChecked ? View.VISIBLE : View.GONE);
                        findViewById(R.id.container_starttime).setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    }
                }
        );
    }

    public void onOK(View v) {

        Log.d(TAG, "onOK Method Starts");


        DBDAO<Medicine> dao = new DBDAO<Medicine>(getBaseContext(), Medicine.class);

        if (emptyTextValidator(_nameText) | emptyTextValidator(_quantityText) | emptyTextValidator(_consumequantityText) |
                emptyTextValidator(_expirefactorText) | emptyTextValidator(_thresholdText)) {
            onAddMedicineFailed();
            return;
        }

        _addMedicineButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(NewMedicineActivity.this, R.style.ThemeOverlay_AppCompat_Light);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Adding Medicine details");
        progressDialog.show();

        String medicine = _nameText.getText().toString();
        String description = _descriptionText.getText().toString();

        int quantity = Integer.parseInt(_quantityText.getText().toString());
        int consumeQuantity = Integer.parseInt(_consumequantityText.getText().toString());
        int dosage = _dosageSpinner.getSelectedItemPosition();
        int expireFactor = Integer.parseInt(_expirefactorText.getText().toString());
        int threshold = Integer.parseInt(_thresholdText.getText().toString());
        Date dateIssued = null;
        try {
            dateIssued = formatter.parse(_dateissuedText.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        reminderFlag = _checkboxRemind.isChecked() ? Remind.Yes : Remind.No;

        int reminderID = 0;
        if (_checkboxRemind.isChecked()) {
            int frequency = Integer.parseInt(_frequencyText.getText().toString());
            int interval = Integer.parseInt(_intervalText.getText().toString());
            Date starttime = null;
            try {
                starttime = formatter.parse(_starttimeText.getText().toString());
            } catch (ParseException ex) {

            }

            DBDAO<Reminder> rdao = new DBDAO<Reminder>(getBaseContext(), Reminder.class);
            Reminder reminder = new Reminder(frequency, starttime, interval);
            reminderID = rdao.save(reminder);
        }

        int catID;
        int selection = spinnerCategoryType.getSelectedItemPosition();
        DBDAO<Category> cdao = new DBDAO<Category>(getBaseContext(), Category.class);
        ArrayList<Category> categories = cdao.getRecords();

        Medicine item = new Medicine(medicine, description, reminderFlag, categories.get(selection).ID, reminderID, quantity, dosage, dateIssued, consumeQuantity, threshold, expireFactor);
//        TODO: Test
        int medicineId = dao.save(item);
        if (reminderFlag == "Y") MedicineNotificationService.startInitNotificationService(this, medicineId);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        onaddMedicineSuccess();
                    }
                }, 3000
        );
        Log.d(TAG, "onOK Method Ends");

    }//addMedicine


    public void onaddMedicineSuccess() {
        Log.d(TAG, "onaddMedicineSuccess() Method Starts");
        _addMedicineButton.setEnabled(false);
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        Log.d(TAG, "onaddMedicineSuccess() Method Ends");
        finish();
    }


    public void onAddMedicineFailed() {

        Log.d(TAG, "onAddMedicineFailed() Method Starts");

        Toast.makeText(getBaseContext(), "Add Medicine Failed", Toast.LENGTH_LONG).show();
        _addMedicineButton.setEnabled(true);
        Log.d(TAG, "onAddMedicineFailed() Method Ends");
    }

    public void onCancel(View v) {
        Log.d(TAG, "onCancel() Method Starts");
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        Log.d(TAG, "onCancel() Method Ends");
        finish();
    }

    private boolean emptyTextValidator(EditText text) {
        boolean isEmpty = TextUtils.isEmpty(text.getText().toString().trim());
        if (isEmpty) {
            text.setError("This field is required");
        }
        return isEmpty;
    }

}
