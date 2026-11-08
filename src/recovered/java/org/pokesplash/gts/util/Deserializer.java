package org.pokesplash.gts.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class Deserializer implements JsonDeserializer {
   private Class<?> implClass;

   public Deserializer(Class<?> c) {
      this.implClass = c;
   }

   public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return context.deserialize(json, this.implClass);
   }
}
