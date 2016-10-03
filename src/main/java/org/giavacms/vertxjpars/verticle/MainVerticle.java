package org.giavacms.vertxjpars.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.giavacms.vertxjpars.repository.DocumentRepository;
import org.giavacms.vertxjpars.service.rs.DocumentRepositoryRs;
import org.giavacms.vertxjpars.service.rs.RequestEnricherRepositoryRs;

import static org.giavacms.vertxjpars.management.AppConstants.*;

/**
 * Created by fiorenzo on 02/10/16.
 */
public class MainVerticle extends AbstractVerticle
{

   private final static Logger logger = LoggerFactory.getLogger(MainVerticle.class);
   private JDBCClient jdbcClient;
   private DocumentRepository documentRepository;

   @Override
   public void start() throws Exception
   {
      Router router = Router.router(vertx);
      router.route(APP_PATH).handler(StaticHandler.create("assets/"));
      router.route(API_PATH + "*").handler(BodyHandler.create());
      jdbcClient = JDBCClient.createShared(vertx, mysqlConfig());
      DocumentRepositoryRs documentRepositoryRs = new DocumentRepositoryRs(router, jdbcClient, vertx);
      vertx.deployVerticle(documentRepositoryRs);

      RequestEnricherRepositoryRs requestEnricherRepositoryRs = new RequestEnricherRepositoryRs(router, jdbcClient,
               vertx);
      vertx.deployVerticle(requestEnricherRepositoryRs);

      HttpServerOptions options = new HttpServerOptions();
      options.setCompressionSupported(true);
      vertx.createHttpServer(options)
               .requestHandler(router::accept)
               .listen(PORT);
   }

   @Override
   public void stop() throws Exception
   {
   }

   public static JsonObject mysqlConfig()
   {
      JsonObject mysqlConfig = new JsonObject()
               .put("url", MYSQL_URL)
               .put("driver_class", MYSQL_DRIVERCLASS)
               .put("user", MYSQL_USER)
               .put("password", MYSQL_PWD)
               .put("max_pool_size", MYSQL_MAXPOOLSIZE);

      return mysqlConfig;
   }

}
