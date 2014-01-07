package models

import com.tinkerpop.frames.{Adjacency, Property, VertexFrame, FramedGraph}
import com.tinkerpop.blueprints.{TransactionalGraph, Element}
import scala.collection.JavaConversions._
import com.tinkerpop.gremlin.java.GremlinPipeline
import java.util.UUID
import com.tinkerpop.frames.structures.FramedVertexIterable
import scala.concurrent.{future, Future}
import play.api.libs.concurrent.Execution.Implicits._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import com.tinkerpop.blueprints.impls.orient.OrientGraph

/**
 * Base VertexFrame with meta getters / setters
 */
trait BaseVertexFrame extends VertexFrame {
  type Extracted <: BaseVertexFrame
  def meta: Base[Extracted]

  @Property("class_")
  def setCls(c: String)

  @Property("class_")
  def getCls: String

  @Property("uid")
  def setUID(uid: String)

  @Property("uid")
  def getUID: String

  @Property("name")
  def setName(name: String)

  @Property("name")
  def getName: String

  @Property("created_at")
  def setCreatedAt(dt: String)

  @Property("created_at")
  def getCreatedAt: String

  @Property("updated_at")
  def setUpdatedAt(dt: String)

  @Property("updated_at")
  def getUpdatedAt: String
}

trait DB[T <: BaseVertexFrame] {

  val CLASS_PROPERTY_NAME = "class_"

  val baseGraph = new OrientGraph("local:/tmp/orient", "admin", "admin")

  def graph = new FramedGraph(baseGraph)

  implicit def dbWrapper(vf: T) = new {
    /**
     * Saves pending changes to the Graph
     * @return
     */
    def save:Future[T] = {
      if(vf.getCreatedAt == null) vf.setCreatedAt(ISODateTimeFormat.dateTime().print(new DateTime))
      vf.setUpdatedAt(ISODateTimeFormat.dateTime().print(new DateTime))

      future {
        baseGraph.asInstanceOf[TransactionalGraph].commit
        vf.asInstanceOf[T]
      }
    }
    /**
     * Removes a Frame from the Graph
     */
    def delete:Future[Unit] = {
      graph.removeVertex(graph.getVertices("uid", vf.getUID).toList.head)

      future {
        baseGraph.asInstanceOf[TransactionalGraph].commit
      }
    }
  }

}

/**
 * Base provides some basic methods for instancing objects from the Graph Database
 * @tparam T
 */
abstract class Base[T <: BaseVertexFrame: Manifest] extends DB[T] { self =>

  def apply(): T = {
    val out:T = graph.frame(graph.addVertex(null), manifest[T].runtimeClass.asInstanceOf[Class[T]])
    out.setUID(UUID.randomUUID.toString)
    out.setCls(manifest[T].runtimeClass.getName)
    out.asInstanceOf[T]
  }

  /**
   * List frames from the graph corresponding to the extended model
   * @return
   */
  def list:Iterable[T] = {
    graph.frameVertices(graph.getVertices(CLASS_PROPERTY_NAME, manifest[T].runtimeClass.getName), manifest[T].runtimeClass.asInstanceOf[Class[T]])
  }
}