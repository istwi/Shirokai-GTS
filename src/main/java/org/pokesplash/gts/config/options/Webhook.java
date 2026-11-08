package org.pokesplash.gts.config.options;

public class Webhook {
   private boolean useWebhooks = false;
   private String url = "WEBHOOK_URL";

   public boolean isUseWebhooks() {
      return this.useWebhooks;
   }

   public String getUrl() {
      return this.url;
   }
}
