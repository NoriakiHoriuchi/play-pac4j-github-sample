package controllers

import javax.inject.Inject

import org.pac4j.core.config.Config
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.core.util.CommonHelper
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala.Security
import org.pac4j.play.store.PlaySessionStore
import play.api.libs.json.Json
import play.api.mvc._
import play.libs.concurrent.HttpExecutionContext

import scala.collection.JavaConversions._

class ApplicationWithFilter @Inject() (val config: Config, val playSessionStore: PlaySessionStore, override val ec: HttpExecutionContext) extends Controller with Security[CommonProfile] {

  private def getProfiles(implicit request: RequestHeader): List[CommonProfile] = {
    val webContext = new PlayWebContext(request, playSessionStore)
    val profileManager = new ProfileManager[CommonProfile](webContext)
    val profiles = profileManager.getAll(true)
    asScalaBuffer(profiles).toList
  }

  def index = Action { implicit request =>
    val profile = getProfiles(request)
    Ok(views.html.index(profile))
  }

  def gitHubIndex = Action { implicit request =>
    Ok(views.html.protectedIndex(getProfiles(request)))
  }

  def gitHubAdminIndex = Action { implicit request =>
    Ok(views.html.protectedIndex(getProfiles(request)))
  }

  def gitHubCustomIndex = Action { implicit request =>
    Ok(views.html.protectedIndex(getProfiles(request)))
  }
}
