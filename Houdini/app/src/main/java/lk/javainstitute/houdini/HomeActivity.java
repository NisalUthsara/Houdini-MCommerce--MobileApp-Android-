package lk.javainstitute.houdini;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.javainstitute.houdini.adapter.productAdapter;
import lk.javainstitute.houdini.model.Product;
import lk.javainstitute.houdini.model.User;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener{

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private ImageView profilePic;
    private static final int SIGN_IN_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(HomeActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();

                firestore = FirebaseFirestore.getInstance();
                storage = FirebaseStorage.getInstance();

                profilePic = findViewById(R.id.profilePic);

                //set profile detail for side nav header
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    TextView textViewUserName = findViewById(R.id.profileUserName);
                    textViewUserName.setText(user.getDisplayName());
                    TextView textViewUserEmail = findViewById(R.id.profileUserEmail);
                    textViewUserEmail.setText(user.getEmail());

                    firestore.collection("User")
                            .whereEqualTo("userEmail",user.getEmail())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (DocumentSnapshot document : task.getResult()) {
                                            User user = document.toObject(User.class);
                                            if (user.getUserImage() != null && !user.getUserImage().isEmpty()) {
                                                // Load and display the profile image using Picasso
                                                StorageReference reference = storage.getReference("user-images").child(user.getUserImage());
                                                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                                    Picasso.get()
                                                            .load(uri)
                                                            .fit()
                                                            .centerCrop()
                                                            .into(profilePic);
                                                });
                                            }
                                        }
                                    }
                                }
                            });

                }

            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        //Home Frag
        getSupportFragmentManager().beginTransaction()
                .add(R.id.FragContainer, HomeFragment.class, null)
                .commit();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.FragContainer);
                if (currentFrag instanceof HomeFragment){
                    getSupportFragmentManager().beginTransaction().remove(currentFrag).commit();
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (item.getItemId() == R.id.sideNavHome){
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.FragContainer, HomeFragment.class, null)
//                    .commit();
            transaction.replace(R.id.FragContainer, HomeFragment.class, null );
            drawerLayout.close();
        }else if (item.getItemId() == R.id.sideNavProduct){
            transaction.replace(R.id.FragContainer, ProductFragment.class, null );
            drawerLayout.close();
        } else if (item.getItemId() == R.id.sideNavCart) {
            if (user != null){
                transaction.replace(R.id.FragContainer, CartFragment.class,null);
                drawerLayout.close();
            }else {
                Toast.makeText(this,"You need to login first..!",Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.sideNavWishlist) {
            if (user != null){
                transaction.replace(R.id.FragContainer, WishlistFragment.class,null);
                drawerLayout.close();
            }else {
                Toast.makeText(this,"You need to login first..!",Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.sideNavLogin) {
            if (user != null){
                Toast.makeText(this,"You have already logged in.",Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(this, SignInActivity.class);
                startActivityForResult(intent,SIGN_IN_REQUEST_CODE);

            }

        } else if (item.getItemId() == R.id.sideNavLogout) {
            if (user != null){
                LogOutDialog dialog = new LogOutDialog();
                dialog.show(getSupportFragmentManager(),"Dialog");
            }else {
                Toast.makeText(this,"You are not logged in yet..!",Toast.LENGTH_SHORT).show();
            }

        }

        transaction.commit();
        return true;//click un item ek gnna
    }

}