fun main() {

  fun finishCollectedNumber(partNumbers: MutableMap<Int, MutableList<PartNumber>>, collectedNumber: String, linePos: Int, ypos: Int) {
    if (collectedNumber != "") {
      val existingList: MutableList<PartNumber>? = partNumbers[linePos]
      if (existingList == null) {
        partNumbers[linePos] = mutableListOf(
          PartNumber(
            number = collectedNumber.toInt(),
            posLine = linePos,
            posy1 = ypos - collectedNumber.length + 1,
            posy2 = ypos,
          )
        )
      } else {
        existingList.add(
          PartNumber(
            number = collectedNumber.toInt(),
            posLine = linePos,
            posy1 = ypos - collectedNumber.length + 1,
            posy2 = ypos,
          )
        )
      }
    }
  }

  fun findAdjacentPartNumbers(partNumbers: MutableList<PartNumber>?, starPosy: Int): MutableList<PartNumber> {
    val resultPartNumbers: MutableList<PartNumber> = mutableListOf()

    if (partNumbers.isNullOrEmpty()) {
      return resultPartNumbers
    }

    for (pn in partNumbers) {
      if (starPosy >= (pn.posy1 - 1) && starPosy <= (pn.posy2 + 1)) {
        // star is adjacent to this part-number
        resultPartNumbers.add(pn)
      }
    }

    return resultPartNumbers
  }

  fun part2(input: List<String>): Int {
    // parse input
    val partNumbers: MutableMap<Int, MutableList<PartNumber>> = mutableMapOf()
    val starPositions: MutableMap<Int, MutableList<Int>> = mutableMapOf()

    var linePos: Int = -1
    input.forEach { line ->
      linePos++

      var ypos: Int = -1
      var collectedNumber = ""

      line.toCharArray().forEach { char ->
        ypos++

        when (char) {
          in '0'..'9' -> {
            collectedNumber += char
          }
          '*' -> {
            val existingList: MutableList<Int>? = starPositions[linePos]
            if (existingList == null) {
              starPositions[linePos] = mutableListOf(ypos)
            } else {
              existingList.add(ypos)
            }

            finishCollectedNumber(partNumbers, collectedNumber, linePos, ypos - 1)
            collectedNumber = ""
          }
          else -> {
            // we found a symbol other than * or digit
            finishCollectedNumber(partNumbers, collectedNumber, linePos, ypos - 1)
            collectedNumber = ""
          }
        }
      }
      // if we found a number before we need to collect it
      finishCollectedNumber(partNumbers, collectedNumber, linePos, ypos)
    }

    // find stars adjacent to exactly 2 numbers
    var result = 0
    for (line in starPositions.keys) {
      val starPositionsOfOneLine = starPositions[line]

      for (starPosy in starPositionsOfOneLine!!) {
        // collect all adjacent part numbers for each star
        val resultPartNumbers: MutableList<PartNumber> = mutableListOf()
        resultPartNumbers.addAll(findAdjacentPartNumbers(partNumbers[line - 1], starPosy))
        resultPartNumbers.addAll(findAdjacentPartNumbers(partNumbers[line], starPosy))
        resultPartNumbers.addAll(findAdjacentPartNumbers(partNumbers[line + 1], starPosy))

        if (resultPartNumbers.size == 2) {
          // a gear!
          result += (resultPartNumbers[0].number * resultPartNumbers[1].number)
        }
      }
    }
    return result
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("3Dec_example")
  check(part2(testInput) == 467835)

  val input = readInput("3Dec_own")
  part2(input).println()
}
