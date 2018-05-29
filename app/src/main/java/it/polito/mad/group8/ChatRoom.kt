package it.polito.mad.group8

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
    private var contactNickname:String? = null
    private var currentUserNickname:String? = null
    private var contactUid:String? = null
    private var bookIsbn:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        chatName = intent.getStringExtra("chatRoomName");
        //contactNickname = intent.getStringExtra("contactNickname");
        contactUid = intent.getStringExtra("contactUid");
        bookIsbn = intent.getStringExtra("bookIsbn")

        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatName)

        // Obtain the NickName of the contact
        FirebaseDatabase.getInstance().getReference("users")
                .child(contactUid)
                .child("nickname")
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                        if (p0 != null) {
                            contactNickname = p0.value.toString()
                        }                    }

                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                        if (p0 != null) {
                            contactNickname = p0.value.toString()
                        }
                    }
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildRemoved(p0: DataSnapshot?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })


        var currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        val editTextMessage: EditText = this.findViewById(R.id.edittext_chatbox)
        val mButton: Button = findViewById(R.id.button_chatbox_send)

        // Obtain the NickName of the log-in user
        FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserUid)
                .child("nickname")
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                        if (p0 != null) {
                            contactNickname = p0.value.toString()
                        }
                    }

                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                        if (p0 != null) {
                            currentUserNickname = p0.value.toString()
                        }
                    }
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildRemoved(p0: DataSnapshot?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })

        mMessageRecycler = this.findViewById(R.id.reyclerview_message_list)
        mMessageAdapter = MessageListAdapter(mContext = applicationContext, mMessageList = messageList)
        mMessageRecycler!!.layoutManager = LinearLayoutManager(this)
        mMessageRecycler!!.adapter = mMessageAdapter

        chatRef?.child("messages")?.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                if (p0 != null) {
                        var message: Message = p0.getValue(Message::class.java)!!
                        messageList.add(message)
                        mMessageAdapter!!.notifyDataSetChanged()
                        mMessageRecycler!!.smoothScrollToPosition(messageList.lastIndex)
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
            var message = Message()
            message.message = editTextMessage.text.toString()
            message.sender = currentUserUid
            message.createdAt = Calendar.getInstance().time.time

            editTextMessage.setText("")
            // Fill-in the log-in user data of the chat
            FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUserUid)
                    .child("chats")
                    .child(chatName)
                    .child("contactNickname")
                    .setValue(contactNickname)
            FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUserUid)
                    .child("chats")
                    .child(chatName)
                    .child("book")
                    .setValue(bookIsbn)
            FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUserUid)
                    .child("chats")
                    .child(chatName)
                    .child("contactUid")
                    .setValue(contactUid)
            FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUserUid)
                    .child("chats")
                    .child(chatName)
                    .child("not read")
                    .setValue(0)
            FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUserUid)
                    .child("chats")
                    .child(chatName)
                    .child("last message")
                    .setValue(message.createdAt)
            // Fill-in the contact user data of the chat
            FirebaseDatabase.getInstance().getReference("users")
                    .child(contactUid)
                    .child("chats")
                    .child(chatName)
                    .child("contactNickname")
                    .setValue(currentUserNickname)
            FirebaseDatabase.getInstance().getReference("users")
                    .child(contactUid)
                    .child("chats")
                    .child(chatName)
                    .child("book")
                    .setValue(bookIsbn)
            FirebaseDatabase.getInstance().getReference("users")
                    .child(contactUid)
                    .child("chats")
                    .child(chatName)
                    .child("contactUid")
                    .setValue(currentUserUid)
            FirebaseDatabase.getInstance().getReference("users")
                    .child(contactUid)
                    .child("chats")
                    .child(chatName)
                    .child("not read")
                    .setValue(0)
            FirebaseDatabase.getInstance().getReference("users")
                    .child(contactUid)
                    .child("chats")
                    .child(chatName)
                    .child("last message")
                    .setValue(message.createdAt)

            chatRef?.child("messages")?.child(message.createdAt.toString())?.setValue(message)
        })


        mMessageRecycler!!.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                mMessageRecycler!!.postDelayed(Runnable {
                    if (mMessageRecycler!!.getAdapter().getItemCount() >0)
                        mMessageRecycler!!.smoothScrollToPosition(mMessageRecycler!!.getAdapter().getItemCount() - 1)
                }, 100)
            }
        })

    }



}
