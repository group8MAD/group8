package it.polito.mad.group8;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ShowReviewsActivityAdapter extends RecyclerView.Adapter<ShowReviewsActivityAdapter.Holder>{

    private List<Review> reviews;
    private Context context;

    public ShowReviewsActivityAdapter(List<Review> transactions, Context context) {
        this.reviews = transactions;
        this.context = context;
    }

    @NonNull
    @Override
    public ShowReviewsActivityAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review, parent, false);
        return new ShowReviewsActivityAdapter.Holder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull ShowReviewsActivityAdapter.Holder holder, int position) {
        Review review = reviews.get(position);

        holder.titleTV.setText(review.getTitle());
        holder.ratingbar.setRating(review.getRating());
        holder.commentTV.setText(review.getComment());
        holder.nicknameTV.setText(review.getReviewerNickname());
        if (!review.getReviewerImageUri().isEmpty()){
            Picasso.get().load(review.getReviewerImageUri()).into(holder.imageIV);
        }

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        public ImageView imageIV;
        public TextView nicknameTV;
        public TextView titleTV;
        public TextView commentTV;
        public RatingBar ratingbar;

        public Holder(View itemView){
            super(itemView);
            imageIV = itemView.findViewById(R.id.thumbnail);
            nicknameTV = itemView.findViewById(R.id.nickname);
            titleTV = itemView.findViewById(R.id.title);
            commentTV = itemView.findViewById(R.id.comment);
            ratingbar = itemView.findViewById(R.id.ratingBar);
        }
    }

}
