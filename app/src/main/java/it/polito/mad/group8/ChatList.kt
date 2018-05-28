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

    // Add Layout and Navigation menu
    private var mDrawerLayout: DrawerLayout? = null
    private var mToggle: ActionBarDrawerToggle? = null
    private var mNavigationView: NavigationView? = null

    // Add firebase stuff
    private val database = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth? = null
    private val authListener: FirebaseAuth.AuthStateListener? = null
    private val myRef: DatabaseReference? = null


    //User...is initialized in updateUi if the user is logged in
    private val user = User()
    private var userID: String? = null
    private var usersRef: DatabaseReference? = null

    // Add widgets
    internal lateinit var recyclerView: RecyclerView
    private var chatListAdaptor: ChatListAdaptor? = null
    internal var chats: MutableList<Chat> = ArrayList()

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        mDrawerLayout = findViewById(R.id.drawer_layout)
        mNavigationView = findViewById(R.id.nav_view)

        recyclerView = findViewById(R.id.recyclerView)
        chatListAdaptor = ChatListAdaptor(applicationContext,  chats)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatListAdaptor

        updateUi(FirebaseAuth.getInstance().currentUser)

        // Creation of the lateral menu
        mNavigationView!!.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    finish()
                    startActivity(Intent(this@ChatList, ShowProfile::class.java))
                    return@OnNavigationItemSelectedListener true
                }

            /*case R.id.nav_user_books:
                        finish();
                        startActivity(new Intent(SearchBookActivity.this, ShowBooks.class));
                        return true;*/

                R.id.nav_share_books_logged -> {
                    finish()
                    startActivity(Intent(this@ChatList, ShareBookActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }

                R.id.nav_search_books -> {
                    finish()
                    startActivity(Intent(this@ChatList, SearchBookActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }

                R.id.logout -> {
                    signOut()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.sign -> {
                    signIn()
                    return@OnNavigationItemSelectedListener true
                }

                else -> mDrawerLayout!!.closeDrawers()
            }
            true
        })

        // Assignment of a Layout Manager, in this case, Liner Layout

        getChats()

    }

    //This is useful for when you're not logged in and you log in
    //if you don't updateUi onStart lateral menu won't change
    override fun onStart() {
        super.onStart()
        updateUi(FirebaseAuth.getInstance().currentUser)
        /* TODO Check the flow of activities and if updateUi here is neccesary */
    }

    fun signOut() {
        mDrawerLayout!!.closeDrawers()
        AuthUI.getInstance()
                .signOut(this@ChatList)
                .addOnCompleteListener { updateUi(FirebaseAuth.getInstance().currentUser) }
    }

    fun signIn() {
        mDrawerLayout!!.closeDrawers()
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .build(),
                SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else if (requestCode == SIGN_IN) {
                // Update the UI if receive the corresponding parameter SIGN_IN from startActivityForResult
                updateUi(FirebaseAuth.getInstance().currentUser)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)

        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mToggle!!.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return if (mToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    //Update the interface in case of signing in or out
    fun updateUi(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            this.userID = currentUser.uid
            mNavigationView!!.menu.clear()
            mNavigationView!!.inflateMenu(R.menu.menu_drawer_loggedin)
            usersRef = database.getReference("users/" + this.userID!!)
            usersRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    getData(dataSnapshot)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

        } else {
            mNavigationView!!.menu.clear()
            mNavigationView!!.inflateMenu(R.menu.menu_drawer_not_loggedin)
        }
    }

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

            setHeaderDrawer()
        }
    }

    fun setHeaderDrawer() {
        val headerView = mNavigationView!!.getHeaderView(0)

        val image = headerView.findViewById<CircleImageView>(R.id.header_image)
    }


    fun getChats() {

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().currentUser?.uid)
                .child("chats")
                .orderByChild("last message")
                .addChildEventListener(object : ChildEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

                    }

                    @SuppressLint("NewApi")
                    @TargetApi(Build.VERSION_CODES.N)
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                        if (p0 != null) {
                            val chat : Chat = Chat()
                            chat.bookIsbn = p0.child("books").value.toString()
                            chat.contactNickname = p0.child("contact").value.toString()
                            chat.notRead = p0.child("not read").value.toString().toLong()
                            chat.lastMessage = p0.child("last message").value.toString().toLong()
                            chat.chatName = p0.key

                            chats.removeIf({t -> t.chatName == p0.key})
                            chats.add(0, chat)
                            chatListAdaptor?.notifyDataSetChanged()

                        }
                    }

                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                        if (p0 != null) {
                                val chat : Chat = Chat()
                                chat.bookIsbn = p0.child("book").value.toString()
                                chat.contactNickname = p0.child("contactNickname").value.toString()
                                chat.contactUid = p0.child("contactUid").value.toString()
                                chat.notRead = p0.child("not read").value.toString().toLong()
                                chat.lastMessage = p0.child("last message").value.toString().toLong()
                                chat.chatName = p0.key

                                chats.add(0, chat)
                                chatListAdaptor?.notifyDataSetChanged()

                        }
                    }

                    override fun onChildRemoved(p0: DataSnapshot?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }


                })


    }

}
