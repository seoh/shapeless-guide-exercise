package ch3

object Main {
  def main(args: Array[String]): Unit = {
    val employees: List[Employee] = List(
      Employee("Bill", 1, true),
      Employee("Peter", 2, false),
      Employee("Milton", 3, false)
    )

    //    println(CsvEncoder.writeCsv(employees))

    val iceCreams: List[IceCream] = List(
      IceCream("Sundae", 1, false),
      IceCream("Cornetto", 0, true),
      IceCream("Banana Split", 0, false)
    )

    //    println(CsvEncoder.writeCsv(iceCreams))

    //    println(CsvEncoder.writeCsv(employees zip iceCreams))

  }
}
