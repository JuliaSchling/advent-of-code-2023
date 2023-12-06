fun main() {
  fun part1(input: List<String>): Int {
    var total = 0
    input.forEach { line ->

      var firstDigit: Char? = null
      var secondDigit: Char? = null

      line.toCharArray()
        .forEach { char ->
          if (char in '0'..'9') {
            if (firstDigit == null) {
              firstDigit = char
            } else {
              secondDigit = char
            }
          }
        }

      if (firstDigit == null) {
        firstDigit = '0'
      }
      if (secondDigit == null) {
        secondDigit = firstDigit
      }

      total += "$firstDigit$secondDigit".toInt()
    }

    return total
  }

  fun part2(input: List<String>): Int {
    var total = 0
    input.forEach { line ->

      var firstDigit: Char? = null
      var secondDigit: Char? = null

      line.replace("one", "o1e")
        .replace("two", "t2o")
        .replace("three", "t3e")
        .replace("four", "f4r")
        .replace("five", "f5e")
        .replace("six", "s6x")
        .replace("seven", "s7n")
        .replace("eight", "e8t")
        .replace("nine", "n9e")
        .replace("zero", "z0o")
        .toCharArray()
        .forEach { char ->
          if (char in '0'..'9') {
            if (firstDigit == null) {
              firstDigit = char
            } else {
              secondDigit = char
            }
          }
        }

      if (firstDigit == null) {
        firstDigit = '0'
      }
      if (secondDigit == null) {
        secondDigit = firstDigit
      }

      total += "$firstDigit$secondDigit".toInt()
    }

    return total
  }

  // test if implementation meets criteria from the description, like:
  val testInput1 = readInput("1Dec_example_part1")
  check(part1(testInput1) == 142)

  val testInput2 = readInput("1Dec_example_part2")
  check(part2(testInput2) == 281)

  val input = readInput("1Dec_own")
  part1(input).println()
  part2(input).println()
}
