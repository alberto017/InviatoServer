package com.example.inviatoserver.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.example.inviatoserver.Model.SolicitudModel;
import com.example.inviatoserver.Model.UsuarioModel;
import com.example.inviatoserver.Remote.IGeoCoordenates;
import com.example.inviatoserver.Remote.RetrofitClient;

import java.net.NetworkInterface;

public class Common {
    public static UsuarioModel currentUsuarioModel;
    public static SolicitudModel currentSolicitudModel;

    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static final String UPDATE = "Actualizar";
    public static final String DELETE = "Eliminar";
    public static final int PICK_IMAGE_REQUEST = 71;
    public static final String baseUrl = "https://maps.googleapis.com";

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Enviada";
        else if (status.equals("1"))
            return "En Camino";
        else
            return "Finalizada";
    }//convertCodeToStatus

    public static IGeoCoordenates getGeoCodeService() {
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordenates.class);
    }//IGeoCoordenates

    public static Bitmap scaleBitMap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0, pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }//Bitmap

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null){
                for(int i = 0; i<info.length;i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }//if
                }//for
            }//if
        }//if
        return false;
    }//isConnectedToInternet

}//Common
