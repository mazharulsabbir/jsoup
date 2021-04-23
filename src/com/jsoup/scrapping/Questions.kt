package com.jsoup.scrapping

data class Questions(
    var no: String,
    var state: String,
    var grade: String,
    var qText: String,
    var questionType: QuestionType,
    var answers: List<String>? = null,
    var answerNumber: List<String>? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}