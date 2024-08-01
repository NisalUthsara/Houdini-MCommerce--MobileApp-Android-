package lk.javainstitute.houdiniadmin.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.javainstitute.houdiniadmin.ProductDeleteDialog;
import lk.javainstitute.houdiniadmin.ProductUpdateActivity;
import lk.javainstitute.houdiniadmin.R;
import lk.javainstitute.houdiniadmin.model.Product;

public class productAdapter extends RecyclerView.Adapter<productAdapter.viewHolder> {

    private ArrayList<Product> products;
    private FirebaseStorage storage;
    private Context context;

    public productAdapter(ArrayList<Product> products, Context context) {
        this.products = products;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public productAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_product_edit, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull productAdapter.viewHolder holder, int position) {
        Product product = products.get(position);
        holder.textName.setText(product.getProductName());
        holder.textBrand.setText(product.getProductBrand());
        //holder.textDesc.setText(product.getProductDesc());
        holder.textPrice.setText(String.valueOf(product.getProductPrice()));
        holder.textQty.setText(String.valueOf(product.getProductQty()));

        holder.editProductDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.i(productAdapter.class.getName(),holder.textName.getText().toString());
                if (context instanceof FragmentActivity){
                    FragmentActivity fragmentActivity = (FragmentActivity) context;
                    ProductDeleteDialog dialog = new ProductDeleteDialog();

                    //Pass doc Id to the dialog
                    Bundle bundle = new Bundle();
                    bundle.putString("documentId",product.getDocumentId());
                    dialog.setArguments(bundle);

                    dialog.show(fragmentActivity.getSupportFragmentManager(),"Delete Dialog");
                }else {

                }

            }
        });

        holder.editProductUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductUpdate(product);
            }
        });

        storage.getReference("product-images/"+product.getProductImage()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .fit()
                                .centerCrop().into(holder.image);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{

        TextView textName, textBrand, textPrice, textDesc, textQty;
        ImageView image;
        Button editProductDeleteBtn,editProductUpdateBtn;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.editProductName);
            textBrand = itemView.findViewById(R.id.editProductBrand);
            textPrice = itemView.findViewById(R.id.editProductPrice);
            textDesc = itemView.findViewById(R.id.editProductDesc);
            textQty = itemView.findViewById(R.id.editProductQty);
            image = itemView.findViewById(R.id.editProductImage);

            editProductDeleteBtn = itemView.findViewById(R.id.editProductDeleteBtn);
            editProductUpdateBtn = itemView.findViewById(R.id.editProductUpdateBtn);
        }
    }

    private void showProductUpdate(Product product){
        Intent intent = new Intent(context, ProductUpdateActivity.class);
        intent.putExtra("documentId",product.getDocumentId());
        intent.putExtra("productName",product.getProductName());
        intent.putExtra("productDesc",product.getProductDesc());
        intent.putExtra("productBrand",product.getProductBrand());
        intent.putExtra("productPrice",String.valueOf(product.getProductPrice()));
        intent.putExtra("productQty",String.valueOf(product.getProductQty()));
        intent.putExtra("productImage",product.getProductImage());

        context.startActivity(intent);
    }
}
