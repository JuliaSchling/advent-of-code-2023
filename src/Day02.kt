class Game(
  val id: Int,
  val sets: List<GameSet>,
)

class GameSet(
  var numberOfRed: Int = 0,
  var numberOfGreen: Int = 0,
  var numberOfBlue: Int = 0,
)

fun main() {
  fun getGames(input: List<String>): List<Game> {
    val games: MutableList<Game> = mutableListOf()
    input.forEach { line ->

      val gameParts = line.split(":")

      val gameId = gameParts[0].replace("Game ", "").trim().toInt()

      val sets: MutableList<GameSet> = mutableListOf()
      gameParts[1].split(";").forEach { setPart ->
        val set = GameSet()
        sets.add(set)

        setPart.split(",").forEach { numberPart ->
          val np = numberPart.trim()
          if (np.contains("red")) {
            set.numberOfRed = np.replace("red", "").trim().toInt()
          } else if (np.contains("blue")) {
            set.numberOfBlue = np.replace("blue", "").trim().toInt()
          } else if (np.contains("green")) {
            set.numberOfGreen = np.replace("green", "").trim().toInt()
          }
        }
      }

      games.add(Game(gameId, sets))
    }

    return games
  }

  fun part1(input: List<String>): Int {
    val games = getGames(input)

    val numberOfRedCubes = 12
    val numberOfGreenCubes = 13
    val numberOfBlueCubes = 14

    var sum = 0
    games.forEach { game ->
      var validGame = true
      for (set in game.sets) {
        if (set.numberOfRed > numberOfRedCubes
          || set.numberOfGreen > numberOfGreenCubes
          || set.numberOfBlue > numberOfBlueCubes
        ) {
          validGame = false
          break
        }
      }
      if (validGame) {
        sum += game.id
      }
    }
    return sum
  }

  fun part2(input: List<String>): Int {
    val games = getGames(input)

    var sumOfPower = 0
    games.forEach { game ->

      var minRed = 0
      var minGreen = 0
      var minBlue = 0

      game.sets.forEach { set ->
        if (set.numberOfRed > minRed) minRed = set.numberOfRed
        if (set.numberOfGreen > minGreen) minGreen = set.numberOfGreen
        if (set.numberOfBlue > minBlue) minBlue = set.numberOfBlue
      }

      val power = minRed * minGreen * minBlue
      sumOfPower += power
    }
    return sumOfPower
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("2Dec_example")
  check(part1(testInput) == 8)
  check(part2(testInput) == 2286)

  val input = readInput("2Dec_own")
  part1(input).println()
  part2(input).println()
}
