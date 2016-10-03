package org.giavacms.vertxjpars.common;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.ParameterizedType;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fiorenzo on 03/06/16.
 */
abstract public class AbstractRepositoryRs<T> extends AbstractVerticle
{

   protected Logger logger = LoggerFactory.getLogger(getClass());

   protected Repository<T> repository;
   protected Router router;
   protected String path;

   public AbstractRepositoryRs(Router router, Repository<T> repository, Vertx vertx, String path)
   {
      this.router = router;
      this.repository = repository;
      this.vertx = vertx;
      this.path = path;
   }

   public AbstractRepositoryRs()
   {
   }

   @Override
   public void start(Future<Void> startFuture) throws Exception
   {
      logger.info("start " + getClass().getSimpleName());
      startWebApp((start) ->
      {
         if (start.succeeded())
         {
            completeStartup(start, startFuture);
         }
         else
         {
            logger.error("error - startWebApp: " + start.cause().getMessage());
         }
      });
   }

   protected void completeStartup(AsyncResult<HttpServer> http, Future<Void> fut)
   {
      if (http.succeeded())
      {
         logger.info(getClass().getSimpleName() + " Application started");
         fut.complete();
      }
      else
      {
         fut.fail(http.cause());
      }
   }

   protected void startWebApp(Handler<AsyncResult<HttpServer>> next)
   {
      router.get(this.path).handler(this::getList);
      router.post(this.path).handler(this::create);
      router.get(this.path + "/:id").handler(this::fetch);
      router.put(this.path + "/:id").handler(this::update);
      router.delete(this.path + ":id").handler(this::delete);
      next.handle(Future.succeededFuture());
   }

   protected void create(RoutingContext routingContext)
   {
      T t = fromBodyAsJson(routingContext.getBodyAsJson());
      logger.error("RICEVUTO:" + t);

      this.repository.create(t, single ->
      {
         if (single.failed())
         {
            end404(routingContext, single.cause().getMessage());
            return;
         }
         logger.info("_id: " + single.result().getKeys());
         HttpServerResponse response = routingContext.response()
                  .setStatusCode(201)
                  .putHeader("content-type",
                           "application/json; charset=utf-8");
         allowOrigin(routingContext, response)
                  .end(Json.encodePrettily(single.result()));
      });

   }

   protected void fetch(RoutingContext routingContext)
   {
      String id = routingContext.request().getParam("id");
      if (id == null)
      {
         end404(routingContext, "no id");
         return;
      }
      this.repository.fetch(repository.castId(id), result ->
      {
         if (result.failed())
         {
            end404(routingContext, result.cause().getMessage());
            return;
         }
         if (result.result().getNumRows() > 0)
         {
            HttpServerResponse response = routingContext.response()
                     .setStatusCode(200)
                     .putHeader("content-type",
                              "application/json; charset=utf-8");
            allowOrigin(routingContext, response)
                     .end(Json.encodePrettily(result.result().getRows().get(0)));
         }
         else
         {
            HttpServerResponse response = routingContext.response()
                     .setStatusCode(401)
                     .putHeader("content-type",
                              "application/json; charset=utf-8");
            allowOrigin(routingContext, response)
                     .end();
         }

      });
   }

   protected void update(RoutingContext routingContext)
   {
      String id = routingContext.request().getParam("id");
      if (id == null)
      {
         end404(routingContext, "no id");
         return;
      }
      T t = decode(routingContext.getBodyAsString());
      this.repository.update(t,
               updated ->
               {
                  if (updated.failed())
                  {
                     end404(routingContext, updated.cause().getMessage());
                     return;
                  }
                  HttpServerResponse response = routingContext.response()
                           .putHeader("content-type",
                                    "application/json; charset=utf-8");
                  allowOrigin(routingContext, response)
                           .end(Json.encodePrettily(updated.result()));

               });
   }

   protected void delete(RoutingContext routingContext)
   {
      String id = routingContext.request().getParam("id");
      if (id == null)
      {
         end404(routingContext, "no id");
         return;
      }
      this.repository.delete(
               repository.castId(id),
               deleted ->
               {
                  if (deleted.failed())
                  {
                     end404(routingContext, deleted.cause().getMessage());
                     return;
                  }
                  HttpServerResponse response = routingContext.response()
                           .setStatusCode(204);
                  allowOrigin(routingContext, response).end();
               }
      );

   }

   protected void getList(RoutingContext routingContext)
   {
      logger.info("query:" + routingContext.request().query());
      final Search<T> search = getSearch(routingContext);
      logger.info("search: " + search);
      Future<ResultSet> size = Future.future();
      Future<ResultSet> all = Future.future();
      repository.listSize(search, size.completer());
      repository.list(search, all.completer());
      CompositeFuture.all(size, all).setHandler(ar ->
      {
         if (ar.succeeded())
         {
            logger.info(size.result().getResults());
            int listSize = 0;
            if (size.result() != null && size.result().getResults() != null && size.result().getResults().size() > 0)
            {
               listSize = size.result().getResults().get(0).getInteger(0);
            }
            List<T> ts
                     = all.result().getRows().stream().map(this::fromBodyAsJson).collect(Collectors.toList());
            HttpServerResponse response = routingContext.response()
                     .putHeader("content-type",
                              "application/json; charset=utf-8")
                     .putHeader("Access-Control-Expose-Headers", "startRow, pageSize, listSize, startRow")
                     .putHeader("startRow", "" + search.startRow)
                     .putHeader("pageSize", "" + search.pageSize)
                     .putHeader("listSize", "" + listSize)
                     .putHeader("startRow", "" + search.startRow);
            allowOrigin(routingContext, response).end(Json.encodePrettily(ts));
         }
         else
         {
            if (size.failed())
            {
               logger.info("SIZE FAILED: " + size.cause().getMessage());
            }
            if (all.failed())
            {
               logger.info("ALL FAILED: " + all.cause().getMessage());
            }
            logger.info(ar.cause().getMessage());
            end404(routingContext, ar.cause().getMessage());
            return;
         }

      });
   }

   protected void end404(RoutingContext routingContext, String msg)
   {
      HttpServerResponse response = routingContext.response()
               .setStatusCode(404).setStatusMessage("ERROR CONTEXT: " + msg);
      allowOrigin(routingContext, response)
               .end();
   }

   public abstract T fromBodyAsJson(JsonObject jsonObject);

   public abstract T decode(String jsonString);

   protected HttpServerResponse allowOrigin(RoutingContext routingContext, HttpServerResponse response)
   {
      if (routingContext.request().getHeader("Origin") != null)
      {
         response.putHeader("Access-Control-Allow-Origin",
                  routingContext.request().getHeader("Origin"));
      }
      return response;
   }

   private Class<T> getClassType()
   {
      Class clazz = getClass();
      while (!(clazz.getGenericSuperclass() instanceof ParameterizedType))
      {
         clazz = clazz.getSuperclass();
      }
      ParameterizedType parameterizedType = (ParameterizedType) clazz
               .getGenericSuperclass();
      return (Class<T>) parameterizedType.getActualTypeArguments()[0];
   }

   protected Search<T> getSearch(RoutingContext routingContext)
   {
      try
      {
         Search<T> search = new Search<T>(getClassType());
         String startRow = routingContext.request().getParam("startRow");
         if (startRow != null && !startRow.trim().isEmpty())
            search.startRow = Integer.valueOf(startRow);
         else
            search.startRow = 0;
         String pageSize = routingContext.request().getParam("pageSize");
         if (pageSize != null && !pageSize.trim().isEmpty())
            search.pageSize = Integer.valueOf(pageSize);
         else
            search.pageSize = 10;
         String orderBy = routingContext.request().getParam("orderBy");
         if (orderBy != null && !orderBy.trim().isEmpty())
            search.order = orderBy;
         if (routingContext != null && routingContext.request().query() != null
                  && !routingContext.request().query().isEmpty())
         {
            makeSearch(routingContext.request().params(), search);
         }
         return search;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         end404(routingContext, e.getMessage());
         return null;
      }
   }

   <T> void makeSearch(MultiMap queryParams, Search<T> s)
   {
      for (String key : queryParams.names())
      {
         try
         {
            T instance = s.obj;
            String value = queryParams.get(key);
            value = URLDecoder.decode(value, "UTF-8");

            String fieldName = key;
            if (key.startsWith("obj."))
            {
               instance = s.obj;
               fieldName = key.substring(4);
            }
            else if (key.startsWith("from."))
            {
               instance = s.from;
               fieldName = key.substring(5);
            }
            else if (key.startsWith("to."))
            {
               instance = s.to;
               fieldName = key.substring(3);
            }
            else if (key.startsWith("like."))
            {
               instance = s.like;
               fieldName = key.substring(5);
            }
            else if (key.startsWith("not."))
            {
               instance = s.not;
               fieldName = key.substring(4);
            }

            RepositoryUtils.setFieldByName(instance.getClass(), instance, fieldName, value);

         }
         catch (Exception e)
         {
            logger.error(e.getMessage());
         }
      }

   }
}


