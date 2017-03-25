package f06.medipal.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import f06.medipal.R;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.PersonalBio;


/**
 * Created by Trung on 11/03/2017.
 */

public class PersonalBioFragment extends Fragment {

    public PersonalBioFragment(){

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personalbio, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button bt = (Button) getActivity().findViewById(R.id.button_create);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText) getActivity().findViewById(R.id.edit_name);
                DatePicker dob = (DatePicker) getActivity().findViewById(R.id.edit_dob);
                EditText idno = (EditText) getActivity().findViewById(R.id.edit_idno);
                EditText address = (EditText) getActivity().findViewById(R.id.edit_address);
                EditText postal = (EditText) getActivity().findViewById(R.id.edit_postal);
                EditText height = (EditText) getActivity().findViewById(R.id.edit_height);
                EditText blood = (EditText) getActivity().findViewById(R.id.edit_blood);

                int month = dob.getMonth() + 1;
                String dob_var = dob.getDayOfMonth() + "-" + month + "-" + dob.getYear();

                if (emptyTextValidator(name) | emptyTextValidator(address) | emptyTextValidator(height)
                        | emptyTextValidator(idno) | emptyTextValidator(postal)) {
                    Toast.makeText(getContext(), "One or more fields need to be completed", Toast.LENGTH_SHORT).show();
                } else {
                    DBDAO<PersonalBio> dao = new DBDAO<PersonalBio>(getActivity(), PersonalBio.class);

                    int heightCentimeter = 0;
                    if (isParsable(String.valueOf(height.getText()))) {
                        heightCentimeter = Integer.parseInt(String.valueOf(height.getText()));
                    }

                    PersonalBio item = new PersonalBio(
                            name.getText().toString(),
                            dob_var,
                            idno.getText().toString(),
                            address.getText().toString(),
                            postal.getText().toString(),
                            heightCentimeter,
                            blood.getText().toString()
                    );

                    dao.save(item);

                    getActivity().finish();

                }
            }
        });
    }

    private  boolean isParsable(String input){
        boolean parsable = true;
        try{
            Integer.parseInt(input);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }

    private boolean emptyTextValidator(EditText text){
        boolean isEmpty =  TextUtils.isEmpty(text.getText().toString().trim());
        if (isEmpty){
            text.setError("This field is required");
        }
        return isEmpty;
    }
}
