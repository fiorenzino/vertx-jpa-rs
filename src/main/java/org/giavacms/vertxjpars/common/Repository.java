package org.giavacms.vertxjpars.common;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;

/**
 * Created by fiorenzo on 03/06/16.
 */
public interface Repository<T>
{

   void create(T t, Handler<AsyncResult<UpdateResult>> handler);

   void update(T t, Handler<AsyncResult<UpdateResult>> handler);

   void list(Search<T> search, Handler<AsyncResult<ResultSet>> handler);

   void listSize(Search<T> search, Handler<AsyncResult<ResultSet>> handler);

   void fetch(Object id, Handler<AsyncResult<ResultSet>> handler);

   void delete(Object id, Handler<AsyncResult<UpdateResult>> handler);

   Object castId(String key);

}
