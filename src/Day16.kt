import kotlin.math.max

enum class MovementDir {
  RIGHTWARD,
  LEFTWARD,
  UPWARD,
  DOWNWARD,
  SPLIT_UP_DOWN,
  SPLIT_LEFT_RIGHT;
}

class Point(
  val x: Int,
  val y: Int,
  val char: Char,
  val visitedDirs: MutableSet<MovementDir> = mutableSetOf(),
)

fun main() {

  fun isValidPoint(x: Int, y: Int, maxX: Int, maxY: Int): Boolean = x in 0 until maxX && y in 0 until maxY

  /**
   * either returns the next point according to the movement direction or null when we hit a wall
   */
  fun findNextPoint(currentPoint: Point, currentMovementDir: MovementDir, input: List<String>, maxX: Int, maxY: Int): Point? {
    val nextX: Int
    val nextY: Int
    when (currentMovementDir) {
      MovementDir.RIGHTWARD -> {
        nextX = currentPoint.x + 1
        nextY = currentPoint.y
      }

      MovementDir.LEFTWARD -> {
        nextX = currentPoint.x - 1
        nextY = currentPoint.y
      }

      MovementDir.UPWARD -> {
        nextX = currentPoint.x
        nextY = currentPoint.y - 1
      }

      MovementDir.DOWNWARD -> {
        nextX = currentPoint.x
        nextY = currentPoint.y + 1
      }

      else -> throw IllegalStateException("shouldn't happen. First translate split.")
    }

    return if (isValidPoint(nextX, nextY, maxX, maxY)) {
      Point(nextX, nextY, input[nextY][nextX])
    } else {
      null
    }
  }

  fun getNextMovementDirection(currentMovementDir: MovementDir, currentPoint: Point): MovementDir {
    return when (currentPoint.char) {
      '.' -> currentMovementDir
      '/' -> {
        when (currentMovementDir) {
          MovementDir.RIGHTWARD -> MovementDir.UPWARD
          MovementDir.LEFTWARD -> MovementDir.DOWNWARD
          MovementDir.UPWARD -> MovementDir.RIGHTWARD
          MovementDir.DOWNWARD -> MovementDir.LEFTWARD
          else -> throw IllegalStateException("shouldn't happen. First translate split.")
        }
      }

      '\\' -> {
        when (currentMovementDir) {
          MovementDir.RIGHTWARD -> MovementDir.DOWNWARD
          MovementDir.LEFTWARD -> MovementDir.UPWARD
          MovementDir.UPWARD -> MovementDir.LEFTWARD
          MovementDir.DOWNWARD -> MovementDir.RIGHTWARD
          else -> throw IllegalStateException("shouldn't happen. First translate split.")
        }
      }

      '|' -> {
        when (currentMovementDir) {
          MovementDir.RIGHTWARD -> MovementDir.SPLIT_UP_DOWN
          MovementDir.LEFTWARD -> MovementDir.SPLIT_UP_DOWN
          MovementDir.UPWARD -> MovementDir.UPWARD
          MovementDir.DOWNWARD -> MovementDir.DOWNWARD
          else -> throw IllegalStateException("shouldn't happen. First translate split.")
        }
      }

      '-' -> {
        when (currentMovementDir) {
          MovementDir.RIGHTWARD -> MovementDir.RIGHTWARD
          MovementDir.LEFTWARD -> MovementDir.LEFTWARD
          MovementDir.UPWARD -> MovementDir.SPLIT_LEFT_RIGHT
          MovementDir.DOWNWARD -> MovementDir.SPLIT_LEFT_RIGHT
          else -> throw IllegalStateException("shouldn't happen. First translate split.")
        }
      }

      else -> {
        throw IllegalStateException("encountered unknown character : $currentPoint")
      }
    }
  }

  fun walkThroughGrid(
    visitedPoints: MutableSet<Point>,
    input: List<String>,
    maxX: Int,
    maxY: Int,
    currentPoint: Point,
    currentMovementDir: MovementDir
  ) {
    val alreadyPoint = visitedPoints.firstOrNull { it.x == currentPoint.x && it.y == currentPoint.y }
    if (alreadyPoint?.visitedDirs?.contains(currentMovementDir) == true) {
      // if we visited this point already in the same movement direction we entered a loop => stop
      return
    } else if (alreadyPoint != null) {
      // if we have this point already visited then just add the direction
      alreadyPoint.visitedDirs.add(currentMovementDir)
    } else {
      // otherwise save the newly visited point with its direction
      visitedPoints.add(currentPoint.apply {
        visitedDirs.add(currentMovementDir)
      })
    }

    when (val nextMovementDir = getNextMovementDirection(currentMovementDir, currentPoint)) {
      MovementDir.SPLIT_LEFT_RIGHT -> {
        // continue in two directions
        MovementDir.LEFTWARD.let { dir ->
          findNextPoint(currentPoint, dir, input, maxX, maxY)?.let { point ->
            walkThroughGrid(visitedPoints, input, maxX, maxY, point, dir)
          }
        }
        MovementDir.RIGHTWARD.let { dir ->
          findNextPoint(currentPoint, dir, input, maxX, maxY)?.let { point ->
            walkThroughGrid(visitedPoints, input, maxX, maxY, point, dir)
          }
        }
      }

      MovementDir.SPLIT_UP_DOWN -> {
        // continue in two directions
        MovementDir.UPWARD.let { dir ->
          findNextPoint(currentPoint, dir, input, maxX, maxY)?.let { point ->
            walkThroughGrid(visitedPoints, input, maxX, maxY, point, dir)
          }
        }
        MovementDir.DOWNWARD.let { dir ->
          findNextPoint(currentPoint, dir, input, maxX, maxY)?.let { point ->
            walkThroughGrid(visitedPoints, input, maxX, maxY, point, dir)
          }
        }
      }

      else -> {
        // continue in the one direction that was calculated
        findNextPoint(currentPoint, nextMovementDir, input, maxX, maxY)?.let { point ->
          walkThroughGrid(visitedPoints, input, maxX, maxY, point, nextMovementDir)
        }
      }
    }
  }

  fun part1(input: List<String>): Int {
    val maxX = input[0].length
    val maxY = input.size

    val visitedPoints: MutableSet<Point> = mutableSetOf()
    walkThroughGrid(visitedPoints, input, maxX, maxY, Point(0, 0, input[0][0]), MovementDir.RIGHTWARD)
    return visitedPoints.size
  }

  fun part2(input: List<String>): Int {
    val maxX = input[0].length
    val maxY = input.size
    var maxSize = 0

    // iterate over all points in top row with downward direction
    for (i in input[0].indices) {
      val visitedPoints: MutableSet<Point> = mutableSetOf()
      walkThroughGrid(visitedPoints, input, maxX, maxY, Point(i, 0, input[0][i]), MovementDir.DOWNWARD)
      maxSize = max(maxSize, visitedPoints.size)
    }

    // iterate over all points in left row with rightward direction
    for (i in input.indices) {
      val visitedPoints: MutableSet<Point> = mutableSetOf()
      walkThroughGrid(visitedPoints, input, maxX, maxY, Point(0, i, input[i][0]), MovementDir.RIGHTWARD)
      maxSize = max(maxSize, visitedPoints.size)
    }

    // iterate over all points in right row with leftward direction
    for (i in input.indices) {
      val visitedPoints: MutableSet<Point> = mutableSetOf()
      walkThroughGrid(visitedPoints, input, maxX, maxY, Point(maxX - 1, i, input[i][maxX - 1]), MovementDir.LEFTWARD)
      maxSize = max(maxSize, visitedPoints.size)
    }

    // iterate over all points in bottom row with upward direction
    for (i in input[0].indices) {
      val visitedPoints: MutableSet<Point> = mutableSetOf()
      walkThroughGrid(visitedPoints, input, maxX, maxY, Point(i, maxY - 1, input[maxY - 1][i]), MovementDir.UPWARD)
      maxSize = max(maxSize, visitedPoints.size)
    }

    return maxSize
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("16Dec_example")
  check(part1(testInput) == 46)
  check(part2(testInput) == 51)

  val input = readInput("16Dec_own")
  part1(input).println()
  part2(input).println()
}
