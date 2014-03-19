package test

import play.api.test.PlaySpecification
import play.api.mvc.Action
import models.{Bar, Foo}
import scala.concurrent.Await
import scala.concurrent.duration._

class GriffonApiSpec extends PlaySpecification {

  //import play.api.libs.concurrent.Execution.Implicits.defaultContext
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  for (foo <- Foo.list) Await.result(foo.delete, Duration.Inf)
  for (bar <- Bar.list) Await.result(bar.delete, Duration.Inf)

  "Make sure the database is empty" in {
    Foo.list.size must equalTo(0)
    Bar.list.size must equalTo(0)
  }

  "test 1" in {

    val prevCount = Foo.list.size

    val foo = Foo()
    foo.setName("test1")
    foo.save

    Foo.list.size must be_>(prevCount)
  }

  "TEST 2" in {

    val prevCount2 = Bar.list.size
    await(for {
      foo <- Foo().save
      bar <- Bar().save
    } yield {
      Bar.list.size
    }) must be_>(prevCount2)

  }

  "TEST 3" in {

    await(Foo().save.map(x => {
      x.setName("test3")
      x.save

      Foo.list.filter(_.getName eq "test3").size
    })) must equalTo(1)
  }
}