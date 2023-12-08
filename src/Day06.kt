class Race(
  val time: Long,
  val bestDistance: Long,
  var numberOfWaysToWin: Long = 0,
)

fun main() {

  fun getRacesPart1(input: List<String>): List<Race> {
    val times = input[0].replace("Time:", "").split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
    val distances = input[1].replace("Distance:", "").split(" ").filter { it.isNotEmpty() }.map { it.toLong() }

    return buildList {
      for (i in times.indices) {
        add(Race(times[i], distances[i]))
      }
    }
  }

  fun getRacesPart2(input: List<String>): List<Race> {
    val time = input[0].replace("[^0-9]".toRegex(), "").toLong()
    val distance = input[1].replace("[^0-9]".toRegex(), "").toLong()

    return buildList {
      add(Race(time, distance))
    }
  }

  fun List<Race>.calculateNumsOfWaysToWin(): List<Race> {
    for (race in this) {
      for (holdTime in 0..race.time) {
        val runTime = race.time - holdTime
        val speed = holdTime
        val distance = runTime * speed
        if (distance > race.bestDistance) {
          race.numberOfWaysToWin++
        }
      }
    }
    return this
  }

  fun part1(input: List<String>): Long {
    var result: Long = 1
    getRacesPart1(input).calculateNumsOfWaysToWin().forEach {
      result *= it.numberOfWaysToWin
    }
    return result
  }

  fun part2(input: List<String>): Long {
    var result: Long = 1
    getRacesPart2(input).calculateNumsOfWaysToWin().forEach {
      result *= it.numberOfWaysToWin
    }
    return result
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("6Dec_example")
  check(part1(testInput) == 288L)
  check(part2(testInput) == 71503L)

  val input = readInput("6Dec_own")
  part1(input).println()
  part2(input).println()
}
