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
import com.squareup.picasso.Picasso;

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

        final String options[] = new String[] {
                getContext().getString(R.string.asNew),
                getContext().getString(R.string.veryGood),
                getContext().getString(R.string.good),
                getContext().getString(R.string.fair),
                getContext().getString(R.string.poor)
        };
        String ownerUidAndIsbn =(String) getItem(position);
        String[] array =  ownerUidAndIsbn.split("-");
        String ownerUid = array[0];
        String bookIsbn = array[1];

        TextView nicknameTV = customView.findViewById(R.id.nickname);
        TextView bookLocation = customView.findViewById(R.id.bookLocation);
        ImageView image = customView.findViewById(R.id.image);

        FirebaseDatabase.getInstance().getReference("users/"+ownerUid)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.child("nickname").exists()){
                                                    nicknameTV.setText(Objects.requireNonNull(dataSnapshot.child("nickname").getValue()).toString());
                                                }else{
                                                    nicknameTV.setText("Nickname not set");
                                                }

                                                if(dataSnapshot.child("imageUri").exists() && !dataSnapshot.child("imageUri").getValue().toString().isEmpty() ){
                                                    Picasso.get().load(dataSnapshot.child("imageUri").getValue().toString()).into(image);
                                                }

                                                bookLocation.setText(dataSnapshot.child("city").getValue().toString());
                                                if (!dataSnapshot.child("province").getValue().toString().isEmpty()){
                                                    bookLocation.append(" ( " + dataSnapshot.child("province").getValue().toString() + " )\t- ");
                                                }
                                                bookLocation.append(getContext().getString(R.string.conditions)
                                                                    +" "
                                                                    +options[Integer.parseInt(dataSnapshot.child("books").child(bookIsbn).child("condition").getValue().toString())]);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


        return customView;
    }
}
