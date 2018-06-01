package it.polito.mad.group8

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ChildEventListener







class ChatRoom : AppCompatActivity() {

    internal var messageList: MutableList<Message> = ArrayList<Message>()

    private var mMessageRecycler: RecyclerView? = null
    private var mMessageAdapter: MessageListAdapter? = null
    private var chatRef: DatabaseReference? = null
    private var chatName:String? = null
    //Contact User Info
    private var contactUserUid:String? = null
    private var contactUserNickname:String? = null
    //Current logged-in User Info
    private var currentUserUid:String? = null
    private var currentUserNickname:String? = null



    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        chatName = intent.getStringExtra("chatRoomName");
        contactUserUid = intent.getStringExtra("contactUid");
        currentUserUid = intent.getStringExtra("currentUserUid")


        Log.e("\t\tChatRoom\t\t\t\tChatRoomName: ", chatName)
        Log.e("\t\tChatRoom\t\t\t\tContact user UID: ", contactUserUid)
        Log.e("\t\tChatRoom\t\t\t\tCurrent user UID: ", currentUserUid)

        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatName)

        val editTextMessage: EditText = this.findViewById(R.id.edittext_chatbox)
        val mButton: Button = findViewById(R.id.button_chatbox_send)

        //resetting notRead
        FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserUid)
                .child("chats")
                .child(chatName)
                .child("notRead")
                .setValue(0)

        //Getting current User Nickname
        FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserUid)
                .child("nickname")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        Log.e("\t\tChatRoom\t\t\t\tCurrent user Nickname: ", p0?.value.toString())
                        currentUserNickname = p0?.value.toString()
                    }

                })

        //Getting contact User Nickname
        FirebaseDatabase.getInstance().getReference("users")
                .child(contactUserUid)
                .child("nickname")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        Log.e("\t\tChatRoom\t\t\t\tContact user Nickname: ", p0?.value.toString())
                        contactUserNickname = p0?.value.toString()
                    }

                })

        mMessageRecycler = this.findViewById(R.id.reyclerview_message_list)
        mMessageAdapter = MessageListAdapter(mContext = applicationContext, mMessageList = messageList)
        mMessageRecycler?.layoutManager = LinearLayoutManager(this)
        mMessageRecycler?.adapter = mMessageAdapter

        chatRef?.child("messages")?.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                if (p0 != null) {
                        var message: Message = p0.getValue(Message::class.java)!!
                        messageList.add(message)
                        mMessageAdapter?.notifyDataSetChanged()
                        mMessageRecycler?.smoothScrollToPosition(messageList.lastIndex)
                }

            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        mButton.setOnClickListener(View.OnClickListener {

            if (editTextMessage.text.isNotEmpty()) {
                val message = Message()
                message.message = editTextMessage.text.toString()
                message.sender = currentUserUid!!
                message.createdAt = Calendar.getInstance().time.time

                editTextMessage.setText("")
                // Fill-in the logged-in user data of the chat
                FirebaseDatabase.getInstance().getReference("users")
                        .child(currentUserUid)
                        .child("chats")
                        .child(chatName)
                        .child("notRead")
                        .setValue(0)
                FirebaseDatabase.getInstance().getReference("users")
                        .child(currentUserUid)
                        .child("chats")
                        .child(chatName)
                        .child("lastMessage")
                        .setValue(message.createdAt)
                FirebaseDatabase.getInstance().getReference("users")
                        .child(currentUserUid)
                        .child("chats")
                        .child(chatName)
                        .child("contactNickname")
                        .setValue(contactUserNickname)
                FirebaseDatabase.getInstance().getReference("users")
                        .child(currentUserUid)
                        .child("chats")
                        .child(chatName)
                        .child("contactUid")
                        .setValue(contactUserUid)


                // Fill-in the contact user data of the chat
                FirebaseDatabase.getInstance().getReference("users")
                        .child(contactUserUid)
                        .child("chats")
                        .child(chatName)
                        .child("contactNickname")
                        .setValue(currentUserNickname)
                FirebaseDatabase.getInstance().getReference("users")
                        .child(contactUserUid)
                        .child("chats")
                        .child(chatName)
                        .child("contactUid")
                        .setValue(currentUserUid)
                FirebaseDatabase.getInstance().getReference("users")
                        .child(contactUserUid)
                        .child("chats")
                        .child(chatName)
                        .child("notRead")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onDataChange(p0: DataSnapshot?) {
                                val currentlyNotReaded = p0?.value.toString().toLong()
                                p0?.ref?.setValue(currentlyNotReaded + 1)
                            }

                        })
                FirebaseDatabase.getInstance().getReference("users")
                        .child(contactUserUid)
                        .child("chats")
                        .child(chatName)
                        .child("lastMessage")
                        .setValue(message.createdAt)


                chatRef?.child("messages")?.child(message.createdAt.toString())?.setValue(message)
            }
        })


        mMessageRecycler!!.addOnLayoutChangeListener(View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                mMessageRecycler!!.postDelayed(Runnable {
                    if (mMessageRecycler!!.adapter.itemCount >0)
                        mMessageRecycler!!.smoothScrollToPosition(mMessageRecycler!!.getAdapter().getItemCount() - 1)
                }, 100)
            }
        })

    }


    override fun onDestroy() {
        //resetting notRead
        FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserUid)
                .child("chats")
                .child(chatName)
                .child("notRead")
                .setValue(0)
        super.onDestroy()
    }
}
