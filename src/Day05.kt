import java.io.File

class LineDay5(
  val destinationRangeStart: Long,
  val sourceDestDistance: Long,
  val sourceRangeStart: Long,
  val rangeLength: Long
)

fun main() {

  fun getLines(fileName: String): List<LineDay5> {
    return buildList {
      File(fileName).forEachLine { line ->
        val parts = line.split(" ")
        val part0 = parts[0].toLong()
        val part1 = parts[1].toLong()
        add(
          LineDay5(
            destinationRangeStart = part0,
            sourceDestDistance = part0 - part1,
            sourceRangeStart = part1,
            rangeLength = parts[2].toLong(),
          )
        )
      }
    }
  }

  fun checkMap(sourceNr: Long, map: List<LineDay5>): Long {
    for (line in map) {
      if (sourceNr in line.sourceRangeStart until line.sourceRangeStart + line.rangeLength) {
        return sourceNr + line.sourceDestDistance
        // return line.destinationRangeStart + (sourceNr - line.sourceRangeStart)
      }
    }
    return sourceNr
  }

  fun part1(infix: String): Long {
    val seedToSoil: List<LineDay5> = getLines("src/5Dec_${infix}_seed-to-soil.txt")
    val soilToFertilizer: List<LineDay5> = getLines("src/5Dec_${infix}_soil-to-fertilizer.txt")
    val fertilizerToWater: List<LineDay5> = getLines("src/5Dec_${infix}_fertilizer-to-water.txt")
    val waterToLight: List<LineDay5> = getLines("src/5Dec_${infix}_water-to-light.txt")
    val lightToTemperature: List<LineDay5> = getLines("src/5Dec_${infix}_light-to-temperature.txt")
    val temperatureToHumidity: List<LineDay5> = getLines("src/5Dec_${infix}_temperature-to-humidity.txt")
    val humidityToLocation: List<LineDay5> = getLines("src/5Dec_${infix}_humidity-to-location.txt")

    var resultLocationNr: Long = Long.MAX_VALUE
    File("src/5Dec_${infix}_seeds.txt").forEachLine { line ->
      line.split(" ").forEach { seedStr ->
        val seedNr = seedStr.toLong()
        val soilNr = checkMap(seedNr, seedToSoil)
        val fertNr = checkMap(soilNr, soilToFertilizer)
        val waterNr = checkMap(fertNr, fertilizerToWater)
        val lightNr = checkMap(waterNr, waterToLight)
        val temperatureNr = checkMap(lightNr, lightToTemperature)
        val humidityNr = checkMap(temperatureNr, temperatureToHumidity)
        val locationNr = checkMap(humidityNr, humidityToLocation)

        if (locationNr < resultLocationNr) {
          resultLocationNr = locationNr
        }
      }
    }
    return resultLocationNr
  }

  fun part2(infix: String): Long {
    val seedToSoil: List<LineDay5> = getLines("src/5Dec_${infix}_seed-to-soil.txt")
    val soilToFertilizer: List<LineDay5> = getLines("src/5Dec_${infix}_soil-to-fertilizer.txt")
    val fertilizerToWater: List<LineDay5> = getLines("src/5Dec_${infix}_fertilizer-to-water.txt")
    val waterToLight: List<LineDay5> = getLines("src/5Dec_${infix}_water-to-light.txt")
    val lightToTemperature: List<LineDay5> = getLines("src/5Dec_${infix}_light-to-temperature.txt")
    val temperatureToHumidity: List<LineDay5> = getLines("src/5Dec_${infix}_temperature-to-humidity.txt")
    val humidityToLocation: List<LineDay5> = getLines("src/5Dec_${infix}_humidity-to-location.txt")

    var resultLocationNr: Long = Long.MAX_VALUE
    File("src/5Dec_${infix}_seeds.txt").forEachLine { line ->
      val seedsRanges = line.split(" ")

      for (i in seedsRanges.indices step 2) {
        val seedStartNr = seedsRanges[i].toLong()
        val seedRange = seedsRanges[i + 1].toLong()

        for (seedNr in seedStartNr until seedStartNr + seedRange) {
          val soilNr = checkMap(seedNr, seedToSoil)
          val fertNr = checkMap(soilNr, soilToFertilizer)
          val waterNr = checkMap(fertNr, fertilizerToWater)
          val lightNr = checkMap(waterNr, waterToLight)
          val temperatureNr = checkMap(lightNr, lightToTemperature)
          val humidityNr = checkMap(temperatureNr, temperatureToHumidity)
          val locationNr = checkMap(humidityNr, humidityToLocation)

          if (locationNr < resultLocationNr) {
            resultLocationNr = locationNr
          }
        }
      }
    }
    return resultLocationNr
  }

  // test if implementation meets criteria from the description, like:
  check(part1("example") == 35L)
  check(part2("example") == 46L)

  part1("own").println()
  part2("own").println()
}
