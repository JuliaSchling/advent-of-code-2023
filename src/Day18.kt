import kotlin.math.max
import kotlin.math.min

abstract class Predicate(
  val followUp: String
) {
  abstract fun visit(machineParts: Map<Char, Int>): String?
}

class ComparisonPredicate(
  val machinePartType: Char,
  val comparisonType: Char,
  val comparisonValue: Int,
  followUp: String,
) : Predicate(followUp) {
  override fun visit(machineParts: Map<Char, Int>): String? {
    val machinePartValue = machineParts[machinePartType]
    if (machinePartValue != null
      && ((comparisonType == '<' && machinePartValue < comparisonValue)
          || (comparisonType == '>' && machinePartValue > comparisonValue))
    ) {
      return followUp
    }
    return null
  }
}

class TruePredicate(
  followUp: String,
) : Predicate(followUp) {
  override fun visit(machineParts: Map<Char, Int>): String? {
    return followUp
  }
}

data class AllowedRanges(
  var minX: Int = 1,
  var maxX: Int = 4000,
  var minM: Int = 1,
  var maxM: Int = 4000,
  var minA: Int = 1,
  var maxA: Int = 4000,
  var minS: Int = 1,
  var maxS: Int = 4000,
)

fun main() {

  fun parseWorkflows(workflowInput: List<String>): Map<String, List<Predicate>> {
    // create map with workflow-name as key
    val workflows: MutableMap<String, List<Predicate>> = mutableMapOf()

    workflowInput.forEach { line ->
      val pattern = "^(\\w+)\\{(\\S+)}\$"
      val match = Regex(pattern).matchEntire(line)
      if (match != null) {
        val (workflowName, preds) = match.destructured

        val predicates: MutableList<Predicate> = mutableListOf()

        preds.split(",").forEach { predicate ->
          if (predicate.contains(":")) {
            val split = predicate.split(":")
            val actualPredicate = split[0] // e.g. a>2590
            val followUp = split[1] // e.g. rtp or A or R

            predicates.add(
              ComparisonPredicate(
                machinePartType = actualPredicate[0],
                comparisonType = actualPredicate[1],
                comparisonValue = actualPredicate.substring(2).toInt(),
                followUp = followUp
              )
            )
          } else {
            // this is the last predicate, e.g. just xyz or A or R
            predicates.add(TruePredicate(predicate))
          }
        }

        workflows[workflowName] = predicates
      }
    }

    return workflows
  }

  fun checkPredicates(
    workflows: Map<String, List<Predicate>>,
    machinePartMap: Map<Char, Int>,
    workflowPredicates: List<Predicate>,
    total: Result
  ) {
    workflowPredicates.forEach { predicate ->
      val followUp = predicate.visit(machinePartMap)
      if (followUp != null) {
        when (followUp) {
          "A" -> {
            total.add(machinePartMap.values.sum())
            return
          }

          "R" -> {
            return
          }

          else -> {
            checkPredicates(workflows, machinePartMap, workflows[followUp]!!, total)
            return
          }
        }
      }
    }
  }

  fun checkAllowedRanges(
    allowedRanges: AllowedRanges,
    workflows: Map<String, List<Predicate>>,
    workflowPredicates: List<Predicate>,
    total: LongResult
  ) {

    workflowPredicates.forEach { predicate ->
      val allowedCopy = allowedRanges.copy()

      if (predicate is ComparisonPredicate) {
        when (predicate.machinePartType) {
          'x' -> when (predicate.comparisonType) {
            '<' -> {
              allowedCopy.maxX = min(allowedCopy.maxX, predicate.comparisonValue - 1)
              allowedRanges.minX = max(allowedRanges.minX, predicate.comparisonValue)
            }

            '>' -> {
              allowedCopy.minX = max(allowedCopy.minX, predicate.comparisonValue + 1)
              allowedRanges.maxX = min(allowedRanges.maxX, predicate.comparisonValue)
            }
          }

          'm' -> when (predicate.comparisonType) {
            '<' -> {
              allowedCopy.maxM = min(allowedCopy.maxM, predicate.comparisonValue - 1)
              allowedRanges.minM = max(allowedRanges.minM, predicate.comparisonValue)
            }

            '>' -> {
              allowedCopy.minM = max(allowedCopy.minM, predicate.comparisonValue + 1)
              allowedRanges.maxM = min(allowedRanges.maxM, predicate.comparisonValue)
            }
          }

          'a' -> when (predicate.comparisonType) {
            '<' -> {
              allowedCopy.maxA = min(allowedCopy.maxA, predicate.comparisonValue - 1)
              allowedRanges.minA = max(allowedRanges.minA, predicate.comparisonValue)
            }

            '>' -> {
              allowedCopy.minA = max(allowedCopy.minA, predicate.comparisonValue + 1)
              allowedRanges.maxA = min(allowedRanges.maxA, predicate.comparisonValue)
            }
          }

          's' -> when (predicate.comparisonType) {
            '<' -> {
              allowedCopy.maxS = min(allowedCopy.maxS, predicate.comparisonValue - 1)
              allowedRanges.minS = max(allowedRanges.minS, predicate.comparisonValue)
            }

            '>' -> {
              allowedCopy.minS = max(allowedCopy.minS, predicate.comparisonValue + 1)
              allowedRanges.maxS = min(allowedRanges.maxS, predicate.comparisonValue)
            }
          }
        }
      }

      when (predicate.followUp) {
        "R" -> {
        }

        "A" -> {
          // an accepted end -> check allowed ranges
          //println("Found an end with these allowed ranges: x:[${allowedCopy.minX}..${allowedCopy.maxX}], m:[${allowedCopy.minM}..${allowedCopy.maxM}], a:[${allowedCopy.minA}..${allowedCopy.maxA}], s:[${allowedCopy.minS}..${allowedCopy.maxS}]")
          total.add((allowedCopy.maxX - allowedCopy.minX + 1).toLong() * (allowedCopy.maxM - allowedCopy.minM + 1).toLong() * (allowedCopy.maxA - allowedCopy.minA + 1).toLong() * (allowedCopy.maxS - allowedCopy.minS + 1).toLong())
        }

        else -> {
          workflows[predicate.followUp]?.let { checkAllowedRanges(allowedCopy, workflows, it, total) }
        }
      }
    }
  }

  fun part1(input: List<String>): Int {

    val workflowInput = input.takeWhile { it.isNotEmpty() }
    val workflows = parseWorkflows(workflowInput)
    val startWorkflowPredicates = workflows["in"]!!

    val total = Result()

    val machinePartsInput = input.subList(workflowInput.size + 1, input.size)
    machinePartsInput.forEach { line ->
      val pattern = "^\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)}\$"
      val match = Regex(pattern).matchEntire(line)
      if (match != null) {
        val (x, m, a, s) = match.destructured

        val machinePartMap: Map<Char, Int> = mapOf(
          'x' to x.toInt(),
          'm' to m.toInt(),
          'a' to a.toInt(),
          's' to s.toInt()
        )

        checkPredicates(workflows, machinePartMap, startWorkflowPredicates, total)
      }
    }

    return total.result
  }

  fun part2(input: List<String>): Long {

    val workflowInput = input.takeWhile { it.isNotEmpty() }
    val workflows = parseWorkflows(workflowInput)
    val startWorkflowPredicates = workflows["in"]!!

    val total = LongResult()
    checkAllowedRanges(AllowedRanges(), workflows, startWorkflowPredicates, total)

    return total.result
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("18Dec_example")
  check(part1(testInput) == 19114)
  check(part2(testInput) == 167409079868000L)

  val input = readInput("18Dec_own")
  part1(input).println()
  part2(input).println()
}
