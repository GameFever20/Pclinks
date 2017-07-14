package utils;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by bunny on 13/07/17.
 */

public class Firebasehandler {
    private FirebaseDatabase mDatabase;


    public Firebasehandler() {
        //hello he
        mDatabase = FirebaseDatabase.getInstance();

    }


    public void uploadCustomMessage(CustomMessage customMessage , final OnCustomMessageListener onCustomMessageListener ){
        DatabaseReference databaseReference = mDatabase.getReference().child("message/"+customMessage.getCustomMessageUserUID());

        databaseReference.push().setValue(customMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                onCustomMessageListener.onCustomMessageUpload(true);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCustomMessageListener.onCustomMessageUpload(false);
            }
        });


    }

    public void downloadCustomMessageList(String userUID , int limitTo, final OnCustomMessageListener onCustomMessageListener){

        DatabaseReference myRef = mDatabase.getReference().child("message/"+userUID);

        Query myref2 = myRef.limitToLast(limitTo);
        myref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<CustomMessage> customMessageArrayList =new ArrayList<CustomMessage>();


                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    customMessageArrayList.add(snapshot.getValue(CustomMessage.class));

                }
                onCustomMessageListener.onCustomMessageListDownLoad(customMessageArrayList ,true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onCustomMessageListener.onCustomMessageListDownLoad(null ,false);

            }
        });

    }




    public interface OnCustomMessageListener{

        public void onCustomMessageUpload(boolean isSuccessful);
        public void onCustomMessageListDownLoad(ArrayList<CustomMessage> messageArrayList ,boolean isSuccessful);
        public void onCustomMessageDownLoad(CustomMessage customMessage ,boolean isSuccessful);

    }


}
