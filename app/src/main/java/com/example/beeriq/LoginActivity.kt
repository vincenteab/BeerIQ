package com.example.beeriq

import android.content.Intent
import android.os.Bundle
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

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseRef = FirebaseDatabase.getInstance().getReference("users")

        //when user hits create button it will create an account
        binding.createAccountButton.setOnClickListener{
            //get the username and password from the edit text
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val tempUser = User(username, password)

            //check if the user has entered a username and password
            if (checkCredentials(tempUser) == 0){
                //check if the username is unique
                checkUniqueUsername(tempUser){ exists ->
                    if (exists){
                        binding.usernameEditText.error = "Username already exists"
                    }else{
                        //create a unique id for the user
                        val uniqueID = firebaseRef.push().key!!
                        //store the user in the database and let user know account was created
                        firebaseRef.child(uniqueID).setValue(tempUser)
                            .addOnCompleteListener{
                                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                            }
                        //continue to the main activity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    //check if the user has entered a username and password *** WIP needs to check if user name is unique
    private fun checkCredentials(user: User) : Int{
        if (user.username?.isEmpty() != false && user.password?.isEmpty() != false){
            binding.usernameEditText.error = "Please enter a username"
            binding.passwordEditText.error = "Please enter a password"
            return 1
        }else if(user.username?.isEmpty() != false){
            binding.usernameEditText.error = "Please enter a username"
            return 1
        }else if (user.password?.isEmpty() != false){
            binding.passwordEditText.error = "Please enter a password"
            return 1
        }

        return 0
    }

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
}