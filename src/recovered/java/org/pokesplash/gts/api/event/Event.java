package org.pokesplash.gts.api.event;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Event<T> {
   private ArrayList<Subscription<T>> subscriptions = new ArrayList<>();

   public Subscription<T> subscribe(Consumer<T> callback) {
      Subscription<T> sub = new Subscription<>(this, callback);
      this.subscriptions.add(sub);
      return sub;
   }

   public void unsubscribe(Subscription<T> subscription) {
      this.subscriptions.remove(subscription);
   }

   public void trigger(T t) {
      for (Subscription<T> sub : this.subscriptions) {
         sub.run(t);
      }
   }
}
