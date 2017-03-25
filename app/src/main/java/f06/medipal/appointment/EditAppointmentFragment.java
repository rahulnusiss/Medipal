package f06.medipal.appointment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import f06.medipal.R;
import f06.medipal.utils.DatePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAppointmentCreatedListener} interface
 * to handle interaction events.
 * Use the {@link EditAppointmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditAppointmentFragment extends Fragment implements View.OnClickListener {

    public static final String DESCRIPTION_KEY = "description";
    public static final String LOCATION_KEY = "location";
    public static final String DATETIME_KEY = "datetime";
    public static final String APPOINTMENT_KEY = "appointment";
    public static final String ID_KEY = "id";
    public static final String REMINDER_ID_KEY = "reminderId";

    private EditText mDescriptionEdit;
    private TextView mDatetimeDisplay;
    private EditText mLocationEdit;
    private Date mPickedDatetime = null;
    private Button mCancelBtn;
    private Button mSaveBtn;
    private ImageButton mOpenLocationInMapBtn;

    public static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
    private static final String TAG = NewAppointmentActivity.class.getSimpleName();

    private OnAppointmentCreatedListener mListener;

    public EditAppointmentFragment() {
        // Required empty public constructor
    }


    public static EditAppointmentFragment newInstance() {
        EditAppointmentFragment fragment = new EditAppointmentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment_edit, container, false);

        mDescriptionEdit = (EditText) view.findViewById(R.id.et_appointment_description);
        mLocationEdit = (EditText) view.findViewById(R.id.et_appointment_location);
        mDatetimeDisplay = (TextView) view.findViewById(R.id.tv_pick_appointment_time);
        mCancelBtn = (Button) view.findViewById(R.id.btn_appointment_cancel);
        mSaveBtn = (Button) view.findViewById(R.id.btn_appointment_save);
        mOpenLocationInMapBtn = (ImageButton) view.findViewById((R.id.btn_appointment_open_location));

        mCancelBtn.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
        mOpenLocationInMapBtn.setOnClickListener(this);

        mDatetimeDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "date_picker");
            }
        });
        return view;
    }

    public void onButtonPressed(Bundle bundle) {
        if (mListener != null) {
            mListener.onAppointmentCreated(bundle);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAppointmentCreatedListener) {
            mListener = (OnAppointmentCreatedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAppointmentCreatedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_appointment_save:
                onSave(v);
                break;
            case R.id.btn_appointment_cancel:
                onCancel(v);
                break;
            case R.id.btn_appointment_open_location:
                onOpenLocationInMap(v);
                break;
        }
    }

    public interface OnAppointmentCreatedListener {
        void onAppointmentCreated(Bundle bundle);
    }


    public void onTimePicked(Date mPickedDatetime) {
        this.mPickedDatetime = mPickedDatetime;
        mDatetimeDisplay.setText(formatter.format(this.mPickedDatetime));
    }

    public void onSave(View v) {
        if (TextUtils.isEmpty(mDescriptionEdit.getText())) {
            Toast.makeText(getContext(), "Please fill in the description.", Toast.LENGTH_LONG).show();
            return;
        }
        if (mPickedDatetime == null) {
            Toast.makeText(getContext(), "Please pick date and time.", Toast.LENGTH_LONG).show();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(DESCRIPTION_KEY, mDescriptionEdit.getText().toString());
        bundle.putString(LOCATION_KEY, mLocationEdit.getText().toString());
        bundle.putLong(DATETIME_KEY, mPickedDatetime.getTime());
        onButtonPressed(bundle);
    }

    public void onCancel(View v) {
        onButtonPressed(null);
    }

    public void onOpenLocationInMap(View v) {
        String addressString = mLocationEdit.getText().toString();
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }

    public void setViewText(String description, Date appointment, String location) {
        this.mPickedDatetime = appointment;
        mDescriptionEdit.setText(description);
        mDatetimeDisplay.setText(formatter.format(this.mPickedDatetime));
        mLocationEdit.setText(location);
    }
}
