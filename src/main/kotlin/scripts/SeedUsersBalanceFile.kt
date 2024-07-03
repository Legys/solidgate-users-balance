package scripts
import java.io.File
import java.io.PrintWriter
import kotlin.random.Random

public enum class FileType(
    val fileName: String,
) {
    LargeFile("large-file.txt"),
    SmallFile("small-file.txt"),
    CorruptedFile("corrupted-file.txt"),
}

fun generateLine(
    id: Int,
    out: PrintWriter,
) {
    val balance = Random.nextInt(100, 701)
    out.println("${id + 1}:$balance")
}

fun generateFile(
    outputDir: File,
    useCase: FileType,
) {
    val file = File(outputDir, useCase.fileName)

    when (useCase) {
        FileType.LargeFile -> {
            file.printWriter().use { out ->
                repeat(1_000_000) { index ->
                    generateLine(index, out)
                }
            }
        }
        FileType.SmallFile -> {
            file.printWriter().use { out ->
                repeat(10) { index ->
                    generateLine(index, out)
                }
            }
        }
        FileType.CorruptedFile -> {
            file.printWriter().use { out ->
                out.println("1:100")
                out.println("2:200")
                out.println("3:300")
                out.println("4:400")
                out.println("5:500")
                out.println("6:abc")
                out.println("7:700")
                out.println("8:fff")
                out.println("9:900")
                out.println("10:1000")
            }
        }
    }
}

fun main() {
    val outputDir = File("artifacts")
    if (!outputDir.exists()) {
        outputDir.mkdir()
    }

    FileType.entries.forEach { generateFile(outputDir, it) }
    println("Text file with 1 million entries has been created successfully.")
}
