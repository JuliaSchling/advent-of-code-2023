class LineDay7 (
  val bid: Int,
  val typeOfHand: Int,
  val firstCardRank: Int,
  val secondCardRank: Int,
  val thirdCardRank: Int,
  val fourthCardRank: Int,
  val fifthCardRank: Int,
)

fun main() {

  fun createMapOfHand(hand: String): MutableMap<Char, Int> {
    val charMap = mutableMapOf<Char, Int>()
    hand.toCharArray().forEach { c ->
      val noc = charMap[c]
      if (noc == null) {
        charMap[c] = 1
      } else {
        charMap[c] = noc + 1
      }
    }
    return charMap
  }

  fun determineTypeRank(charMap: MutableMap<Char, Int>): Int = when (charMap.keys.size) {
    1 -> {
      // all cards in this hand are the same -> five of a kind
      7
    }

    2 -> {
      // 2 different cards, 1 of one kind and 4 of the other -> four of a kind
      if (charMap.values.contains(4)) {
        6
      } else {
        // 2 different cards, 2 of one kind and 3 of the other -> full house
        5
      }
    }

    3 -> {
      if (charMap.values.contains(3)) {
        // 3 different cards, 3 of one kind, and 2 different others -> three of a kind
        4
      } else {
        // 3 different cards, 2 of one kind, 2 of the other and 1 other -> two pairs
        3
      }
    }

    4 -> {
      // 4 different cards, 2 of one kind, 1 of each of the other -> one pair
      2
    }

    5 -> {
      // five different cards -> high card
      1
    }

    else -> 0
  }

  val cardRanksPart1 = listOf(
    '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'
  )

  fun determineCardRankPart1(c: Char): Int = cardRanksPart1.indexOf(c)

  fun determineTypeOfHandPart1(hand: String): Int {
    val charMap = createMapOfHand(hand)
    return determineTypeRank(charMap)
  }

  val cardRanksPart2 = listOf(
    'J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A'
  )

  fun determineCardRankPart2(c: Char): Int = cardRanksPart2.indexOf(c)

  fun determineTypeOfHandPart2(hand: String): Int {
    val charMap = createMapOfHand(hand)

    // new code for part 2: a 'J' raises the number of cards of the card that already has the highest number
    if (charMap.keys.contains('J')) {
      if (charMap.keys.size == 1) {
        // special case: all cards in this hand are a 'J'
        charMap.remove('J')
        charMap['2'] = 5

      } else {
        var entryWithHighestNumberOfCards: MutableMap.MutableEntry<Char, Int>? = null

        charMap.entries.forEach { entry ->
          if (entry.key != 'J') {
            if (entryWithHighestNumberOfCards == null) {
              entryWithHighestNumberOfCards = entry
            } else {
              if (entry.value > entryWithHighestNumberOfCards!!.value) {
                entryWithHighestNumberOfCards = entry
              }
            }
          }
        }

        // we add the number of 'J' to the entry with the highest number of cards
        // and remove the 'J' entry
        val numberOfJs = charMap['J']!!
        charMap.remove('J')
        charMap[entryWithHighestNumberOfCards!!.key] = entryWithHighestNumberOfCards!!.value + numberOfJs
      }
    }

    return determineTypeRank(charMap)
  }

  fun calculateTotal(
    input: List<String>,
    determineTypeOfHandAction: (hand: String) -> Int,
    determineCardRankAction: (card: Char) -> Int,
  ): Int {
    val lines: List<LineDay7> = buildList {
      input.forEach { line ->
        val lineParts = line.split(" ")
        val hand = lineParts[0]
        add(
          LineDay7(
            bid = lineParts[1].toInt(),
            typeOfHand = determineTypeOfHandAction(hand),
            firstCardRank = determineCardRankAction(hand[0]),
            secondCardRank = determineCardRankAction(hand[1]),
            thirdCardRank = determineCardRankAction(hand[2]),
            fourthCardRank = determineCardRankAction(hand[3]),
            fifthCardRank = determineCardRankAction(hand[4]),
          )
        )
      }
    }

    // sort the whole list, first by type then by card rank
    val sortedLines = lines.sortedWith(
      compareBy(
        { it.typeOfHand },
        { it.firstCardRank },
        { it.secondCardRank },
        { it.thirdCardRank },
        { it.fourthCardRank },
        { it.fifthCardRank })
    )

    // iterate over list and multiply the bid with the correct factor
    var total = 0
    for (i in sortedLines.indices) {
      total += sortedLines[i].bid * (i + 1)
    }
    return total
  }

  fun part1(input: List<String>): Int =
    calculateTotal(
      input,
      ::determineTypeOfHandPart1,
      ::determineCardRankPart1,
    )

  fun part2(input: List<String>): Int =
    calculateTotal(
      input,
      ::determineTypeOfHandPart2,
      ::determineCardRankPart2,
    )

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("7Dec_example")
  check(part1(testInput) == 6440)
  check(part2(testInput) == 5905)

  val input = readInput("7Dec_own")
  part1(input).println()
  part2(input).println()
}
