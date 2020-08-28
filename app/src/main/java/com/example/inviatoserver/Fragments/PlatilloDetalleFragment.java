package com.example.inviatoserver.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.inviatoserver.Model.PlatilloModel;
import com.example.inviatoserver.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class PlatilloDetalleFragment extends Fragment {

    //Declaracion de variables
    private ImageView imgFoodDetalle;
    private TextView lblNombreDetalle;
    private TextView lblPrecioDetalle;
    private TextView lblDescuentoDetalle;
    private TextView lblIngredientesDetalle;
    private TextView lblTituloCantidadDetalle;

    private TextView lblTituloPrecioDetalle;
    private TextView lblTituloDescuentoDetalle;
    private TextView lblTituloIngredientesDetalle;

    private ElegantNumberButton spinnerCantidadDetalle;
    private CollapsingToolbarLayout collapsingDetalle;
    private AppBarLayout app_bar_layout;
    private String platilloID = "";
    String SpinnerValue = "";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    PlatilloModel platilloModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = this.getArguments();
        if (data != null) {
            platilloID = data.getString("FoodID");
            //Toast.makeText(getActivity(),platilloID,Toast.LENGTH_LONG).show();
        }//if
    }//onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_platillo_detalle, container, false);

        collapsingDetalle = view.findViewById(R.id.collapsingDetalle);
        app_bar_layout = view.findViewById(R.id.app_bar_layout);
        imgFoodDetalle = view.findViewById(R.id.imgFoodDetalle);

        lblTituloPrecioDetalle = view.findViewById(R.id.lblTituloPrecioDetalle);
        lblTituloDescuentoDetalle = view.findViewById(R.id.lblTituloDescuentoDetalle);
        lblTituloIngredientesDetalle = view.findViewById(R.id.lblTituloIngredientesDetalle);
        lblTituloCantidadDetalle = view.findViewById(R.id.lblTituloCantidadDetalle);

        lblNombreDetalle = view.findViewById(R.id.lblNombreDetalle);
        lblPrecioDetalle = view.findViewById(R.id.lblPrecioDetalle);
        lblDescuentoDetalle = view.findViewById(R.id.lblDescuentoDetalle);
        lblIngredientesDetalle = view.findViewById(R.id.lblIngredientesDetalle);

        spinnerCantidadDetalle = view.findViewById(R.id.spinnerCantidadDetalle);

        //Asignacion de la fuente
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NABILA.TTF");

        lblNombreDetalle.setTypeface(typeface);

        //Conexion e instanciacion a  firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Food");

        cargarDetalle(platilloID);
        return view;
    }//onCreateView

    private void cargarDetalle(String platilloID) {
        databaseReference.child(platilloID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                platilloModel = dataSnapshot.getValue(PlatilloModel.class);

                //Set Image
                Picasso.with(getActivity().getBaseContext()).load(platilloModel.getImage())
                        .into(imgFoodDetalle);

                collapsingDetalle.setTitle(platilloModel.getName());
                lblNombreDetalle.setText(platilloModel.getName());
                lblPrecioDetalle.setText(platilloModel.getPrice());
                lblDescuentoDetalle.setText(platilloModel.getDiscount());
                lblIngredientesDetalle.setText(platilloModel.getDescription());
            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }//cargarDetalle

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lblIngredientesDetalle.setText(platilloID);
    }//onViewCreated
}//PlatilloDetalleFragment