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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraViewModel(private val repository: BeerRepository) : ViewModel() {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val beerCountMap: MutableMap<Beer, Int> = mutableMapOf()
    var beerResult = MutableLiveData<List<Beer>>()
    var noResult = MutableLiveData<Int>()

    suspend fun getBeerFullName(fullName: String): List<Beer> {
        var beerList: List<Beer> = arrayListOf()
        try {
            Log.d("testing", "Searching for beer: $fullName")
            beerList = repository.getBeerFullName(fullName)
            Log.d("testing", "LIST FOR ${fullName}: ${beerList}")
            //beerResult.postValue(beerList)
        } catch (e: Exception) {
            Log.e("Error", "Failed to fetch beer: ${e.message}", e)
        }
        return beerList
    }

    fun resetBuffer() {
        beerResult.value = emptyList()
    }

    fun recognizeTextFromImage(image: InputImage) {
        beerCountMap.clear()
        noResult.postValue(1)
        viewModelScope.launch {
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    viewModelScope.launch(Dispatchers.IO) {
                        val deferredResults = mutableListOf<Deferred<List<Beer>>>()

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
                                Log.d("TextRecognition", "Line: $lineText")

                                // Iterate through each Element (word or word-like entity) in this Line
                                for (element in line.elements) {
                                    val elementText = element.text
                                    if (elementText.length > 2 && elementText != "and" && elementText != "the") {
                                        deferredResults.add(async {
                                            getBeerFullName("*$elementText*")
                                        })
                                    }
                                    Log.d("TextRecognition", "Element: $elementText")
                                }
                            }
                        }
                        val resultList = deferredResults.awaitAll().flatten()
                        synchronized(beerCountMap) {
                            resultList.forEach { beer ->
                                beerCountMap[beer] = (beerCountMap[beer] ?: 0) + 1
                            }
                        }
                        val sortedList = synchronized(beerCountMap) {
                            beerCountMap.entries.sortedByDescending { it.value }.map { it.key }
                        }
                        withContext(Dispatchers.Main) {
                            if (sortedList.isEmpty()) {
                                noResult.postValue(-1)
                            }
                            beerResult.postValue(sortedList)
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