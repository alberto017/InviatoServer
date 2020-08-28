package com.example.inviatoserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inviatoserver.Common.Common;
import com.example.inviatoserver.Model.UsuarioModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    //Declaracion de variables
    private TextView lblTitle;
    private EditText edtPhone;
    private EditText edtPassword;
    private Button btnSignIn;
    private TextView lblRecovery;
    private ImageView imgSignIn_signIn;
    private DatabaseReference databaseReference = null;
    private CheckBox cbRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Referencia de controladores
        lblTitle = findViewById(R.id.lblTitle_signIn);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn_signIn);
        lblRecovery = findViewById(R.id.lblRecovery_signIn);
        imgSignIn_signIn = findViewById(R.id.imgSignIn_signIn);
        cbRemember = findViewById(R.id.cbRemember);

        //Iniciamos Paper
        Paper.init(this);

        //Validamos el login con los datos almacenados en Paper
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        /*
        if(user != null && pwd != null){
            if(!user.isEmpty() && !pwd.isEmpty()){
                login(user,pwd);
            }//if
        }//user
       */



        //Asignacion de la fuente
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        lblTitle.setTypeface(typeface);

        //Conexion a firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.isConnectedToInternet(getBaseContext())) {

                    //Guardamos usuario y contraseña
                    if(cbRemember.isChecked()){
                        Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());
                    }//if


                    final ProgressDialog progressDialog = new ProgressDialog(SignIn.this);
                    progressDialog.setMessage("Espere un momento...");
                    progressDialog.show();

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (validaciones()) {
                                //Verificamos que exista el usiario posteriormente la contrasena
                                if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                    progressDialog.dismiss();
                                    UsuarioModel usuarioModel = dataSnapshot.child(edtPhone.getText().toString()).getValue(UsuarioModel.class);
                                    if (usuarioModel.getPassword().equals(edtPassword.getText().toString())) {
                                        Intent menuLateral = new Intent(SignIn.this, MenuLateral.class);
                                        Common.currentUsuarioModel = usuarioModel; //Obtenemos el usuario actual
                                        startActivity(menuLateral);
                                        finish();
                                    } else {
                                        Toast.makeText(SignIn.this, "¡Contraseña incorrecta!", Toast.LENGTH_SHORT).show();
                                    }//else
                                } else {
                                    Toast.makeText(SignIn.this, "¡Usuario incorecto!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }//else
                            } else {
                                progressDialog.dismiss();
                            }//else
                        }//onDataChange

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }//onCancelled
                    });
                } else {
                    Toast.makeText(SignIn.this, "¡Revisa tu Conexion a Internet!", Toast.LENGTH_LONG).show();
                    return;
                }//if
            }//onClick
        });
    }//onCreate

    private void login(final String phone, final String pwd) {
        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog progressDialog = new ProgressDialog(SignIn.this);
            progressDialog.setMessage("Espere un momento...");
            progressDialog.show();

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (validaciones()) {
                        //Verificamos que exista el usiario posteriormente la contrasena
                        if (dataSnapshot.child(phone).exists()) {
                            progressDialog.dismiss();
                            UsuarioModel usuarioModel = dataSnapshot.child(phone).getValue(UsuarioModel.class);
                            //usuarioModel.setPhone(phone);
                            if (usuarioModel.getPassword().equals(pwd)) {
                                Intent menuLateral = new Intent(SignIn.this, MenuLateral.class);
                                Common.currentUsuarioModel = usuarioModel; //Obtenemos el usuario actual
                                startActivity(menuLateral);
                                finish();
                            } else {
                                Toast.makeText(SignIn.this, "¡Contraseña incorrecta!", Toast.LENGTH_SHORT).show();
                            }//else
                        } else {
                            Toast.makeText(SignIn.this, "¡Usuario incorecto!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }//else
                    } else {
                        progressDialog.dismiss();
                    }//else
                }//onDataChange

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }//onCancelled
            });
        } else {
            Toast.makeText(SignIn.this, "¡Revisa tu Conexion a Internet!", Toast.LENGTH_LONG).show();
            return;
        }//if
    }//login

    public boolean validaciones() {
        if (edtPhone.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()) {
            Toast.makeText(SignIn.this, "¡Existen campos vacios!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }//else
    }//validaciones

    public void onClick(View view) {
    }
}//SignIn