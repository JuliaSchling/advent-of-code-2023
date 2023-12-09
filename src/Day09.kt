fun main() {

  fun calculateDifferences(allDifferences: MutableList<MutableList<Long>>, historyValues: List<Long>): List<List<Long>> {
    val differences: MutableList<Long> = mutableListOf()
    allDifferences.add(differences)
    var allZeros = true

    historyValues.windowed(size = 2, step = 1, partialWindows = false).forEach { window ->
      val difference = window[1] - window[0]

      differences.add(difference)
      if (difference != 0L) {
        allZeros = false
      }
    }

    return if (allZeros) {
      allDifferences
    } else {
      calculateDifferences(allDifferences, differences)
    }
  }

  fun calculateNextHistoryValue(allValues: MutableList<MutableList<Long>>): Long {
    // start with last two lists of differences and work the way up
    for (i in (allValues.size - 1) downTo 1 step 1) {
      val lowerList = allValues[i]
      val listAbove = allValues[i - 1]

      // take LAST value of each list, calculate new value and add to list above at the END
      listAbove.add(listAbove.last() + lowerList.last())
    }

    return allValues.first().last()
  }

  fun extrapolateFirstHistoryValue(allValues: MutableList<MutableList<Long>>): Long {
    // start with last two lists of differences and work the way up
    for (i in (allValues.size - 1) downTo 1 step 1) {
      val lowerList = allValues[i]
      val listAbove = allValues[i - 1]

      // take FIRST value of each list, calculate new value and add to list above at the BEGINNING
      listAbove.add(0, listAbove.first() - lowerList.first())
    }

    return allValues.first().first()
  }

  fun calculate(input: List<String>, calculateAction: (MutableList<MutableList<Long>>) -> Long): Long {
    var total = 0L

    input.forEach { historyStr ->
      val historyValues = historyStr.split(" ").map { it.toLong() }
      val allDifferences: MutableList<MutableList<Long>> = mutableListOf()
      calculateDifferences(allDifferences, historyValues)

      allDifferences.add(0, historyValues.toMutableList())
      val nextValue: Long = calculateAction(allDifferences)
      total += nextValue
    }

    return total
  }

  fun part1(input: List<String>): Long = calculate(input, ::calculateNextHistoryValue)

  fun part2(input: List<String>): Long = calculate(input, ::extrapolateFirstHistoryValue)

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("9Dec_example")
  check(part1(testInput) == 114L)
  check(part2(testInput) == 2L)

  val input = readInput("9Dec_own")
  part1(input).println()
  part2(input).println()
}
