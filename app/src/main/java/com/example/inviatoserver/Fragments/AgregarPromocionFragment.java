package com.example.inviatoserver.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inviatoserver.R;


public class AgregarPromocionFragment extends Fragment {

    //Declaracion de variables
    private TextView lblTitle_newCategory;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agregar_promocion, container, false);

        lblTitle_newCategory = view.findViewById(R.id.lblTitle_newFood);

        //Asignacion de la fuente
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NABILA.TTF");
        lblTitle_newCategory.setTypeface(typeface);
        return view;
    }//onCreateView
}//AgregarCategoriaFragment