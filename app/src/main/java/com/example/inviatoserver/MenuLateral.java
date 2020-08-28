package com.example.inviatoserver;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inviatoserver.Common.Common;
import com.example.inviatoserver.Service.ListenOrder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.paperdb.Paper;

public class MenuLateral extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private long backPressedTime;
    private Toast backToast;
    private TextView lblUserName_Header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        lblUserName_Header = findViewById(R.id.lblUserName_Header);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Asignar usuario
        View headerView = navigationView.getHeaderView(0);
        lblUserName_Header = (TextView) headerView.findViewById(R.id.lblUserName_Header);
        lblUserName_Header.setText(Common.currentUsuarioModel.getName());

        //Iniciamos Paper
        Paper.init(this);

        //Iniciamos en servicio de notificaciones
        Intent service = new Intent(MenuLateral.this, ListenOrder.class);
        startService(service);
    }//onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.salir, menu);
        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                Intent actionExit = new Intent(getApplicationContext(), SignIn.class);
                actionExit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(actionExit);
                //Eliminamos cache de usuario
                Paper.book().destroy();
        }//switch
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }//onSupportNavigateUp

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.show();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(MenuLateral.this, "¡Presiona nuevamente para salir!", Toast.LENGTH_SHORT);
            backToast.show();
        }//else
        backPressedTime = System.currentTimeMillis();
    }//onBackPressed
}//MenuLateral