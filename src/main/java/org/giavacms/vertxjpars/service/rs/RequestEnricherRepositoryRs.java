package org.giavacms.vertxjpars.service.rs;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import org.giavacms.vertxjpars.common.AbstractRepositoryRs;
import org.giavacms.vertxjpars.management.AppConstants;
import org.giavacms.vertxjpars.model.Document;
import org.giavacms.vertxjpars.model.RequestEnricher;
import org.giavacms.vertxjpars.repository.DocumentRepository;
import org.giavacms.vertxjpars.repository.RequestEnricherRepository;

/**
 * Created by fiorenzo on 02/10/16.
 */
public class RequestEnricherRepositoryRs extends AbstractRepositoryRs<RequestEnricher>
{

   public RequestEnricherRepositoryRs(Router router, JDBCClient jdbcClient, Vertx vertx)
   {
      super(router, new RequestEnricherRepository(jdbcClient), vertx, AppConstants.REQUESTS_PATH);
   }

   public RequestEnricherRepositoryRs()
   {
   }

   @Override public RequestEnricher fromBodyAsJson(JsonObject jsonObject)
   {
      return new RequestEnricher(jsonObject);
   }

   @Override public RequestEnricher decode(String jsonString)
   {
      return Json.decodeValue(jsonString, RequestEnricher.class);
   }
}
