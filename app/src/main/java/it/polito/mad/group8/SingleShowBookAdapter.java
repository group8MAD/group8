package it.polito.mad.group8;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SingleShowBookAdapter extends ArrayAdapter{
    public SingleShowBookAdapter(@NonNull Context context, String[] resource) {
        super(context, R.layout.single_show_book_row, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("ViewHolder") View customView = inflater.inflate(R.layout.single_show_book_row, parent, false);

        String ownerUid = (String) getItem(position);
        TextView nicknameTV = customView.findViewById(R.id.nickname);
        ImageView image = customView.findViewById(R.id.image);

        FirebaseDatabase.getInstance().getReference("users/"+ownerUid+"/nickname")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    nicknameTV.setText(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                                                }else{
                                                    nicknameTV.setText("Nickname not set");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


        return customView;
    }
}
