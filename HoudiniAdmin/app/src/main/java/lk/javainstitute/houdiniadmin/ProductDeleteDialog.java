package lk.javainstitute.houdiniadmin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class ProductDeleteDialog extends DialogFragment {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        firestore = FirebaseFirestore.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String documentID = getArguments().getString("documentId");
                        Log.i(ProductDeleteDialog.class.getName(),"Document ID: " + documentID);

                        if (documentID != null){
                            deleteProduct(documentID);
                        }else {
                            Log.i(ProductDeleteDialog.class.getName(), "Document ID is null");
                        }

                        Toast.makeText(getContext(),"Deleted",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }

    private void deleteProduct(String documentId){
        firestore.collection("Product").document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(ProductDeleteDialog.class.getName(), "Document deleted successfully");
                    }
                });
    }
}
