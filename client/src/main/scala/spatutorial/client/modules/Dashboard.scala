package spatutorial.client.modules

import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import japgolly.scalajs.react.extra.router2.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.SPAMain.{TodoLoc, Loc}
import spatutorial.client.components._

object Dashboard {
  case class State(index : Option[Int] = None, prevIndex : Option[Int] = None)

  class Backend(t: BackendScope[RouterCtl[Loc], State]) {
    def newInd(lc: LineChart,index: Option[Int]): Unit = {
      if (t.state.index != index) {
        t.modState(curr => State(index, curr.index))
        t.state.index match {
          case Some(i) => lc.datasets(i).strokeColor = "rgba(64,64,128,1)"
          case _ => ()
        }
        t.state.prevIndex match {
          case Some(i) => lc.datasets(i).strokeColor = "rgba(64,64,128,0.5)"
          case _ => ()
        }
        lc.draw(lc)
      }
    }
  }

  // create the React component for Dashboard
  val component = ReactComponentB[RouterCtl[Loc]]("Dashboard")
    .initialState(State())
    .backend(new Backend(_))
    .render((ctl, S, B) => {
      // create dummy data for the chart
      val cp = Chart.ChartProps(
        "Test chart",
        Chart.LineChart,
        ChartData(Seq("A", "B", "C"),
          Seq(
            ChartDataset(Seq(6, 8, 3), "Data1", "rgba(220,220,220,0)"),
            ChartDataset(Seq(1, 2, 7), "Data2", "rgba(220,220,220,0)")
          )),
        additionalSetup = (canvas, lc) => {
          try {
            Helpers.addEvent(canvas, "mousemove", (event: Helpers.Event) => {
              val mouse = Helpers.getRelativePosition(event)
              val min = lc.datasets.zipWithIndex.flatMap(t => {
                var prev : Option[Helpers.Position] = None
                var curr : Option[Helpers.Position] = None
                (t._1.points.to[List].dropWhile(point => {
                  prev = curr
                  curr = Some(point)
                  point.x < mouse.x
                }), prev) match {
                  case (Nil, _) => None
                  case (_, None) => None
                  case (rightPoint::_, Some(leftPoint)) =>
                    Some(t._2, Math.abs(
                      leftPoint.y - mouse.y + (
                        (mouse.x - leftPoint.x) * (rightPoint.y - leftPoint.y) /
                          (rightPoint.x - leftPoint.x)
                        )))
                }
              }).reduceOption(Ordering.by((_ : (Int, Double))._2).min) match {
                case Some((ind, distance)) if distance < 5.0 => Some(ind)
                case _ => None
              }
              B.newInd(lc, min)
            })
          } catch {
            case e => println(e)
          }
        }
      )
      <.div(
        // header, MessageOfTheDay and chart components
        <.h2("Dashboard"),
        Motd(),
        Chart(cp),
        // create a link to the Todo view
        <.h2(S.index),
        <.div(ctl.link(TodoLoc)("Check your todos!"))
      )
    }).build
}
