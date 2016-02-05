package com.dummies.silentmodetoggle.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Created by RubenGuillermo on 12/28/2015.
 */
public class AppWidget extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
         context.startService(new Intent(context,AppWidgetService.class));
    }
}
