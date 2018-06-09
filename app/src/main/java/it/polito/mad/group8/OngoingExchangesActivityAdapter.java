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

public class OngoingExchangesActivityAdapter extends RecyclerView.Adapter<OngoingExchangesActivityAdapter.Holder>{

    private List<OngoingTransaction> transactions;
    private Context context;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public OngoingExchangesActivityAdapter(List<OngoingTransaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
    }

    @NonNull
    @Override
    public OngoingExchangesActivityAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ongoing_transaction, parent, false);
        return new OngoingExchangesActivityAdapter.Holder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull OngoingExchangesActivityAdapter.Holder holder, int position) {
        OngoingTransaction transaction = transactions.get(position);

        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        calendarStart.setTimeInMillis(Long.parseLong(transaction.getStartDate()));
        calendarEnd.setTimeInMillis(Long.parseLong(transaction.getEndDate()));

        String bookHTML = context.getString(R.string.book)+": <b>"+transaction.getBookTitle()+"</b>";
        holder.book.setText(Html.fromHtml(bookHTML));
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(transaction.getRequesterUid())) {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(transaction.getBookOwnerUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String contactHTML = context.getString(R.string.contactUser)+": <b>"+dataSnapshot.child("name").getValue().toString()+" ( "+dataSnapshot.child("nickname").getValue().toString()+" ) "+"</b>";
                            holder.contact.setText(Html.fromHtml(contactHTML));
                            holder.email.setText(dataSnapshot.child("email").getValue().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            holder.borrow.setText(context.getString(R.string.loaned));
            holder.borrow.setTextColor(Color.GREEN);
        }
        else {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(transaction.getRequesterUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String contactHTML = context.getString(R.string.contactUser)+": <b>"+dataSnapshot.child("name").getValue().toString()+" ( "+dataSnapshot.child("nickname").getValue().toString()+" ) "+"</b>";
                            holder.contact.setText(Html.fromHtml(contactHTML));
                            holder.email.setText("Email: "+dataSnapshot.child("email").getValue().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            holder.borrow.setText(context.getString(R.string.lent));
            holder.borrow.setTextColor(Color.RED);
        }
        String periodHTML = context.getString(R.string.period)+": "+context.getString(R.string.from)+" <b>"+formatter.format(Long.parseLong(transaction.getStartDate()))+"</b> "
                + context.getString(R.string.to)+" <b>"+formatter.format(Long.parseLong(transaction.getEndDate()))+"</b>";
        holder.period.setText(Html.fromHtml(periodHTML));


        if (calendarStart.get(Calendar.YEAR) > currentDate.get(Calendar.YEAR) || calendarStart.get(Calendar.DAY_OF_YEAR) >= currentDate.get(Calendar.DAY_OF_YEAR)){
            String statusHYML = context.getString(R.string.notStartedExchange);

        }else if ( (currentDate.get(Calendar.DAY_OF_YEAR) <= calendarEnd.get(Calendar.DAY_OF_YEAR) || currentDate.get(Calendar.YEAR) < calendarEnd.get(Calendar.YEAR)) &&
                   (currentDate.get(Calendar.DAY_OF_YEAR) >= calendarStart.get(Calendar.DAY_OF_YEAR) || currentDate.get(Calendar.YEAR) > calendarStart.get(Calendar.YEAR))){
            String statusHYML = context.getString(R.string.ongoingExchange);

        }else{

            holder.rate.setVisibility(View.VISIBLE);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PublicShowProfile.class);
                    intent.putExtra("rate", "yes");
                    if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(transaction.getBookOwnerUid())){
                        intent.putExtra("contactUid", transaction.getRequesterUid());
                        intent.putExtra("currentUserUid", transaction.getBookOwnerUid());
                    }else{
                        intent.putExtra("currentUserUid", transaction.getRequesterUid());
                        intent.putExtra("contactUid", transaction.getBookOwnerUid());
                    }
                    context.startActivity(intent);
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView book;
        public TextView contact;
        public TextView borrow;
        public TextView period;
        public TextView rate;
        public TextView email;


        public Holder(View itemView){
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewID);
            book = itemView.findViewById(R.id.book);
            contact = itemView.findViewById(R.id.contact);
            borrow = itemView.findViewById(R.id.borrow);
            period = itemView.findViewById(R.id.period);
            rate = itemView.findViewById(R.id.rate);
            email = itemView.findViewById(R.id.email);
        }
    }

}
