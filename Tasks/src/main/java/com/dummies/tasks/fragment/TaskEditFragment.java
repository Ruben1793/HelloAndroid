package com.dummies.tasks.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.dummies.tasks.R;
import com.dummies.tasks.activity.TaskEditActivity;
import com.dummies.tasks.interfaces.OnEditFinished;
import com.dummies.tasks.provider.TaskProvider;
import com.dummies.tasks.util.ReminderManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by RubenGuillermo on 1/3/2016.
 */
public class TaskEditFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String DEFAULT_FRAGMENT_TAG="taskEditFragment";
    static final String TASK_ID="taskId";
    private static final int MENU_SAVE=1;
    static final String TASK_DATE_AND_TIME="taskDateAndTime";

    //Views
    View rootView;
    EditText titleText;
    EditText notesText;
    ImageView imageView;
    TextView dateButton;
    TextView timeButton;

    long taskId;
    Calendar taskDateAndTime;


    /**
     * Called when the user finishes editing a task.
     */

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        // If we didn't have a previous date, use "now"
        if (taskDateAndTime == null) {
            taskDateAndTime = Calendar.getInstance();
        }

        // Set the task id from the intent arguments, if available.
        Bundle arguments = getArguments();
        if (arguments != null) {
            taskId = arguments.getLong(TASK_ID);
        }

        // If we're restoring state from a previous activity, restore the
        // previous date as well
        if (savedInstanceState != null) {
            taskId = savedInstanceState.getLong(TASK_ID);
            taskDateAndTime = (Calendar)savedInstanceState.getSerializable(TASK_DATE_AND_TIME);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_task_edit, container, false);
        rootView = v.getRootView();
        titleText = (EditText) v.findViewById(R.id.title);
        notesText = (EditText) v.findViewById(R.id.notes);
        imageView = (ImageView) v.findViewById(R.id.image);
        dateButton = (TextView) v.findViewById(R.id.task_date);
        timeButton = (TextView) v.findViewById(R.id.task_time);


        //Tell the date and the time buttons what to do when we click on them
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showDatePicker();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        if (taskId == 0) {

            // This is a new task - add defaults from preferences if set.
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String defaultTitleKey = getString(R.string.pref_task_title_key);
            String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);

            String defaultTitle = prefs.getString(defaultTitleKey, null);
            String defaultTime = prefs.getString(defaultTimeKey, null);

            if (defaultTitle != null)
                titleText.setText(defaultTitle);

            if (defaultTime != null && defaultTime.length() > 0)
                taskDateAndTime.add(Calendar.MINUTE, Integer.parseInt(defaultTime));

            updateDateAndTimeButtons();
        }
        else{
            // Fire off a background loader to retrieve the data from the
            // database
            getLoaderManager().initLoader(0, null, this);
        }

        return v;
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        //This field may have changed while our activity was running, so make sure we save it to our outState bundle
        //so we can restore it later in onCreate

        outState.putLong(TASK_ID, taskId);
        outState.putSerializable(TASK_DATE_AND_TIME,taskDateAndTime);
    }

    public static TaskEditFragment newInstance(long id){
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle args = new Bundle();
        args.putLong(TaskEditActivity.EXTRA_TASKID,id);
        fragment.setArguments(args);
        return fragment;
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0,MENU_SAVE,0,R.string.confirm).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            //The SAVE button was pressed
            case MENU_SAVE:
                save();
                ((OnEditFinished)getActivity()).finishEditingTask();
                        return true;
        }
        //if we can't handle this menu item, see if our parent can
        return super.onOptionsItemSelected(item);
    }

    private void updateDateAndTimeButtons(){

        //set the time button text, make sure you import java text DateFormat below
        DateFormat timeFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        String timeForButton = timeFormat.format(taskDateAndTime.getTime());
        timeButton.setText(timeForButton);

        //set the date button text
        DateFormat dateFormat = DateFormat.getDateInstance();
        String dateForButton = dateFormat.format(taskDateAndTime.getTime());
        dateButton.setText(dateForButton);

    }


    private void showDatePicker() {
        // Create a fragment transaction
       FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();

        // Create the DatePickerDialogFragment and initialize it with
        // the appropriate values

       DatePickerDialogFragment newFragment = DatePickerDialogFragment.newInstance( taskDateAndTime );

        // Show the dialog, and name it "datePicker".  By naming it,
        // Android can automatically manage its state for us if it
        // needs to be killed and recreated.


      //  newFragment.show(ft,"DatePicker");
    }

    private void showTimePicker() {
        // Create a fragment transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        // Create the TimePickerDialogFragment and initialize it with
        // the appropriate values.
        DialogFragment fragment = TimePickerDialogFragment.newInstance(taskDateAndTime);

        // Show the dialog, and name it "timePicker".  By naming it,
        // Android can automatically manage its state for us if it
        // needs to be killed and recreated.


       //fragment.show(ft, "timePicker");
    }



    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        taskDateAndTime.set(Calendar.YEAR, year);
        taskDateAndTime.set(Calendar.MONTH, monthOfYear);
        taskDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateAndTimeButtons();
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        taskDateAndTime.set(Calendar.HOUR_OF_DAY,hour);
        taskDateAndTime.set(Calendar.MINUTE, minute);
    }

    private void save(){
        // Put all the values the user entered into a
        // ContentValues object
        String title = titleText.getText().toString();
        ContentValues values = new ContentValues();
        values.put(TaskProvider.COLUMN_TITLE,title);
        values.put(TaskProvider.COLUMN_NOTES,notesText.getText().toString());
        values.put(TaskProvider.COLUMN_DATE_TIME,taskDateAndTime.getTimeInMillis());


        // taskId==0 when we create a new task,
        // otherwise it's the id of the task being edited.
        if (taskId == 0) {

            // Create the new task and set taskId to the id of
            // the new task.
            Uri itemUri = getActivity().getContentResolver()
                    .insert(TaskProvider.CONTENT_URI, values);
            taskId = ContentUris.parseId(itemUri);

        } else {

            // Edit the task
            int count = getActivity().getContentResolver().update(
                    ContentUris.withAppendedId(TaskProvider.CONTENT_URI,
                            taskId),
                    values, null, null);

            // If somehow we didn't edit exactly one task,
            // throw an error
            if (count != 1)
                throw new IllegalStateException(
                        "Unable to update " + taskId);
        }

        // Notify the user of the change using a Toast
        Toast.makeText(getActivity(),
                getString(R.string.task_saved_message),
                Toast.LENGTH_SHORT).show();

        //Create a reminder for this task
        ReminderManager.setReminder(getActivity(),taskId,title,taskDateAndTime);
    }

    /**
     * This method is called when Android needs to create a loader to
     * load our task from the database.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri taskUri = ContentUris.withAppendedId(TaskProvider.CONTENT_URI,taskId);
        return new CursorLoader(getActivity(),taskUri,null,null,null,null);
    }

    /**
     * This methid is called when the loader has finished loading its
     * data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor task) {
        // Sanity check.  If we weren't able to load anything,
        // then just close this activity.
        if (task.getCount() == 0) {
            // onLoadFinished is called from a background thread.  Many
            // operations that affect the UI aren't allowed from
            // background threads.  So make sure that we call
            // finishEditingTask from the UI thread instead of from a
            // background thread.
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((OnEditFinished) getActivity()).finishEditingTask();
                }
            });
            return;
        }

        // Set our title and notes from the DB
        titleText.setText(task.getString(task.getColumnIndexOrThrow(TaskProvider.COLUMN_TITLE)));
        notesText.setText(task.getString(task.getColumnIndexOrThrow(TaskProvider.COLUMN_NOTES)));

        // The task date from the database
        Long dateInMillis = task.getLong(task.getColumnIndexOrThrow(TaskProvider.COLUMN_DATE_TIME));
        Date date = new Date(dateInMillis);
        taskDateAndTime.setTime(date);

        // Set the thumbnail image
        Picasso.with(getActivity()).load(TaskProvider.getImageUrlForTask(taskId)).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Activity activity = getActivity();

                // Because Picasso downloads images in the
                // background, we can't be sure that the user
                // didn't close the activity while the image
                // was being loaded.  If they did,
                // we will bomb out, so check do a sanity check
                // to be sure.
                if( activity==null )
                    return;

                // Set the colors of the activity based on the
                // colors of the image, if available
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                Palette palette = Palette.generate(bitmap, 32);
                int bgColor = palette.getLightMutedColor(0);
                if( bgColor!=0 ) {
                    rootView.setBackgroundColor(bgColor);
                }
            }

            @Override
            public void onError() {
                // do nothing, we'll use the default colors
            }
        });

        // We updated our task info, so update the date and time buttons
        updateDateAndTimeButtons();
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
