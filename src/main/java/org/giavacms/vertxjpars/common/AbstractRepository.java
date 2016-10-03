package org.giavacms.vertxjpars.common;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;

import java.lang.reflect.ParameterizedType;

/**
 * Created by fiorenzo on 03/06/16.
 */
public abstract class AbstractRepository<T> implements Repository<T>
{

   protected JDBCClient jdbcClient;

   protected Logger logger = LoggerFactory.getLogger(getClass());

   @Override
   public void create(T t, Handler<AsyncResult<UpdateResult>> handler)
   {
      //        String sql = "INSERT INTO Whisky (name, origin) VALUES ?, ?";
      jdbcClient.getConnection(connection ->
      {
         if (connection.failed())
         {
            logger.error("create : connection operation has failed...: " + connection.cause().getMessage());
            handler.handle(Future.failedFuture(connection.cause()));
         }
         else
         {
            System.out.println("getInsertQueryWithParams: " + getInsertQueryWithParams(t));
            //                connection.result().updateWithParams(getInsertQuery(t), getInsertJsonArray(t), handler);
            connection.result().update(getInsertQueryWithParams(t), handler);
         }
         connection.result().close();
      });
   }

   @Override
   public void update(T t, Handler<AsyncResult<UpdateResult>> handler)
   {
      jdbcClient.getConnection(connection ->
      {
         if (connection.failed())
         {
            logger.error("update : connection operation has failed...: " + connection.cause().getMessage());
            handler.handle(Future.failedFuture(connection.cause()));
         }
         else
         {
            System.out.println("getUpdateQuery: " + getUpdateQuery(t));
            connection.result().updateWithParams(getUpdateQuery(t), getUpdateJsonArray(t), handler);
         }
         connection.result().close();
      });
   }

   @Override
   public void list(Search<T> search, Handler<AsyncResult<ResultSet>> handler)
   {
      jdbcClient.getConnection(connection ->
      {
         if (connection.failed())
         {
            logger.error("list: connection operation has failed...: " + connection.cause().getMessage());
            handler.handle(Future.failedFuture(connection.cause()));
         }
         else
         {
            try
            {
               getRestrictions(search, false);
            }
            catch (Exception e)
            {

            }
            connection.result().queryWithParams(search.query, search.jsonArray, handler);
         }
         connection.result().close();
      });
   }

   @Override
   public void listSize(Search<T> search, Handler<AsyncResult<ResultSet>> handler)
   {
      jdbcClient.getConnection(connection ->
      {
         if (connection.failed())
         {
            logger.error("list size: connection operation has failed...: " + connection.cause().getMessage());
            handler.handle(Future.failedFuture(connection.cause()));
         }
         else
         {
            try
            {
               getRestrictions(search, true);
            }
            catch (Exception e)
            {

            }
            connection.result().queryWithParams(search.query, search.jsonArray, handler);
         }
         connection.result().close();
      });
   }

   @Override
   public void fetch(Object id, Handler<AsyncResult<ResultSet>> handler)
   {
      jdbcClient.getConnection(connection ->
      {
         if (connection.failed())
         {
            logger.error("fetch : connection operation has failed...: " + connection.cause().getMessage());
            handler.handle(Future.failedFuture(connection.cause()));
         }
         else
         {
            connection.result().queryWithParams(getFetchQuery(), new JsonArray().add(id), handler);
         }
         connection.result().close();
      });
   }

   @Override
   public void delete(Object id, Handler<AsyncResult<UpdateResult>> handler)
   {
      jdbcClient.getConnection(connection ->
      {
         if (connection.failed())
         {
            logger.error("delete : connection operation has failed...: " + connection.cause().getMessage());
            handler.handle(Future.failedFuture(connection.cause()));
         }
         else
         {
            connection.result().updateWithParams(getDeleteQuery(), new JsonArray().add(id), handler);
         }
         connection.result().close();
      });

   }

   public Object castId(String key)
   {
      try
      {
         return RepositoryUtils.castId(key, getEntityType());
      }
      catch (Exception e)
      {

      }
      return null;
   }

   protected Class<T> getEntityType() throws Exception
   {
      ParameterizedType parameterizedType = (ParameterizedType) getClass()
               .getGenericSuperclass();
      return (Class<T>) parameterizedType.getActualTypeArguments()[0];
   }

   public abstract String getInsertQueryWithParams(T object);

   public abstract String getUpdateQuery(T object);

   public abstract JsonArray getUpdateJsonArray(T object);

   public abstract String getDeleteQuery();

   public abstract String getFetchQuery();

   public String getOrderBy(String alias, String orderBy) throws Exception
   {
      try
      {
         if (orderBy == null || orderBy.length() == 0)
         {
            orderBy = getDefaultOrderBy();
         }
         StringBuffer result = new StringBuffer();
         String[] orders = orderBy.split(",");
         for (String order : orders)
         {
            result.append(", ").append(alias).append(".")
                     .append(order.trim()).append(" ");
         }
         return " order by " + result.toString().substring(2);
      }
      catch (Exception e)
      {
         return "";
      }
   }

   protected void getRestrictions(Search<T> search, boolean justCount)
            throws Exception
   {
      search.jsonArray = new JsonArray();
      String alias = "c";
      StringBuffer sb = new StringBuffer(getBaseList(search.obj.getClass(), alias, justCount));
      String separator = " where ";

      applyRestrictions(search, alias, separator, sb);

      if (!justCount)
      {
         sb.append(getOrderBy(alias, search.order));
         sb.append(getLimit(search));

      }
      search.query = sb.toString();
      System.out.println("---------------");
      System.out.println(search.query);
      System.out.println(search.jsonArray);
      System.out.println("---------------");

   }

   protected void applyRestrictions(Search<T> search, String alias,
            String separator, StringBuffer sb)
            throws Exception
   {
   }

   protected String getBaseList(Class<? extends Object> clazz, String alias,
            boolean count) throws Exception
   {
      String tableName = RepositoryUtils.getTableName(clazz);
      if (tableName == null)
         tableName = clazz.getSimpleName();
      if (count)
      {
         return "select count(*) from " + tableName
                  + " " + alias + " ";
      }
      else
      {
         return "select * from " + tableName + " "
                  + alias + " ";
      }
   }

   protected abstract String getDefaultOrderBy();

   protected abstract String getLimit(Search<T> search);

   protected String likeParam(String param)
   {
      return "%" + param + "%";
   }

   protected String likeParamL(String param)
   {
      return "%" + param;
   }

   protected String likeParamR(String param)
   {
      return param + "%";
   }

}
