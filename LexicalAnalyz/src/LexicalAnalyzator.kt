import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class LexicalAnalyzator {
    var reservedWords = ArrayList<String>()
    var operations = ArrayList<String>()
    var separators = ArrayList<Char>()
    var identificators = ArrayList<String>()
    var numberConst = ArrayList<String>()
    var stringConst = ArrayList<String>()
    var buffer = ""
    var currentSymbol = ""
    var tempSymbol : Char? = null
    val WIDTH = 14
    val HEIGHT = 31
    val file = File("output.txt").bufferedWriter()
    var it: Char = '0'

    var automaton: Array<Array<String>>

    init {
       var file = File("automaton.txt")
        var scan = Scanner(file)
        automaton = Array(HEIGHT){ _ ->
            Array(WIDTH){ _ ->
                scan.next()
            }
        }

        scan = Scanner(File("reservedwords.txt"))
        while (scan.hasNext())
            reservedWords.add(scan.next())

        scan = Scanner(File("operations.txt"))
        while (scan.hasNext())
            operations.add(scan.next())

        scan = Scanner(File("separators.txt"))
        while (scan.hasNext())
            separators.add(scan.next()[0])

        var test = 5
        separators.add('\n')
    }



    fun analyze(str : String){
        var index: Int
        var state = 0
        for(element in str){
            println("Buffer is $buffer")
            println("State is $state")
            currentSymbol = "1"
            it= element
            index = when(it){
                'C' ->  2
                'E' ->  4
                in 'A'..'Z'->  0
                in '0'..'9' ->  1
                '.' ->  3
                '+', '-' ->  5
                '*' ->  6
                '/', '=' ->  7
                '"', '\'' ->  8
                '\n' -> 11
                '#' -> 13
                in separators -> 9
                ' ' -> 10
                else -> 12
            }
            println("Index is $index")
            if(automaton[state][index] == "F"){
                println("Ошибка!")
                file.close()
                println(it)
                return
            }
        if (automaton[state][index].contains(",")){
            var temp = automaton[state][index].split(",")
            state = temp[0].toInt()
            semantyc(temp[1].toInt())
        }
            else state = automaton[state][index].toInt()
        if(state == 15){
            println("Ошибка!")
            return
        }
        if(currentSymbol != "" && it != ' ')
            buffer+=it
        }
        file.close()
        println("success")

    }


    fun semantyc(n : Int){
        println("procedure $n")
        when(n){
            1-> {
                if (identificators.indexOf(buffer) == -1) {
                    identificators.add(buffer)
                    file.write("i${identificators.size} ")
                }
                else file.write("i${identificators.indexOf(buffer)+1} ")
                buffer = ""
                }
            2-> {
                if (reservedWords.indexOf(buffer) != -1) {
                    file.write("w${reservedWords.indexOf(buffer) + 1} ")
                    buffer=""
                }
                else semantyc(1)
                }
            3-> {
                if(numberConst.indexOf(buffer) == -1)
                    numberConst.add(buffer)
                file.write("n${numberConst.indexOf(buffer) + 1} ")
                buffer=""
                }
            4-> {
                file.write("r${separators.indexOf(buffer[0])+1} ")
                if(separators.indexOf(buffer[0]) == 3 )
                    file.newLine()
                buffer=""
                }
            5-> {
                file.write("o${operations.indexOf(buffer)+1 } ")
                buffer=""
                }
            6-> {
                buffer+=it
                file.write("o${operations.indexOf(buffer)+1} ")
                buffer=""
                it=' '
                }
            7-> {
                buffer.substring(1)
                stringConst.add(buffer)
                file.write("c${stringConst.size-1} ")
                buffer=""
                it=' '
                }
            8-> {
                buffer=""
                }
        }

    }


    companion object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            var str = ""
            File("fortran.txt").bufferedReader().forEachLine { str += it+'\n' }
            var analysis = LexicalAnalyzator()
            if(str!="")
            analysis.analyze(str)
        }
    }

}