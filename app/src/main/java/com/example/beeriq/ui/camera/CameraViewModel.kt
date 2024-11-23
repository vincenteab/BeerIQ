package com.example.beeriq.ui.camera

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    val inputText = MutableLiveData<String>()

    fun recognizeTextFromImage(image: InputImage) {
        viewModelScope.launch {
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Process the recognized text
                    val resultText = visionText.text
                    Log.d("TextRecognition", "Full Text: $resultText")

                    // Iterate through each TextBlock (paragraph or larger block)
                    for (textBlock in visionText.textBlocks) {
                        val blockText = textBlock.text
                        //Log.d("TextRecognition", "TextBlock: $blockText")

                        // Iterate through each Line in this TextBlock
                        for (line in textBlock.lines) {
                            val lineText = line.text
                            //Log.d("TextRecognition", "Line: $lineText")

                            // Iterate through each Element (word or word-like entity) in this Line
                            for (element in line.elements) {
                                val elementText = element.text
                                //Log.d("TextRecognition", "Element: $elementText")
                            }
                        }
                    }
                }
        }
    }
}