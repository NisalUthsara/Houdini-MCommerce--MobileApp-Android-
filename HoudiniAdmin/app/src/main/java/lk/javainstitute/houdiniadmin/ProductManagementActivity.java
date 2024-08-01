package lk.javainstitute.houdiniadmin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import lk.javainstitute.houdiniadmin.model.Product;

public class ProductManagementActivity extends AppCompatActivity {

    public static final String TAG = ProductManagementActivity.class.getName();

    private ImageButton imageButton;
    private FirebaseStorage storage;
    private FirebaseFirestore fireStore;

    private Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        fireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        imageButton = findViewById(R.id.imageButtonProduct);

        //spinner setup

        Spinner spinner = findViewById(R.id.spinnerBrand);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
          ProductManagementActivity.this,
          R.array.brand_names,
           android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //spinner setup

        //image Btn
        findViewById(R.id.imageButtonProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher.launch(Intent.createChooser(intent,"Select an image"));

            }
        });

        //add new product
        findViewById(R.id.productAddBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editTextProductName = findViewById(R.id.editTextProductName);
                EditText editTextProductPrice = findViewById(R.id.editTextProuductPrice);
                EditText editTextProductDesc = findViewById(R.id.editTextProductDesc);
                EditText editTextProductQty = findViewById(R.id.editTextProductQty);

                String name = editTextProductName.getText().toString().trim();
                String priceStr = editTextProductPrice.getText().toString().trim();
                String desc = editTextProductDesc.getText().toString().trim();
                String qtyStr = editTextProductQty.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Please enter product name..!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(priceStr)) {
                    Toast.makeText(getApplicationContext(), "Please enter product price..!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(desc)) {
                    Toast.makeText(getApplicationContext(), "Please enter product Description..!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(qtyStr)) {
                    Toast.makeText(getApplicationContext(), "Please enter product quantity..!", Toast.LENGTH_SHORT).show();
                } else {

                    double price = Double.parseDouble(priceStr);
                    double qty = Double.parseDouble(qtyStr);

                    String selectedBrand = spinner.getSelectedItem().toString();

                    String imageId = UUID.randomUUID().toString();

                    Product product = new Product(name,price,desc,qty,selectedBrand,imageId);

                    ProgressDialog dialog = new ProgressDialog(ProductManagementActivity.this);
                    dialog.setMessage("Adding Product..!");
                    dialog.setCancelable(false);
                    dialog.show();

                    fireStore.collection("Product").add(product)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    if (imagePath != null){
                                        dialog.setMessage("Uploading image...!");

                                        StorageReference reference = storage.getReference("product-images")
                                                .child(imageId);

                                        reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                dialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                double progress = (100.0*snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                dialog.setMessage("Uploading : "+(int)progress+"%");
                                            }
                                        });
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });

                    editTextProductName.setText("");
                    editTextProductPrice.setText("");
                    editTextProductPrice.setText("");
                    editTextProductDesc.setText("");
                    editTextProductQty.setText("");
                }
            }

        });

        findViewById(R.id.productEditBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductManagementActivity.this,ProductEditActivity.class);
                startActivity(intent);
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        imagePath = result.getData().getData();

                        Picasso.get()
                                .load(imagePath)
                                .fit()
                                .centerCrop().into(imageButton);
                    }
                }
            }
    );

}