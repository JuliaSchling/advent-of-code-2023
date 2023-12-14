import kotlin.math.min

class Pattern(
  val pattern: List<String>,
  val wrongVerticalMirrorTheories: MutableList<Int> = mutableListOf(),
  val wrongHorizontalMirrorTheories: MutableList<Int> = mutableListOf(),
  var currentWorkingCandidate: Int? = null,
)

fun main() {

  // TODO find out why this function randomly looses lines
  fun getPatternsFromInput_BROKEN(input: List<String>): List<Pattern> {
    val varInput = input.toMutableList()
    val patterns: MutableList<Pattern> = mutableListOf()
    while (varInput.isNotEmpty()) {
      val pattern = varInput.takeWhile { it.isNotEmpty() }
      patterns.add(Pattern(pattern))

      varInput.removeAll(pattern)
      if (varInput.isNotEmpty()) varInput.removeAt(0)
    }
    return patterns
  }

  fun getPatternsFromInput(input: List<String>): List<Pattern> {
    val patterns: MutableList<Pattern> = mutableListOf()

    var currentPattern: MutableList<String> = mutableListOf()
    input.forEach { line ->
      if (line.isNotEmpty()) {
        currentPattern.add(line)
      } else {
        patterns.add(Pattern(currentPattern))
        currentPattern = mutableListOf()
      }
    }
    patterns.add(Pattern(currentPattern))

    return patterns
  }

  fun findMirrorCandidate(line: String, patternDto: Pattern, wrongTheoriesRef: MutableList<Int>): Boolean {
    val stack = ArrayDeque<Char>()

    var inStackingMode = true
    var mirrorTheory: Int? = null

    for (columnIndex in line.indices) {
      val char = line[columnIndex]
      val topChar = stack.lastOrNull()

      if (inStackingMode) {
        if (topChar != null && topChar == char && !wrongTheoriesRef.contains(columnIndex)) {
          // same char as before: we might've found a mirror, start to pop
          // + we check the column number to make sure to walk over the columns that are already marked as wrong candidates
          stack.removeLast()
          inStackingMode = false
          mirrorTheory = columnIndex // mirror might be between columnIndex-1 and columnIndex

        } else {
          stack.addLast(char)
        }
      } else {
        // in popping mode
        if (topChar != null) {
          if (topChar != char) {
            // theory for mirror didn't work out
            // remember the column as the wrong one
            wrongTheoriesRef.add(mirrorTheory!!)
            return true

          } else {
            // keep popping
            stack.removeLast()
          }
        } else {
          // we reached the end of the stack, it's empty again. And no errors. This could be a mirror.
          // -> test theory on next lines. If it doesn't work, start over at first line
          patternDto.currentWorkingCandidate = mirrorTheory
          return true
        }
      }
    }
    // cleaning up after we went over the whole line:
    return if (inStackingMode) {
      // didn't work out at all, no vertical mirror in this row
      // -> continue checking columns for horizontal mirrors
      false

    } else {
      // still in popping mode, no errors so far. Could be a mirror, e.g. .#.##.
      // -> test theory on next lines. If it doesn't work, start over at first line
      patternDto.currentWorkingCandidate = mirrorTheory
      true
    }
  }

  fun testTheoryOnString(currentCandidate: Int, str: String): Boolean {
    val part1 = str.substring(0, currentCandidate)
    val part2 = str.substring(currentCandidate, str.length)
    val lengthToCompare = min(part1.length, part2.length)

    var j = part1.length - 1
    for (i in 0 until lengthToCompare) {
      if (part1[j] != part2[i]) {
        return false
      }
      j--
    }
    return true
  }

  fun testTheoryOnAllRows(currentCandidate: Int, pattern: List<String>): Boolean {
    pattern.forEach { row ->
      if (!testTheoryOnString(currentCandidate, row)) {
        return false
      }
    }
    return true
  }

  fun getColumnAt(pattern: List<String>, i: Int): String {
    return buildString {
      pattern.forEach { row ->
        this.append(row[i])
      }
    }
  }

  fun testTheoryOnAllColumns(currentCandidate: Int, pattern: List<String>): Boolean {
    for (i in pattern[0].indices) {
      if (!testTheoryOnString(currentCandidate, getColumnAt(pattern, i))) {
        return false
      }
    }
    return true
  }

  fun part1(input: List<String>): Int {
    // separate patterns
    val patterns: List<Pattern> = getPatternsFromInput(input)
    var patternCounter = 0

    // search each pattern for mirrors
    var totalResult = 0
    patterns.forEach patternLoop@{ patternDto ->
      patternCounter++

      // first search for vertical mirror on rows
      var keepSearchingThisRow = true
      while (keepSearchingThisRow) {
        keepSearchingThisRow = findMirrorCandidate(patternDto.pattern[0], patternDto, patternDto.wrongVerticalMirrorTheories)

        if (patternDto.currentWorkingCandidate != null) {
          val currentCandidate: Int = patternDto.currentWorkingCandidate!!

          if (testTheoryOnAllRows(currentCandidate, patternDto.pattern)) {
            // a working theory :) add to total result and stop at this pattern
            // println("RESULT : vertical result for pattern $patternCounter : $currentCandidate")
            totalResult += currentCandidate
            return@patternLoop

          } else {
            // nope. return to start.
            patternDto.wrongVerticalMirrorTheories.add(currentCandidate)
            patternDto.currentWorkingCandidate = null
          }
        }
      }

      // couldn't find vertical mirror on rows, continue searching horizontal mirror on columns
      var keepSearchingThisColumn = true
      while (keepSearchingThisColumn) {
        keepSearchingThisColumn =
          findMirrorCandidate(getColumnAt(patternDto.pattern, 0), patternDto, patternDto.wrongHorizontalMirrorTheories)

        if (patternDto.currentWorkingCandidate != null) {
          val currentCandidate: Int = patternDto.currentWorkingCandidate!!

          if (testTheoryOnAllColumns(currentCandidate, patternDto.pattern)) {
            // a working theory :) add to total result and stop at this pattern
            // println("RESULT : horizontal result for pattern $patternCounter : $currentCandidate")
            totalResult += currentCandidate * 100
            return@patternLoop

          } else {
            // nope. return to start.
            patternDto.wrongHorizontalMirrorTheories.add(currentCandidate)
            patternDto.currentWorkingCandidate = null
          }
        }
      }

      println("ERROR : couldn't find any mirror for pattern $patternCounter. There must be something wrong.")
      println("pattern: \n${patternDto.pattern}")
    }

    return totalResult
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("13Dec_example")
  check(part1(testInput) == 405)

  val input = readInput("13Dec_own")
  part1(input).println()
}

