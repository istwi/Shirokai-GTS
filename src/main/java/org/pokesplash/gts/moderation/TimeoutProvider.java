package org.pokesplash.gts.moderation;

import com.google.gson.Gson;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import org.pokesplash.gts.util.Utils;

public class TimeoutProvider {
   private final String FILEPATH = "/config/gts";
   private final String FILENAME = "timeout.json";
   private HashMap<String, Long> timeouts = new HashMap<>();

   public boolean hasTimeoutExpired(UUID player) {
      if (this.containsPlayer(player)) {
         long endTime = this.timeouts.get(player.toString());
         if (endTime < new Date().getTime()) {
            this.removeTimeout(player);
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public Long getTimeout(UUID player) {
      return this.timeouts.get(player.toString());
   }

   private boolean containsPlayer(UUID player) {
      return this.timeouts.containsKey(player.toString());
   }

   public void addTimeout(UUID player, long endTime) {
      if (this.containsPlayer(player)) {
         this.removeTimeout(player);
      }

      this.timeouts.put(player.toString(), endTime);
      this.write();
   }

   public void removeTimeout(UUID player) {
      this.timeouts.remove(player.toString());
      this.write();
   }

   private void write() {
      Utils.writeFileAsync("/config/gts", "timeout.json", Utils.newGson().toJson(this.timeouts));
   }

   public void read() {
      Utils.readFileAsync("/config/gts", "timeout.json", e -> {
         Gson gson = Utils.newGson();
         HashMap<String, Long> data = (HashMap<String, Long>)gson.fromJson(e, HashMap.class);
         this.timeouts = data;
      });
   }
}
