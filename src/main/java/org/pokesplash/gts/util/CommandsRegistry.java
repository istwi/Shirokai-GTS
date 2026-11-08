package org.pokesplash.gts.util;

import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import org.pokesplash.gts.command.superclass.BaseCommand;

public abstract class CommandsRegistry {
   private static ArrayList<BaseCommand> commands = new ArrayList<>();

   public static void addCommand(BaseCommand command) {
      commands.add(command);
   }

   public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
      for (BaseCommand command : commands) {
         command.register(dispatcher);
      }
   }

   public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext, RegistrationEnvironment commandSelection) {
      registerCommands(dispatcher);
   }
}
