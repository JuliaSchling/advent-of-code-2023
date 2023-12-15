class Lens(
  val label: String,
  var focalLength: Int,
)

class Box(
  val lenses: MutableList<Lens> = mutableListOf()
)

fun main() {

  fun calculateHash(str: String): Int {
    var currentValue = 0
    str.forEach { char ->
      currentValue = ((currentValue + char.code) * 17).mod(256)
    }
    return currentValue
  }

  fun part2(input: List<String>): Int {
    val boxes: MutableList<Box> = MutableList(256) { Box() }
    val hashes: MutableMap<String, Int> = mutableMapOf()

    input[0].split(",").forEach { step ->
      val pattern = "^(\\w+)-|(\\w+)=(\\d+)\$"
      val match = Regex(pattern).matchEntire(step)
      if (match != null) {
        val (labelRemoveOp, labelAddOp, focalLength) = match.destructured

        if (labelRemoveOp.isNotEmpty()) {
          // this could be an operation like 'cm-'
          val hash = hashes.getOrPut(labelRemoveOp) { calculateHash(labelRemoveOp) }
          val box = boxes[hash]
          box.lenses.firstOrNull { it.label == labelRemoveOp }?.let { lens -> box.lenses.remove(lens) }

        } else {
          // this could be an operation like 'rn=1'
          val hash = hashes.getOrPut(labelAddOp) { calculateHash(labelAddOp) }
          val box = boxes[hash]
          val flNum = focalLength.toInt()
          val existingLens = box.lenses.firstOrNull { it.label == labelAddOp }
          if (existingLens != null) {
            existingLens.focalLength = flNum
          } else {
            box.lenses.add(Lens(labelAddOp, flNum))
          }

        }
      }
    }

    // iterate over boxes to calculate total
    var total = 0
    for (boxNum in boxes.indices) {
      val box = boxes[boxNum]
      for (lensNum in box.lenses.indices) {
        val lens = box.lenses[lensNum]
        total += (boxNum + 1) * (lensNum + 1) * lens.focalLength
      }
    }
    return total
  }

  fun part1(input: List<String>): Int {
    var total = 0
    input[0].split(",").forEach { step ->
      total += calculateHash(step)
    }
    return total
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("15Dec_example")
  check(part1(testInput) == 1320)
  check(part2(testInput) == 145)

  val input = readInput("15Dec_own")
  //part1(input).println()
  part2(input).println()
}
