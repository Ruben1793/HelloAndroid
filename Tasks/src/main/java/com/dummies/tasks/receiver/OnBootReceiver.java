package com.dummies.tasks.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.dummies.tasks.provider.TaskProvider;
import com.dummies.tasks.util.ReminderManager;

import java.util.Calendar;

/**
 * Created by RubenGuillermo on 1/6/2016.
 */
public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Cursor cursor = context.getContentResolver().query(TaskProvider.CONTENT_URI,null,null,null,null);

        //if our db is empty, don't do anything
        if (cursor == null ){
            return;
        }

        try{
            cursor.moveToFirst();

            int taskIdColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TASKID);
            int dateTimeColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_DATE_TIME);
            int titleColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TITLE);

            // Loop over all of the tasks in the db
            while (!cursor.isAfterLast()) {

                // Get the id and dateTime for this task
                long taskId = cursor.getLong(taskIdColumnIndex);
                long dateTime = cursor.getLong(dateTimeColumnIndex);
                String title = cursor.getString(titleColumnIndex);

                Calendar cal = Calendar.getInstance();
                cal.setTime(new java.util.Date(dateTime));

                // Set the reminder
                ReminderManager.setReminder(context, taskId, title, cal);
                cursor.moveToNext();

                Log.d("OnBootReceiver","Adding Alarm from boot");
            }

        } finally {
            // Always close the cursor when we finish!
            cursor.close();
        }
    }
}


