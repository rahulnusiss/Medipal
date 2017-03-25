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

public class ICEDetailsActivity extends AppCompatActivity {

    private ICE _item;
    private TextView textViewName;
    private EditText editTextName;
    private TextView textViewType;
    private Spinner spinnerType;
    private TextView textViewNumber;
    private EditText editTextNumber;
    private TextView textViewDescription;
    private EditText editTextDescription;
    private Button buttonUpdate;
    private Button buttonDelete;
    private Button buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ice_details);

        textViewName = (TextView)findViewById( R.id.textViewName );
        editTextName = (EditText)findViewById( R.id.editTextName );
        textViewType = (TextView)findViewById( R.id.textViewType );
        spinnerType = (Spinner)findViewById( R.id.spinnerType );
        textViewNumber = (TextView)findViewById( R.id.textViewNumber );
        editTextNumber = (EditText)findViewById( R.id.editTextNumber );
        textViewDescription = (TextView)findViewById( R.id.textViewDescription );
        editTextDescription = (EditText)findViewById( R.id.editTextDescription );

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),
                R.array.contact_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        int ID = getIntent().getIntExtra("ID", 0);
        DBDAO<ICE> dao = new DBDAO<ICE>(getBaseContext(), ICE.class);
        _item = dao.getRecord(ID);
        this.loadData();
    }


    public void loadData()
    {
        editTextName.setText(_item.Name);
        editTextDescription.setText(_item.Description);
        editTextNumber.setText(_item.ContactNo);
        spinnerType.setSelection( _item.ContactType);
    }

    public void onUpdate(View v) {
        DBDAO<ICE> dao = new DBDAO<ICE>(getBaseContext(), ICE.class);
        _item.Name = editTextName.getText().toString();
        _item.ContactNo = editTextNumber.getText().toString();
        _item.ContactType = 0;
        _item.Description = editTextDescription.getText().toString();
        dao.update(_item);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onDelete(View v) {
        DBDAO<ICE> dao = new DBDAO<ICE>(getBaseContext(), ICE.class);
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
}
