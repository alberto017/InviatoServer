package com.example.inviatoserver.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.example.inviatoserver.Fragments.OrderListaFragment;
import com.example.inviatoserver.Model.SolicitudModel;
import com.example.inviatoserver.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ListenOrder extends Service implements ChildEventListener {

    //Declaracion de variables
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public ListenOrder() {
    }//ListenOrder

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Request");
    }//onCreate

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseReference.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }//onStartCommand

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }//onBind

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        SolicitudModel solicitudModel = dataSnapshot.getValue(SolicitudModel.class);
        if (solicitudModel.getStatus().equals("0")) {
            showNotification(dataSnapshot.getKey(), solicitudModel);
        }//if
    }//onChildAdded

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }//onChildChanged

    private void showNotification(String key, SolicitudModel solicitudModel) {
        Intent intent = new Intent(getBaseContext(), OrderListaFragment.class);
        intent.putExtra("userPhone", solicitudModel.getPhone());
        PendingIntent contentIntent = PendingIntent
                .getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("foodStatus", "foodStatus", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }//if

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), "foodStatus");
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("food")
                .setContentInfo("Nueva Orden")
                .setContentText("Orden #" + key)
                .setContentIntent(contentIntent)
                .setContentInfo("Info")
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.notify(1, builder.build());
        //Asignar clave unica a la notificacion
        int randomInt = new Random().nextInt(9999-1)+1;
        notificationManager.notify(randomInt,builder.build());
    }//showNotification

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }//onChildRemoved

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }//onChildMoved

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }//onCancelled
}
