package com.example.jetnews.data.classifiers

data class Classifier(
    val id: String,
    val name: String,
    val modelTflite: String,
    val classNamesCsv: String,
    val classNamesConfigCsv: String
)

/**
 * Interface to the Interests data layer.
 */
interface ClassifierRepository {

    /**
     * Get relevant topics to the user.
     */
    suspend fun getClassifierList(): Result<List<Classifier>>


    /**
     * Get relevant topics to the user.
     */
    suspend fun getClassifier(): Result<Classifier>


    /**
     * Save config
     */
    suspend fun saveClassifierConfig(): Unit
}
