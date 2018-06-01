package it.polito.mad.group8

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat

class ChatListAdaptor(private val context: Context, private val chatList: MutableList<Chat>) : RecyclerView.Adapter<ChatListAdaptor.ChatHolder>() {
    val formatter = SimpleDateFormat("kk:mm MM/dd/yy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat, parent, false)
        return ChatHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListAdaptor.ChatHolder, position: Int) {
        val chat = chatList[position]


        holder.contactNickname.text = chat.contactNickname
        holder.lastMessage.text =  formatter.format(chat.lastMessage)
        if (chat.notRead.toString().toInt() == 0){
            holder.notRead.text = " "
            holder.notRead.setBackgroundColor(Color.WHITE)
        }else if (chat.notRead.toString().toInt() < 100){
            holder.notRead.text = chat.notRead.toString()
            holder.notRead.setBackgroundResource(R.drawable.yellow_circle)
        }else{
            holder.notRead.text = "99+"
            holder.notRead.setBackgroundResource(R.drawable.yellow_circle)
        }
        holder.cardView.setOnClickListener {
            val intent = Intent(context, ChatRoom::class.java)
            intent.putExtra("chatRoomName", chat.chatName)
            intent.putExtra("contactUid", chat.contactUid)
            intent.putExtra("currentUserUid", FirebaseAuth.getInstance().currentUser?.uid)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class ChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var notRead: TextView
        var contactNickname: TextView
        var lastMessage: TextView
        var thumbnail: ImageView
        var cardView: CardView


        init {
            notRead = itemView.findViewById(R.id.notRead)
            contactNickname = itemView.findViewById(R.id.textView2)
            lastMessage = itemView.findViewById(R.id.lastMessage)
            thumbnail = itemView.findViewById(R.id.thumbnail)
            cardView = itemView.findViewById(R.id.cardViewID)
        }
    }

}
