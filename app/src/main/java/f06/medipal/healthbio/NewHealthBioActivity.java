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

import f06.medipal.dao.DBDAO;
import f06.medipal.R;
import f06.medipal.model.HealthBio;

/**
 * Created by Trung on 12/03/2017.
 */

public class NewHealthBioActivity extends AppCompatActivity {

    private EditText condition;
    private DatePicker start;
    private Spinner conditionType;
    private String[] conditionlist = {"Allergy", "Condition"};
    private String[] clist = {"A", "C"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthbio_new);

        conditionType = (Spinner) findViewById(R.id.spinner_condition);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_spinner_item, conditionlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionType.setAdapter(dataAdapter);
        if (conditionType.getTag() == null){
            conditionType.setTag(0);
        }
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
        Button bt = (Button) findViewById(R.id.button_create);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                condition = (EditText) findViewById(R.id.edit_condition);
                start = (DatePicker) findViewById(R.id.edit_date);

                int month = start.getMonth() + 1;
                String start_var = start.getDayOfMonth() +"-" + month + "-" + start.getYear();
                if (emptyTextValidator(condition)){
                    Toast.makeText(getBaseContext(), "Field indicated need to be completed", Toast.LENGTH_SHORT).show();
                }
                else {
                    DBDAO<HealthBio> dao = new DBDAO<HealthBio>(getBaseContext(), HealthBio.class);
                    HealthBio item = new HealthBio(
                            condition.getText().toString().trim(),
                            start_var,
                            clist[conditionType.getSelectedItemPosition()]
                    );
                    dao.save(item);
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
