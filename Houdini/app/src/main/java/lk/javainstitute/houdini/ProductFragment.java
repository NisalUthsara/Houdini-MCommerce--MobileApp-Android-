package lk.javainstitute.houdini;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.javainstitute.houdini.adapter.productAdapter;
import lk.javainstitute.houdini.model.Product;

public class ProductFragment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private ArrayList<Product> products;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        products = new ArrayList<>();

        RecyclerView productView = fragment.findViewById(R.id.productView);

        productAdapter productAdapter = new productAdapter(products, fragment.getContext());
        GridLayoutManager LayoutManager = new GridLayoutManager(getContext(),2);
        productView.setLayoutManager(LayoutManager);
        productView.setAdapter(productAdapter);

        firestore.collection("Product").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        products.clear();
                        for (QueryDocumentSnapshot snapshot: task.getResult()){
                            Product product = snapshot.toObject(Product.class);
                            products.add(product);
                        }

                        productAdapter.notifyDataSetChanged();

                    }
                });

    }
}