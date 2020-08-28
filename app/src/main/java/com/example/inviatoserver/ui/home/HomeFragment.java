package com.example.inviatoserver.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.inviatoserver.Fragments.PromocionListaFragment;
import com.example.inviatoserver.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FrameLayout fl_main;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fl_main = view.findViewById(R.id.fl_home);
        setFrament(new PromocionListaFragment());
        return view;
    }//onCreateView

    //Llamo fragment de menu de productos
    private void setFrament(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(fl_main.getId(), fragment);
        fragmentTransaction.commit();
    }//setFrament

}//HomeFragment