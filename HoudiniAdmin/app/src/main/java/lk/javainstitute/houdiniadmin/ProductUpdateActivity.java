package lk.javainstitute.houdiniadmin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductUpdateActivity extends AppCompatActivity {

    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private String documentId;
    private Uri newImagePath;
    private ImageButton productUpImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_update);

        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        EditText productUpProductName = findViewById(R.id.productUpProductName);
        EditText productUpProductPrice = findViewById(R.id.productUpProductPrice);
        EditText productUpProductDesc = findViewById(R.id.productUpProductDesc);
        EditText productUpProductQty = findViewById(R.id.productUpProductQty);

        Intent intent = getIntent();
        if (intent != null){
            documentId = intent.getStringExtra("documentId");
            String productName = intent.getStringExtra("productName");
            String productDesc = intent.getStringExtra("productDesc");
            String productBrand = intent.getStringExtra("productBrand");
            String productPrice = intent.getStringExtra("productPrice");
            String productQty = intent.getStringExtra("productQty");
            String productImage = intent.getStringExtra("productImage");


            productUpImageBtn = findViewById(R.id.productUpImageBtn);

            productUpProductName.setText(productName);
            productUpProductPrice.setText(productPrice);
            productUpProductDesc.setText(productDesc);
            productUpProductQty.setText(productQty);

            storage.getReference("product-images/"+productImage)
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .fit()
                                    .centerCrop()
                                    .into(productUpImageBtn);
                        }
                    });

            productUpImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent();
                    intent1.setType("image/*");
                    intent1.setAction(Intent.ACTION_GET_CONTENT);

                    activityResultLauncher.launch(Intent.createChooser(intent1,"Select new image"));
                }
            });

            // Set onClickListener for the Save button
            findViewById(R.id.productUpSaveBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String updatedProductName = productUpProductName.getText().toString();
                    String updatedProductPrice = productUpProductPrice.getText().toString();
                    String updatedProductDesc = productUpProductDesc.getText().toString();
                    String updatedProductQty = productUpProductQty.getText().toString();

                    // Update product details in Firestore
                    updatedProductDetails(updatedProductName,updatedProductPrice,updatedProductDesc,updatedProductQty);

                    if (newImagePath != null){
                        uploadNewImageAndSetProductImage(newImagePath);
                    }

                    finish();

                }
            });

        }

    }

    private void updatedProductDetails(String productName, String productPrice, String productDesc, String productQty){
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("productName",productName);
        updateMap.put("productPrice",Double.parseDouble(productPrice));
        updateMap.put("productDesc",productDesc);
        updateMap.put("productQty",Double.parseDouble(productQty));

        firestore.collection("Product").document(documentId)
                .update(updateMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Product details updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to update product details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadNewImageAndSetProductImage(Uri newImageUri){
        String newImageId = UUID.randomUUID().toString();
        StorageReference newImageReference = storage.getReference("product-images").child(newImageId);

        ProgressDialog dialog = new ProgressDialog(ProductUpdateActivity.this);
        dialog.setMessage("Adding Product..!");
        dialog.setCancelable(false);
        dialog.show();

        newImageReference.putFile(newImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        updateProductImageInFirestore(newImageId);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0*snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        dialog.setMessage("Uploading : "+(int)progress+"%");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to upload new image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProductImageInFirestore(String newImageId){
        firestore.collection("Product").document(documentId)
                .update("productImage",newImageId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Product image updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to update product image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK){
                        newImagePath = result.getData().getData();

                        Picasso.get()
                                .load(newImagePath)
                                .fit()
                                .centerCrop().into(productUpImageBtn);
                    }
                }
            }
    );
}