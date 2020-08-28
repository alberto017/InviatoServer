package com.example.inviatoserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.paperdb.Paper;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inviatoserver.Common.Common;
import com.example.inviatoserver.Model.UsuarioModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Inicio extends AppCompatActivity {

    //Declaracion de variables
    private TextView lblSlogan;
    private Button btnSignUp;
    private Button btnSignIn;
    private TextView lblRecovery;

    private boolean locationPermissionGranted;
    private static final int PERMISSION_REQUEST_CODE = 9001;

    //Declaracion de variables
    private DatabaseReference databaseReference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        //Enlazar controladores
        lblSlogan = findViewById(R.id.lblSlogan);
        btnSignUp = findViewById(R.id.btnSignUp_main);
        btnSignIn = findViewById(R.id.btnSignIn_main);

        //Asignacion de la fuente
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        lblSlogan.setTypeface(typeface);

        if(locationPermissionGranted){
            Toast.makeText(Inicio.this,"¡Permisos activados!",Toast.LENGTH_LONG).show();
        }else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
                }//if
            }//if
        }//else

        //Iniciamos Paper
        Paper.init(this);

        //Validar checkBox de guardado de sesion
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if (user != null && pwd != null) {
            if (!user.isEmpty() && !pwd.isEmpty()) {
                login(user, pwd);
            }//if
        }//if

    }//onCreate

    //Evento onClick
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp_main:
                Intent signUp = new Intent(Inicio.this,Help.class);
                startActivity(signUp);
                break;
            case R.id.btnSignIn_main:
                Intent signIn = new Intent(Inicio.this,SignIn.class);
                startActivity(signIn);
                break;
        }//onClick
    }//onClick


    private void login(final String phone, final String pwd) {

        //Conexion a firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User");

        if (Common.isConnectedToInternet(getBaseContext())) {
            final ProgressDialog progressDialog = new ProgressDialog(Inicio.this);
            progressDialog.setMessage("Espere un momento...");
            progressDialog.show();

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Verificamos que exista el usiario posteriormente la contrasena
                    if (dataSnapshot.child(phone).exists()) {
                        progressDialog.dismiss();
                        UsuarioModel usuarioModel = dataSnapshot.child(phone).getValue(UsuarioModel.class);
                        if (usuarioModel.getPassword().equals(pwd)) {
                            Intent menuLateral = new Intent(Inicio.this, MenuLateral.class);
                            Common.currentUsuarioModel = usuarioModel; //Obtenemos el usuario actual
                            startActivity(menuLateral);
                            finish();
                        } else {
                            Toast.makeText(Inicio.this, "¡Contraseña incorrecta!", Toast.LENGTH_SHORT).show();
                        }//else
                    } else {
                        Toast.makeText(Inicio.this, "¡Usuario incorecto!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }//else
                }//onDataChange

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }//onCancelled
            });
        } else {
            Toast.makeText(Inicio.this, "¡Revisa tu Conexion a Internet!", Toast.LENGTH_LONG).show();
            return;
        }//else
    }//login

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted = true;
            Toast.makeText(Inicio.this,"¡Permisos concedidos!",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(Inicio.this,"¡Permisos denegados!",Toast.LENGTH_LONG).show();
        }
    }//onRequestPermissionsResult
}//Inicio