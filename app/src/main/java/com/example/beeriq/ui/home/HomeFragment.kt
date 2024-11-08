package com.example.beeriq.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.beeriq.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class HomeFragment : Fragment() {
    private lateinit var cameraResult: ActivityResultLauncher<Void?>
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                0
            )
        }

        cameraResult = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                // Update the ViewModel with the captured bitmap
                viewModel.userImage.value = it
            }
        }

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.userImage.observe(viewLifecycleOwner) { it ->

            val image = InputImage.fromBitmap(it, 0)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Process the recognized text (top-level Text object)
                    val resultText = visionText.text
                    Log.d("TextRecognition", "Full Text: $resultText")
                    val fullText: TextView = view.findViewById(R.id.full_text)
                    fullText.text = resultText

                    // Iterate through each TextBlock (paragraph or larger block)
                    for (textBlock in visionText.textBlocks) {
                        val blockText = textBlock.text
                        Log.d("TextRecognition", "TextBlock: $blockText")

                        // Iterate through each Line in this TextBlock
                        for (line in textBlock.lines) {
                            val lineText = line.text
                            Log.d("TextRecognition", "Line: $lineText")

                            // Iterate through each Element (word or word-like entity) in this Line
                            for (element in line.elements) {
                                val elementText = element.text
                                Log.d("TextRecognition", "Element: $elementText")
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("TextRecognition", "Text recognition failed", e)
                }
        }

        val changeButton: Button = view.findViewById(R.id.button)
        changeButton.setOnClickListener{
            try {
                cameraResult.launch(null)
            } catch (e: Exception) {
                Log.e("CameraLaunch", "Error launching camera", e)
            }
        }
    }
}



//class HomeFragment : Fragment() {
//
//    private var _binding: FragmentHomeBinding? = null
//
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
//
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}