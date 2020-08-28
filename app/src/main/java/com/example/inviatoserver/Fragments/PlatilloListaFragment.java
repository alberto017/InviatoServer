package com.example.inviatoserver.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import info.hoang8f.widget.FButton;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inviatoserver.Common.Common;
import com.example.inviatoserver.Helper.MySwipeHelper;
import com.example.inviatoserver.Interface.IItemClickListener;
import com.example.inviatoserver.Interface.IMyButtonClickListener;
import com.example.inviatoserver.Model.PlatilloModel;
import com.example.inviatoserver.R;
import com.example.inviatoserver.ViewHolder.PlatilloViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class PlatilloListaFragment extends Fragment {

    //Inicializacion de variables
    private FrameLayout flPlatilloLista;
    private FrameLayout flCategoriaLista;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FloatingActionButton btnAddFood;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private TextView lblCategoryID;
    private String categoryID = "";

    private RecyclerView rvPlatillo;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<PlatilloModel, PlatilloViewHolder> adapter;

    private FirebaseRecyclerAdapter<PlatilloModel, PlatilloViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    private MaterialSearchBar searchBar;

    //Agregar layout de menu
    private MaterialEditText edtFoodName;
    private MaterialEditText edtFoodDescription;
    private MaterialEditText edtFoodPrice;
    private MaterialEditText edtFoodDiscount;
    private MaterialEditText edtFoodStatus;
    private FButton btnSelectFood;
    private FButton btnUploadFood;

    PlatilloModel platilloModel;
    Uri saveUri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = this.getArguments();
        if (data != null) {
            categoryID = data.getString("categoryID");
        }//if
    }//onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_platillo_lista, container, false);

        flPlatilloLista = view.findViewById(R.id.flPlatilloLista);
        flCategoriaLista = view.findViewById(R.id.flCategoriaLista);
        searchBar = view.findViewById(R.id.searchBar);
        rvPlatillo = view.findViewById(R.id.rvPlatillo);

        //Conexion e instanciacion a  firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Food");
        btnAddFood = view.findViewById(R.id.fbAddFood);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("images/");


        //Cargar platillos
        rvPlatillo.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvPlatillo.setLayoutManager(layoutManager);

        cargarPlatillo(categoryID);

        //Search
        searchBar.setHint("Inserta el platillo");

        loadSuggest();
        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }//beforeTextChanged

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase())) {
                        suggest.add(search);
                    }//if
                }//for
            }//onTextChanged

            @Override
            public void afterTextChanged(Editable editable) {

            }//afterTextChanged
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //Restauramos el adaptador cuando la busqueda se cierra
                if (!enabled) {
                    rvPlatillo.setAdapter(adapter);
                }//if
            }//onSearchStateChanged

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //Mostramos resultado cuando la busqueda termina
                startSearch(text);
            }//onSearchConfirmed

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFrament(new AgregarPlatilloFragment());
                //showDialog();
            }//onClick
        });

        MySwipeHelper mySwipeHelper = new MySwipeHelper(getActivity(), rvPlatillo, 800) {

            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MySwipeHelper.MyButton> buffer) {
                buffer.add(new MyButton(getActivity(),
                        "Eliminar",
                        70,
                        0,
                        Color.parseColor("#FF3C30"),
                        new IMyButtonClickListener() {
                            @Override
                            public void onClickSwipe(int pos) {
                                Toast.makeText(getActivity(), "¡Platillo eliminado!", Toast.LENGTH_LONG).show();
                                DatabaseReference item = adapter.getRef(pos);
                                item.removeValue();
                            }
                        }));
                buffer.add(new MyButton(getActivity(),
                        "Actualizar",
                        70,
                        0,
                        Color.parseColor("#FF9502"),
                        new IMyButtonClickListener() {
                            @Override
                            public void onClickSwipe(int pos) {
                                Toast.makeText(getActivity(), "¡Platillo actualizado!", Toast.LENGTH_LONG).show();
                            }
                        }));
            }//instantiateMyButton
        };
        return view;
    }//onCreateView

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<PlatilloModel, PlatilloViewHolder>(
                PlatilloModel.class,
                R.layout.food_item,
                PlatilloViewHolder.class,
                databaseReference.orderByChild("Name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(PlatilloViewHolder platilloViewHolder, PlatilloModel platilloModel, int i) {

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Espere un momento...");
                progressDialog.show();

                platilloViewHolder.lblFood_title.setText(platilloModel.getName());
                Picasso.with(getActivity().getBaseContext()).load(platilloModel.getImage())
                        .into(platilloViewHolder.lblFood_image);
                platilloViewHolder.lblFood_price.setText(platilloModel.getPrice());
                platilloViewHolder.lblFood_discount.setText(platilloModel.getDiscount());
                platilloViewHolder.lblFood_status.setText(platilloModel.getStatus());

                progressDialog.dismiss();

                //Asignacion de la fuente
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NABILA.TTF");
                platilloViewHolder.lblFood_title.setTypeface(typeface);

                final PlatilloModel platillos = platilloModel;
                platilloViewHolder.setItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        PlatilloDetalleFragment platilloDetalleFragment = new PlatilloDetalleFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("FoodID", searchAdapter.getRef(position).getKey());
                        platilloDetalleFragment.setArguments(bundle);

                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        fragmentTransaction.replace(flPlatilloLista.getId(), platilloDetalleFragment);
                        fragmentTransaction.commit();

                    }//onClick
                });
            }//populateViewHolder
        };
        rvPlatillo.setAdapter(searchAdapter);
    }//startSearch

    private void loadSuggest() {

        databaseReference.orderByChild("menuID").equalTo(categoryID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            PlatilloModel item = postSnapshot.getValue(PlatilloModel.class);
                            suggestList.add(item.getName());
                        }//for
                    }//onDataChange

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }//onCancelled
                });

    }//loadSuggest


    private void cargarPlatillo(String categoryID) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Espere un momento...");
        progressDialog.show();

        //Equivalete a select * from food where MenuID =
        adapter = new FirebaseRecyclerAdapter<PlatilloModel, PlatilloViewHolder>(PlatilloModel.class, R.layout.food_item, PlatilloViewHolder.class, databaseReference.orderByChild("menuID").equalTo(categoryID)) {
            @Override
            protected void populateViewHolder(PlatilloViewHolder platilloViewHolder, PlatilloModel platilloModel, int i) {
                platilloViewHolder.lblFood_title.setText(platilloModel.getName());
                Picasso.with(getActivity().getBaseContext()).load(platilloModel.getImage())
                        .into(platilloViewHolder.lblFood_image);
                platilloViewHolder.lblFood_price.setText(platilloModel.getPrice());
                platilloViewHolder.lblFood_discount.setText(platilloModel.getDiscount());
                platilloViewHolder.lblFood_status.setText(platilloModel.getStatus());

                progressDialog.dismiss();

                //Asignacion de la fuente
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NABILA.TTF");
                platilloViewHolder.lblFood_title.setTypeface(typeface);

                final PlatilloModel platillos = platilloModel;
                platilloViewHolder.setItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        PlatilloDetalleFragment platilloDetalleFragment = new PlatilloDetalleFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("FoodID", adapter.getRef(position).getKey());
                        platilloDetalleFragment.setArguments(bundle);

                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        fragmentTransaction.replace(flPlatilloLista.getId(), platilloDetalleFragment);
                        fragmentTransaction.commit();
                    }//onClick
                });
            }//populateViewHolder
        };
        rvPlatillo.setAdapter(adapter);
    }//cargarPlatillo

    /*
    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("NUEVO PLATILLO");
        alertDialog.setMessage("¡Introduce los datos por favor!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_menu = inflater.inflate(R.layout.fragment_agregar_platillo, null);
        edtFoodName = add_new_menu.findViewById(R.id.edtFoodName);
        edtFoodDescription = add_new_menu.findViewById(R.id.edtFoodDescription);

        edtFoodPrice = add_new_menu.findViewById(R.id.edtFoodPrice);
        edtFoodDiscount = add_new_menu.findViewById(R.id.edtFoodDiscount);

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
                if (platilloModel != null) {
                    databaseReference.push().setValue(platilloModel);
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
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            //btnSelectFood.setText("¡Imagen seleccionada!");
        }//if
    }//onActivityResult

    private void chooseImage() {
        Intent select = new Intent();
        select.setType("image/*");
        select.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(select, "Selecciona imagen"), Common.PICK_IMAGE_REQUEST);
    }//chooseImage

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
                                    platilloModel = new PlatilloModel();
                                    platilloModel.setName(edtFoodName.getText().toString());
                                    platilloModel.setDescription(edtFoodDescription.getText().toString());
                                    /*
                                    platilloModel.setPrice(edtFoodPrice.toString());
                                    platilloModel.setDiscount(edtFoodDiscount.toString());
                                     */
                                    platilloModel.setMenuID(categoryID);
                                    platilloModel.setImage(uri.toString());
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

    //Llamo fragment de menu de productos
    private void setFrament(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(flPlatilloLista.getId(), fragment);
        fragmentTransaction.commit();
    }//setFrament

}//ProductoListaFragment