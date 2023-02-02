package search
import java.io.File


val peoples = "Kristofer Galley\n" +
        "Fernando Marbury fernando_marbury@gmail.com\n" +
        "Kristyn Nix nix-kris@gmail.com\n" +
        "Regenia Enderle\n" +
        "Malena Gray\n" +
        "Colette Mattei\n" +
        "Wendolyn Mcphillips\n" +
        "Jim Gray\n" +
        "Coreen Beckham\n" +
        "Bob Yeh bobyeah@gmail.com\n" +
        "Shannan Bob stropeshah@gmail.com\n" +
        "Yer Fillion\n" +
        "Margene Resendez marres@gmail.com\n" +
        "Blossom Ambler\n" +
        "Teri Ledet teri_ledet@gmail.com\n" +
        "Dana Baron baron@gmail.com\n" +
        "Abram Goldsberry\n" +
        "Yer Leopold\n" +
        "Stefania Trunzo\n" +
        "Alexis Leopold\n" +
        "Carlene Bob\n" +
        "Oliver Dacruz\n" +
        "Jonie Richter\n" +
        "Pasquale Gallien gallien@evilcorp.com\n" +
        "Verdie Gentle\n" +
        "Gerardo Strouth gallien@evilcorp.com\n" +
        "Agripina Bob\n" +
        "Latricia Niebuhr\n" +
        "Malena Schommer\n" +
        "Drema Leopold\n" +
        "Heide Payeur\n" +
        "Ranae Digiovanni\n" +
        "Simona Pereira\n" +
        "Nick Digiovanni\n" +
        "Angelita Wigington gallien@evilcorp.com\n" +
        "Elin Gray\n" +
        "Dwain Trunzo\n" +
        "Boris Beiler\n" +
        "Remi Malek fsociefy@gmail.com\n" +
        "Demetria Hostetler gallien@evilcorp.com\n" +
        "Nydia Mcduffie\n" +
        "Florencio Defibaugh\n" +
        "Warner Giblin\n" +
        "Bob Mans\n" +
        "Shu Gray\n" +
        "Kaycee Gray\n" +
        "Victorina Froehlich victory@gmail.com\n" +
        "Roseanne Gray\n" +
        "Erica Radford hisam@gmail.com\n" +
        "Elyse Pauling"

fun main(args: Array<String>) {
    if(args[0] == "--data") {
        val peoples = readPeopleFromFile(args[1])
        val map = createdInvertedIndex(peoples)
        var exit = false
        while (!exit) {
            showMenu()
            val option = readMenuOption()
            if (option == "1") {
                val strategy = readStrategy()
                val query = readPeopleQuery()
                val queries = query.split(Regex("\\s"))
                val results = mutableListOf<List<Int>>()
                for (value in queries) {
                    val index =  map[value.lowercase()]
                    if(index != null) {
                        results.add(index)
                    }
                }
                val indexes = when (strategy) {
                    "any" -> getUnion(results)
                    "all" -> getIntersection(results)
                    "none" -> getNegation(getUnion(results), peoples.size)
                    else -> listOf()
                }

                val result = getResultFromInvertedIndex(peoples, indexes)
                if (result == "") {
                    println("No matching people found.\n")
                } else {
                    println(result)
                }
            } else if (option == "2") {
                printPeople(peoples)
            } else if (option == "0") {
                println("Bye!")
                exit = true
            } else {
                println("\nIncorrect option! Try again")
            }
        }
    }
}

fun getResultFromInvertedIndex(peoples: List<String>, indexes: List<Int>?): String {
    var result = "\n"
    if (indexes != null && indexes.isNotEmpty()) {
        result += "${indexes.size} persons found:\n"
        for(i in indexes) {
           result += peoples[i]
            result += "\n"
        }
    }
    return result;
}

fun createdInvertedIndex(peoples: List<String>): MutableMap<String, List<Int>> {
    val map = mutableMapOf<String, List<Int>>()
    for(people in peoples) {
        val keys = people.split(Regex("\\s"))
        for(key in keys) {
            val value = returnSearchIndex(key, peoples);
            map[key.lowercase()] = value;
        }
    }
    return map
}

fun readPeopleFromFile(path: String): List<String> {
    val file = File(path)
    val data = file.readLines()
    val peoples = mutableListOf<String>()
    for(line in data) {
        peoples.add(line)
    }
    return peoples;
}

fun readPeopleFromCommand(): List<String> {
    val numPeople = readln().toInt()
    val peoples = mutableListOf<String>()
    println("Enter all people:")
    for (i in 1..numPeople) {
        val people = readln()
        peoples.add(people)
    }
    return peoples
}

fun readPeopleQuery(): String {
    println("\nEnter a name or email to search all matching people.")
    return readln()
}

fun searchPeople(query: String, peoples: List<String>): String {
    var result = ""
    val regex = Regex(query.lowercase())
    for(people in peoples) {
        val matches = regex.containsMatchIn(people.lowercase())
        if(matches) {
            result += people
            result += "\n"
        }
    }
    return result
}

fun returnSearchIndex(query: String, peoples: List<String>): List<Int> {
    val result = mutableListOf<Int>()
    val regex = Regex(query.lowercase())
    for(i in peoples.indices) {
        val matches = regex.containsMatchIn(peoples[i].lowercase())
        if(matches) {
            result.add(i)
        }
    }
    return result
}

fun showMenu() {
    println("=== Menu ===")
    println("1. Find a person")
    println("2. Print all people")
    println("0. Exit")
}

fun printPeople(peoples: List<String>) {
    println("=== List of people ===")
    var result = ""
    for(people in peoples) {
        result += people
        result += "\n"
    }
    print(result)
}

fun readMenuOption(): String {
    return readln()
}

fun readStrategy(): String {
    println("\nSelect a matching strategy: ALL, ANY, NONE")
    return readln().lowercase()
}

fun getUnion(lists: List<List<Int>>): List<Int> {
    val map = mutableMapOf<Int, Boolean>();
    val result = mutableListOf<Int>()
    for (list in lists) {
        for(index in list) {
            if(map[index] == null) {
                result.add(index)
                map[index] = true
            }
        }
    }
    return result
}

fun getIntersection(lists: List<List<Int>>): List<Int> {
    val map = mutableMapOf<Int, Int>()
    val result = mutableListOf<Int>()
    if (lists.isEmpty()) {
        return result
    }
    for(index in lists[0]) {
        map[index] = 0
    }
    for (list in lists) {
        for(index in list) {
            if(map[index] != null) {
                map[index] = map[index]?.plus(1) ?: 0
            }
        }
    }
    for((k,v) in map) {
        if(v == lists.size) {
            result.add(k)
        }
    }
    return result
}

fun getNegation(list: List<Int>, size: Int): List<Int> {
    val map = mutableMapOf<Int, Int>()
    val result = mutableListOf<Int>()
    for(index in list) {
        map[index] = 0
    }
    for(i in 0 until size) {
        if(map[i] == null) {
            result.add(i)
        }
    }
    return result
}


