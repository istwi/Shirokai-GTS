package org.pokesplash.gts.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2561.class_2562;
import org.pokesplash.gts.Gts;

public final class ColorUtil {
   private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]){6}");
   private static final Pattern LEGACY_PATTERN = Pattern.compile("[&§]([0-9a-fA-fk-oK-OrR])");

   public static class_2561 parse(String text) {
      try {
         Class.forName("net.kyori.adventure.text.Component");
         class_2561 component = toText(parseColour(text));
         return class_2561.method_43473().method_10862(class_2583.field_24360.method_10978(false)).method_10852(component);
      } catch (ClassNotFoundException var2) {
         return class_2561.method_43470(text);
      }
   }

   public static Component parseColour(String input) {
      return MiniMessage.miniMessage().deserialize(replaceCodes(input));
   }

   public static class_2561 toText(Component component) {
      return fromJson(toJson(component));
   }

   public static String toJson(class_2561 text) {
      return class_2562.method_10867(text, Gts.server.method_30611());
   }

   public static class_2561 fromJson(String json) {
      return class_2562.method_10877(json, Gts.server.method_30611());
   }

   public static String toJson(Component component) {
      return (String)GsonComponentSerializer.gson().serialize(component);
   }

   public static class_2561 parseColourToText(String input) {
      return toText(parseColour(input));
   }

   private static String replaceCodes(String input) {
      for (Matcher matcher = HEX_PATTERN.matcher(input); matcher.find(); matcher = HEX_PATTERN.matcher(input)) {
         input = input.replace(matcher.group(), "<reset><c:" + matcher.group().substring(1) + ">");
      }

      return replaceLegacyCodes(input);
   }

   private static String replaceLegacyCodes(String input) {
      for (Matcher matcher = LEGACY_PATTERN.matcher(input); matcher.find(); matcher = LEGACY_PATTERN.matcher(input)) {
         input = input.replace(matcher.group(), getLegacyReplacement(matcher.group().substring(1)));
      }

      return input;
   }

   private static String getLegacyReplacement(String input) {
      String var1 = input.toUpperCase(Locale.ENGLISH);

      return switch (var1) {
         case "0" -> "<reset><c:#000000>";
         case "1" -> "<reset><c:#0000AA>";
         case "2" -> "<reset><c:#00AA00>";
         case "3" -> "<reset><c:#00AAAA>";
         case "4" -> "<reset><c:#AA0000>";
         case "5" -> "<reset><c:#AA00AA>";
         case "6" -> "<reset><c:#FFAA00>";
         case "7" -> "<reset><c:#AAAAAA>";
         case "8" -> "<reset><c:#555555>";
         case "9" -> "<reset><c:#5555FF>";
         case "A" -> "<reset><c:#55FF55>";
         case "B" -> "<reset><c:#55FFFF>";
         case "C" -> "<reset><c:#FF5555>";
         case "D" -> "<reset><c:#FF55FF>";
         case "E" -> "<reset><c:#FFFF55>";
         case "F" -> "<reset><c:#FFFFFF>";
         case "K" -> "<obf>";
         case "L" -> "<b>";
         case "M" -> "<st>";
         case "N" -> "<u>";
         case "O" -> "<i>";
         case "R" -> "<reset>";
         default -> input;
      };
   }
}
