package f06.medipal.utils;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private Calendar mPickedCalender;
    private OnTimePickedListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimePickedListener) {
            mListener = (OnTimePickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimePickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mPickedCalender == null) mPickedCalender = Calendar.getInstance();
        mPickedCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mPickedCalender.set(Calendar.MINUTE, minute);
        mPickedCalender.set(Calendar.SECOND, 0);
        // Invoke the onTimePicked method
        mListener.onTimePicked(mPickedCalender.getTime());
    }

    public void setDate(Calendar pickedDate){
        mPickedCalender = pickedDate;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnTimePickedListener {
        void onTimePicked(Date pickedDatetime);
    }


}
