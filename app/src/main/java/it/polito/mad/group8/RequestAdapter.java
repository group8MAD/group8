package it.polito.mad.group8;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder>{

    private List<Request> requests;
    private Context context;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public RequestAdapter(List<Request> requests, Context context) {
        this.requests = requests;
        this.context = context;
    }

    @NonNull
    @Override
    public RequestAdapter.RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_request, parent, false);
        return new RequestAdapter.RequestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.RequestHolder holder, int position) {
        Request request = requests.get(position);


        //setting
        FirebaseDatabase.getInstance().getReference("users")
                .child(request.getRequesterUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        holder.email.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
                        String contact = dataSnapshot.child("name").getValue().toString() + " ("+ request.getRequesterNickname()+") ";
                        holder.contact.setText(contact);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        holder.bookTitle.setText(request.getBookTitle());

        String periodHTML = context.getString(R.string.period)+": "+context.getString(R.string.from)+" <b>"
                +formatter.format(Long.parseLong(request.getStartDate()))+"</b> "
                + context.getString(R.string.to)+" <b>"+formatter.format(Long.parseLong(request.getEndDate()))+"</b>";
        holder.period.setText(Html.fromHtml(periodHTML));
        if (!request.getRequesterImageUri().isEmpty())
            Picasso.get().load(request.getRequesterImageUri()).into(holder.thumbnail);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleRequestShow.class);
                intent.putExtra("requesterUid", request.getRequesterUid());
                intent.putExtra("requesterNickname", request.getRequesterNickname());
                intent.putExtra("requesterImageUri", request.getRequesterImageUri());
                intent.putExtra("bookIsbn", request.getBookIsbn());
                intent.putExtra("bookTitle", request.getBookTitle());
                intent.putExtra("startDate", request.getStartDate());
                intent.putExtra("endDate", request.getEndDate());
                intent.putExtra("city", request.getCity());
                intent.putExtra("province", request.getProvince());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class RequestHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail;
        public ConstraintLayout layout;
        public TextView bookTitle;
        public TextView contact;
        public TextView period;
        public TextView email;



        public RequestHolder(View itemView){
            super(itemView);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            thumbnail = itemView.findViewById(R.id.image);
            contact = itemView.findViewById(R.id.contact);
            period = itemView.findViewById(R.id.period);
            layout = itemView.findViewById(R.id.layout);
            email = itemView.findViewById(R.id.email);
        }
    }

}
