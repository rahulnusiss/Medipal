package f06.medipal.contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import f06.medipal.R;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.ICE;

public class ICECreateActivity extends AppCompatActivity {
    private ICE _item;
    private TextView textViewName;
    private EditText editTextName;
    private TextView textViewType;
    private Spinner spinnerType;
    private TextView textViewNumber;
    private EditText editTextNumber;
    private TextView textViewDescription;
    private EditText editTextDescription;
    private Button buttonCreate;
    private Button buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ice_new);

        textViewName = (TextView)findViewById( R.id.textViewName );
        editTextName = (EditText)findViewById( R.id.editTextName );
        textViewType = (TextView)findViewById( R.id.textViewType );
        spinnerType = (Spinner)findViewById( R.id.spinnerType );
        textViewNumber = (TextView)findViewById( R.id.textViewNumber );
        editTextNumber = (EditText)findViewById( R.id.editTextNumber );
        textViewDescription = (TextView)findViewById( R.id.textViewDescription );
        editTextDescription = (EditText)findViewById( R.id.editTextDescription );
        buttonCreate = (Button)findViewById( R.id.buttonCreate );
        buttonCancel = (Button)findViewById( R.id.buttonCancel );

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.contact_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    public void onOK(View v) {
        DBDAO<ICE> dao = new DBDAO<ICE>(getBaseContext(), ICE.class);
        ICE item = new ICE(editTextName.getText().toString(), editTextNumber.getText().toString(), spinnerType.getSelectedItemPosition(), editTextDescription.getText().toString());
        dao.save(item);
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onCancel(View v) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
