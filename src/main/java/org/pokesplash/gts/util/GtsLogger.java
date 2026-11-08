package org.pokesplash.gts.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GtsLogger {
   private Logger logger = LogManager.getLogger("gts");

   public void info(String message) {
      this.logger.info(message);
   }

   public void error(String message) {
      this.logger.error(message);
   }

   public void fatal(String message) {
      this.logger.fatal(message);
   }

   private static enum Level {
      INFO,
      ERROR,
      FATAL;
   }
}
