package com.example.beeriq.ui.editprofile

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.beeriq.FirebaseRepo
import com.example.beeriq.R
import com.example.beeriq.User
import com.example.beeriq.tools.Util
import java.io.File

class EditProfileFragment : AppCompatActivity() {

    // Variables for handling camera capture and image display
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var imgUri: Uri
    private lateinit var imageView: ImageView
    private val tempImgFileName = "profile_image.jpg"
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    // UI elements for profile data
    private lateinit var btnChangePhoto: Button
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var username: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPhone: EditText
    private lateinit var radioGenres: RadioGroup
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        // Initialize views
        imageView =findViewById(R.id.profile_image)
        btnChangePhoto =findViewById(R.id.btn_change_photo)
        btnSave =findViewById(R.id.btn_save)
        btnCancel =findViewById(R.id.btn_cancel)

        username = findViewById(R.id.Input_Username)
        inputEmail =findViewById(R.id.Input_Email)
        inputPhone =findViewById(R.id.Input_Phone)
        password = findViewById(R.id.Input_Password)


        radioGenres =findViewById(R.id.Radio_Gender_Option)

        // Load saved profile data from SharedPreferences
        loadFormData()

        // Load the profile image from the local directory
        loadImage()

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageView.setImageURI(imgUri)
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageView.setImageURI(imageUri)
            }
        }



        // Ensure required permissions are granted for camera access
        Util.checkPermissions(this)

        btnChangePhoto.setOnClickListener {
            showImageSourceDialog()
        }

        // Saving functionalities
        btnSave.setOnClickListener{
            if(isValidForm()){
                saveFormData()
                finish()
            }


        }

        // Cancellation functionality
        btnCancel.setOnClickListener{
            loadImage()
            loadFormData()
            finish()
        }

        // Register for camera result to handle the photo capture and display
        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result:ActivityResult ->

            if (result.resultCode == Activity.RESULT_OK){
                val bitmap = Util.getBitmap(this, imgUri)
                imageView.setImageBitmap(bitmap)
               findViewById<ImageView>(R.id.profile_image).setImageURI(imgUri)
                val imageFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), tempImgFileName)

            }
        }
    }

    private fun showImageSourceDialog() {
        // Display dialog for the user to choose camera or gallery
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(this)
            .setTitle("Change Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()  // Camera option
                    1 -> openGallery() // Gallery option
                }
            }
            .show()
    }

    // Check to see if form is valid
    private fun isValidForm(): Boolean{

        // check if input fields aren't empty
        if (getTextVal(username).isEmpty() ||
            getTextVal(password).isEmpty()){
//            snackBarMessage("All fields must be complete", R.color.red)
            return false
        }

        // check if email is valid
        //https://chatgpt.com/share/66edf43c-ee40-8002-900a-a93fb6389d72
        val TLDs = listOf(
            ".com",
            ".org",
            ".net",
            ".edu",
            ".gov",
            ".mil",
            ".info",
            ".biz",
            ".name",
            ".pro",
            ".xyz",
            ".io",
            ".tech",
            ".online",
            ".app",
            ".store",
            ".blog",
            ".shop",
            ".club",
            ".guru",
            ".design",
            ".photography",
            ".music",
            ".news"
        )


        if (!getTextVal(inputEmail).contains('@') || !containsValidTLD(getTextVal((inputEmail)) ,TLDs)){
//            snackBarMessage("Invalid email", R.color.red)
            return false
        }

        val selectedRadioButtonId = radioGenres.checkedRadioButtonId

        // Check if a radio postButton has been selected
        if (selectedRadioButtonId != -1) {
            // Find the selected RadioButton by id
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)

            // Ensure the selectedRadioButton is not null before accessing its text property
            if (selectedRadioButton != null) {
                val selectedRadioText = selectedRadioButton.text.toString() // Get the text of the selected radio postButton
                // Do something with the selected radio postButton value
                println("Selected Gender: $selectedRadioText")
            }
        } else {
            // No radio postButton is selected
            println("No gender selected")
//            snackBarMessage("Gender not selected", R.color.red)
            return false
        }
        return true
    }

    private fun openCamera() {
        val imageFile = File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), tempImgFileName)
        imgUri = FileProvider.getUriForFile(this, "${this.packageName}.fileprovider", imageFile)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        cameraLauncher.launch(takePictureIntent)
    }


    private fun openGallery() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(pickPhotoIntent)
    }

    // Helper function to get the text from an EditText field
    private fun getTextVal(inputField:EditText): String{
        return inputField.text.toString()
    }

    private fun saveFormData() {
        val firebaseRepo = FirebaseRepo(this.getSharedPreferences("UserDetails", Context.MODE_PRIVATE))

        // Determine selected gender
        val selectedGender = when {
            findViewById<RadioButton>(R.id.Radio_Male)?.isChecked == true -> "Male"
            findViewById<RadioButton>(R.id.Radio_Female)?.isChecked == true -> "Female"
            else -> ""
        }

        // Create a User object with updated information
        val updatedUser = User(
            username = username.text.toString(),
            password = password.text.toString(),
            email = inputEmail.text.toString(),
            phone = inputPhone.text.toString(),
            gender = selectedGender, // Include gender
            friends = mutableListOf(),
            outgoingFriends = mutableListOf(),
            incomingFriends = mutableListOf(),
            posts = mutableListOf()
        )

        // Call FirebaseRepo to update user data
        firebaseRepo.updateUser(updatedUser) { success ->
            if (success) {
                println("Debug: Profile saved successfully in Firebase.")
            } else {
                println("Debug: Failed to save profile in Firebase.")
            }
        }
    }

    // Load saved form data from SharedPreferences
    private fun loadFormData() {
        val sharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val firebaseRepo = FirebaseRepo(sharedPreferences)

        val usernameKey = sharedPreferences.getString("username", "")

        println("TEST:")
        println(usernameKey.toString())
        if (usernameKey.toString().isEmpty()) {
            println("Debug: Username field is empty. Please enter your username to load data.")
            return
        }

        firebaseRepo.fetchUserData(usernameKey.toString()) { user ->
            if (user != null) {
                username.setText(user.username)
                inputEmail.setText(user.email)
                inputPhone.setText(user.phone)
                password.setText(user.password)

                println("${user.username}, ${user.email}")

                when (user.gender) {
                    "Male" -> findViewById<RadioButton>(R.id.Radio_Male)?.isChecked = true
                    "Female" -> findViewById<RadioButton>(R.id.Radio_Female)?.isChecked = true
                    else -> println("Debug: Gender not set.")
                }

                println("Debug: User data loaded successfully.")
            } else {
                println("Debug: Failed to load user data for username: $usernameKey")
            }
        }
    }





    // Load profile image from external storage
    private fun loadImage() {
        val imageFile = File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), tempImgFileName)
        if (imageFile.exists()) {
            imageView.setImageURI(Uri.fromFile(imageFile))
        }
    }


    // Validate if the email has a valid top-level domain (TLD)
    private fun containsValidTLD(email: String, validTLDs: List<String>): Boolean {
        val domain = email.substringAfterLast(".")
        return validTLDs.any { it.substringAfter(".").equals(domain, ignoreCase = true) }
    }
}