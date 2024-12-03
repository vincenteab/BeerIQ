package com.example.beeriq

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.tasks.await
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
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Disable back button
            }
        })


        //when user hits create postButton it will create an account
        binding.createAccountButton.setOnClickListener{
            //get the username and password from the edit text
            username = binding.usernameTextField.editText?.text.toString()
            password = binding.passwordTextField.editText?.text.toString()
            val tempUser = User(username, password)

            //check if the user has entered a username and password
            if (checkCredentials(tempUser) == 0){
                //check if the username is unique


                lifecycleScope.launch {
                    val usernameExists = withContext(Dispatchers.IO) {
                        checkUniqueUsername(tempUser)
                    }
                    if (usernameExists) {
                        binding.usernameTextField.error = "Username already exists"
                        binding.passwordTextField.error = null
                    } else {
                        createAccount(tempUser)
                    }
                }
            }
        }

        //when user hits login postButton it will check if the user exists
        binding.loginButton.setOnClickListener {
            binding.loginButton.startLoading()

            Handler(Looper.getMainLooper()).postDelayed({
                username = binding.usernameTextField.editText?.text.toString()
                password = binding.passwordTextField.editText?.text.toString()
                val tempLoggedUser = User(username, password)
                //check if the user has entered a username and password
                if (checkCredentials(tempLoggedUser) == 0){

                    lifecycleScope.launch {
                        val userExists = withContext(Dispatchers.IO){
                            checkUserExists(tempLoggedUser)
                        }
                        if (userExists){
                            val userData = withContext(Dispatchers.IO){
                                fetchUserData(username)
                            }
                            userData?.let{
                                storeUserDataLocally(it.username, it.password, it.email, it.phone, it.friends, it.profileImg)
                            }
                            binding.loginButton.doResult(true)
                            binding.usernameTextField.error = null
                            binding.passwordTextField.error = null
                            Toast.makeText(this@LoginActivity, "Welcome $username", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        }else{
                            binding.usernameTextField.error = "Incorrect username or password"
                            binding.passwordTextField.error = "Incorrect username or password"
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
    private suspend fun checkUniqueUsername(user: User) : Boolean{
        //check if the username is unique
        return withContext(Dispatchers.IO){
            val result = firebaseRef.orderByChild("username").equalTo(user.username).get().await()
            result.exists()
        }

    }

    //check if the user exists in the database
    private suspend fun checkUserExists(user: User) :Boolean{
        //goes through different usernames in the database and checks if the password matches

        return withContext(Dispatchers.IO){
            val result = firebaseRef.orderByChild("username").equalTo(user.username).get().await()
            result.children.any { it.getValue(User::class.java)?.password == user.password }
        }
    }

    private suspend fun fetchUserData(username: String): User? {
        return withContext(Dispatchers.IO) {
            val result = firebaseRef.orderByChild("username").equalTo(username).get().await()
            result.children.firstOrNull()?.getValue(User::class.java)
        }
    }

    //store user data locally
    fun storeUserDataLocally(username: String?, password: String?, email: String?, phone: String?, friends: List<String>?, profilePic: String?) {
        val sharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putString("email", email)
        editor.putString("phone", phone)
        editor.putStringSet("friends", friends?.toSet())
        editor.putString("profilePic", profilePic)
        editor.apply()
    }


    private suspend fun createAccount(user: User){
        withContext(Dispatchers.IO){
            val uniqueID = firebaseRef.push().key!!
            firebaseRef.child(uniqueID).setValue(user).await()
        }
        withContext(Dispatchers.Main){
            Toast.makeText(this@LoginActivity, "Account created", Toast.LENGTH_SHORT).show()
            storeUserDataLocally(user.username, user.password, null, null, null, null)
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }

    }

}