package com.example.flowersmarket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowersmarket.models.Item;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class FlowersAdapter extends FirestoreRecyclerAdapter<Item, FlowersAdapter.ViewHolder> {
    Context context;
    Activity activity;
    FirebaseFirestore firestore;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FlowersAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Item> options, Activity activity, Context context) {
        super(options);
        this.activity = activity;
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull FlowersAdapter.ViewHolder holder, int position, @NonNull @NotNull Item item) {

        DocumentSnapshot itemDocument = getSnapshots().getSnapshot(holder.getAdapterPosition());
        String id = itemDocument.getId();
        getValues( itemDocument, id, holder, item);

        holder.btnModificar.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ModifyActivity.class);
            intent.putExtra("itemId", id);
            activity.startActivity(intent);
        });

        //--- Delete product
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("flowers").document(id).delete().addOnSuccessListener(unused ->
                    Toast
                        .makeText(activity, "Se elimino el producto", Toast.LENGTH_SHORT)
                        .show())
                        .addOnFailureListener(e -> Toast
                                .makeText(activity, "No se pudo eliminar el contacto", Toast.LENGTH_SHORT)
                                .show());
            }
        });
    }

    private void getValues(DocumentSnapshot itemDocument, String itemId, FlowersAdapter.ViewHolder holder, Item item) {
        firestore.collection("flowers").document(itemId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String titulo = documentSnapshot.getString("titulo");
                    String descripcion = documentSnapshot.getString("descripcion");
                    long precio = documentSnapshot.getLong("precio");
                    String imgUrl = documentSnapshot.getString("url");

                    holder.lblTitulo.setText(item.getTitulo());
                    holder.lblDescripcion.setText(item.getDescripcion());
                    holder.lblPrecio.setText(String.valueOf(item.getPrecio()));
                    Picasso.with(context).load(imgUrl).into(holder.imageView);
                }
            }
        });
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item, parent, false);
        return new ViewHolder(view);
    }


    //-------- mapping the components ---------
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView lblTitulo;
        TextView lblDescripcion;
        TextView lblPrecio;
        Button btnModificar;
        Button btnEliminar;
        ImageView imageView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            lblTitulo = itemView.findViewById(R.id.lblTitulo);
            lblDescripcion = itemView.findViewById(R.id.lblDescripcion);
            lblPrecio = itemView.findViewById((R.id.lblPrecio));
            btnModificar = itemView.findViewById(R.id.btnModificar);
            btnEliminar = itemView.findViewById(R.id.btnBorrar);
            imageView = itemView.findViewById(R.id.imgProducto);
        }
    }
}
