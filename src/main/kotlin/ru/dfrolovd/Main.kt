package ru.dfrolovd

import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.size != 2) {
        error("Expected launch with command program.java <in> <out>")
    }

    val inputFilename = args[0]
    val outputFilename = args[1]

    val inputFile = Paths.get(inputFilename)
    val outputFile = Paths.get(outputFilename)

    val lines = Files.newBufferedReader(inputFile).use { it.readText() }
    val parsed = parse(lines)

    Files.newBufferedWriter(outputFile).use { writer ->
        writer.write(parsed)
    }
}