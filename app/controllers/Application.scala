package controllers

import javax.inject.Inject

import org.pac4j.core.client.{Clients, IndirectClient}
import org.pac4j.core.config.Config
import org.pac4j.core.credentials.Credentials
import org.pac4j.core.profile._
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala._
import org.pac4j.play.store.PlaySessionStore
import play.api.mvc._
import play.libs.concurrent.HttpExecutionContext

import scala.collection.JavaConversions._

class Application @Inject() (val config: Config, val playSessionStore: PlaySessionStore, override val ec: HttpExecutionContext) extends Controller with Security[CommonProfile] {

  private def getProfiles(implicit request: RequestHeader): List[CommonProfile] = {
    val webContext = new PlayWebContext(request, playSessionStore)
    val profileManager = new ProfileManager[CommonProfile](webContext)
    val profiles = profileManager.getAll(true)
    asScalaBuffer(profiles).toList
  }

  def index = Action { request =>
    val profiles = getProfiles(request)
    Ok(views.html.index(profiles))
  }

  def gitHubIndex = Secure("GitHubClient") { profiles =>
    Action { request =>
      Ok(views.html.protectedIndex(profiles))
    }
  }

  def gitHubAdminIndex = Secure("GitHubClient", "admin") { profiles =>
    Action { request =>
      Ok(views.html.protectedIndex(profiles))
    }
  }

  def gitHubCustomIndex = Secure("GitHubClient", "custom") { profiles =>
    Action { request =>
      Ok(views.html.protectedIndex(profiles))
    }
  }

  def forceLogin = Action { request =>
    val context: PlayWebContext = new PlayWebContext(request, playSessionStore)
    val client = config.getClients.findClient(context.getRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER)).asInstanceOf[IndirectClient[Credentials,CommonProfile]]
    Redirect(client.getRedirectAction(context).getLocation)
  }
}
