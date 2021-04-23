package com.jsoup.scrapping

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object QuestionParser {
    fun String.parseHtml(): Questions {
        val jsoup = Jsoup.parse(this)

        val no = jsoup.getElementsByClass("no").text()
        val state = jsoup.getElementsByClass("state").text()
        val grade = jsoup.getElementsByClass("grade").text()
        val qText = jsoup.getElementsByClass("qtext").html()
        val qType = getQuesType(jsoup)
        val answers = funGetAnswers(jsoup, qType)
        val answerNumber = funGetAnswerNumber(jsoup, qType)

        println("$no, $state, $grade, $qText, $qType, $answerNumber, $answers")
        println()
        return Questions(no, state, grade, qText, qType, answers, answerNumber)
    }

    private fun funGetAnswerNumber(doc: Document, type: QuestionType): List<String>? {
        val answerNumber = mutableListOf<String>()

        when (type) {
            QuestionType.MULTI_CHOICE -> {
                doc.getElementsByClass("answer").forEach {
                    it.getElementsByClass("answernumber").forEach {
                        answerNumber.add(it.text())
                    }
                }
            }
            QuestionType.TRUE_FALSE -> {
                doc.getElementsByClass("answer").forEach {
                    it.getElementsByClass("prompt").forEach {
                        answerNumber.add(it.text())
                    }
                }
            }
            QuestionType.SHORT_ANS -> {

            }
        }
        return answerNumber
    }

    private fun getQuesType(names: Document): QuestionType {
        return when {
            !names.getElementsByClass("shortanswer").isNullOrEmpty() -> QuestionType.SHORT_ANS
            !names.getElementsByClass("truefalse").isNullOrEmpty() -> QuestionType.TRUE_FALSE
            else -> QuestionType.MULTI_CHOICE
        }
    }

    private fun funGetAnswers(doc: Document, type: QuestionType): List<String>? {
        val answers = mutableListOf<String>()
        when (type) {
            QuestionType.MULTI_CHOICE -> {
                doc.getElementsByClass("answer").forEach {
                    it.getElementsByClass("ml-1").forEach {
                        answers.add(it.text())
                    }
                }
            }
            QuestionType.TRUE_FALSE -> {
                doc.getElementsByClass("answer").forEach {
                    it.getElementsByClass("mr-2").forEach {
                        answers.add(it.text())
                    }
                }
            }
            QuestionType.SHORT_ANS -> {
                answers.add(doc.getElementsByClass("ablock").text())
            }
        }
        return answers
    }
}