package services

import java.util.{UUID, Date}

import slick.driver
import spatutorial.shared.Api
import spatutorial.shared.TodoItem
import spatutorial.shared.{TodoLow, TodoNormal, TodoHigh}

import slick.driver.JdbcProfile
import play.api.db.slick.{HasDatabaseConfig, DatabaseConfigProvider}
import play.api.Play

import models._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scala.util.{Try, Success, Failure}


class ApiService extends Api with DeverrorTable with HasDatabaseConfig[JdbcProfile] {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  import driver.api._

  private val Deverrors = TableQuery[Deverrors]

  Await.ready(db.run(Deverrors.schema.create), Duration.Inf).value.get match {
    case Success(_) => println("LIVIN THO")
    case Failure(e) => println(e)
  }

  var todos = Seq(
    TodoItem("41424344-4546-4748-494a-4b4c4d4e4f50", 0x61626364, "Wear shirt that says “Life”. Hand out lemons on street corner.", TodoLow, false),
    TodoItem("2", 0x61626364, "Make vanilla pudding. Put in mayo jar. Eat in public.", TodoNormal, false),
    TodoItem("3", 0x61626364, "Walk away slowly from an explosion without looking back.", TodoHigh, false),
    TodoItem("4", 0x61626364, "Sneeze in front of the pope. Get blessed.", TodoNormal, true)
  )

  override def motd(name: String): String = {
    Await.ready(db.run(Deverrors.result), Duration.Inf).value.get match {
      case Success(t) => t.length.toString
      case Failure(e) => e.toString
    }
  }

  override def getTodos(): Seq[TodoItem] = {
    // provide some fake Todos
    println(s"Sending ${todos.size} Todo items")
    todos
  }

  // update a Todo
  override def updateTodo(item: TodoItem): Seq[TodoItem] = {
    // TODO, update database etc :)
    if(todos.exists(_.id == item.id)) {
      todos = todos.collect {
        case i if i.id == item.id => item
        case i => i
      }
      println(s"Todo item was updated: $item")
    } else {
      // add a new item
      val newItem = item.copy(id = UUID.randomUUID().toString)
      todos :+= newItem
      println(s"Todo item was added: $newItem")
    }
    todos
  }

  // delete a Todo
  override def deleteTodo(itemId: String): Seq[TodoItem] = {
    println(s"Deleting item with id = $itemId")
    todos = todos.filterNot(_.id == itemId)
    todos
  }
}
