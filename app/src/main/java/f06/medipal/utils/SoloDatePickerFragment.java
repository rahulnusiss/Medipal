package f06.medipal.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * A date picker fragment.
 * If you just need the date, you should use this fragment in your activity.
 * This fragment will deliver the date by callback function.
 * Activities that contain this fragment must implement the
 * {@link SoloDatePickerFragment.OnDatePickedListener} interface
 * to handle interaction events.
 */
public class SoloDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnDatePickedListener mListener;
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


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mPickedDate.set(year, month, dayOfMonth, 0, 0, 0);
        mListener.onDatePicked(mPickedDate.getTime());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDatePickedListener) {
            mListener = (OnDatePickedListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDatePickedListener {
        void onDatePicked(Date pickedDatetime);
    }
}
