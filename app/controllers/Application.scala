package controllers

import play.api.mvc.{Action, Controller}
import models.{Bar, Foo}
import play.api.libs.concurrent.Execution.Implicits._

object Application extends Controller {
  def index = Action {
    val prevCount = Foo.list.size

    val foo = Foo()
    foo.setName("test1")
    foo.save

    if (Foo.list.size > prevCount) println("GOOD") else println("BAD")

    val prevCount2 = Bar.list.size
    for {
      foo <- Foo().save
      bar <- Bar().save
    } yield {
      if (Bar.list.size > prevCount2) println("GOOD") else println("BAD")
    }

    Ok
  }
}