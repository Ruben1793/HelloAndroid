package com.dummies.tasks.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dummies.tasks.R;
import com.dummies.tasks.activity.TaskEditActivity;
import com.dummies.tasks.provider.TaskProvider;

/**
 * Created by RubenGuillermo on 1/6/2016.
 */
public class OnAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Important: DO NOT DO ANY ASYNCHRONUS OPERATIONS IN BROADCAST RECEIVER.ONRECEIVE! SEE THE SIDEBAR

        NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent taskEditIntent = new Intent(context, TaskEditActivity.class);
        long taskId= intent.getLongExtra(TaskProvider.COLUMN_TASKID,-1);
        String title = intent.getStringExtra(TaskProvider.COLUMN_TITLE);
        taskEditIntent.putExtra(TaskProvider.COLUMN_TASKID,taskId);

        PendingIntent pi = PendingIntent.getActivity(context,0,taskEditIntent,PendingIntent.FLAG_ONE_SHOT);

        //Build the notification object using a Notification Builder
        Notification note = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.notify_new_task_title))
                .setContentText(title)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        //Send notification
        mgr.notify((int) taskId, note);

    }
}
