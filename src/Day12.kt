fun main() {

  fun isValid(springReport: String, conditionRecords: List<Int>): Boolean {
    var pattern = "^\\.*"
    conditionRecords.forEach { rec ->
      pattern += "#{$rec}\\.+"
    }
    pattern = pattern.dropLast(1)
    pattern += "*\$"

    return if (pattern.toRegex().matches(springReport)) {
      println("valid: $springReport (${conditionRecords.joinToString()})")
      true
    } else {
      println("not valid: $springReport (${conditionRecords.joinToString()})")
      false
    }
  }

  fun calculate(springReport: String, conditionRecords: List<Int>): Int {
    // breaking conditions for recursive loop
    if (conditionRecords.isEmpty()) {
      return if (springReport.contains("#")) {
        0
      } else {
        1
      }
    }
    if (springReport.isEmpty()) {
      return 1
    }

    // search for the biggest condition in the condition records
    val maxCondition =
      conditionRecords.maxOrNull() ?: throw IllegalStateException("what?? each list of conditions should have a max number")
    val indexOfMax = conditionRecords.indexOf(maxCondition)

    // separate the condition records in two lists: before and after the biggest condition
    val part1 = conditionRecords.subList(0, indexOfMax)
    val part2 = conditionRecords.subList(indexOfMax + 1, conditionRecords.size)

    // cut out a window in the middle of the spring report where we will search for valid places/ windows for the biggest condition.
    // leave just enough space for the rest of the conditions at the start and the end
    val startSubSeq = if (part1.isNotEmpty()) {
      part1.sum() + part1.size
    } else {
      0
    }
    val endSubSeq = if (part2.isNotEmpty()) {
      springReport.length - (part2.sum() + part2.size)
    } else {
      springReport.length
    }

    var result = 0
    for (windowStart in startSubSeq..(endSubSeq - maxCondition)) {
      val windowEnd = windowStart + maxCondition
      val window = springReport.substring(windowStart, windowEnd)

      if (window.contains(".") || springReport.getOrNull(windowStart - 1) == '#' || springReport.getOrNull(windowEnd) == '#') {
        // invalid window:
        // if it contains a .
        // or if there's a # before or after the window we're looking at.
      } else {
        // we found a valid window
        // call recursively for the parts before and after (leave out one buffer char around the window)
        val substr1 = if (windowStart != 0) springReport.substring(0, windowStart - 1) else ""
        val result1 = calculate(substr1, part1)
        val substr2 = if (windowEnd + 1 <= springReport.length) springReport.substring(windowEnd + 1, springReport.length) else ""
        val result2 = calculate(substr2, part2)

        result += (result1 * result2)
      }
    }
    return result
  }

  fun part1(input: List<String>): Int {
    var total = 0
    input.forEach { line ->
      val s = line.split(" ")
      val springReport = s[0]
      val conditionRecords = s[1].split(",").map { it.toInt() }

      // stuff I found out but didn't need for the solution:
      // val indicesOfQuestionMarks = "\\?".toRegex().findAll(springReport).map { it.range.first }.toList()
      // val numberOfQuestionMarks = indicesOfQuestionMarks.size
      // val numberOfHashtagsInFinalReport = conditionRecords.reduce { acc, i -> acc + i }
      // val numberOfHashtagsAlreadyInString = "#".toRegex().findAll(springReport).count()
      // val numberOfHashtagsThatNeedToBeInserted = numberOfHashtagsInFinalReport - numberOfHashtagsAlreadyInString
      // val numberOfDotsThatNeedToBeInserted = numberOfQuestionMarks - numberOfHashtagsThatNeedToBeInserted
      // isValid(springReport, conditionRecords)

      val result = calculate(springReport, conditionRecords)
      total += result
    }

    return total
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("12Dec_example")
  check(part1(testInput) == 21)

  val input = readInput("12Dec_own")
  part1(input).println()
}
