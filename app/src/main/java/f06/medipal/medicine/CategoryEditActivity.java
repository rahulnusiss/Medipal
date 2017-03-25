package f06.medipal.medicine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import f06.medipal.R;
import f06.medipal.constants.Remind;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Category;

public class CategoryEditActivity extends AppCompatActivity {

    private static final String TAG = CategoryEditActivity.class.getSimpleName();

    private int categoryId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        categoryId = getIntent().getIntExtra("ID", 0);
        if (categoryId == 0) {
            Button buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
            buttonUpdate.setText("Create");
        } else {
            DBDAO<Category> dao = new DBDAO<Category>(getBaseContext(), Category.class);
            Category item = dao.getRecord(categoryId);

            EditText editTextCategory = (EditText) findViewById(R.id.editTextCategory);
            EditText editTextCode = (EditText) findViewById(R.id.editTextCode);
            MultiAutoCompleteTextView category_description = (MultiAutoCompleteTextView) findViewById(R.id.category_description);
            switch (item.Remind) {
                case Remind.Yes:
                    RadioButton remind_yes = (RadioButton) findViewById(R.id.remind_yes);
                    remind_yes.setChecked(true);
                    break;
                case Remind.No:
                    RadioButton remind_no = (RadioButton) findViewById(R.id.remind_no);
                    remind_no.setChecked(true);
                    break;

                case Remind.Optional:
                    RadioButton remind_optional = (RadioButton) findViewById(R.id.remind_optional);
                    remind_optional.setChecked(true);
                    break;
            }
            editTextCategory.setText(item.Category);
            editTextCode.setText(item.Code);
            category_description.setText(item.Description);
        }
    }

    public void onUpdate(View v) {
        DBDAO<Category> dao = new DBDAO<Category>(getBaseContext(), Category.class);
        Category item;
        if (categoryId == 0) {
            item = new Category();
        } else {
            item = dao.getRecord(categoryId);
        }
        EditText editTextCategory = (EditText) findViewById(R.id.editTextCategory);
        EditText editTextCode = (EditText) findViewById(R.id.editTextCode);
        MultiAutoCompleteTextView category_description = (MultiAutoCompleteTextView) findViewById(R.id.category_description);
        RadioGroup remind = (RadioGroup) findViewById(R.id.remind);
        switch (remind.getCheckedRadioButtonId()) {
            case R.id.remind_yes:
                item.Remind = Remind.Yes;
                break;
            case R.id.remind_no:
                item.Remind = Remind.No;
                break;
            case R.id.remind_optional:
                item.Remind = Remind.Optional;
                break;
        }
        item.Category = editTextCategory.getText().toString().trim();
        item.Code = editTextCode.getText().toString().trim();
        item.Description = category_description.getText().toString().trim();
        if (categoryId == 0) {
            dao.save(item);
        } else {
            dao.update(item);
        }
        finish();
    }

    public void onDelete(View v) {
        DBDAO<Category> dao = new DBDAO<Category>(getBaseContext(), Category.class);
        dao.delete(categoryId);
        finish();
    }

    public void onCancel(View v) {
        finish();
    }

}
