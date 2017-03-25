package f06.medipal.utils;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;


import android.widget.DatePicker;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Calendar mPickedDate = Calendar.getInstance();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * Show the timePicker in this callback method,
     * make sure the user pick date and time simultaneously.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        TimePickerFragment timePicker = new TimePickerFragment();
        timePicker.show(getFragmentManager(), "time_picker");
        mPickedDate.set(year, month, dayOfMonth);
        timePicker.setDate(mPickedDate);
    }
}
