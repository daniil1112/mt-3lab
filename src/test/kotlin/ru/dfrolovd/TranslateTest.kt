package ru.dfrolovd

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TranslateTest {

    fun getFromFilename(file: String): String {
        return this.javaClass.packageName.replace(".", "/") + "/from/${file}"
    }

    fun getToFilename(file: String): String {
        return this.javaClass.packageName.replace(".", "/") + "/to/${file}"
    }

    fun canonizeResourcesFilenames(): List<Arguments> {
        val fileNames = listOf("variables.java", "functions.java", "for.java", "if.java", "while.java", "combine.java")
        return fileNames.map { Arguments.arguments(it) }
    }

    @ParameterizedTest
    @MethodSource("canonizeResourcesFilenames")
    fun testTranslate(file: String) {
        val from = getResourceAsText(getFromFilename(file))!!
        val expectedResult = getResourceAsText(getToFilename(file))!!
        val result = parse(from)
        assertEquals(expectedResult, result)
    }

    @ParameterizedTest
    @MethodSource("canonizeResourcesFilenames")
    fun testNotMovingPoint(file: String) {
        val from = getResourceAsText(getToFilename(file))!!
        val result = parse(from)
        assertEquals(from, result)
    }

    private fun getResourceAsText(path: String): String? =
        this.javaClass.classLoader.getResource(path)?.readText()
}