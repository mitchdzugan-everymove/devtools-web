package spatutorial.client.modules

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router2.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.SPAMain.Loc
import spatutorial.client.components.Bootstrap._

object Home {
  private val MainMenu = ReactComponentB[RouterCtl[Loc]]("MainMenu")
    .render( c => {
      Panel(Panel.Props("Everymove Dev Tools"),
        <.div(
          <.span("I will use this to dump various tools and visualizations that I think could be beneficial. " +
              "Feel free to contribute on "),
          <.a(^.href := "https://github.com/mitchdzugan-everymove/devtools-web")("github"),
          <.span(" or shoot me an email to request features.")
        ))
    }).build

  def apply(c : RouterCtl[Loc]) = MainMenu(c)
}
