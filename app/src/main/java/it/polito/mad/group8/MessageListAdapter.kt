package it.polito.mad.group8

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat

class MessageListAdapter(private val mContext: Context, private val mMessageList: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val formatter = SimpleDateFormat("kk:mm MM/dd/yy")
        val message = mMessageList[position]
        Log.e("\titemviewType", holder.itemViewType.toString())
        Log.e("\tMessage", message.message)
        when (holder.itemViewType) {

            VIEW_TYPE_MESSAGE_SENT -> {
                (holder as SentMessageHolder).messageText.text = message.message
                (holder as SentMessageHolder).timeText.text = formatter.format(message.createdAt)
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                (holder as ReceivedMessageHolder).messageText.text = message.message
                (holder as ReceivedMessageHolder).timeText.text = formatter.format(message.createdAt)
            }
        }
    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    // Determines the appropriate ViewType according to the sender of the message.
    override fun getItemViewType(position: Int): Int {

        if (mMessageList[position].sender == FirebaseAuth.getInstance().currentUser!!.uid) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View

         if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
             return SentMessageHolder(view)
        } else  {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
             return ReceivedMessageHolder(view)
        }
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.


    inner class SentMessageHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var messageText: TextView = itemView.findViewById<View>(R.id.text_message_body) as TextView
        internal var timeText: TextView = itemView.findViewById<View>(R.id.text_message_time) as TextView



    }

    inner class ReceivedMessageHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var messageText: TextView = itemView.findViewById<View>(R.id.text_message_body) as TextView
        internal var timeText: TextView = itemView.findViewById<View>(R.id.text_message_time) as TextView
    }

    companion object {
        private val VIEW_TYPE_MESSAGE_SENT = 1
        private val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }
}
