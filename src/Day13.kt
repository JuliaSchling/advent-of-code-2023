import kotlin.math.min

class Pattern(
  val pattern: List<String>,
  var verticalMirror: Int? = null,
  var horizontalMirror: Int? = null,
  var verticalMirrorAfterSmudge: Int? = null,
  var horizontalMirrorAfterSmudge: Int? = null,
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

  fun findMirrorCandidate(line: String, currentWorkingCandidate: IntBag, wrongTheoriesRef: MutableList<Int>): Boolean {
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
          currentWorkingCandidate.set(mirrorTheory)
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
      currentWorkingCandidate.set(mirrorTheory)
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

  fun findVerticalMirror(pattern: List<String>, wrongVerticalMirrorTheory: Int? = null): Int? {
    val wrongVerticalMirrorTheories: MutableList<Int> = mutableListOf()
    wrongVerticalMirrorTheory?.let { wrongVerticalMirrorTheories.add(wrongVerticalMirrorTheory) }

    val currentWorkingCandidate = IntBag(null)
    var keepSearchingThisRow = true

    while (keepSearchingThisRow) {
      keepSearchingThisRow = findMirrorCandidate(pattern[0], currentWorkingCandidate, wrongVerticalMirrorTheories)

      if (currentWorkingCandidate.number != null) {
        val currentCandidate: Int = currentWorkingCandidate.number!!

        if (testTheoryOnAllRows(currentCandidate, pattern)) {
          return currentCandidate

        } else {
          // nope. return to start.
          wrongVerticalMirrorTheories.add(currentCandidate)
          currentWorkingCandidate.set(null)
        }
      }
    }

    return null
  }

  fun findHorizontalMirror(pattern: List<String>, wrongHorizontalMirrorTheory: Int? = null): Int? {
    val wrongHorizontalMirrorTheories: MutableList<Int> = mutableListOf()
    wrongHorizontalMirrorTheory?.let { wrongHorizontalMirrorTheories.add(wrongHorizontalMirrorTheory) }

    val currentColumn = getColumnAt(pattern, 0)
    val currentWorkingCandidate = IntBag(null)
    var keepSearchingThisColumn = true

    while (keepSearchingThisColumn) {
      keepSearchingThisColumn = findMirrorCandidate(currentColumn, currentWorkingCandidate, wrongHorizontalMirrorTheories)

      if (currentWorkingCandidate.number != null) {
        val currentCandidate: Int = currentWorkingCandidate.number!!

        if (testTheoryOnAllColumns(currentCandidate, pattern)) {
          return currentCandidate

        } else {
          // nope. return to start.
          wrongHorizontalMirrorTheories.add(currentCandidate)
          currentWorkingCandidate.set(null)
        }
      }
    }

    return null
  }

  fun findMirrorForEachPattern(patterns: List<Pattern>) {
    var patternCounter = 0
    patterns.forEach { patternDto ->
      patternCounter++

      patternDto.verticalMirror = findVerticalMirror(patternDto.pattern)

      if (patternDto.verticalMirror == null) {
        patternDto.horizontalMirror = findHorizontalMirror(patternDto.pattern)
      }

      if (patternDto.verticalMirror == null && patternDto.horizontalMirror == null) {
        println("ERROR : couldn't find any mirror for pattern $patternCounter. There must be something wrong.")
      }
    }
  }

  fun flip(mutablePattern: MutableList<MutableList<Char>>, i: Int, j: Int) {
    when (mutablePattern[i][j]) {
      '.' -> mutablePattern[i][j] = '#'
      '#' -> mutablePattern[i][j] = '.'
    }
  }

  fun findAlternativeMirrorForEachPattern(patterns: List<Pattern>) {
    patterns.forEach patternLoop@{ patternDto ->

      val mutablePattern = toCompletelyMutableList(patternDto.pattern)

      for (i in mutablePattern.indices) {
        for (j in mutablePattern[0].indices) {
          flip(mutablePattern, i, j)

          patternDto.verticalMirrorAfterSmudge = findVerticalMirror(toCompletelyImmutableList(mutablePattern), patternDto.verticalMirror)

          if (patternDto.verticalMirrorAfterSmudge == null) {
            patternDto.horizontalMirrorAfterSmudge =
              findHorizontalMirror(toCompletelyImmutableList(mutablePattern), patternDto.horizontalMirror)
          }

          if (patternDto.verticalMirrorAfterSmudge != null || patternDto.horizontalMirrorAfterSmudge != null) {
            return@patternLoop
          }

          flip(mutablePattern, i, j)
        }
      }
    }
  }

  fun part1(input: List<String>): Int {
    val patterns: List<Pattern> = getPatternsFromInput(input)

    findMirrorForEachPattern(patterns)

    var totalResult = 0
    patterns.forEach { pattern ->
      pattern.verticalMirror?.let { totalResult += it }
      pattern.horizontalMirror?.let { totalResult += it * 100 }
    }
    return totalResult
  }

  fun part2(input: List<String>): Int {
    val patterns: List<Pattern> = getPatternsFromInput(input)

    findMirrorForEachPattern(patterns)

    findAlternativeMirrorForEachPattern(patterns)

    var totalResult = 0
    patterns.forEach { pattern ->
      pattern.verticalMirrorAfterSmudge?.let { totalResult += it }
      pattern.horizontalMirrorAfterSmudge?.let { totalResult += it * 100 }
    }
    return totalResult
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("13Dec_example")
  check(part1(testInput) == 405)
  check(part2(testInput) == 400)

  val input = readInput("13Dec_own")
  part1(input).println()
  part2(input).println()
}
