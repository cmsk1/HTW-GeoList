package com.htwberlin.geolist.notification.helper;

import android.content.Context;

public interface NotificationSender {
    void sendNotification(String text, Context context);
}
