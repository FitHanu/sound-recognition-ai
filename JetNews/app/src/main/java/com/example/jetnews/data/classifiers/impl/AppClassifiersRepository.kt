package com.example.jetnews.data.classifiers.impl


import com.example.jetnews.data.classifiers.Classifier
import com.example.jetnews.data.classifiers.ClassifierRepository

/**
 * List, save, get config from file
 */
class AppClassifiersRepository: ClassifierRepository {

    override suspend fun getClassifierList(): Result<List<Classifier>> {
        TODO("Not yet implemented")
    }

    override suspend fun getClassifier(): Result<Classifier> {
        val config = Classifier(
            id = "yamnet",
            name = "yamnet",
            modelTflite = "yamnet.tflite",
            classNamesCsv = "classes_default_config.csv",
            classNamesConfigCsv = "classes_default_config.csv"
        )
        return Result.success(config)
    }

    override suspend fun saveClassifierConfig() {
        TODO("Not yet implemented")
    }

}