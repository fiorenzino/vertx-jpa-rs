package org.giavacms.vertxjpars.service.rs;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import org.giavacms.vertxjpars.common.AbstractRepositoryRs;
import org.giavacms.vertxjpars.management.AppConstants;
import org.giavacms.vertxjpars.model.Document;
import org.giavacms.vertxjpars.repository.DocumentRepository;

/**
 * Created by fiorenzo on 02/10/16.
 */
public class DocumentRepositoryRs extends AbstractRepositoryRs<Document>
{

   public DocumentRepositoryRs(Router router, JDBCClient jdbcClient, Vertx vertx)
   {
      super(router, new DocumentRepository(jdbcClient), vertx, AppConstants.DOCUMENTS_PATH);
   }

   public DocumentRepositoryRs()
   {
   }

   @Override public Document fromBodyAsJson(JsonObject jsonObject)
   {
      return new Document(jsonObject);
   }

   @Override public Document decode(String jsonString)
   {
      return Json.decodeValue(jsonString, Document.class);
   }
}
