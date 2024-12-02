package com.example.beeriq.ui.userprofile.editprofile

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
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
import java.io.ByteArrayOutputStream
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
    private lateinit var Currentusername: String
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
                if (username.text.toString() != Currentusername){
                    val firebaseRepo = FirebaseRepo(this.getSharedPreferences("UserData", Context.MODE_PRIVATE))
                    firebaseRepo.checkIfUsernameExists(username.text.toString()) { exists ->
                        if(!exists){
                            saveFormData()
                            finish()
                        }else{
                            AlertDialog.Builder(this)
                                .setTitle("Invalid Input")
                                .setMessage("Username Already exists. Create another one.")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }else{
                    saveFormData()
                    finish()
                }
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
    private fun isValidForm(): Boolean {
        // Helper function to show popup message
        fun showPopupMessage(message: String) {
            AlertDialog.Builder(this)
                .setTitle("Invalid Input")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }

        // Check if input fields are empty
        if (getTextVal(username).isEmpty() || getTextVal(password).isEmpty()) {
            showPopupMessage("All fields must be completed.")
            return false
        }

        // Check if email is valid
        val TLDs = listOf(
            ".com", ".org", ".net", ".edu", ".gov", ".mil", ".info", ".biz", ".name",
            ".pro", ".xyz", ".io", ".tech", ".online", ".app", ".store", ".blog",
            ".shop", ".club", ".guru", ".design", ".photography", ".music", ".news", ".ca"
        )

        val email = getTextVal(inputEmail)
        if (email.isNotEmpty() && (!email.contains('@') || !containsValidTLD(email, TLDs))) {
            showPopupMessage("Invalid email address. Please provide a valid email.")
            return false
        }


        // If all validations pass
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

        // Downscale the profile image and convert to Base64
        val profileImageBase64 = if (imageView.drawable is BitmapDrawable) {
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val resizedBitmap = downscaleBitmap(bitmap, 200, 200) // Resize to 200x200 pixels
            bitmapToBase64(resizedBitmap, Bitmap.CompressFormat.JPEG, 80) // Compress to 80% quality
        } else {
            "" // Default to empty string if no image or if it's not a BitmapDrawable
        }

        // Create a User object with updated information
        val updatedUser = User(
            username = username.text.toString(),
            password = password.text.toString(),
            email = inputEmail.text.toString(),
            phone = inputPhone.text.toString(),
            gender = selectedGender,
            profileImg = profileImageBase64
        )

        // Call FirebaseRepo to update user data
        firebaseRepo.updateUser(updatedUser, Currentusername) { success ->
            if (success) {
                println("Debug: Profile saved successfully in Firebase.")
            } else {
                println("Debug: Failed to save profile in Firebase.")
            }
        }

        println(username.text.toString())

        val sharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("username", updatedUser.username).apply()
        sharedPreferences.edit().putString("profilePic", profileImageBase64).apply()
    }


    // Load saved form data from SharedPreferences
    private fun loadFormData() {
        val sharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val firebaseRepo = FirebaseRepo(sharedPreferences)

        val usernameKey = sharedPreferences.getString("username", "")

        println(usernameKey.toString())
        if (usernameKey.toString().isEmpty()) {
            println("Debug: Username field is empty. Please enter your username to load data.")
            return
        }

        firebaseRepo.fetchUserData(usernameKey.toString()) { user ->
            if (user != null) {
                username.setText(user.username)
                Currentusername = user.username
                inputEmail.setText(user.email)
                inputPhone.setText(user.phone)
                password.setText(user.password)

                if (!user.profileImg.isNullOrEmpty()) {
                    val bitmap = base64ToBitmap(user.profileImg)
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap)
                        println("Debug: loaded image from firebase.")
                    } else {
                        println("Debug: Failed to decode profile image.")
                    }
                } else {
                    println("Debug: No profile image found for user.")
                }

                when (user.gender) {
                    "Male" -> findViewById<RadioButton>(R.id.Radio_Male)?.isChecked = true
                    "Female" -> findViewById<RadioButton>(R.id.Radio_Female)?.isChecked = true
                    else -> println("Debug: Gender not set.")
                }

                // Load profile image from the User object
                if (!user.profileImg.isNullOrEmpty()) {
                    val bitmap = base64ToBitmap(user.profileImg)
                    imageView.setImageBitmap(bitmap)
                } else {
                    println("Debug: No profile image found for user.")
                }

                println("Debug: User data loaded successfully.")
            } else {
                println("Debug: Failed to load user data for username: $usernameKey")
            }
        }
    }



    private fun bitmapToBase64(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(format, quality, byteArrayOutputStream) // Compress with given format and quality
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun downscaleBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
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