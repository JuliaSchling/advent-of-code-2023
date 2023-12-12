import kotlin.math.abs

class Galaxy(
  var lineIndex: Int,
  var columnIndex: Int,
)

fun main() {

  fun calculateSumOfDistances(input: List<String>, factor: Int): Long {
    // build various lists
    val galaxies: MutableList<Galaxy> = mutableListOf()
    val setOfEmptyLines: MutableSet<Int> = mutableSetOf()
    val setOfNonEmptyColumns: MutableSet<Int> = mutableSetOf()
    val setOfAllColumns: MutableSet<Int> = mutableSetOf()

    for (lineIndex in input.indices) {
      val line = input[lineIndex]

      var foundNoGalaxy = true
      for (columnIndex in line.indices) {
        setOfAllColumns.add(columnIndex)

        if (line[columnIndex] == '#') {
          // we found a galaxy! memorize its position:
          galaxies.add(Galaxy(lineIndex, columnIndex))

          // this line isn't empty
          foundNoGalaxy = false
          // this column isn't empty
          setOfNonEmptyColumns.add(columnIndex)
        }
      }
      if (foundNoGalaxy) {
        // this is an empty line: memorize
        setOfEmptyLines.add(lineIndex)
      }
    }

    val setOfEmptyColumns: Set<Int> = setOfAllColumns - setOfNonEmptyColumns

    // change coordinates of galaxies
    for (galaxy in galaxies) {
      // search for the number of empty lines that are before the lineIndex of the galaxy
      repeat(setOfEmptyLines.filter { it < galaxy.lineIndex }.size) {
        galaxy.lineIndex += factor
      }

      // search for the number of empty columns that are before the columnIndex of the galaxy
      repeat(setOfEmptyColumns.filter { it < galaxy.columnIndex }.size) {
        galaxy.columnIndex += factor
      }
    }

    // calculate distances between all galaxies
    var result = 0L
    for (i in galaxies.indices) {
      val galaxy = galaxies[i]

      for (j in i + 1 until galaxies.size) {
        val otherGalaxy = galaxies[j]

        // calculate distance between the two galaxies
        val distance = abs(galaxy.lineIndex - otherGalaxy.lineIndex) + abs(galaxy.columnIndex - otherGalaxy.columnIndex)
        result += distance
      }
    }

    return result
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("11Dec_example")
  check(calculateSumOfDistances(testInput, 1) == 374L)
  check(calculateSumOfDistances(testInput, 9) == 1030L)
  check(calculateSumOfDistances(testInput, 99) == 8410L)

  val input = readInput("11Dec_own")
  calculateSumOfDistances(input, 1).println()
  calculateSumOfDistances(input, 999999).println()
}
