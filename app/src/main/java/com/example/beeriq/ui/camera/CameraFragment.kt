package com.example.beeriq.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.graphics.Matrix
import android.net.Uri
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.BeerDatabase
import com.example.beeriq.data.local.beerDatabase.BeerRepository
import com.example.beeriq.tools.Util
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class CameraFragment : Fragment() {
    private lateinit var previewView: PreviewView
    private lateinit var captureButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var libraryButton: ImageButton
    private lateinit var loadingScreen: View
    private lateinit var cameraUI: ConstraintLayout

    private lateinit var cameraSelector: CameraSelector
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraViewModel: CameraViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()

        backButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        libraryButton = view.findViewById(R.id.libraryButton)
        libraryButton.setOnClickListener {
            openPhotoLibrary()
        }

        cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        previewView = view.findViewById(R.id.preview)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
        {
            val cameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(view.display.rotation)
                .build()
            bindPreview(cameraProvider)

        }, ContextCompat.getMainExecutor(requireActivity()))

        val database = BeerDatabase.getInstance(requireContext())
        val repository = BeerRepository(database.beerDatabaseDao)
        val cameraViewModelFactory = CameraViewModelFactory(repository)
        cameraViewModel = ViewModelProvider(requireActivity(), cameraViewModelFactory).get(CameraViewModel::class.java)

        cameraUI = view.findViewById(R.id.cameraUI)
        loadingScreen = view.findViewById(R.id.loading_screen)

        captureButton = view.findViewById(R.id.capture)
        captureButton.setOnClickListener {
            cameraUI.visibility = View.GONE
            cameraViewModel.resetBuffer()
            var byteArray = ByteArray(10)
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    capturePhoto { bitmap ->
                        val image = InputImage.fromBitmap(bitmap, 0)
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        val rotatedBitmap = rotateBitMap(bitmap, 90f)
                        val scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, 600, 1000, true)
                        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                        byteArray = byteArrayOutputStream.toByteArray()

                        cameraViewModel.recognizeTextFromImage(image)
                    }
                }
                loadingScreen.visibility = View.VISIBLE
                cameraViewModel.beerResult.observe(viewLifecycleOwner) { beer ->
                    if (beer.isNotEmpty()) {
                        loadingScreen.visibility = View.GONE
                    }
                    val bundle = Bundle().apply {
                        putSerializable("beer_object", ArrayList(beer))
                        putByteArray("bitmap", byteArray)
                    }
                    Log.d("testing", "FRAGMENT: $beer")
                    if (beer.isNotEmpty()) {
                        findNavController().navigate(R.id.navigation_camera_result, bundle)
                    }
                }
            }
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        var preview: Preview = Preview.Builder().build()

        preview.setSurfaceProvider(previewView.getSurfaceProvider())

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
        } catch (e: Exception) { }
    }

    private fun capturePhoto(onBitmapReady: (Bitmap) -> Unit) {
        val executor = ContextCompat.getMainExecutor(requireContext())
        imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = image.toBitmap()
                onBitmapReady(bitmap)
                image.close()
            }
            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraFragment", "Photo capture failed: ${exception.message}", exception)
            }
        })
    }

    private fun rotateBitMap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun openPhotoLibrary() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 1) {
            cameraUI.visibility = View.GONE
            cameraViewModel.resetBuffer()
            var byteArray = ByteArray(10)
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        val bitmap = Util.getBitmap(requireContext(), it)
                        val image = InputImage.fromBitmap(bitmap, 0)
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 600, 1000, true)
                        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                        byteArray = byteArrayOutputStream.toByteArray()

                        cameraViewModel.recognizeTextFromImage(image)
                }
            }
                loadingScreen.visibility = View.VISIBLE
                cameraViewModel.beerResult.observe(viewLifecycleOwner) { beer ->
                    if (beer.isNotEmpty()) {
                        loadingScreen.visibility = View.GONE
                    }
                    val bundle = Bundle().apply {
                        putSerializable("beer_object", ArrayList(beer))
                        putByteArray("bitmap", byteArray)
                    }
                    if (beer.isNotEmpty()) {
                        findNavController().navigate(R.id.navigation_camera_result, bundle)
                    }
                }
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                0
            )
        }
    }
}