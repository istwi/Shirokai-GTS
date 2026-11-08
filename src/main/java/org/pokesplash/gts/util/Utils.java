package org.pokesplash.gts.util;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.MinecraftServer;
import org.pokesplash.gts.Gts;

public abstract class Utils {
   public static CompletableFuture<Boolean> writeFileAsync(String filePath, String filename, String data) {
      final CompletableFuture<Boolean> future = new CompletableFuture<>();
      Path path = Paths.get(new File("").getAbsolutePath() + filePath, filename);
      final File file = path.toFile();
      if (!Files.exists(path.getParent())) {
         file.getParentFile().mkdirs();
      }

      try (final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
            path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
         )) {
         ByteBuffer buffer = ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8));
         fileChannel.write(buffer, 0L, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            public void completed(Integer result, ByteBuffer attachment) {
               attachment.clear();

               try {
                  fileChannel.close();
               } catch (IOException var4) {
                  throw new RuntimeException(var4);
               }

               future.complete(true);
            }

            public void failed(Throwable exc, ByteBuffer attachment) {
               future.complete(Utils.writeFileSync(file, data));
            }
         });
      } catch (SecurityException | IOException var11) {
         Gts.LOGGER.fatal("Unable to write file asynchronously, attempting sync write.");
         future.complete(future.complete(false));
      }

      return future;
   }

   public static boolean writeFileSync(File file, String data) {
      try {
         FileWriter writer = new FileWriter(file);
         writer.write(data);
         writer.close();
         return true;
      } catch (Exception var3) {
         Gts.LOGGER.fatal("Unable to write to file for gts.\nStack Trace: ");
         var3.printStackTrace();
         return false;
      }
   }

   public static boolean deleteFile(String filePath, String filename) {
      try {
         Path path = Paths.get(new File("").getAbsolutePath() + filePath, filename);
         File file = path.toFile();
         if (!file.exists()) {
            return true;
         } else {
            file.delete();
            return true;
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         return false;
      }
   }

   public static CompletableFuture<Boolean> readFileAsync(String filePath, String filename, Consumer<String> callback) {
      CompletableFuture<Boolean> future = new CompletableFuture<>();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      Path path = Paths.get(new File("").getAbsolutePath() + filePath, filename);
      File file = path.toFile();
      if (!file.exists()) {
         future.complete(false);
         executor.shutdown();
         return future;
      } else {
         try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate((int)fileChannel.size());
            Future<Integer> readResult = fileChannel.read(buffer, 0L);
            readResult.get();
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            String fileContent = new String(bytes, StandardCharsets.UTF_8);
            callback.accept(fileContent);
            fileChannel.close();
            executor.shutdown();
            future.complete(true);
         } catch (Exception var14) {
            future.complete(readFileSync(file, callback));
            executor.shutdown();
         }

         return future;
      }
   }

   public static boolean readFileSync(File file, Consumer<String> callback) {
      try {
         Scanner reader = new Scanner(file);
         String data = "";

         while (reader.hasNextLine()) {
            data = data + reader.nextLine();
         }

         reader.close();
         callback.accept(data);
         return true;
      } catch (Exception var4) {
         Gts.LOGGER.fatal("Unable to read file " + file.getName() + " for gts.\nStack Trace: ");
         var4.printStackTrace();
         return false;
      }
   }

   public static File checkForDirectory(String path) {
      File dir = new File(new File("").getAbsolutePath() + path);
      if (!dir.exists()) {
         dir.mkdirs();
      }

      return dir;
   }

   public static Gson newGson() {
      return new GsonBuilder().setPrettyPrinting().create();
   }

   public static String formatMessage(String message, Boolean isPlayer) {
      return isPlayer ? message.trim() : message.replaceAll("§[0-9a-fk-or]", "").trim();
   }

   public static boolean isStringInt(String string) {
      try {
         Integer.parseInt(string);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static String parseLongDate(long time) {
      long second = 1000L;
      long minute = second * 60L;
      long hour = minute * 60L;
      long day = hour * 24L;
      long timeLeft = time;
      String output = "";
      if (time > day) {
         output = output + (time - time % day) / day + "d ";
         timeLeft = time % day;
      }

      if (timeLeft > hour) {
         output = output + (timeLeft - timeLeft % hour) / hour + "h ";
         timeLeft %= hour;
      }

      if (timeLeft > minute) {
         output = output + (timeLeft - timeLeft % minute) / minute + "m ";
         timeLeft %= minute;
      }

      if (timeLeft > second) {
         output = output + (timeLeft - timeLeft % second) / second + "s ";
         timeLeft %= second;
      }

      return output;
   }

   public static String capitaliseFirst(String message) {
      if (message.trim().isEmpty()) {
         return message;
      } else {
         String output = message.trim().replaceAll("\\[|\\]", "");
         String[] messages = output.split("_| ");
         if (messages.length <= 1) {
            return output.trim().substring(0, 1).toUpperCase() + output.substring(1).toLowerCase(Locale.ROOT);
         } else {
            output = "";

            for (String msg : messages) {
               output = output + capitaliseFirst(msg) + " ";
            }

            return output;
         }
      }
   }

   public static boolean isHA(Pokemon pokemon) {
      List<PotentialAbility> abilities = (List<PotentialAbility>)pokemon.getForm().getAbilities().getMapping().get(Priority.LOW);
      if (abilities != null && abilities.size() == 1) {
         String ability = ((PotentialAbility)((List)pokemon.getForm().getAbilities().getMapping().get(Priority.LOW)).get(0)).getTemplate().getName();
         return pokemon.getAbility().getName().equalsIgnoreCase(ability);
      } else {
         return false;
      }
   }

   public static String formatPlaceholders(String message, double minPrice, String listing, String seller, String buyer) {
      String newMessage = message;
      if (message == null) {
         return "";
      } else {
         if (listing != null) {
            newMessage = message.replaceAll("\\{listing\\}", listing);
         }

         if (seller != null) {
            newMessage = newMessage.replaceAll("\\{seller\\}", seller);
         }

         if (buyer != null) {
            newMessage = newMessage.replaceAll("\\{buyer\\}", buyer);
         }

         return newMessage.replaceAll("\\{min_price\\}", minPrice + "")
            .replaceAll("\\{max_listings\\}", Gts.config.getMaxListingsPerPlayer() + "")
            .replaceAll("\\{max_price\\}", Gts.config.getMaximumPrice() + "");
      }
   }

   public static ItemStack parseItemId(String id) {
      NbtCompound tag;
      try {
         tag = StringNbtReader.parse(id);
      } catch (Exception var3) {
         tag = new NbtCompound();
         tag.putString("id", id);
         tag.putInt("Count", 1);
      }

      return (ItemStack)ItemStack.fromNbt(WrapperLookup.of(Stream.empty()), tag).get();
   }

   public static void broadcastClickable(String message, String command) {
      Text component = Text.literal(message)
         .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(Action.RUN_COMMAND, command)));
      MinecraftServer server = Gts.server;

      for (ServerPlayerEntity pl : new ArrayList<>(server.getPlayerManager().getPlayerList())) {
         pl.sendMessage(component);
      }
   }

   public static boolean hasSpace(ServerPlayerEntity player, ItemStack stack) {
      for (ItemStack item : player.getInventory().main) {
         if (!item.isEmpty()
            && ItemStack.areItemsAndComponentsEqual(item, stack)
            && item.isStackable()
            && item.getCount() + stack.getCount() <= item.getMaxCount()) {
            return true;
         }
      }

      return player.getInventory().getEmptySlot() != -1;
   }
}
