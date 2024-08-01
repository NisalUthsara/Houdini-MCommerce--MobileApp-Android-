package lk.javainstitute.houdini;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.ramotion.circlemenu.CircleMenuView;

import java.util.ArrayList;

import lk.javainstitute.houdini.adapter.productAdapter;
import lk.javainstitute.houdini.model.Product;


public class HomeFragment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private ArrayList<Product> products;
    private FirebaseUser user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        ImageSlider imageSlider = fragment.findViewById(R.id.imageSliderBanner);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner3, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);


        //card set to the slider
        products = new ArrayList<>();
        RecyclerView cardSliderView = fragment.findViewById(R.id.homeCardSlider);

        productAdapter productAdapter = new productAdapter(products, fragment.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        cardSliderView.setLayoutManager(linearLayoutManager);
        cardSliderView.setAdapter(productAdapter);

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



        //circle menu process
        CircleMenuView circleMenuView = fragment.findViewById(R.id.circleMenu);
        circleMenuView.setEventListener(new CircleMenuView.EventListener(){
            @Override
            public void onButtonClickAnimationStart(@NonNull CircleMenuView view, int buttonIndex) {
                super.onButtonClickAnimationStart(view, buttonIndex);
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (buttonIndex == 3){
                    System.out.println("User");
                    if (user != null){
                        Intent intent = new Intent(fragment.getContext(), UserProfileActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(fragment.getContext(),"Please Log In",Toast.LENGTH_SHORT).show();
                    }
                } else if (buttonIndex == 1) {
                    System.out.println("Settings");
                }
            }
        });

    }
}