package com.example.tinni.ui.notification;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tinni.MainActivity;

/**
 * <h1>Notification Dummy</h1>
 * A dummy activity to handle clicks on a notification
 * Check whether app is running or not
 *
 * Source: https://stackoverflow.com/a/9532756/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   25.06.2020
 */

public class NotificationDummy extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (isTaskRoot())
        {
            Intent startAppIntent = new Intent(getApplicationContext(), MainActivity.class);
            startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startAppIntent);
        }

        finish();
    }
}
