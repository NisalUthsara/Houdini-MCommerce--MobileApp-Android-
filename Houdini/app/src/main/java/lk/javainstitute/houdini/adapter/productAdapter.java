package lk.javainstitute.houdini.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.javainstitute.houdini.R;
import lk.javainstitute.houdini.SingleProductActivity;
import lk.javainstitute.houdini.model.Product;

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
        View view = inflater.inflate(R.layout.layout_product_row, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull productAdapter.viewHolder holder, int position) {

        Product product = products.get(position);
        holder.textName.setText(product.getProductName());
        holder.textBrand.setText(product.getProductBrand());

        String priceWithCurrency = "Rs. " + String.valueOf(product.getProductPrice());

        holder.textPrice.setText(priceWithCurrency);
        //holder.textPrice.setText(String.valueOf(product.getProductPrice()));

        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSingleProduct(product);
            }
        });


        storage.getReference("product-images/"+product.getProductImage())
                .getDownloadUrl()
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

        TextView textName, textBrand, textPrice;
        ImageView image;
        Button addToCartButton;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.cardProductName);
            textBrand = itemView.findViewById(R.id.cardProductBrand);
            textPrice = itemView.findViewById(R.id.cardProductPrice);
            image = itemView.findViewById(R.id.cardProductImage);

            addToCartButton = itemView.findViewById(R.id.productCardId);
        }
    }

    private void showSingleProduct(Product product){
        Intent intent = new Intent(context,SingleProductActivity.class);
        intent.putExtra("productName",product.getProductName());
        intent.putExtra("productDesc",product.getProductDesc());
        intent.putExtra("productBrand",product.getProductBrand());
        intent.putExtra("productPrice", String.valueOf(product.getProductPrice()));
        intent.putExtra("productQty", String.valueOf(product.getProductQty()));
        intent.putExtra("productImage",product.getProductImage());

        //intent.setData(storage.getReference("product-images/"+product.getProductImage()).getDownloadUrl())

        context.startActivity(intent);
    }
}
