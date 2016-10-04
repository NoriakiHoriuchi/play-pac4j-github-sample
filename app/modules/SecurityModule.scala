package modules

import com.google.inject.AbstractModule
import controllers.{CustomAuthorizer, DemoHttpActionAdapter}
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.oauth.client.GitHubClient
import org.pac4j.play.store.{PlayCacheStore, PlaySessionStore}
import org.pac4j.play.{ApplicationLogoutController, CallbackController}
import play.api.{Configuration, Environment}

/**
 * Guice DI module to be included in application.conf
 */
class SecurityModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {
    val gitHubId = configuration.getString("gitHubId").get
    val gitHubSecret = configuration.getString("gitHubSecret").get
    val baseUrl = configuration.getString("baseUrl").get

    // OAuth
    val gitHubClient = new GitHubClient(gitHubId, gitHubSecret)
    gitHubClient.setScope("repo,user") // default is "user"

    val clients = new Clients(baseUrl + "/callback", gitHubClient)

    val config = new Config(clients)
    config.addAuthorizer("admin", new RequireAnyRoleAuthorizer[Nothing]("ROLE_ADMIN"))
    config.addAuthorizer("custom", new CustomAuthorizer)
    config.setHttpActionAdapter(new DemoHttpActionAdapter())
    bind(classOf[Config]).toInstance(config)

    bind(classOf[PlaySessionStore]).to(classOf[PlayCacheStore])

    // callback
    val callbackController = new CallbackController()
    callbackController.setDefaultUrl("/?defaulturlafterlogout")
    callbackController.setMultiProfile(true)
    bind(classOf[CallbackController]).toInstance(callbackController)

    // logout
    val logoutController = new ApplicationLogoutController()
    logoutController.setDefaultUrl("/")
    bind(classOf[ApplicationLogoutController]).toInstance(logoutController)
  }
}
