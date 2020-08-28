package com.example.inviatoserver.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inviatoserver.R;

public class AgregarPlatilloFragment extends Fragment {

    //Declaracion de variables
    private TextView lblTitle_newFood;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agregar_platillo, container, false);

        lblTitle_newFood = view.findViewById(R.id.lblTitle_newFood);

        //Asignacion de la fuente
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NABILA.TTF");
        lblTitle_newFood.setTypeface(typeface);
        return view;
    }//onCreateView

}//AgregarPlatilloFragment