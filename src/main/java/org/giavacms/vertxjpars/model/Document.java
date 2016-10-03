package org.giavacms.vertxjpars.model;

import io.vertx.core.json.JsonObject;
import org.giavacms.vertxjpars.common.Id;
import org.giavacms.vertxjpars.common.Table;

import java.time.Instant;

/**
 * Created by fiorenzo on 02/10/16.
 */
@Table(name = "Document")
public class Document
{
   @Id
   public String uuid;
   public String documentType;
   public Instant receivedDate;
   public String recipientVat;
   public String senderVat;
   public String status;

   public Document()
   {
   }

   public Document(String uuid, String documentType, Instant receivedDate, String recipientVat, String senderVat,
            String status)
   {
      this.uuid = uuid;
      this.documentType = documentType;
      this.receivedDate = receivedDate;
      this.recipientVat = recipientVat;
      this.senderVat = senderVat;
      this.status = status;
   }

   public Document(JsonObject json)
   {
      this.uuid = json.getString("uuid");
      this.documentType = json.getString("documentType");
      this.receivedDate = json.getInstant("receivedDate");
      this.recipientVat = json.getString("recipientVat");
      this.senderVat = json.getString("senderVat");
      this.status = json.getString("status");

   }

   public JsonObject toJson()
   {
      JsonObject jsonObject = new JsonObject();
      jsonObject.put("uuid", this.uuid)
               .put("documentType", this.documentType)
               .put("receivedDate", this.receivedDate)
               .put("recipientVat", this.recipientVat)
               .put("senderVat", this.senderVat)
               .put("status", this.status);
      return jsonObject;
   }

   @Override public String toString()
   {
      return "Document{" +
               "uuid='" + uuid + '\'' +
               ", documentType='" + documentType + '\'' +
               ", receivedDate=" + receivedDate +
               ", recipientVat='" + recipientVat + '\'' +
               ", senderVat='" + senderVat + '\'' +
               ", status='" + status + '\'' +
               '}';
   }
}
