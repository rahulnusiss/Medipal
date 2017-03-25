package f06.medipal.healthbio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import f06.medipal.dao.DBDAO;
import f06.medipal.R;
import f06.medipal.model.HealthBio;

/**
 * Created by Trung on 18/03/2017.
 */

public class EditHealthBioActivity extends AppCompatActivity {

    public final static String ID = "f06.medipal.NewHealthBioActivity.ID";
    public final static String CONDITION = "f06.medipal.NewHealthBioActivity.CONDITION";
    public final static String START = "f06.medipal.NewHealthBioActivity.START";
    public final static String TYPE = "f06.medipal.NewHealthBioActivity.TYPE";

    private EditText condition;
    private DatePicker start;
    private Spinner conditionType;
    private int databaseID;
    private String[] conditionlist = {"Allergy", "Condition"};
    private String[] clist = {"A", "C"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthbio_edit);

        databaseID = getIntent().getIntExtra(ID, 0);
        String con = getIntent().getStringExtra(CONDITION);
        String startTime = getIntent().getStringExtra(START);
        String type = getIntent().getStringExtra(TYPE);

        Pattern p = Pattern.compile("(\\w+?)-(\\w+?)-(\\w+)");
        Matcher matcher = p.matcher(startTime);
        matcher.find();

        int day = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int year = Integer.parseInt(matcher.group(3));

        condition = (EditText) findViewById(R.id.edit_condition);
        start = (DatePicker) findViewById(R.id.edit_date);

        condition.setText(con);

        conditionType = (Spinner) findViewById(R.id.spinner_condition);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_spinner_item, conditionlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionType.setAdapter(dataAdapter);

        int tag=0;
        conditionType.setTag(0);
        if (type.equals("C")){
            tag = 1;
            conditionType.setTag(1);
        }
        conditionType.setSelection(tag);
        conditionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((int) conditionType.getTag() != position){
                    conditionType.setTag(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        start.updateDate(year, month - 1, day);


        Button update = (Button) findViewById(R.id.button_update);
        Button cancel = (Button) findViewById(R.id.button_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int month = start.getMonth() + 1;
                String start_var = start.getDayOfMonth() + "-" + month + "-" + start.getYear();
                if (emptyTextValidator(condition)) {
                    Toast.makeText(getBaseContext(), "One or more fields need to be completed", Toast.LENGTH_SHORT).show();
                } else {
                    String start_day = start.getDayOfMonth() +"-" + month + "-" + start.getYear();
                    DBDAO<HealthBio> dao = new DBDAO<HealthBio>(getBaseContext(), HealthBio.class);
                    HealthBio health = dao.getRecord(databaseID);
                    health.Condition = condition.getText().toString().trim();
                    health.ConditionType= clist[conditionType.getSelectedItemPosition()];
                    health.StartDate = start_day;
                    dao.update(health);
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    private boolean emptyTextValidator(EditText text){
        boolean isEmpty =  TextUtils.isEmpty(text.getText().toString().trim());
        if (isEmpty){
            text.setError("This field is required");
        }
        return isEmpty;
    }
}
