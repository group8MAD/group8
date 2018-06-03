package it.polito.mad.group8

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ChildEventListener
import android.view.MenuInflater









class ChatRoom : AppCompatActivity() {

    internal var messageList: MutableList<Message> = ArrayList<Message>()

    private var mMessageRecycler: RecyclerView? = null
    private var mMessageAdapter: MessageListAdapter? = null
    private var chatRef: DatabaseReference? = null
    private var chatName:String? = null
    //Contact User Info
    private var contactUserUid:String? = null
    //Current logged-in User Info
    private var currentUserUid:String? = null

    private var chatStatus:String? = null


    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        chatName = intent.getStringExtra("chatRoomName");
        contactUserUid = intent.getStringExtra("contactUid");
        currentUserUid = intent.getStringExtra("currentUserUid")
        chatStatus = intent.getStringExtra("chat")

        val contactUserChatInfo = Chat()
        val currentUserChatInfo = Chat()

        contactUserChatInfo.chatName = chatName
        contactUserChatInfo.contactUid = currentUserUid

        currentUserChatInfo.chatName = chatName
        currentUserChatInfo.contactUid = contactUserUid

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.e("\t\tChatRoom\t\t\t\tChatRoomName: ", chatName)
        Log.e("\t\tChatRoom\t\t\t\tContact user UID: ", contactUserUid)
        Log.e("\t\tChatRoom\t\t\t\tCurrent user UID: ", currentUserUid)

        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatName)

        val editTextMessage: EditText = this.findViewById<EditText>(R.id.edittext_chatbox)
        val mButton: Button = findViewById(R.id.button_chatbox_send)



        //resetting notRead
        if (chatStatus == "old") {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUserUid)
                    .child("chats")
                    .child(chatName)
                    .child("notRead")
                    .setValue(0)
        }

        //Getting current User Nickname
        FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserUid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        Log.e("\t\tChatRoom\t\t\t\tCurrent user Nickname: ", p0?.child("nickname")?.value.toString())
                        contactUserChatInfo.contactNickname = p0?.child("nickname")?.value.toString()
                        contactUserChatInfo.contactImageUri = p0?.child("imageUri")?.value.toString()
                    }

                })

        //Getting contact User Nickname and imageUri
        FirebaseDatabase.getInstance().getReference("users")
                .child(contactUserUid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        Log.e("\t\tChatRoom\t\t\t\tContact user Nickname: ", p0?.child("nickname")?.value.toString())
                        currentUserChatInfo.contactNickname = p0?.child("nickname")?.value.toString()
                        currentUserChatInfo.contactImageUri = p0?.child("imageUri")?.value.toString()
                        supportActionBar?.title = currentUserChatInfo.contactNickname
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
                Log.e("chatStatus", chatStatus)

                // Fill-in the logged-in user data of the chat
                currentUserChatInfo.notRead = 0
                currentUserChatInfo.lastMessage = message.createdAt
                currentUserChatInfo.lastMessageText = message.message


                FirebaseDatabase.getInstance().getReference("users")
                        .child(currentUserUid)
                        .child("chats")
                        .child(chatName)
                        .setValue(currentUserChatInfo)


                // Fill-in the contact user data of the chat
                contactUserChatInfo.lastMessage = message.createdAt
                contactUserChatInfo.lastMessageText = message.message


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
                                if (p0?.exists()!!) {
                                    val currentlyNotRead = p0.value.toString().toLong()
                                    contactUserChatInfo.notRead = currentlyNotRead +1
                                    p0.ref.parent.setValue(contactUserChatInfo)
                                }else{
                                    contactUserChatInfo.notRead = 1
                                    p0.ref.parent.setValue(contactUserChatInfo)
                                }
                            }

                        })
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



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.chat_room_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.borrowBook){
            val borrowIntent = Intent(applicationContext, BorrowBook::class.java)
            borrowIntent.putExtra("contactUid", contactUserUid);
            startActivity(borrowIntent)
        }else
            this.finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        //resetting notRead
        if (chatStatus == "old") {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUserUid)
                    .child("chats")
                    .child(chatName)
                    .child("notRead")
                    .setValue(0)
        }
        super.onDestroy()
    }
}
