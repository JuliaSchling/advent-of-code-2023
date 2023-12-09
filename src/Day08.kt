class Node(
  val name: String,
  val leftNeighbour: String,
  val rightNeighbour: String,
)

fun main() {

  fun calculateNumberOfSteps(startNode: Node, instructions: String, nodes: MutableMap<String, Node>, stopAction: (String) -> Boolean): Int {
    var stepCounter = 0
    var currentNode = startNode
    while (true) {
      instructions.toCharArray().forEach { c ->
        stepCounter++

        currentNode = when (c) {
          'L' -> {
            nodes[currentNode.leftNeighbour]
              ?: throw IllegalStateException("couldn't find left neighbour ${currentNode.leftNeighbour} of ${currentNode.name}")
          }
          'R' -> {
            nodes[currentNode.rightNeighbour]
              ?: throw IllegalStateException("couldn't find right neighbour ${currentNode.rightNeighbour} of ${currentNode.name}")
          }
          else -> throw IllegalStateException("unknown instruction: $c")
        }

        if (stopAction(currentNode.name)) {
          return stepCounter
        }
      }
    }
  }

  fun calculateNumberOfStepsForAll(startNodes: List<Node>, instructions: String, nodes: MutableMap<String, Node>): List<Int> =
    buildList {
      startNodes.forEach {
        add(calculateNumberOfSteps(it, instructions, nodes) { nodeName ->
          nodeName[2] == 'Z'
        })
      }
    }

  fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm: Long = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
      if (lcm % a == 0L && lcm % b == 0L) {
        return lcm
      }
      lcm += larger
    }
    return maxLcm
  }

  fun calculateLCM(numberOfSteps: List<Int>): Long {
    var result: Long = numberOfSteps[0].toLong()
    for (i in 1 until numberOfSteps.size) {
      result = findLCM(result, numberOfSteps[i].toLong())
    }
    return result
  }

  fun part1(input: List<String>): Int {
    // first line: instructions
    val instructions = input[0]

    // third line and following: nodes and connections
    val nodeStrings = input.drop(2)
    val nodes: MutableMap<String, Node> = mutableMapOf()
    nodeStrings.forEach { nodeStr ->

      val pattern = "^(\\w{3}) = \\((\\w{3}), (\\w{3})\\)\$"
      val match = Regex(pattern).matchEntire(nodeStr)
      if (match != null) {
        val (nodeName, leftNeighbour, rightNeighbour) = match.destructured
        nodes[nodeName] = Node(nodeName, leftNeighbour, rightNeighbour)
      }
    }

    // follow instructions through tree
    val startNode = nodes["AAA"] ?: throw IllegalStateException("couldn't find start node")
    return calculateNumberOfSteps(startNode, instructions, nodes) { nodeName ->
      nodeName == "ZZZ"
    }
  }

  fun part2(input: List<String>): Long {
    // first line: instructions
    val instructions = input[0]

    // third line and following: nodes and connections.
    // part 2: not only collect nodes in map, but also collect all nodes that qualify as start node (end in A)
    val nodeStrings = input.drop(2)
    val nodes: MutableMap<String, Node> = mutableMapOf()
    val startNodes: MutableList<Node> = mutableListOf()
    nodeStrings.forEach { nodeStr ->

      val pattern = "^(\\w{3}) = \\((\\w{3}), (\\w{3})\\)\$"
      val match = Regex(pattern).matchEntire(nodeStr)
      if (match != null) {
        val (nodeName, leftNeighbour, rightNeighbour) = match.destructured
        val newNode = Node(nodeName, leftNeighbour, rightNeighbour)
        nodes[nodeName] = newNode

        if (nodeName[2] == 'A') {
          startNodes.add(newNode)
        }
      }
    }

    // calculate number of steps for each start node in list
    val numberOfSteps: List<Int> = calculateNumberOfStepsForAll(startNodes, instructions, nodes)

    // find the least common multiple of all the number of steps
    return calculateLCM(numberOfSteps)
  }

  // test if implementation meets criteria from the description, like:
  val testInput1 = readInput("8Dec_example_1")
  check(part1(testInput1) == 2)
  val testInput2 = readInput("8Dec_example_2")
  check(part1(testInput2) == 6)
  val testInput3 = readInput("8Dec_example_3")
  check(part2(testInput3) == 6L)

  val input = readInput("8Dec_own")
  part1(input).println()
  part2(input).println()
}
