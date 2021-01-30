package com.example.MA02_20170953;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {

    private final static String TAG = "SmsReceiver";

    String content;
    Date receivedDate;
    Context c;
    Foreground foreground;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "message received");

        c = context;
        foreground = Foreground.get();

        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = parseSmsMessage(bundle);

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

        if(messages.length > 0){
            content = messages[0].getMessageBody().toString();
            receivedDate = new Date(messages[0].getTimestampMillis());
        }
        String date = format.format(receivedDate);

        //Log.d(TAG, "문자내용 : " + content + "\n수신일 : " + date);
        String [] splitWithEscape = content.split("\n");

        if(splitWithEscape[0].equals("[여성안심택배함]")){
            Log.d(TAG, "여성안심택배함 문자 수신");
            String [] column = splitWithEscape[1].split(":");
            String boxName = column[1];

            column = splitWithEscape[2].split(":");
            int partition = Integer.valueOf(column[1]);

            column = splitWithEscape[3].split(":");
            String password = column[1];

            Log.d(TAG, boxName + ", " + partition + ", " + password);

            sendToActivity(boxName, partition, password, date);
        }
    }

    private SmsMessage[] parseSmsMessage(Bundle bundle){
        // PDU: Protocol Data Units
        Object[] objs = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for(int i=0; i<objs.length; i++){
            messages[i] = SmsMessage.createFromPdu((byte[])objs[i]);
        }

        return messages;
    }

    private void sendToActivity(String boxName, int partition, String password, String date){
        Intent intent = new Intent(c, SmsActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                |Intent.FLAG_ACTIVITY_SINGLE_TOP
                |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("boxName", boxName);
        intent.putExtra("partition", partition);
        intent.putExtra("password", password);
        intent.putExtra("date", date);

        if(!foreground.isBackground()){ // 어플이 foreground 상태라면
            c.startActivity(intent);
            Log.d(TAG, "어플 forground");
        }else { // 어플이 background 상태라면
            Log.d(TAG, "어플 background");
            PendingIntent pi = PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder
                    = new NotificationCompat.Builder(c, c.getString(R.string.CHANNEL_ID))
                    .setSmallIcon(R.drawable.ic_woman)
                    .setContentTitle("여성안심택배함")
                    .setContentText(c.getString(R.string.notification))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(c.getString(R.string.notification)))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pi)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
            int notificationId = 100;
            notificationManager.notify(notificationId, builder.build()); // 알림생성(화면에 noti출력됨)
        }
    }

}
