package lk.javainstitute.houdini;

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
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import az.zero.az_edit_text.AZEditText;
import lk.javainstitute.houdini.model.User;

public class UserProfileActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        imageButton = findViewById(R.id.userProfImgBtn);

        //imageBtn
        findViewById(R.id.userProfImgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(Intent.createChooser(intent,"Select profile picture"));
            }
        });

        AZEditText username = findViewById(R.id.userProfUserName);
        AZEditText useremail = findViewById(R.id.userProfUserEmail);
        AZEditText userAddress = findViewById(R.id.userProfUserAddress);
        AZEditText userContact = findViewById(R.id.userProfUserContact);

// Set user email
        useremail.setText(user.getEmail().toString());

// Retrieve user data from Firestore
        firestore.collection("User")
                .whereEqualTo("userEmail", user.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            if (user != null) {
                                // Set username if not null or empty
                                if (user.getUserName() != null && !user.getUserName().isEmpty()) {
                                    username.setText(user.getUserName());
                                }

                                // Set user address if not null or empty
                                if (user.getUserAddress() != null && !user.getUserAddress().isEmpty()) {
                                    userAddress.setText(user.getUserAddress());
                                }

                                // Set user contact number if not null or empty
                                if (user.getUserContactNo() != null && !user.getUserContactNo().isEmpty()) {
                                    userContact.setText(user.getUserContactNo());
                                }

                                // Check if user has a profile image
                                if (user.getUserImage() != null && !user.getUserImage().isEmpty()) {
                                    // Load and display the profile image using Picasso
                                    StorageReference reference = storage.getReference("user-images").child(user.getUserImage());
                                    reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                        Picasso.get()
                                                .load(uri)
                                                .fit()
                                                .centerCrop()
                                                .into(imageButton);
                                    });
                                }
                            }
                        }
                    }
                });


//        AZEditText username = findViewById(R.id.userProfUserName);
//        AZEditText useremail = findViewById(R.id.userProfUserEmail);
//
//        username.setText(user.getDisplayName().toString());
//        useremail.setText(user.getEmail().toString());

        // Check if the user has a profile image in Firestore
//        firestore.collection("User")
//                .whereEqualTo("userEmail",user.getEmail())
//                        .get()
//                                .addOnCompleteListener(task -> {
//                                    if (task.isSuccessful()){
//                                        for (DocumentSnapshot document : task.getResult()){
//                                            User user1 = document.toObject(User.class);
//                                            if (user1.getUserImage() != null && !user1.getUserImage().isEmpty()){
//                                                StorageReference reference = storage.getReference("user-images")
//                                                        .child(user1.getUserImage());
//                                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                                    @Override
//                                                    public void onSuccess(Uri uri) {
//                                                        Picasso.get()
//                                                                .load(uri)
//                                                                .fit()
//                                                                .centerCrop()
//                                                                .into(imageButton);
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    }
//                                });


        //Save user Details
        findViewById(R.id.userProfSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AZEditText userAddress = findViewById(R.id.userProfUserAddress);
                AZEditText userContact = findViewById(R.id.userProfUserContact);

                String name = username.getText().toString();
                String email = useremail.getText().toString();
                String address = userAddress.getText().toString();
                String contactNo = userContact.getText().toString();

                String imageId = UUID.randomUUID().toString();

                User user1 = new User(name,email,address,contactNo,imageId);

                ProgressDialog dialog = new ProgressDialog(UserProfileActivity.this);
                dialog.setMessage("Saving User..!");
                dialog.setCancelable(false);
                dialog.show();

                firestore.collection("User").add(user1)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                if (imageId != null){
                                    dialog.setMessage("Uploading Profile Picture..!");

                                    StorageReference reference = storage.getReference("user-images").child(imageId);

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
                                            double progress = (100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
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
                                .centerCrop()
                                .into(imageButton);
                    }
                }
            }
    );
}