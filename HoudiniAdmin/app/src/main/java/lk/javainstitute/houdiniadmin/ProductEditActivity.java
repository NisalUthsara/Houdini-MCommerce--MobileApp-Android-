package lk.javainstitute.houdiniadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.javainstitute.houdiniadmin.adapter.productAdapter;
import lk.javainstitute.houdiniadmin.model.Product;

public class ProductEditActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        products = new ArrayList<>();

        RecyclerView productEditView = findViewById(R.id.editProductView);

        productAdapter productAdapter = new productAdapter(products,ProductEditActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        productEditView.setLayoutManager(linearLayoutManager);
        productEditView.setAdapter(productAdapter);

//        firestore.collection("Product").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        products.clear();
//                        for (QueryDocumentSnapshot snapshot: task.getResult()){
//                            Product product = snapshot.toObject(Product.class);
//                            products.add(product);
//                        }
//                        productAdapter.notifyDataSetChanged();
//                    }
//                });

        firestore.collection("Product").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
               // products.clear();
                for (DocumentChange change: value.getDocumentChanges()){
                    Product product = change.getDocument().toObject(Product.class);
                    product.setDocumentId(change.getDocument().getId());
                    switch (change.getType()){
                        case ADDED:
                            products.add(product);
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            products.remove(product);
                            break;
                    }
                }
                productAdapter.notifyDataSetChanged();
            }
        });

    }
}