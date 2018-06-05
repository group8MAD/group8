package it.polito.mad.group8;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder>{

    private List<Request> requests;
    private Context context;

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

        holder.title.setText(request.getRequesterNickname());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleRequestShow.class);
                intent.putExtra("requesterUid", request.getRequesterUid());
                intent.putExtra("requesterNickname", request.getRequesterNickname());
                intent.putExtra("bookIsbn", request.getBookIsbn());
                intent.putExtra("bookTitle", request.getBookTitle());
                intent.putExtra("startDate", request.getStartDate());
                intent.putExtra("endDate", request.getEndDate());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class RequestHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView thumbnail;
        public ConstraintLayout layout;


        public RequestHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.nickname);
            thumbnail = itemView.findViewById(R.id.image);
            layout = itemView.findViewById(R.id.layout);
        }
    }

}
