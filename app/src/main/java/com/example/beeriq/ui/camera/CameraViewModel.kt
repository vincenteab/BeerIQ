package com.example.beeriq.ui.camera

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.beeriq.data.local.beerDatabase.Beer
import com.example.beeriq.data.local.beerDatabase.BeerRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CameraViewModel(private val repository: BeerRepository) : ViewModel() {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    var beerResult = MutableLiveData<Beer?>()

    fun getBeerFullName(fullName: String): MutableLiveData<Beer?> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("testing", "Searching for beer: $fullName")
                val beer = repository.getBeerFullName(fullName)
                beerResult.postValue(beer)
            } catch (e: Exception) {
                Log.e("Error", "Failed to fetch beer: ${e.message}", e)
            }
        }
        return beerResult
    }

    fun recognizeTextFromImage(image: InputImage) {
        viewModelScope.launch {
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Process the recognized text
                    val resultText = visionText.text
                    beerResult = getBeerFullName("Asahi Breweries Ltd Asahi Premium")
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

class CameraViewModelFactory(private val repository: BeerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            return CameraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}