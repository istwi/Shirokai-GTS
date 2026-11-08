package org.pokesplash.gts.api.event;

import java.util.function.Consumer;

public class Subscription<T> {
   private Event<T> event;
   private Consumer<T> callback;

   public Subscription(Event<T> event, Consumer<T> callback) {
      this.event = event;
      this.callback = callback;
   }

   public void run(T t) {
      this.callback.accept(t);
   }
}
