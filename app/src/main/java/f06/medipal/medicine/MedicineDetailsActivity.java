package f06.medipal.medicine;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

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

public class MedicineDetailsActivity extends AppCompatActivity implements TimePickerFragment.OnTimePickedListener {

    private static final String TAG = MedicineDetailsActivity.class.getSimpleName();

    private Medicine _item;
    private EditText editTextMedicineName;
    private EditText editTextDescription;
    private RadioGroup radioRemind;
    private RadioButton remindYes;
    private RadioButton remindNo;
    private EditText editTextQuantity;
    private Spinner spinnerDosage;
    private EditText editTextDateIssued;
    private EditText editTextConsumeQuantity;
    private EditText editTextThreshold;
    private EditText editTextExpireFactor;
    private DatePickerDialog datePickerDialog;
    private CheckBox _checkboxRemind;
    private EditText _starttimeText;
    private EditText _frequencyText;
    private EditText _intervalText;

    //spinner
    private Spinner spinnerCategoryType;


    private Button buttonUpdate;
    private Button buttonDelete;
    private Button buttonCancel;
    private int day, month, year;

    @Override
    public void onTimePicked(Date pickedDatetime) {
        // Receive the result and show it in text view.
        _starttimeText.setText(formatter.format(pickedDatetime));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_details);
        editTextMedicineName = (EditText) findViewById(R.id.editTextMedicineName);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
        editTextDateIssued = (EditText) findViewById(R.id.editTextDateIssued);
        editTextConsumeQuantity = (EditText) findViewById(R.id.editTextConsumeQuantity);
        editTextThreshold = (EditText) findViewById(R.id.editTextThreshold);
        editTextExpireFactor = (EditText) findViewById(R.id.editTextExpireFactor);
        spinnerDosage = (Spinner) findViewById(R.id.spinner_dosage);
        spinnerCategoryType = (Spinner) findViewById(R.id.spinnerCategory);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        _starttimeText = (EditText) findViewById(R.id.input_starttime);
        _frequencyText = (EditText) findViewById(R.id.input_frequency);
        _intervalText = (EditText) findViewById(R.id.input_interval);
        _checkboxRemind = (CheckBox) findViewById(R.id.checkBoxRemind);


        int id = getIntent().getIntExtra("ID", 0);
        DBDAO<Medicine> mdao = new DBDAO<Medicine>(getBaseContext(), Medicine.class);
        _item = mdao.getRecord(id);
        this.loadData();

        DBDAO<Category> cdao = new DBDAO<Category>(getBaseContext(), Category.class);
        ArrayList<Category> categories = cdao.getRecords();
        ArrayList<String> data = new ArrayList<String>();
        int i = 0, selection = 0;
        for (Category item : categories) {
            data.add(item.Category);
            if (_item.catID == item.ID) {
                selection = i;
            }
            i++;
        }
        ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data.toArray(new String[data.size()]));
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryType.setAdapter(categoryArrayAdapter);
        spinnerCategoryType.setSelection(selection);

        ArrayAdapter<String> dosageArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Dosage.Dosages);
        dosageArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDosage.setAdapter(dosageArrayAdapter);
        spinnerDosage.setSelection(_item.dosage);

        //Set or Update new Date
        editTextDateIssued.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(MedicineDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, day);
                        Date date = cal.getTime();
                        editTextDateIssued.setText(formatter.format(date));
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                //datePickerDialog.updateDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
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
                        showRemind(isChecked);
                    }
                }
        );
    }

    private void showRemind(boolean isChecked){
        findViewById(R.id.textview_frequency).setVisibility(isChecked ? View.VISIBLE : View.GONE);
        findViewById(R.id.textview_interval).setVisibility(isChecked ? View.VISIBLE : View.GONE);
        findViewById(R.id.textview_starttime).setVisibility(isChecked ? View.VISIBLE : View.GONE);
        findViewById(R.id.input_frequency).setVisibility(isChecked ? View.VISIBLE : View.GONE);
        findViewById(R.id.input_interval).setVisibility(isChecked ? View.VISIBLE : View.GONE);
        findViewById(R.id.input_starttime).setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    public void loadData() {
        editTextMedicineName.setText(_item.medicine);
        editTextDescription.setText(_item.description);
        editTextConsumeQuantity.setText(String.valueOf(_item.consumeQuantity));
        editTextQuantity.setText(String.valueOf(_item.quantity));
        editTextExpireFactor.setText(String.valueOf(_item.expireFactor));
        editTextThreshold.setText(String.valueOf(_item.threshold));
        spinnerDosage.setSelection(_item.dosage);
        editTextDateIssued.setText(formatter.format(_item.dateIssued));
        spinnerCategoryType.setSelection(_item.catID);

        showRemind(_item.remind.equals(Remind.Yes));
        if (_item.remind.equals(Remind.Yes)) {
            _checkboxRemind.setChecked(true);
            DBDAO<Reminder> rdao = new DBDAO<Reminder>(getBaseContext(), Reminder.class);
            Reminder reminder = rdao.getRecord(_item.reminderID);
            _frequencyText.setText(String.valueOf(reminder.Frequency));
            _intervalText.setText(String.valueOf(reminder.Interval));
            _starttimeText.setText(formatter.format(reminder.StartTime));
        }
    }//loadData


    public void onUpdate(View v) {
        DBDAO<Medicine> dao = new DBDAO<Medicine>(getBaseContext(), Medicine.class);
        DBDAO<Reminder> rdao = new DBDAO<Reminder>(getBaseContext(), Reminder.class);

        if (emptyTextValidator(editTextMedicineName) | emptyTextValidator(editTextQuantity) | emptyTextValidator(editTextConsumeQuantity) |
                emptyTextValidator(editTextExpireFactor) | emptyTextValidator(editTextThreshold)) {
            return;
        }

        _item.medicine = editTextMedicineName.getText().toString();
        _item.description = editTextDescription.getText().toString();
        _item.expireFactor = Integer.parseInt(editTextExpireFactor.getText().toString());
        _item.quantity = Integer.parseInt(editTextQuantity.getText().toString());
        _item.consumeQuantity = Integer.parseInt(editTextConsumeQuantity.getText().toString());
        _item.quantity = Integer.parseInt(editTextQuantity.getText().toString());
        _item.threshold = Integer.parseInt(editTextThreshold.getText().toString());
        _item.dosage = spinnerDosage.getSelectedItemPosition();
        _item.catID = spinnerCategoryType.getSelectedItemPosition();

        if (_checkboxRemind.isChecked()) {
            Reminder reminder;
            if (_item.remind.equals(Remind.No)) {
                reminder = new Reminder();
            } else {
                reminder = rdao.getRecord(_item.reminderID);
                if (reminder == null) reminder = new Reminder();
            }

            reminder.Frequency = Integer.parseInt(_frequencyText.getText().toString());
            reminder.Interval = Integer.parseInt(_intervalText.getText().toString());
            try {
                reminder.StartTime = formatter.parse(_starttimeText.getText().toString());
            } catch (ParseException ex) {

            }

            if (_item.remind.equals(Remind.No)) {
                _item.reminderID = rdao.save(reminder);
                _item.remind = Remind.Yes;
            } else {
                rdao.update(reminder);
            }
            MedicineNotificationService.startInitNotificationService(this, _item.ID);
        } else {
            MedicineNotificationService.startCancelNotificationService(this, _item.ID);
            if (_item.remind.equals(Remind.Yes)) {
                rdao.delete(_item.reminderID);
            }
        }

        try {
            _item.dateIssued = formatter.parse(editTextDateIssued.getText().toString());
        } catch (ParseException e) {
            Log.v(TAG, "Exception: " + e.getMessage());
        }

        dao.update(_item);
//        TODO: Test notification
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onDelete(View v) {
        DBDAO<Medicine> dao = new DBDAO<Medicine>(getBaseContext(), Medicine.class);
        dao.delete(_item);
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onCancel(View v) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
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
