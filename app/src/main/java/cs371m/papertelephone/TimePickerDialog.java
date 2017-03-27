package cs371m.papertelephone;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;


public class TimePickerDialog extends DialogFragment implements android.app.TimePickerDialog.OnTimeSetListener {
    public final static String TAG = "TimePickerDialog";
    int hour = 12;
    int minute = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new android.app.TimePickerDialog(getActivity(), this, hour, minute, false);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d(TAG, "hourOfDay: " + hourOfDay + "; minute: " + minute);
    }

/*    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerDialog();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }*/

}
