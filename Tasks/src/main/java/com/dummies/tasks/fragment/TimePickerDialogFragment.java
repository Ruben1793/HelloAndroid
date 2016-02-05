package com.dummies.tasks.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.Calendar;

/**
 * Created by RubenGuillermo on 1/5/2016.
 */
public class TimePickerDialogFragment extends DialogFragment {
    static final String HOUR="hour";
    static final String MINS = "mins";

    public static TimePickerDialogFragment newInstance(Calendar time){
        TimePickerDialogFragment  fragment = new TimePickerDialogFragment();

        Bundle args = new Bundle();
        args.putInt(HOUR,time.get(Calendar.HOUR_OF_DAY));
        args.putInt(MINS,time.get(Calendar.MINUTE));
        fragment.setArguments(args);
        return fragment;
    }

    public Dialog onCreateDialog (Bundle savedInstanceState ){
        OnTimeSetListener listener = (OnTimeSetListener)getFragmentManager().findFragmentByTag(TaskEditFragment.DEFAULT_FRAGMENT_TAG);

        Bundle args = getArguments();
        return new TimePickerDialog(getActivity(),listener,args.getInt(HOUR),args.getInt(MINS),false);

    }


}
