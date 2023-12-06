import kotlin.math.pow

fun convertLinePart(part: String) = part.trim().split(" ").mapNotNull { if (it.isNotEmpty()) it.toInt() else null }.toSet()

fun main() {

  fun part1(input: List<String>): Int {
    var total = 0
    input.forEach { line ->

      val numberParts = line.split(":")[1].split("|")
      val winningSet: Set<Int> = convertLinePart(numberParts[0])
      val ownSet: Set<Int> = convertLinePart(numberParts[1])

      val intersection = ownSet.intersect(winningSet)

      if (intersection.isNotEmpty()) {
        val base = 2.0F
        val result = base.pow(intersection.size - 1)
        total += result.toInt()
      }
    }
    return total
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("4Dec_example")
  check(part1(testInput) == 13)

  val input = readInput("4Dec_own")
  part1(input).println()
}
