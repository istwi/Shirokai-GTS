package org.pokesplash.gts.util;

import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import net.minecraft.class_2168;
import net.minecraft.class_7157;
import net.minecraft.class_2170.class_5364;
import org.pokesplash.gts.command.superclass.BaseCommand;

public abstract class CommandsRegistry {
   private static ArrayList<BaseCommand> commands = new ArrayList<>();

   public static void addCommand(BaseCommand command) {
      commands.add(command);
   }

   public static void registerCommands(CommandDispatcher<class_2168> dispatcher) {
      for (BaseCommand command : commands) {
         command.register(dispatcher);
      }
   }

   public static void registerCommands(CommandDispatcher<class_2168> dispatcher, class_7157 commandBuildContext, class_5364 commandSelection) {
      registerCommands(dispatcher);
   }
}
