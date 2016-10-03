package org.giavacms.vertxjpars.repository;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import org.giavacms.vertxjpars.common.AbstractRepository;
import org.giavacms.vertxjpars.common.Search;
import org.giavacms.vertxjpars.model.RequestEnricher;

/**
 * Created by fiorenzo on 02/10/16.
 */
public class RequestEnricherRepository extends AbstractRepository<RequestEnricher>
{

   static String INSERT = "INSERT INTO RequestEnricher ( ";
   static String VALUES = " VALUES( ";

   public RequestEnricherRepository()
   {
   }

   public RequestEnricherRepository(JDBCClient jdbcClient)
   {
      this.jdbcClient = jdbcClient;
   }

   @Override public String getInsertQueryWithParams(RequestEnricher requestEnricher)
   {
      JsonArray jsonArray = new JsonArray();
      StringBuffer sb = new StringBuffer();
      StringBuffer values = new StringBuffer();

      return new StringBuffer(INSERT).append(sb.substring(1))
               .append(" )").append(VALUES)
               .append(values.substring(1))
               .append(")").toString();
   }

   @Override public String getUpdateQuery(RequestEnricher object)
   {
      return " UPDATE RequestEnricher set uuid = ?, documentType = ?, receivedDate = ?, recipientVat = ?, senderVat = ?  where id = ?";
   }

   @Override public JsonArray getUpdateJsonArray(RequestEnricher object)
   {
      return null;
   }

   @Override public String getDeleteQuery()
   {
      return "delete from RequestEnricher where id = ?";
   }

   @Override
   public String getFetchQuery()
   {
      return "select * from RequestEnricher where id = ?";
   }

   @Override
   protected void applyRestrictions(Search<RequestEnricher> search, String alias, String separator,
            StringBuffer sb) throws Exception
   {
      if (search.obj.senderVat != null)
      {
         search.jsonArray.add(search.obj.senderVat);
         sb.append(separator).append(alias).append(".senderVat = ? ");
         separator = " and ";
      }
   }

   @Override protected String getDefaultOrderBy()
   {
      return " id desc";
   }

   @Override protected String getLimit(Search<RequestEnricher> search)
   {
      return " LIMIT " + search.startRow + "," + search.pageSize;
   }

}
