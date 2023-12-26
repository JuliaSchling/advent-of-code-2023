import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Result(
  var result: Int = 0
) {
  fun add(number: Int) {
    result += number
  }
}

class LongResult(
  var result: Long = 0
) {
  fun add(number: Long) {
    result += number
  }
}

class IntBag(
  var number: Int? = null
) {
  fun set(num: Int?) {
    number = num
  }
}

fun toCompletelyMutableList(input: List<String>): MutableList<MutableList<Char>> {
  return buildList {
    input.forEach { line ->
      add(line.toMutableList())
    }
  }.toMutableList()
}

fun toCompletelyImmutableList(input: MutableList<MutableList<Char>>): List<String> {
  return buildList {
    input.forEach { chars ->
      var line = ""
      chars.forEach {
        line += it.toString()
      }
      add(line)
    }
  }
}

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
  .toString(16)
  .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
