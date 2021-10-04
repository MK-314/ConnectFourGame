package com.example.myapplication


class Player {
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

class BRow(boardCol: Int?) {
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
        var bField = mutableListOf<BRow>()

        fun drawBoard(player1: Player, player2: Player) {
            println("${player1.name} VS ${player2.name}")
            println("$boardRow X $boardCol board")
            drawnums()
            repeat(boardRow!!) {
                bField.add(BRow(boardCol))
                println()
                for (i in 1..boardCol!! + 1) {
                    when (i) {
                        boardCol!! + 1 -> print("|")
                        else -> print("| ")
                    }
                }
            }
            println()
            drawbottom()
        }

        fun redraw(player: Player, c: Char) {
            drawnums()
            val colIndex = Game.allChoices.filter { it.equals(player.choice) }
            val lastrow = bField[bField.lastIndex - colIndex.size]
            val playerCell = lastrow.rowOfCells.get(player.choice!!.toInt() - 1)
            playerCell.setCellValue(c)
            println()
            for (row in bField) {
                for (cell in row.rowOfCells) {
                    print(cell.cellValue)
                }
                println()
            }
            drawbottom()
            Game.allChoices.add(player.choice!!)
        }

        private fun drawbottom() {
            for (i in 1..boardCol!! * 2 + 1) {
                print("=")
            }

        }

        private fun drawnums() {
            print(" ")
            for (i in 1..boardCol!!) {
                print("$i ")
            }
        }

        fun columnIsFull(player: Player): Boolean {
            val fistrow = bField.get(0)
            val playerCell = fistrow.rowOfCells.get(player.choice!!.toInt() - 1)
            return !playerCell.checkCell()
        }
    }
}

class Game {
    companion object {
        var over: Boolean = false
        var allChoices = mutableListOf<String>()

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
                        Board.drawBoard(player1, player2)
                        break
                    } else {
                        val dimensionsOnboardArr = Board.dimensionsBoard?.lowercase()?.split("x")
                        Board.boardRow = dimensionsOnboardArr?.get(0).toString().toInt()
                        Board.boardCol = dimensionsOnboardArr?.get(1).toString().toInt()
                        if (Board.boardRow in 5..9) {
                            if (Board.boardCol in 5..9) {
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
    }
}

fun main() {
    val player1 = Player()
    val player2 = Player()
    var gameSwitchBoolean: Boolean = true

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

