package ch3

import shapeless.{:+:, ::, CNil, Coproduct, Generic, HList, HNil, Inl, Inr}

trait CsvEncoder[A] {
  def encode(value: A): List[String]
}

case class Employee(name: String, number: Int, manager: Boolean)

case class IceCream(name: String, numCherries: Int, inCone: Boolean)



sealed trait Shape
final case class Rectangle(width: Double, height: Double) extends Shape
final case class Circle(radius: Double) extends Shape



//// OLD-fashioned encoder
//object CsvEncoder {
//  implicit val employeeEncoder = new CsvEncoder[Employee] {
//    override def encode(e: Employee): List[String] =
//      List(
//        e.name,
//        e.number.toString,
//        if (e.manager) "yes" else "no"
//      )
//  }
//
//  implicit val iceCreamEncoder = new CsvEncoder[IceCream] {
//    override def encode(i: IceCream): List[String] =
//      List(
//        i.name,
//        i.numCherries.toString,
//        if(i.inCone) "yes" else "no"
//      )
//  }
//
//  implicit def pairEncoder[A, B](
//    implicit
//    aEncoder: CsvEncoder[A],
//    bEncoder: CsvEncoder[B]
//  ): CsvEncoder[(A, B)] = new CsvEncoder[(A, B)] {
//    override def encode(pair: (A, B)) = {
//      val (a, b) = pair
//      aEncoder.encode(a) ++ bEncoder.encode(b)
//    }
//  }
//
//
//  def writeCsv[A](values: List[A])(implicit enc: CsvEncoder[A]): String =
//    values.map(value => enc.encode(value).mkString(",")).mkString("\n")
//}


object CsvEncoder {
  def createEncoder[A](func: A => List[String]) = new CsvEncoder[A] {
    override def encode(value: A): List[String] = func(value)
  }

  // about shapeless
  implicit val hnilEncoder: CsvEncoder[HNil] =
    createEncoder(hnil => Nil)

  implicit def hlistEncoder[H, T <: HList](
                                            implicit
                                            hEncoder: CsvEncoder[H],
                                            tEncoder: CsvEncoder[T]
                                          ): CsvEncoder[H :: T] =
    createEncoder {
      case h :: t => hEncoder.encode(h) ++ tEncoder.encode(t)
    }


  implicit val stringEncoder: CsvEncoder[String] =
    createEncoder(str => List(str))
  implicit val intEncoder: CsvEncoder[Int] =
    createEncoder(num => List(num.toString))
  implicit val booleanEncoder: CsvEncoder[Boolean] =
    createEncoder(bool => List(if (bool) "yes" else "no"))

  //  val reprEncoder: CsvEncoder[String :: Int :: Boolean :: HNil] = implicitly
  //  val reprTest: List[String] = reprEncoder.encode("abc" :: 123 :: true :: HNil)

  //// without Aux
  //  implicit def genericEncoder[A, R](
  //                                  implicit
  //                                  gen: Generic[A] { type Repr = R },
  //                                  enc: CsvEncoder[R]
  //                                ): CsvEncoder[A] =
  //    createEncoder(a => enc.encode(gen.to(a)))

  // with Aux
  implicit def genericEncoder[A, R](
                                     implicit
                                     gen: Generic.Aux[A, R],
                                     enc: CsvEncoder[R]
                                   ): CsvEncoder[A] =
    createEncoder(a => enc.encode(gen.to(a)))

//  val iceCreamEncoder: CsvEncoder[IceCream] = implicitly


  implicit val cnilEncoder: CsvEncoder[CNil] =
    createEncoder(cnil => throw new Exception("Inconceivable"))

  implicit def coproductEncoder[H, T <: Coproduct](
                                                  implicit
                                                  hEncoder: CsvEncoder[H],
                                                  tEncoder: CsvEncoder[T]
                                                  ) : CsvEncoder[H :+: T] =
    createEncoder {
      case Inl(h) => hEncoder.encode(h)
      case Inr(t) => tEncoder.encode(t)
    }

  implicit val doubleEncoder: CsvEncoder[Double] =
    createEncoder(d => List(d.toString))
}