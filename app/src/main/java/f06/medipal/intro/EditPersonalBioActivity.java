package f06.medipal.intro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import f06.medipal.dao.DBDAO;
import f06.medipal.R;
import f06.medipal.model.PersonalBio;

/**
 * Created by Trung on 19/03/2017.
 */

public class EditPersonalBioActivity extends AppCompatActivity {

    private EditText name;
    private DatePicker dob;
    private EditText idno;
    private EditText address;
    private EditText postal;
    private EditText height;
    private EditText blood;
    private DBDAO<PersonalBio> dao;
    private Button update;
    private Button cancel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalbio_edit);
        displayDefaultValue();
        update = (Button) findViewById(R.id.button_update);
        cancel = (Button) findViewById(R.id.button_cancel);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyTextValidator(name) | emptyTextValidator(address) | emptyTextValidator(height)
                        | emptyTextValidator(idno) | emptyTextValidator(postal)){
                    Toast.makeText(getBaseContext(), "One or more fields need to be completed", Toast.LENGTH_SHORT).show();
                }
                else {
                    int month = dob.getMonth() + 1;
                    String start_day = dob.getDayOfMonth() +"-" + month + "-" + dob.getYear();
                    PersonalBio person = dao.getRecord(1);
                    person.ID = 1;
                    person.Name = name.getText().toString().trim();
                    person.Dob = start_day;
                    person.Idno = idno.getText().toString().trim();
                    person.Address = address.getText().toString().trim();
                    person.Postal = postal.getText().toString().trim();
                    person.Height = Integer.parseInt(height.getText().toString());
                    person.BloodType = blood.getText().toString().trim();
                    dao.update(person);
                    dao.close();
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void displayDefaultValue(){
        name = (EditText) findViewById(R.id.edit_name);
        dob = (DatePicker) findViewById(R.id.edit_dob);
        idno = (EditText) findViewById(R.id.edit_idno);
        address = (EditText) findViewById(R.id.edit_address);
        postal = (EditText) findViewById(R.id.edit_postal);
        height = (EditText) findViewById(R.id.edit_height);
        blood = (EditText) findViewById(R.id.edit_blood);
        dao = new DBDAO<PersonalBio>(getBaseContext(), PersonalBio.class);
        PersonalBio personalBios = dao.getRecords().get(0);

        String time = personalBios.Dob;
        Pattern p = Pattern.compile("(\\w+?)-(\\w+?)-(\\w+)");
        Matcher matcher = p.matcher(time);
        matcher.find();

        int day = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int year = Integer.parseInt(matcher.group(3));

        name.setText(personalBios.Name);
        dob.updateDate(year, month - 1, day);
        idno.setText(personalBios.Idno);
        address.setText(personalBios.Address);
        postal.setText(personalBios.Postal);
        height.setText(personalBios.Height + "");
        blood.setText(personalBios.BloodType);
    }

    private boolean emptyTextValidator(EditText text){
        boolean isEmpty =  TextUtils.isEmpty(text.getText().toString().trim());
        if (isEmpty){
            text.setError("This field is required");
        }
        return isEmpty;
    }


}
