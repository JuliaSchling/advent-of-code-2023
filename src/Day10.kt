import java.util.Objects

enum class Direction {
  ABOVE,
  BELOW,
  RIGHT,
  LEFT;

  fun getOpposite(): Direction =
    when (this) {
      ABOVE -> BELOW
      BELOW -> ABOVE
      RIGHT -> LEFT
      LEFT -> RIGHT
    }
}

class NeighbourData(
  val dir: Direction,
  val tile: Tile,
  val nextDir: Direction,
)

class TileDirDef(
  val dirOne: Direction,
  val dirTwo: Direction,
)

class Tile(
  val xPos: Int,
  val yPos: Int,
  val char: Char,
) : Comparable<Tile> {

  override fun compareTo(other: Tile) = compareValuesBy(this, other,
    { it.xPos },
    { it.yPos }
  )

  override fun equals(other: Any?): Boolean = (other is Tile)
      && xPos == other.xPos
      && yPos == other.yPos

  override fun hashCode(): Int = Objects.hash(xPos, yPos)

  override fun toString(): String {
    return "[$xPos][$yPos] ($char)"
  }
}

fun main() {

  val tileMap: Map<Char, TileDirDef> = mapOf(
    '|' to TileDirDef(Direction.ABOVE, Direction.BELOW), // is a vertical pipe connecting north and south
    '-' to TileDirDef(Direction.RIGHT, Direction.LEFT), // is a horizontal pipe connecting east and west
    'L' to TileDirDef(Direction.ABOVE, Direction.RIGHT), // is a 90-degree bend connecting north and east
    'J' to TileDirDef(Direction.ABOVE, Direction.LEFT), // is a 90-degree bend connecting north and west
    '7' to TileDirDef(Direction.BELOW, Direction.LEFT), // is a 90-degree bend connecting south and west
    'F' to TileDirDef(Direction.BELOW, Direction.RIGHT), // is a 90-degree bend connecting south and east
  )

  fun determineTypeByDirs(dirOne: Direction, dirTwo: Direction): Char {
    if (dirOne == Direction.ABOVE && dirTwo == Direction.BELOW) {
      return '|'
    } else if (dirOne == Direction.RIGHT && dirTwo == Direction.LEFT) {
      return '-'
    } else if (dirOne == Direction.ABOVE && dirTwo == Direction.RIGHT) {
      return 'L'
    } else if (dirOne == Direction.ABOVE && dirTwo == Direction.LEFT) {
      return 'J'
    } else if (dirOne == Direction.BELOW && dirTwo == Direction.LEFT) {
      return '7'
    } else if (dirOne == Direction.BELOW && dirTwo == Direction.RIGHT) {
      return 'F'
    }
    return '.'
  }

  fun getNeighbour(maxLineLength: Int, dir: Direction, xPos: Int, yPos: Int): Pair<Int, Int>? {
    val neighbourPos = when (dir) {
      Direction.ABOVE -> xPos - 1 to yPos
      Direction.BELOW -> xPos + 1 to yPos
      Direction.RIGHT -> xPos to yPos + 1
      Direction.LEFT -> xPos to yPos - 1
    }

    return if (neighbourPos.first < 0 || neighbourPos.first >= maxLineLength || neighbourPos.second < 0 || neighbourPos.second >= maxLineLength) {
      // no neighbour here, we're at the edge
      null
    } else {
      neighbourPos
    }
  }

  fun isValidNeighbour(walkingDir: Direction, c: Char): Direction? {
    val tileDirDef = tileMap[c] ?: return null // no tileDirDef -> most likely we're back at S

    val oppositeDir = walkingDir.getOpposite()
    if (tileDirDef.dirOne != oppositeDir && tileDirDef.dirTwo != oppositeDir) {
      // invalid tile, no connection to the tile we're coming from
      return null
    } else if (tileDirDef.dirOne != oppositeDir) {
      // return next direction to walk to (the other direction of where we're coming from)
      return tileDirDef.dirOne
    } else if (tileDirDef.dirTwo != oppositeDir) {
      // return next direction to walk to (the other direction of where we're coming from)
      return tileDirDef.dirTwo
    }

    return null
  }

  fun findMainLoop(input: List<String>): Pair<Int, List<Tile>> {
    val maxLineLength = input[0].length
    val tilesOfMainLoop: MutableList<Tile> = mutableListOf()

    // find S
    var xPosS = -1
    var yPosS = -1
    for (i in input.indices) {
      yPosS = input[i].indexOf('S')
      if (yPosS != -1) {
        xPosS = i
        break
      }
    }

    // get all valid neighbours of S and pick the first valid one for a start
    val validNeighbours: MutableList<NeighbourData> = mutableListOf()
    Direction.entries.forEach { dir ->
      getNeighbour(maxLineLength, dir, xPosS, yPosS)?.let { neighbour ->
        val char = input[neighbour.first][neighbour.second]
        isValidNeighbour(dir, char)?.let { nextDir ->
          validNeighbours.add(NeighbourData(dir, Tile(neighbour.first, neighbour.second, char), nextDir))
        }
      }
    }

    if (validNeighbours.size != 2) {
      throw IllegalStateException("not prepared for this")
    }

    // determine type of S (needed for part 2)
    val typeOfS = determineTypeByDirs(validNeighbours[0].dir, validNeighbours[1].dir)
    tilesOfMainLoop.add(Tile(xPosS, yPosS, typeOfS))

    // walk from neighbour to neighbour in the given direction until we reach S again
    var currentPos: Tile = validNeighbours[0].tile
    var currentDir: Direction = validNeighbours[0].nextDir
    var stepCounter = 1

    while (true) {
      stepCounter++
      tilesOfMainLoop.add(currentPos)

      val neighbour = getNeighbour(maxLineLength, currentDir, currentPos.xPos, currentPos.yPos)
        ?: throw IllegalStateException("we went into the wrong direction at some point")

      val char = input[neighbour.first][neighbour.second]
      currentPos = Tile(neighbour.first, neighbour.second, char)

      val nextDir = isValidNeighbour(currentDir, char)
      if (nextDir == null) {
        // maybe we're back at S?
        if (char == 'S') {
          stepCounter++
          break
        }
        throw IllegalStateException("we went into the wrong direction at some point")
      } else {
        currentDir = nextDir
      }
    }

    return stepCounter to tilesOfMainLoop
  }

  fun part1(input: List<String>): Int {
    val result = findMainLoop(input)
    return result.first / 2
  }

  fun part2(input: List<String>): Int {
    val result = findMainLoop(input)
    val tilesOfMainLoop = result.second

    // we walk through the grid: when stepping over a '|' that is part of the main loop, we're 'inside',
    // next time we're stepping over a '|' that is part of the main loop, we're out. Count number of 'inside' tiles.
    // watch out for corners!
    var inside = false
    var tilesInside = 0
    for (x in input.indices) {
      val line = input[x]

      var firstCorner: Char? = null
      var stateBeforeFirstCorner: Boolean? = null
      var secondCorner: Char? = null

      for (y in line.indices) {
        var char = line[y]

        if (char == 'S') {
          val startTile = tilesOfMainLoop.find { tile -> tile.xPos == x && tile.yPos == y }
            ?: throw IllegalStateException("couldn't find start-tile in list of tiles")
          char = startTile.char
        }

        val currentTileInMainLoop = tilesOfMainLoop.contains(Tile(x, y, char))

        if (currentTileInMainLoop) {
          if (char == '|') {
            inside = !inside

          } else if (char == '7' || char == 'J' || char == 'F' || char == 'L') {
            if (firstCorner == null) {
              firstCorner = char
              stateBeforeFirstCorner = inside

            } else {
              secondCorner = char

              if ((firstCorner == 'F' && secondCorner == 'J') || (firstCorner == 'L' && secondCorner == '7')) {
                // this is a combination of corners after which we change the inside-state.
                // If we were outside before, we're now inside and the other way round.
                inside = !stateBeforeFirstCorner!!

              } else if ((firstCorner == 'F' && secondCorner == '7') || (firstCorner == 'L' && secondCorner == 'J')) {
                // this is a combination of corners after which we stay in the same inside-state
                inside = stateBeforeFirstCorner!!
              }

              // reset corner states
              firstCorner = null
              stateBeforeFirstCorner = null
              secondCorner = null
            }
          }

        } else if (inside && !currentTileInMainLoop) {
          tilesInside++
        }
      }
    }

    return tilesInside
  }

  // test if implementation meets criteria from the description, like:
  val testInput11 = readInput("10Dec_example_11")
  check(part1(testInput11) == 4)
  val testInput12 = readInput("10Dec_example_12")
  check(part1(testInput12) == 8)

  val testInput21 = readInput("10Dec_example_21")
  check(part2(testInput21) == 4)
  val testInput22 = readInput("10Dec_example_22")
  check(part2(testInput22) == 8)
  val testInput23 = readInput("10Dec_example_23")
  check(part2(testInput23) == 10)

  val input = readInput("10Dec_own")
  part1(input).println()
  part2(input).println()
}
