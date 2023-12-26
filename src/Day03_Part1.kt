class PartNumber(
  val number: Int = 0,
  val posLine: Int = -1,
  val posy1: Int = -1,
  val posy2: Int = -1,
)

fun MutableList<PartNumber>.addCollectedNumber(collectedNumber: String, linePos: Int, ypos: Int) {
  if (collectedNumber != "") {
    add(
      PartNumber(
        number = collectedNumber.toInt(),
        posLine = linePos,
        posy1 = ypos - collectedNumber.length + 1,
        posy2 = ypos,
      )
    )
  }
}

fun main() {

  fun checkLine(pn: PartNumber, line: MutableList<Int>?, resultPartNumbers: Result): Boolean {
    if (line.isNullOrEmpty()) {
      return false
    }

    // check if there's a symbol between (posy1-1) and (posy+1)
    for (posy in line) {
      if (posy < (pn.posy1 - 1) || posy > (pn.posy2 + 1)) {
        continue
      } else {
        // adjacent symbol, this is a "real" part number
        resultPartNumbers.add(pn.number)
        return true
      }
    }
    return false
  }

  fun part1(input: List<String>): Int {
    // parse input
    val partNumbers: MutableList<PartNumber> = mutableListOf()
    val symbolPositions: MutableMap<Int, MutableList<Int>> = mutableMapOf()

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
          '.' -> {
            partNumbers.addCollectedNumber(collectedNumber, linePos, ypos - 1)
            collectedNumber = ""
          }
          else -> {
            // we found a symbol other than . or digit
            val existingList: MutableList<Int>? = symbolPositions[linePos]
            if (existingList == null) {
              symbolPositions[linePos] = mutableListOf(ypos)
            } else {
              existingList.add(ypos)
            }

            partNumbers.addCollectedNumber(collectedNumber, linePos, ypos - 1)
            collectedNumber = ""
          }
        }
      }
      // if we found a number before we need to collect it
      partNumbers.addCollectedNumber(collectedNumber, linePos, ypos)
    }

    // find numbers adjacent to symbols
    val resultPartNumbers = Result()
    for (pn in partNumbers) {
      if (checkLine(pn, symbolPositions[pn.posLine - 1], resultPartNumbers)) continue
      if (checkLine(pn, symbolPositions[pn.posLine], resultPartNumbers)) continue
      if (checkLine(pn, symbolPositions[pn.posLine + 1], resultPartNumbers)) continue
    }
    return resultPartNumbers.result
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("3Dec_example")
  check(part1(testInput) == 4361)

  val input = readInput("3Dec_own")
  part1(input).println()
}
