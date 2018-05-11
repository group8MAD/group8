package it.polito.mad.group8;

import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

public class BookHolder extends RecyclerView.ViewHolder {

    private final TextView title;


    public BookHolder(View itemView){
        super(itemView);
        title = itemView.findViewById(R.id.bookTitle);
    }

    public void setTitle(String title){
        this.title.setText(title);
    }
}
