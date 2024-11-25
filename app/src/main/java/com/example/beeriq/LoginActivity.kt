package com.example.beeriq

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.beeriq.databinding.ActivityLoginBinding
import com.example.beeriq.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var username: String
    private lateinit var password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseRef = FirebaseDatabase.getInstance().getReference("users")


        //when user hits create button it will create an account
        binding.createAccountButton.setOnClickListener{
            //get the username and password from the edit text
            username = binding.usernameTextField.editText?.text.toString()
            password = binding.passwordTextField.editText?.text.toString()
            val tempUser = User(username, password)

            //check if the user has entered a username and password
            if (checkCredentials(tempUser) == 0){
                //check if the username is unique
                checkUniqueUsername(tempUser){ exists ->
                    if (exists){
                        binding.usernameTextField.error = "Username already exists"
                        binding.usernameTextField.error = null
                    }else{
                        //create a unique id for the user
                        val uniqueID = firebaseRef.push().key!!
                        //store the user in the database and let user know account was created
                        firebaseRef.child(uniqueID).setValue(tempUser)
                            .addOnCompleteListener{
                                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                            }
                        //continue to the main activity
                        storeUserDataLocally(username, password, null, null, null)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

        //when user hits login button it will check if the user exists
        binding.loginButton.setOnClickListener {
            binding.loginButton.startLoading()

            Handler(Looper.getMainLooper()).postDelayed({
                username = binding.usernameTextField.editText?.text.toString()
                password = binding.passwordTextField.editText?.text.toString()
                val tempLoggedUser = User(username, password)
                //check if the user has entered a username and password
                if (checkCredentials(tempLoggedUser) == 0){
                    checkUserExists(tempLoggedUser){ exists ->
                        //if the user exists, continue to the main activity
                        if (exists){

                            fetchUserData(username){ userData ->
                                if (userData != null) {
                                    val email = userData.email
                                    val phone = userData.phone
                                    val friends = userData.friends as List<String>?
                                    storeUserDataLocally(username, password, email, phone, friends)
                                }


                            }
                            binding.loginButton.doResult(true)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            binding.loginButton.doResult(false)
                        }
                    }
                }else{
                    binding.loginButton.doResult(false)
                }


            }, 2000) // 3 seconds delay
        }
    }

    //check if the user has entered a username and password
    private fun checkCredentials(user: User) : Int{
        if (user.username?.isEmpty() != false && user.password?.isEmpty() != false){
            binding.usernameTextField.error = "Please enter a username"
            binding.passwordTextField.error = "Please enter a password"
            return 1
        }else if(user.username?.isEmpty() != false){
            binding.usernameTextField.error = "Please enter a username"
            binding.passwordTextField.error = null
            return 1
        }else if (user.password?.isEmpty() != false){
            binding.passwordTextField.error = "Please enter a password"
            binding.usernameTextField.error = null
            return 1
        }

        return 0
    }

    //check if the username is unique
    private fun checkUniqueUsername(user: User, callback:(exists: Boolean) -> Unit): Unit{
        //check if the username is unique
        firebaseRef.orderByChild("username").equalTo(user.username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    //check if the user exists in the database
    private fun checkUserExists(user: User, callback:(exists: Boolean) -> Unit): Unit{
        //goes through different usernames in the database and checks if the password matches
        firebaseRef.orderByChild("username").equalTo(user.username)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                //function that is called when the data is changed
                override fun onDataChange(snapshot: DataSnapshot) {
                    //if username exists in db
                    if (snapshot.exists()) {
                        //go through each user in the database and check their password
                        for (userSnapshot in snapshot.children) {
                            val userPass = userSnapshot.getValue(User::class.java)?.password
                            //if the password matches the user's password, data goes through
                            if (userPass == user.password) {
                                val text = userSnapshot.getValue(User::class.java)?.username
                                Toast.makeText(this@LoginActivity, "Welcome $text", Toast.LENGTH_SHORT).show()
                                callback(true)
                            //if the password does not match the user's password, error message is displayed
                            } else {
                                binding.usernameTextField.error = "Incorrect username or password"
                                binding.passwordTextField.error = "Incorrect username or password"
                                callback(false)
                            }
                        }
                    //if username does not exist in db, error message is displayed
                    }else{
                        binding.usernameTextField.error = "Account does not exist"
                        binding.passwordTextField.error = null
                        callback(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    //store user data locally
    fun storeUserDataLocally(username: String?, password: String?, email: String?, phone: String?, friends: List<String>?) {
        val sharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putString("email", email)
        editor.putString("phone", phone)
        editor.putStringSet("friends", friends?.toSet())
        editor.apply()
    }

    //fetch user data from the database
    //Pass through username as string and get back User object
    fun fetchUserData(user: String, onComplete: (User?) -> Unit) {
        firebaseRef.orderByChild("username").equalTo(user)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (child in snapshot.children) {
                            val userData = child.getValue(User::class.java) // Convert to User class


                            onComplete(userData) // Pass the fetched data to the callback

                        }
                    } else {
                        println("debug: No user found with username: $user")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Query cancelled: ${error.message}")
                }
            })
    }

}