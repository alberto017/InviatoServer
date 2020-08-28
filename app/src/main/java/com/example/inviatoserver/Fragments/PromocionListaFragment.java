package com.example.inviatoserver.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import info.hoang8f.widget.FButton;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.inviatoserver.Common.Common;
import com.example.inviatoserver.Interface.IItemClickListener;
import com.example.inviatoserver.MapsFragment;
import com.example.inviatoserver.Model.PlatilloModel;
import com.example.inviatoserver.Model.PromocionModel;
import com.example.inviatoserver.R;
import com.example.inviatoserver.ViewHolder.PromocionViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;


public class PromocionListaFragment extends Fragment {

    //Inicializacion de variables
    private FrameLayout flPromociones;
    private FloatingActionButton fbAddPromotion;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    Uri saveUri;

    private RecyclerView rvPromocion;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<PromocionModel, PromocionViewHolder> adapter;
    private PromocionModel promocionModel;

    //Agregar layout de menu
    private MaterialEditText edtFoodName;
    private MaterialEditText edtFoodDescription;
    private MaterialEditText edtFoodPrice;
    private MaterialEditText edtFoodDiscount;
    private MaterialEditText edtFoodStatus;
    private FButton btnSelectFood;
    private FButton btnUploadFood;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }//onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promocion_lista, container, false);
        flPromociones = view.findViewById(R.id.flPromociones);
        rvPromocion = view.findViewById(R.id.rvPromocion);
        fbAddPromotion = view.findViewById(R.id.fbAddPromotion);

        //Conexion e instanciacion a  firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Promotion");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("images/");


        //Cargar platillos
        rvPromocion.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvPromocion.setLayoutManager(layoutManager);
        cargarPromocion();

        fbAddPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFrament(new AgregarPromocionFragment());
                //setFrament(new MapsFragment());
            }//onClick
        });

        return view;
    }//onCreateView

    private void cargarPromocion() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Espere un momento...");
        progressDialog.show();

        //Equivalete a select * from food where MenuID =
        adapter = new FirebaseRecyclerAdapter<PromocionModel, PromocionViewHolder>(PromocionModel.class, R.layout.promotion_item, PromocionViewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(PromocionViewHolder promocionViewHolder, PromocionModel promocionModel, int i) {
                promocionViewHolder.lblPromotion_Title.setText(promocionModel.getTitle());
                Picasso.with(getActivity().getBaseContext()).load(promocionModel.getImage())
                        .into(promocionViewHolder.ImgPromotion_image);
                promocionViewHolder.lblPromotion_RestaurantName.setText(promocionModel.getRestaurantName());
                promocionViewHolder.lblPromotion_price.setText(promocionModel.getPrice());
                promocionViewHolder.lblPromotion_DateEnd.setText(promocionModel.getDateEnd());

                progressDialog.dismiss();

                //Asignacion de la fuente
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NABILA.TTF");
                promocionViewHolder.lblPromotion_Title.setTypeface(typeface);

                final PromocionModel clickItem = promocionModel;
                promocionViewHolder.setItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        /*
                        PlatilloDetalleFragment platilloDetalleFragment = new PlatilloDetalleFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("FoodID", adapter.getRef(position).getKey());
                        platilloDetalleFragment.setArguments(bundle);

                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        fragmentTransaction.replace(flPromociones.getId(), platilloDetalleFragment);
                        fragmentTransaction.commit();
                        */
                    }//onClick
                });
            }//populateViewHolder
        };
        rvPromocion.setAdapter(adapter);
    }//cargarPromocion
/*
    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("NUEVA PROMOCION");
        alertDialog.setMessage("¡Introduce los datos por favor!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_menu = inflater.inflate(R.layout.add_new_food, null);
        edtFoodName = add_new_menu.findViewById(R.id.edtFoodName);
        edtFoodDescription = add_new_menu.findViewById(R.id.edtFoodDescription);
        edtFoodStatus = add_new_menu.findViewById(R.id.edtFoodStatus);
        btnSelectFood = add_new_menu.findViewById(R.id.btnSelectFood);
        btnUploadFood = add_new_menu.findViewById(R.id.btnUploadFood);

        btnSelectFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }//onClick
        });

        btnUploadFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }//onClick
        });

        alertDialog.setView(add_new_menu);
        alertDialog.setIcon(R.drawable.cart);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (promocionModel != null) {
                    databaseReference.push().setValue(promocionModel);
                }//if
            }//onClick
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }//onClick
        });
        alertDialog.show();
    }//showDialog
/*
    private void chooseImage() {
        Intent select = new Intent();
        select.setType("image/*");
        select.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(select, "Selecciona imagen"), Common.PICK_IMAGE_REQUEST);
    }//chooseImage
 */

    /*
    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Subiendo...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "¡Carga finalizada!", Toast.LENGTH_LONG).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //platilloModel = new PlatilloModel(edtCategoryName.getText().toString(), uri.toString());
                                    promocionModel = new PromocionModel();
                                    promocionModel.setTitle();
                                    promocionModel.setImage(uri.toString());
                                    promocionModel.setRestaurantName();
                                    promocionModel.setPrice();
                                    promocionModel.setDateEnd();
                                    promocionModel.setRestaurantID();
                                }//onSuccess
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }//onFailure
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Carga finalizada" + progress + "%");
                        }//onProgress
                    });
        }//if
    }//uploadImage
     */

    //Llamo fragment de menu de productos
    private void setFrament(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(flPromociones.getId(), fragment);
        fragmentTransaction.commit();
    }//setFrament

}//PromocionListaFragment