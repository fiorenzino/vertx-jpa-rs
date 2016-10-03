package org.giavacms.vertxjpars.model;

import io.vertx.core.json.JsonObject;
import org.giavacms.vertxjpars.common.Id;
import org.giavacms.vertxjpars.common.Table;

import java.time.Instant;

/**
 * Created by fiorenzo on 02/10/16.
 */
@Table(name = "RequestEnricher")
public class RequestEnricher
{
   @Id
   public Long id;
   public String deliveryTypeId;
   public String documentId;
   public String orderReferenceId;
   public Instant periodEnd;
   public Instant periodStart;
   public String recipientVat;
   public String senderVat;
   public String workingOrderId;
   public String pod;
   public boolean podValid;

   public RequestEnricher()
   {
   }

   public RequestEnricher(JsonObject json)
   {
      this.id = json.getLong("id");
      this.deliveryTypeId = json.getString("deliveryTypeId");
      this.documentId = json.getString("documentId");
      this.orderReferenceId = json.getString("orderReferenceId");
      this.periodEnd = json.getInstant("periodEnd");
      this.periodStart = json.getInstant("periodStart");
      this.recipientVat = json.getString("recipientVat");
      this.senderVat = json.getString("senderVat");
      this.workingOrderId = json.getString("workingOrderId");
      this.pod = json.getString("pod");
      this.podValid = json.getBoolean("podValid");

   }

   public JsonObject toJson()
   {
      JsonObject jsonObject = new JsonObject()
               .put("id", this.id)
               .put("deliveryTypeId", this.deliveryTypeId)
               .put("documentId", this.documentId)
               .put("orderReferenceId", this.orderReferenceId)
               .put("periodEnd", this.periodEnd)
               .put("periodStart", this.periodStart)
               .put("recipientVat", this.periodStart)
               .put("senderVat", this.periodStart)
               .put("workingOrderId", this.periodStart)
               .put("pod", this.periodStart)
               .put("podValid", this.periodStart);
      return jsonObject;
   }

   @Override public String toString()
   {
      return "RequestEnricher{" +
               "id=" + id +
               ", deliveryTypeId='" + deliveryTypeId + '\'' +
               ", documentId='" + documentId + '\'' +
               ", orderReferenceId='" + orderReferenceId + '\'' +
               ", periodEnd=" + periodEnd +
               ", periodStart=" + periodStart +
               ", recipientVat='" + recipientVat + '\'' +
               ", senderVat='" + senderVat + '\'' +
               ", workingOrderId='" + workingOrderId + '\'' +
               ", pod='" + pod + '\'' +
               ", podValid=" + podValid +
               '}';
   }
}
