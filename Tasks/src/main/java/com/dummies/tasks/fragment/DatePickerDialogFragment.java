package com.dummies.tasks.fragment;


import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.dummies.tasks.R;

import java.util.Calendar;

/**
 * a lightweight wrapper for a DatePickerDialog that wraps the dialog ina  fragment
 */
public class DatePickerDialogFragment extends DialogFragment {

    static final String YEAR = "year";
    static final String MONTH ="month";
    static final String DAY = "day";

    public static DatePickerDialogFragment newInstance(Calendar date){
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();

        Bundle args = new Bundle();
        args.putInt(YEAR,date.get(Calendar.YEAR));
        args.putInt(MONTH,date.get(Calendar.MONTH));
        args.putInt(DAY,date.get(Calendar.DAY_OF_MONTH));
        fragment.setArguments(args);
        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        OnDateSetListener callback = (OnDateSetListener)getFragmentManager().findFragmentByTag(TaskEditFragment.DEFAULT_FRAGMENT_TAG);

        Bundle args = getArguments();
        return new DatePickerDialog(getActivity(),callback,args.getInt(YEAR),args.getInt(MONTH),args.getInt(DAY));
    }

}
