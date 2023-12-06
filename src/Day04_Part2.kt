class Scratchcard(
  val cardNumber: Int = 0,
  val numberOfMatchingNums: Int = 0,
  var numberOfCopies: Int = 1,
) {
  fun addCopy() {
    numberOfCopies++
  }
}

fun main() {

  fun part2(input: List<String>): Int {
    // collect scratchcards
    val scratchcards: MutableList<Scratchcard> = mutableListOf()
    input.forEach { line ->

      val firstParts = line.split(":")
      val cardNumber = firstParts[0].filter { it.isDigit() }.toInt()

      val numberParts = firstParts[1].split("|")
      val winningSet: Set<Int> = convertLinePart(numberParts[0])
      val ownSet: Set<Int> = convertLinePart(numberParts[1])

      val intersection = ownSet.intersect(winningSet)

      scratchcards += Scratchcard(cardNumber = cardNumber, numberOfMatchingNums = intersection.size)
    }

    // multiply scratchcards
    for (i in 0 until scratchcards.size) {
      val sc = scratchcards[i]
      if (sc.numberOfMatchingNums > 0) {
        for (j in 1 until sc.numberOfMatchingNums + 1) {
          for (k in 0 until sc.numberOfCopies) {
            scratchcards[i + j].addCopy()
          }
        }
      }
    }

    // count number of copies
    var result = 0
    scratchcards.forEach { sc ->
      result += sc.numberOfCopies
    }
    return result
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("4Dec_example")
  check(part2(testInput) == 30)

  val input = readInput("4Dec_own")
  part2(input).println()
}
