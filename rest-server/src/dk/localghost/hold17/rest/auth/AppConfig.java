package dk.localghost.hold17.rest.auth;

import dk.localghost.hold17.rest.config.Routes;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath(Routes.OAUTH_ROOT)
public class AppConfig extends Application {}