fun main() {

  fun determineResult(columnLength: Int, startRow: Int, roundedRocks: Int): Int {
    if (roundedRocks <= 0) {
      return 0
    }

    var total = 0
    val startWeigh = columnLength - startRow
    for (i in 0 until roundedRocks) {
      total += startWeigh - i
    }
    return total
  }

  fun part1(input: List<String>): Int {
    val columnLength = input.size
    var total = 0

    for (i in input[0].indices) {
      var roundedRocks = 0
      var startRow = 0

      for (j in 0 until columnLength) {
        val char = input[j][i]

        if (char == 'O') {
          roundedRocks++

        } else if (char == '#') {
          val result = determineResult(columnLength, startRow, roundedRocks)
          total += result

          startRow = j + 1
          roundedRocks = 0
        }
      }
      val result = determineResult(columnLength, startRow, roundedRocks)
      total += result
    }

    return total
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("14Dec_example")
  check(part1(testInput) == 136)

  val input = readInput("14Dec_own")
  part1(input).println()
}
