package it.polito.mad.group8

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator


import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList

@Suppress("NAME_SHADOWING")
class ChatList : AppCompatActivity() {
    val SIGN_IN = 1000

    //User...is initialized in updateUi if the user is logged in
    private val user = User()
    private var userID: String? = null
    private var usersRef: DatabaseReference? = null

    // Add widgets
    internal lateinit var recyclerView: RecyclerView
    internal lateinit var nothing: TextView
    private var chatListAdaptor: ChatListAdaptor? = null
    internal var chats: MutableList<Chat> = ArrayList()

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)


        nothing = findViewById(R.id.nothing)
        recyclerView = findViewById(R.id.recyclerView)
        chatListAdaptor = ChatListAdaptor(applicationContext, chats)
        recyclerView.layoutManager = LinearLayoutManager(this) // Assignment of a Layout Manager, in this case, Liner Layout
        recyclerView.adapter = chatListAdaptor

        supportActionBar?.title = getString(R.string.chatList)
        getChats()

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)

        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    //Update the interface in case of signing in or out

    //Getting user data to be shown in the header
    private fun getData(dataSnapshot: DataSnapshot) {
        if (!dataSnapshot.exists()) {
            usersRef!!.setValue(this.user)
        } else {
            this.user.name = dataSnapshot.getValue(User::class.java)!!.name
            this.user.email = dataSnapshot.getValue(User::class.java)!!.email
            this.user.biography = dataSnapshot.getValue(User::class.java)!!.biography
            this.user.nickname = dataSnapshot.getValue(User::class.java)!!.nickname
            /*TODO The field Biography is not include in the file drawer_header.xml*/
        }
    }


    fun getChats() {

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().currentUser?.uid)
                .child("chats")
                .orderByChild("lastMessage")
                .addChildEventListener(object : ChildEventListener{

                    @SuppressLint("NewApi")
                    @TargetApi(Build.VERSION_CODES.N)
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                        if (p0 != null) {
                            val chat = p0.getValue(Chat::class.java)
                            chat?.chatName = p0.key
                            chats.removeIf({t -> t.chatName == p0.key})
                            chat?.let { chats.add(0, it) }
                            chatListAdaptor?.notifyDataSetChanged()
                            nothing.visibility = View.GONE
                        }
                    }

                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                        if (p0 != null) {
                            val chat = p0.getValue(Chat::class.java)
                            chat?.chatName = p0.key

                            chat?.let { chats.add(0, it) }
                            chatListAdaptor?.notifyDataSetChanged()
                            nothing.visibility = View.GONE

                        }
                    }
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    @SuppressLint("NewApi")
                    @TargetApi(Build.VERSION_CODES.N)
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

                    }


                    @SuppressLint("NewApi")
                    @TargetApi(Build.VERSION_CODES.N)
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onChildRemoved(p0: DataSnapshot?) {
                        chats.removeIf({t -> t.chatName == p0?.key})
                        chatListAdaptor?.notifyDataSetChanged()
                    }

                })


    }

}
