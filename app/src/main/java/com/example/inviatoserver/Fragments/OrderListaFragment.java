package com.example.inviatoserver.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import info.hoang8f.widget.FButton;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.inviatoserver.Common.Common;
import com.example.inviatoserver.Helper.MySwipeHelper;
import com.example.inviatoserver.Interface.IItemClickListener;
import com.example.inviatoserver.Interface.IMyButtonClickListener;
import com.example.inviatoserver.Model.SolicitudModel;
import com.example.inviatoserver.R;
import com.example.inviatoserver.TrackingOrder;
import com.example.inviatoserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;


public class OrderListaFragment extends Fragment {

    //Inicializacion de variables
    public RecyclerView rvOrden;
    public MaterialSpinner spinner;
    public RecyclerView.LayoutManager layoutManager;

    //Agregar layout de menu
    private MaterialEditText edtCategoryName;
    private FButton btnSelectCategory;
    private FButton btnUploadCategory;

    private FrameLayout flCategoriaLista;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<SolicitudModel, OrderViewHolder> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_lista, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Request");

        rvOrden = view.findViewById(R.id.rvOrden);
        rvOrden.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvOrden.setLayoutManager(layoutManager);

        cargarOrdenes();

        MySwipeHelper mySwipeHelper = new MySwipeHelper(getActivity(), rvOrden, 800) {

            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(getActivity(),
                        "Eliminar",
                        70,
                        0,
                        Color.parseColor("#FF3C30"),
                        new IMyButtonClickListener() {
                            @Override
                            public void onClickSwipe(int pos) {
                                Toast.makeText(getActivity(), "¡Orden eliminada!" + pos, Toast.LENGTH_LONG).show();
                                DatabaseReference item = adapter.getRef(pos);
                                item.removeValue();
                            }//onClickSwipe
                        }));

                buffer.add(new MyButton(getActivity(),
                        "Actualizar",
                        70,
                        0,
                        Color.parseColor("#FF9502"),
                        new IMyButtonClickListener() {
                            @Override
                            public void onClickSwipe(final int pos) {

                                final DatabaseReference item = adapter.getRef(pos);
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                alertDialog.setTitle("Actualizar Orden");
                                //alertDialog.setMessage("Selecciona estado");

                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                final View view = inflater.inflate(R.layout.update_order_layout, null);
                                spinner = (MaterialSpinner) view.findViewById(R.id.statusSpinner);
                                spinner.setItems("Recibido", "Procesando", "Entregado");
                                alertDialog.setView(view);

                                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        /*
                                        dialogInterface.dismiss();
                                        adapter.getItem(pos).setStatus(String.valueOf(spinner.getSelectedIndex()));
                                        //item.setName(edtCategoryName.getText().toString());
                                        //databaseReference.child(key).setValue(item);
                                        databaseReference.child(adapter.getRef(menuItem.getOrder()).getKey()).setValue(adapter.getItem(pos));
                                        item.setValue(adapter.getItem(pos));
                                         */
                                    }//onClick
                                });

                                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }//onClick
                                });
                                alertDialog.show();

                                /*
                                final DatabaseReference item = adapter.getRef(pos);
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                alertDialog.setTitle("ACTUALIZAR CATEGORIA");
                                alertDialog.setMessage("¡Introduce los datos por favor!");

                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                View add_new_menu = inflater.inflate(R.layout.add_new_category, null);
                                edtCategoryName = add_new_menu.findViewById(R.id.edtCategoryName);
                                btnSelectCategory = add_new_menu.findViewById(R.id.btnSelectCategory);
                                btnUploadCategory = add_new_menu.findViewById(R.id.btnUploadCategory);

                                //Asignar nombre por default
                                edtCategoryName.setText(adapter.getItem(pos).getName());

                                btnSelectCategory.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        chooseImage();
                                    }//onClick
                                });

                                btnUploadCategory.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changeImage(adapter.getItem(pos));
                                    }//onClick
                                });

                                alertDialog.setView(add_new_menu);
                                alertDialog.setIcon(R.drawable.cart);

                                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        adapter.getItem(pos).setName(edtCategoryName.getText().toString());
                                        //item.setName(edtCategoryName.getText().toString());
                                        //databaseReference.child(key).setValue(item);
                                        item.setValue(adapter.getItem(pos));
                                    }//onClick
                                });

                                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }//onClick
                                });
                                alertDialog.show();
                                 */
                            }//onClickSwipe
                        }));
            }//instantiateMyButton
        };

        return view;
    }//onCreateView

    private void cargarOrdenes() {
        adapter = new FirebaseRecyclerAdapter<SolicitudModel, OrderViewHolder>(
                SolicitudModel.class,
                R.layout.order_item,
                OrderViewHolder.class,
                databaseReference
        ) {

            @Override
            protected void populateViewHolder(final OrderViewHolder orderViewHolder, final SolicitudModel solicitudModel, int i) {
                orderViewHolder.lblOrderItemName.setText(adapter.getRef(i).getKey());
                orderViewHolder.lblOrderItemStatus.setText(Common.convertCodeToStatus(solicitudModel.getStatus()));
                orderViewHolder.lblOrderItemAddress.setText(solicitudModel.getAddress());
                orderViewHolder.lblOrderItemPhone.setText(solicitudModel.getPhone());
                orderViewHolder.lblOrderItemDate.setText(solicitudModel.getDate());
                orderViewHolder.setiItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(getActivity(),"¡Order seleccionada!",Toast.LENGTH_LONG).show();
                        Intent trackingOrder = new Intent(getActivity(), TrackingOrder.class);
                        Common.currentSolicitudModel = solicitudModel;
                        startActivity(trackingOrder);
                    }//onClick
                });
            }//populateViewHolder
        };
        adapter.notifyDataSetChanged();
        rvOrden.setAdapter(adapter);
    }//loadOrdenes


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            deleteCategory(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }//else

        return super.onContextItemSelected(item);
    }//onContextItemSelected

    private void deleteCategory(String key, SolicitudModel item) {
        //databaseReference.child(key).removeValue();
        //Toast.makeText(getActivity(), "¡Elemento eliminado!", Toast.LENGTH_LONG).show();
        Toast.makeText(getActivity(), "¡No se puede eliminar la orden!", Toast.LENGTH_LONG).show();
    }//deleteCategory

    private void showUpdateDialog(final String key, final SolicitudModel item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Actualizar Orden");
        //alertDialog.setMessage("Selecciona estado");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);
        spinner = (MaterialSpinner) view.findViewById(R.id.statusSpinner);
        spinner.setItems("Enviada", "En Camino", "Finalizada");
        alertDialog.setView(view);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                databaseReference.child(key).setValue(item);
            }//onClick
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }//onClick
        });
        alertDialog.show();
    }//showUpdateDialog
}//StatusOrderFragment