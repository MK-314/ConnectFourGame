package com.game.connectFour


class Player {
    var score = 0
    var choice: String? = null
    var name: String? = null
    fun setpName(askForName: String) {
        println(askForName)
        name = readLine()?.trim().toString()
    }
}

class BoardCell {
    var cellValue: String? = "| "
    fun setCellValue(symbolCell: Char) {
        val chars = cellValue?.toCharArray()
        chars?.set(1, symbolCell)
        cellValue = chars?.let { String(it) }
    }

    fun checkCell(): Boolean {
        val chars = cellValue?.toCharArray()
        return chars?.get(1) == ' '
    }
}

class BoardRow(boardCol: Int?) {
    var rowOfCells = mutableListOf<BoardCell>()

    init {
        repeat(boardCol!!) {
            rowOfCells.add(BoardCell())
        }
        rowOfCells[rowOfCells.lastIndex].cellValue = "| |"
    }
}

class Board {
    companion object {
        @JvmStatic
        var dimensionsBoard: String? = null
        var boardCol: Int? = 7
        var boardRow: Int? = 6
        var boardField = mutableListOf<BoardRow>()

        fun drawBoard(player1: Player, player2: Player) {
            println("${player1.name} VS ${player2.name}")
            println("$boardRow X $boardCol board")
            when (Game.numOfGames.count()) {
                1 -> println("Single game")
                else -> {
                    Game.singleGame = false
                    println("Total ${Game.numOfGames.count()} games\nGame #${Game.numOfGames[0]}")
                }
            }
            drawInnerPart()
        }

        private fun drawInnerPart() {
            drawNums()
            repeat(boardRow!!) {
                boardField.add(BoardRow(boardCol))
                println()
                for (i in 1..boardCol!! + 1) {
                    when (i) {
                        boardCol!! + 1 -> print("|")
                        else -> print("| ")
                    }
                }
            }
            println()
            drawBottom()
        }

        fun redraw(player: Player, c: Char) {
            drawNums()
            val colIndex = Game.allChoices.filter { it == player.choice }
            val lastrow = boardField[boardField.lastIndex - colIndex.size]
            val playerCell = lastrow.rowOfCells.get(player.choice!!.toInt() - 1)
            playerCell.setCellValue(c)
            println()
            for (row in boardField) {
                for (cell in row.rowOfCells) {
                    print(cell.cellValue)
                }
                println()
            }
            drawBottom()
            Game.allChoices.add(player.choice!!)
            checkWinner(player, c)
        }

        private fun checkWinner(player: Player, c: Char) {
            horizontalCheck(player, c)
            verticalCheck(player, c)
            checkXdirections(player, c, boardCol)
            checkXdirections(player, c, boardCol?.minus(2))
            checkifDraw()
        }

        private fun checkifDraw() {
            var strOfField = ""
            // making a string
            for (row in boardField) {
                for (cell in row.rowOfCells) {
                    strOfField += cell.cellValue?.get(1)
                }
            }
            when {
                Game.over -> return
                ' ' !in strOfField -> {
                    println("It is a draw")
                    if (Game.singleGame) {
                        println("Game over!")
                        Game.over = true
                    } else {
                        println("Score")
                        Game.player1.score += 1
                        Game.player2.score += 1
                        println("${Game.player1.name}: ${Game.player1.score} ${Game.player2.name}: ${Game.player2.score}")
                        Game.numOfGames.removeAt(0)
                        Game.allChoices = mutableListOf<String>()
                        boardField = mutableListOf<BoardRow>()
                        if (Game.numOfGames.count() == 0) {
                            println("Game over!")
                            Game.over = true
                        } else {
                            println("Game #${Game.numOfGames[0]}")
                            drawInnerPart()
                        }
                    }
                }
            }
        }

        private fun checkXdirections(player: Player, c: Char, spacesBeforeCheck: Int?) {
            var stringField = ""
            var stringFieldArr = "".toCharArray()
            var counter = 0
            var hit = false
            var colCounter = spacesBeforeCheck
            // making a string from Game's field
            for (row in boardField) {
                for (cell in row.rowOfCells) {
                    stringField += cell.cellValue?.get(1)
                }
            }
            // iterating string to find a winner
            for ((i, letter) in stringField.withIndex()) {
                // fist hit:
                if ((letter == c) && !hit) {
                    // checking whether or not hit on the same line. We don't wanna have it on the same line
                    stringFieldArr = stringField.toCharArray()
                    // marking hit in array by via char 'g'
                    stringFieldArr[i] = 'g'
                    // end of marking (same line)
                    counter++
                    hit = true
                    continue
                }
                // following hits:
                if (hit) {
                    if (colCounter == 0) {
                        if (letter == c) {
                            // marking cell with char 'g'
                            stringFieldArr[i] = 'g'
                            // we are checking X direction winner so we don't wanna see 2 hits on the same line
                            val resultOfLineCheck = sameLineCheck(stringFieldArr, hit, counter)
                            counter = resultOfLineCheck.first
                            hit = resultOfLineCheck.second
                            //==============
                            counter++
                            colCounter = spacesBeforeCheck
                            if (counter == 4) {
                                winnerMsg(player)
                            }
                            continue
                        } else {
                            hit = false
                        }
                    }
                    colCounter = colCounter?.minus(1)
                }
            }
        }

        private fun sameLineCheck(
            stringFieldArr: CharArray,
            hit: Boolean,
            counter: Int
        ): Pair<Int, Boolean> {
            var thisHit = hit
            var thisCounter = counter
            var cellCounterInOneRow = 0
            var oneline = ""
            // array of lines / rows
            val lineArr = mutableListOf<String>()
            // making lines with number of chars == number of columns
            for (item in stringFieldArr) {
                if (cellCounterInOneRow != boardCol) {
                    oneline += item
                    cellCounterInOneRow++
                } else {
                    lineArr.add(oneline)
                    cellCounterInOneRow = 0
                    oneline = ""
                    oneline += item
                    cellCounterInOneRow++
                }
            }
            // checking whether or not two marked cells in line one if so -> it's not a winner -> return false
            for (row in lineArr) {
                if (row.count { it == 'g' } > 1) {
                    thisHit = false
                    thisCounter = -1
                }
            }
            return Pair(thisCounter, thisHit)
        }


        private fun verticalCheck(player: Player, c: Char) {
            var counter = 0
            for (i in 0 until boardCol!!) {
                boardField.forEach { row ->
                    if (row.rowOfCells[i].cellValue?.get(1) == c) counter++ else counter = 0
                    if (counter == 4) {
                        winnerMsg(player)
                    }
                }
                counter = 0
            }
        }

        private fun horizontalCheck(player: Player, c: Char) {
            boardField.forEach { row ->
                var counter = 0
                row.rowOfCells.forEach { cell ->
                    when (c) {
                        cell.cellValue!!.get(1) -> counter++
                        else -> counter = 0
                    }
                    if (counter == 4) {
                        winnerMsg(player)
                    }
                }
            }
        }

        private fun winnerMsg(player: Player) {
            println()
            println("Player ${player.name} won")
            if (Game.singleGame) {
                println("Game over!")
                Game.over = true
            } else {
                println("Score")
                player.score += 2
                println("${Game.player1.name}: ${Game.player1.score} ${Game.player2.name}: ${Game.player2.score}")
                Game.numOfGames.removeAt(0)
                Game.allChoices = mutableListOf<String>()
                boardField = mutableListOf<BoardRow>()
                if (Game.numOfGames.count() == 0) {
                    println("Game over!")
                    Game.over = true
                } else {
                    println("Game #${Game.numOfGames[0]}")
                    drawInnerPart()
                }
            }
        }

        private fun drawBottom() {
            for (i in 1..boardCol!! * 2 + 1) {
                print("=")
            }

        }

        private fun drawNums() {
            print(" ")
            for (i in 1..boardCol!!) {
                print("$i ")
            }
        }

        fun columnIsFull(player: Player): Boolean {
            val fistRow = boardField[0]
            val playerCell = fistRow.rowOfCells[player.choice!!.toInt() - 1]
            return !playerCell.checkCell()
        }
    }
}

class Game {
    companion object {
        var singleGame = true
        var numOfGames = mutableListOf<String>()
        var over: Boolean = false
        var allChoices = mutableListOf<String>()
        var player1 = Player()
        var player2 = Player()

        @JvmStatic
        fun greeting() {
            println("Connect Four")
        }

        fun askForDimensions(player1: Player, player2: Player) {
            while (true) {
                try {
                    println("Set the board dimensions (Rows x Columns)")
                    println("Press Enter for default (6 x 7)")
                    Board.dimensionsBoard = readLine()?.trim()?.replace("\\s".toRegex(), "")
                    if (Board.dimensionsBoard == "") {
                        howManyGames()
                        Board.drawBoard(player1, player2)
                        break
                    } else {
                        val dimensionsOnboardArr = Board.dimensionsBoard?.lowercase()?.split("x")
                        Board.boardRow = dimensionsOnboardArr?.get(0).toString().toInt()
                        Board.boardCol = dimensionsOnboardArr?.get(1).toString().toInt()
                        if (Board.boardRow in 5..9) {
                            if (Board.boardCol in 5..9) {
                                howManyGames()
                                Board.drawBoard(player1, player2)
                                break
                            } else {
                                println("Board columns should be from 5 to 9")
                            }
                        } else {
                            println("Board rows should be from 5 to 9")
                        }
                    }
                } catch (e: Exception) {
                    println("Invalid input")
                }
            }
        }

        fun turn(player: Player) {
            println("${player.name}'s turn:")
            player.choice = readLine()
        }

        fun scenarios(player: Player, c: Char) {
            try {
                when {
                    player.choice == "end" -> {
                        println("Game over!")
                        over = true
                    }
                    player.choice?.toInt() !in 1..Board.boardCol!! -> {
                        println("The column number is out of range (1 - ${Board.boardCol})")
                        turn(player)
                        scenarios(player, c)
                    }
                    Board.columnIsFull(player) -> {
                        println("Column ${player.choice} is full")
                        turn(player)
                        scenarios(player, c)
                    }
                    player.choice?.toInt() in 1..Board.boardCol!! -> {
                        Board.redraw(player, c)
                    }
                    else -> {
                        println("Incorrect column number")
                        turn(player)
                        scenarios(player, c)
                    }
                }
            } catch (e: Exception) {
                println("Incorrect column number")
                turn(player)
                scenarios(player, c)
            }
        }

        private fun howManyGames() {
            println("Do you want to play single or multiple games?")
            println("For a single game, input 1 or press Enter")
            println("Input a number of games:")
            try {
                var userInput = readLine()
                var gameNumber = if (userInput == "") 1 else (userInput!!.toInt())
                if (gameNumber == 0) {
                    println("Invalid input")
                    howManyGames()
                }
                for (i in 1..gameNumber) {
                    numOfGames.add(i.toString())
                }
            } catch (e: Exception) {
                println("Invalid input")
                howManyGames()
            }
        }
    }
}

fun main() {
    val player1 = Game.player1
    val player2 = Game.player2
    var gameSwitchBoolean = true

    Game.greeting()
    player1.setpName("First player's name:")
    player2.setpName("Second player's name:")
    Game.askForDimensions(player1, player2)
    println()
    while (!Game.over) {
        when (gameSwitchBoolean) {
            true -> {
                Game.turn(player1)
                Game.scenarios(player1, 'o')
                gameSwitchBoolean = !gameSwitchBoolean
            }
            else -> {
                Game.turn(player2)
                Game.scenarios(player2, '*')
                gameSwitchBoolean = !gameSwitchBoolean
            }
        }
        println()
    }
}

