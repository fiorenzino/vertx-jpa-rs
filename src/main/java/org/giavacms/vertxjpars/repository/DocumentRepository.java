package org.giavacms.vertxjpars.repository;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import org.giavacms.vertxjpars.common.AbstractRepository;
import org.giavacms.vertxjpars.common.Search;
import org.giavacms.vertxjpars.model.Document;

/**
 * Created by fiorenzo on 02/10/16.
 */
public class DocumentRepository extends AbstractRepository<Document>
{

   static String INSERT = "INSERT INTO Document ( ";
   static String VALUES = " VALUES( ";

   public DocumentRepository()
   {
   }

   public DocumentRepository(JDBCClient jdbcClient)
   {
      this.jdbcClient = jdbcClient;
   }

   @Override public String getInsertQueryWithParams(Document document)
   {
      JsonArray jsonArray = new JsonArray();
      StringBuffer sb = new StringBuffer();
      StringBuffer values = new StringBuffer();

      if (document.uuid != null)
      {
         jsonArray.clear();
         jsonArray.add(document.uuid);
         sb.append(", uuid");
         values.append(", '" + jsonArray.getString(0) + "'");
      }

      if (document.documentType != null)
      {
         jsonArray.clear();
         jsonArray.add(document.documentType);
         sb.append(", documentType");
         values.append(", '" + jsonArray.getString(0) + "'");
      }
      if (document.receivedDate != null)
      {
         jsonArray.clear();
         jsonArray.add(document.receivedDate);
         sb.append(", receivedDate");
         values.append(", '" + jsonArray.getInstant(0) + "'");
      }
      if (document.recipientVat != null)
      {
         jsonArray.clear();
         jsonArray.add(document.recipientVat);
         sb.append(", recipientVat");
         values.append(", '" + jsonArray.getString(0) + "'");
      }
      if (document.senderVat != null)
      {
         jsonArray.clear();
         jsonArray.add(document.senderVat);
         sb.append(", senderVat");
         values.append(", '" + jsonArray.getString(0) + "'");
      }
      if (document.status != null)
      {
         jsonArray.clear();
         jsonArray.add(document.status);
         sb.append(", status");
         values.append(", '" + jsonArray.getString(0) + "'");
      }

      return new StringBuffer(INSERT).append(sb.substring(1))
               .append(" )").append(VALUES)
               .append(values.substring(1))
               .append(")").toString();
   }

   @Override public String getUpdateQuery(Document object)
   {
      return " UPDATE document set uuid = ?, documentType = ?, receivedDate = ?, recipientVat = ?, senderVat = ?  where id = ?";
   }

   @Override public JsonArray getUpdateJsonArray(Document object)
   {
      return null;
   }

   @Override public String getDeleteQuery()
   {
      return "delete from Document where uuid = ?";
   }

   @Override
   public String getFetchQuery()
   {
      return "select * from Document where uuid = ?";
   }

   @Override protected String getDefaultOrderBy()
   {
      return " receivedDate desc";
   }

   @Override protected String getLimit(Search<Document> search)
   {
      return " LIMIT " + search.startRow + "," + search.pageSize;
   }

   @Override
   protected void applyRestrictions(Search<Document> search, String alias, String separator,
            StringBuffer sb) throws Exception
   {
      if (search.obj.senderVat != null)
      {
         search.jsonArray.add(search.obj.senderVat);
         sb.append(separator).append(alias).append(".senderVat = ? ");
         separator = " and ";
      }
      if (search.obj.status != null)
      {
         search.jsonArray.add(search.obj.status);
         sb.append(separator).append(alias).append(".status = ? ");
         separator = " and ";
      }
      if (search.obj.documentType != null)
      {
         sb.append(separator).append(alias).append(".documentType = ? ");
         search.jsonArray.add(search.obj.documentType);
         separator = " and ";
      }
   }
}
