package lk.javainstitute.houdini;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mcdev.quantitizerlibrary.HorizontalQuantitizer;
import com.mcdev.quantitizerlibrary.QuantitizerListener;
import com.squareup.picasso.Picasso;

public class SingleProductActivity extends AppCompatActivity {

    private static final String TAG = SingleProductActivity.class.getName();
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product);

        storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        if (intent != null){
            String productName = intent.getStringExtra("productName");
            String productDesc = intent.getStringExtra("productDesc");
            String productBrand = intent.getStringExtra("productBrand");
            String productPrice = intent.getStringExtra("productPrice");
            String productQty = intent.getStringExtra("productQty");
            String productImage = intent.getStringExtra("productImage");

            TextView singleCardProductName = findViewById(R.id.singleCardProductName);
            TextView singleCardProductDesc = findViewById(R.id.singleCardProductDesc);
            TextView singleCardProductPrice = findViewById(R.id.singleCardProductPrice);
            TextView singleCardProductBrand = findViewById(R.id.singleCardProductBrand);

            ImageView singleCardImageView = findViewById(R.id.singleCardImageView);

            singleCardProductName.setText(productName);
            singleCardProductDesc.setText(productDesc);

            String priceWithCurruncy = "Rs. "+String.valueOf(productPrice);
            singleCardProductPrice.setText(priceWithCurruncy);

            singleCardProductBrand.setText(productBrand);

            storage.getReference("product-images/"+productImage)
                    .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get()
                                            .load(uri)
                                            .fit()
                                            .centerCrop()
                                            .into(singleCardImageView);
                                }
                            });



            HorizontalQuantitizer hq = findViewById(R.id.h_q);
            hq.setReadOnly(true);
            hq.setValue(1);
            hq.setQuantitizerListener(new QuantitizerListener() {
                @Override
                public void onIncrease() {
                    String productQty = intent.getStringExtra("productQty");
                    int productQtyD = (int) Double.parseDouble(productQty);
                    if (hq.getValue()>productQtyD){
                        hq.setValue(productQtyD);
                        Toast.makeText(getApplicationContext(),"Cannot increase more..!",Toast.LENGTH_SHORT).show();
                    }
                   // Log.i(TAG,"VALUE INCREASE : "+hq.getValue());
                }

                @Override
                public void onDecrease() {
                    if (hq.getValue()<1){
                        hq.setValue(1);
                    }
                   // Log.i(TAG,"VALUE DECREASE : "+hq.getValue());
                }

                @Override
                public void onValueChanged(int i) {
                    String productPrice = intent.getStringExtra("productPrice");
                    Double productPriceD = Double.parseDouble(productPrice)*hq.getValue();
                    String priceWithCurruncy = "Rs. "+String.valueOf(productPriceD);
                    singleCardProductPrice.setText(priceWithCurruncy);

                }
            });

        }
    }
}