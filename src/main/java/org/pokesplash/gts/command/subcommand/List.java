package org.pokesplash.gts.command.subcommand;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ItemListing;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.UI.CurrencySelection;
import org.pokesplash.gts.api.GtsAPI;
import org.pokesplash.gts.command.superclass.Subcommand;
import org.pokesplash.gts.config.options.PokemonAspects;
import org.pokesplash.gts.economy.GtsTransactions;
import org.pokesplash.gts.economy.ListingPrices;
import org.pokesplash.gts.economy.MinimumPricePolicy;
import org.pokesplash.gts.util.CodecUtils;
import org.pokesplash.gts.util.Utils;

public class List extends Subcommand {
   public List() {
      super("§9Usage:\n§3- gts sell <pokemon/item>");
   }

   public LiteralCommandNode<ServerCommandSource> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("sell")
                     .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), "sell") : true))
                  .executes(this::showUsage))
               .then(
                  ((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("pokemon")
                           .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.config.isEnablePokemonSales() : false))
                        .executes(this::showPokemonUsage))
                     .then(((RequiredArgumentBuilder)CommandManager.argument("slot", IntegerArgumentType.integer()).suggests((ctx, builder) -> {
                        for (int x = 0; x < 6; x++) {
                           builder.suggest(x + 1);
                        }

                        return builder.buildFuture();
                     }).executes(this::showPokemonUsage)).then(CommandManager.argument("price", DoubleArgumentType.doubleArg()).suggests((ctx, builder) -> {
                        for (double price : Gts.config.getAllPokemonPrices()) {
                           if (price > 0.0) {
                              builder.suggest((int)price);
                           }
                        }

                        return builder.buildFuture();
                     }).executes(this::run)))
               ))
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("item")
                        .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.config.isEnableItemSales() : false))
                     .executes(this::showItemUsage))
                  .then(
                     ((RequiredArgumentBuilder)CommandManager.argument("price", DoubleArgumentType.doubleArg()).suggests((ctx, builder) -> {
                           for (int i = 1; i <= 11; i++) {
                              builder.suggest(i * 100);
                           }

                           return builder.buildFuture();
                        }).executes(this::showItemUsage))
                        .then(((RequiredArgumentBuilder)CommandManager.argument("amount", IntegerArgumentType.integer()).suggests((ctx, builder) -> {
                           for (int i = 0; i <= 64; i++) {
                              builder.suggest(i + 1);
                           }

                           return builder.buildFuture();
                        }).executes(this::run)).then(CommandManager.argument("stackSize", IntegerArgumentType.integer()).suggests((ctx, builder) -> {
                           for (int i = 1; i <= 32; i++) {
                              builder.suggest(i);
                           }

                           return builder.buildFuture();
                        }).executes(this::run)))
                  )
            ))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      if (!((ServerCommandSource)context.getSource()).isExecutedByPlayer()) {
         return this.showUsage(context);
      } else {
         ServerPlayerEntity player = ((ServerCommandSource)context.getSource()).getPlayer();
         if (!Gts.permissions.hasPermission(player, "sell")) {
            return 1;
         } else {
            double price = DoubleArgumentType.getDouble(context, "price");

            try {
               MinimumPricePolicy.beforeSelection(price, Gts.config.getMultiCurrency().allowZeroPrices, Gts.config.getMaximumPrice());
            } catch (IllegalArgumentException var12) {
               player.sendMessage(Text.literal(Gts.language.getMultiCurrency().invalidPrice));
               return 1;
            }

            boolean isPokemon = context.getInput().contains("pokemon");
            UUID pokemonId = null;
            ItemStack preview = player.getMainHandStack().copy();
            if (isPokemon) {
               int slot = IntegerArgumentType.getInteger(context, "slot") - 1;
               if (slot < 0 || slot >= 6) {
                  return this.showPokemonUsage(context);
               }

               Pokemon pokemon = Cobblemon.INSTANCE.getStorage().getParty(player).get(slot);
               if (pokemon == null) {
                  return this.showPokemonUsage(context);
               }

               pokemonId = pokemon.getUuid();
               preview = new PokemonListing(player.getUuid(), player.getName().getString(), price, pokemon).getIcon();
            } else if (preview.isEmpty()) {
               player.sendMessage(Text.literal(Gts.language.getNoItemInHand()));
               return 1;
            }

            if (!isPokemon) {
               int count = IntegerArgumentType.getInteger(context, "amount");

               int stack;
               try {
                  stack = IntegerArgumentType.getInteger(context, "stackSize");
               } catch (IllegalArgumentException var11) {
                  stack = count;
               }

               if (count > 0 && stack > 0 && stack <= count) {
                  preview.setCount(stack);
               }
            }

            UUID expectedPokemon = pokemonId;
            ItemStack expectedItem = player.getMainHandStack().copy();
            Function<String, MinimumPricePolicy.Quote> validate = currency -> {
               if (player.isDisconnected()) {
                  return null;
               } else {
                  String assetName = expectedItem.getName().getString();

                  try {
                     Gts.config.getMultiCurrency().requireSelectable(currency);
                     if (!Gts.permissions.hasPermission(player, "sell") || !GtsTransactions.permitted(player, currency)) {
                        player.sendMessage(Text.literal(Gts.language.getMultiCurrency().currencyUnavailable));
                        return null;
                     } else if (isPokemon) {
                        Pokemon current = Cobblemon.INSTANCE.getStorage().getParty(player).get(IntegerArgumentType.getInteger(context, "slot") - 1);
                        if (current != null && current.getUuid().equals(expectedPokemon)) {
                           assetName = current.getDisplayName(Gts.showPokemonDisplayName).getString();
                           return ListingPrices.pokemon(current, currency, price);
                        } else {
                           player.sendMessage(Text.literal(Gts.language.getMultiCurrency().changed));
                           return null;
                        }
                     } else if (!ItemStack.areItemsAndComponentsEqual(expectedItem, player.getMainHandStack())) {
                        player.sendMessage(Text.literal(Gts.language.getMultiCurrency().changed));
                        return null;
                     } else {
                        return ListingPrices.item(player.getMainHandStack(), currency, price);
                     }
                  } catch (MinimumPricePolicy.Rejected var10x) {
                     player.sendMessage(Text.literal(ListingPrices.rejection(var10x, currency, assetName, player.getName().getString())));
                     return null;
                  } catch (IllegalArgumentException var11x) {
                     player.sendMessage(Text.literal(Gts.language.getMultiCurrency().currencyUnavailable));
                     return null;
                  }
               }
            };
            CurrencySelection.open(player, preview, price, validate, currency -> {
               if (validate.apply(currency) != null) {
                  this.runSelected(context, currency);
               }
            });
            return 1;
         }
      }
   }

   private int runSelected(CommandContext<ServerCommandSource> context, String currency) {
      ServerPlayerEntity selectedPlayer = ((ServerCommandSource)context.getSource()).getPlayer();
      if (selectedPlayer != null
         && !selectedPlayer.isDisconnected()
         && Gts.permissions.hasPermission(selectedPlayer, "sell")
         && GtsTransactions.permitted(selectedPlayer, currency)) {
         try {
            Gts.config.getMultiCurrency().requireSelectable(currency);
         } catch (IllegalArgumentException var9) {
            return 1;
         }

         if (!((ServerCommandSource)context.getSource()).isExecutedByPlayer()) {
            ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("This command must be ran by a player."));
            return 1;
         } else if (!Gts.timeouts.hasTimeoutExpired(((ServerCommandSource)context.getSource()).getPlayer().getUuid())) {
            long endTime = Gts.timeouts.getTimeout(((ServerCommandSource)context.getSource()).getPlayer().getUuid());
            ((ServerCommandSource)context.getSource())
               .sendMessage(Text.literal("§cYou have been timed out for " + Utils.parseLongDate(endTime - new Date().getTime())));
            return 1;
         } else {
            try {
               PokemonBattle battle = BattleRegistry.getBattleByParticipatingPlayer(((ServerCommandSource)context.getSource()).getPlayer());
               if (battle != null) {
                  ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cYou can not list to GTS while in a battle."));
                  return 1;
               } else {
                  int totalListings = Gts.listings.getListingsByPlayer(((ServerCommandSource)context.getSource()).getPlayer().getUuid()).size();
                  java.util.List<Listing> expiredListings = Gts.listings
                     .getExpiredListingsOfPlayer(((ServerCommandSource)context.getSource()).getPlayer().getUuid());
                  int totalExpiredListings = expiredListings == null ? 0 : expiredListings.size();
                  if (totalListings + totalExpiredListings >= Gts.config.getMaxListingsPerPlayer()) {
                     ((ServerCommandSource)context.getSource())
                        .sendMessage(
                           Text.literal(
                              Utils.formatPlaceholders(
                                 Gts.language.getMaximumListings(), 0.0, null, ((ServerCommandSource)context.getSource()).getPlayer().getDisplayName().getString(), null
                              )
                           )
                        );
                     return 1;
                  } else {
                     return context.getInput().contains("pokemon") ? this.runPokemon(context, currency) : this.runItem(context, currency);
                  }
               }
            } catch (Exception var8) {
               ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cSomething went wrong."));
               var8.printStackTrace();
               return 1;
            }
         }
      } else {
         return 1;
      }
   }

   public int runPokemon(CommandContext<ServerCommandSource> context) {
      return this.runPokemon(context, Gts.config.getMultiCurrency().defaultCurrency);
   }

   private int runPokemon(CommandContext<ServerCommandSource> context, String currency) {
      if (!Gts.config.isEnablePokemonSales()) {
         return 1;
      } else {
         ServerPlayerEntity player = ((ServerCommandSource)context.getSource()).getPlayer();
         int slot = IntegerArgumentType.getInteger(context, "slot") - 1;
         double price = DoubleArgumentType.getDouble(context, "price");
         PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
         Pokemon pokemon = party.get(slot);
         if (pokemon == null) {
            ((ServerCommandSource)context.getSource())
               .sendMessage(
                  Text.literal(Utils.formatPlaceholders(Gts.language.getNoPokemonInSlot(), 0.0, null, player.getDisplayName().getString(), null))
               );
            return 1;
         } else if (!pokemon.getTradeable()) {
            ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cThis Pokemon is not tradeable."));
            return 1;
         } else if (party.occupied() < 2) {
            ((ServerCommandSource)context.getSource())
               .sendMessage(
                  Text.literal(Utils.formatPlaceholders(Gts.language.getOnlyOnePokemonInParty(), 0.0, null, player.getDisplayName().getString(), null))
               );
            return 1;
         } else {
            double minPrice;
            try {
               minPrice = ListingPrices.pokemon(pokemon, currency, price).minimum();
            } catch (MinimumPricePolicy.Rejected var14) {
               player.sendMessage(
                  Text.literal(
                     ListingPrices.rejection(var14, currency, pokemon.getDisplayName(Gts.showPokemonDisplayName).getString(), player.getName().getString())
                  )
               );
               return 1;
            }

            if (price > Gts.config.getMaximumPrice()) {
               ((ServerCommandSource)context.getSource())
                  .sendMessage(
                     Text.literal(
                        Utils.formatPlaceholders(
                           Gts.language.getMaximumListingPrice(),
                           minPrice,
                           pokemon.getDisplayName(Gts.showPokemonDisplayName).getString(),
                           player.getDisplayName().getString(),
                           null
                        )
                     )
                  );
               return 1;
            } else {
               for (PokemonAspects bannedMon : Gts.config.getBannedPokemon()) {
                  if (bannedMon.equals(pokemon)) {
                     ((ServerCommandSource)context.getSource())
                        .sendMessage(
                           Text.literal(
                              Utils.formatPlaceholders(
                                 Gts.language.getBannedPokemon(), 0.0, pokemon.getSpecies().getName(), player.getDisplayName().getString(), null
                              )
                           )
                        );
                     return 1;
                  }
               }

               PokemonListing listing = new PokemonListing(player.getUuid(), player.getName().getString(), price, pokemon, currency);
               boolean success = GtsAPI.addListing(listing, player, slot);
               if (success) {
                  ((ServerCommandSource)context.getSource())
                     .sendMessage(Text.literal(GtsTransactions.message(Gts.language.getMultiCurrency().listed, listing)));
               } else {
                  ((ServerCommandSource)context.getSource())
                     .sendMessage(
                        Text.literal(
                           Utils.formatPlaceholders(
                              Gts.language.getListingFail(),
                              minPrice,
                              pokemon.getDisplayName(Gts.showPokemonDisplayName).getString(),
                              player.getDisplayName().getString(),
                              null
                           )
                        )
                     );
               }

               return 1;
            }
         }
      }
   }

   public int runItem(CommandContext<ServerCommandSource> context) {
      return this.runItem(context, Gts.config.getMultiCurrency().defaultCurrency);
   }

   private int runItem(CommandContext<ServerCommandSource> context, String currency) {
      if (!Gts.config.isEnableItemSales()) {
         return 1;
      } else {
         ServerPlayerEntity player = ((ServerCommandSource)context.getSource()).getPlayer();
         int amount = IntegerArgumentType.getInteger(context, "amount");
         double price = DoubleArgumentType.getDouble(context, "price");

         int stackSize;
         try {
            stackSize = IntegerArgumentType.getInteger(context, "stackSize");
         } catch (Exception var20) {
            stackSize = amount;
         }

         java.util.List<JsonElement> bannedItems = Gts.config.getBannedItems();

         try {
            ItemStack item = ((ServerCommandSource)context.getSource()).getPlayer().getMainHandStack();
            if (item == null) {
               ((ServerCommandSource)context.getSource())
                  .sendMessage(
                     Text.literal(Utils.formatPlaceholders(Gts.language.getNoItemInHand(), 0.0, null, player.getDisplayName().getString(), null))
                  );
               return 1;
            } else if (amount <= 0) {
               ((ServerCommandSource)context.getSource())
                  .sendMessage(
                     Text.literal(
                        Utils.formatPlaceholders(Gts.language.getZeroItemAmount(), 0.0, item.toHoverableText().getString(), player.getDisplayName().getString(), null)
                     )
                  );
               return 1;
            } else {
               for (JsonElement bannedItem : bannedItems) {
                  ItemStack banned = CodecUtils.decodeItem(bannedItem);
                  if (banned.getItem().equals(item.getItem()) && ItemStack.areItemsAndComponentsEqual(banned, item)) {
                     ((ServerCommandSource)context.getSource())
                        .sendMessage(
                           Text.literal(
                              Utils.formatPlaceholders(
                                 Gts.language.getBannedItem(), 0.0, item.toHoverableText().getString(), player.getDisplayName().getString(), null
                              )
                           )
                        );
                     return 1;
                  }
               }

               double minPrice;
               try {
                  minPrice = ListingPrices.item(item, currency, price).minimum();
               } catch (MinimumPricePolicy.Rejected var19) {
                  player.sendMessage(
                     Text.literal(ListingPrices.rejection(var19, currency, item.toHoverableText().getString(), player.getName().getString()))
                  );
                  return 1;
               }

               if (price > Gts.config.getMaximumPrice()) {
                  ((ServerCommandSource)context.getSource())
                     .sendMessage(
                        Text.literal(
                           Utils.formatPlaceholders(
                              Gts.language.getMaximumListingPrice(), minPrice, item.toHoverableText().getString(), player.getDisplayName().getString(), null
                           )
                        )
                     );
                  return 1;
               } else if (item.getCount() < amount) {
                  ((ServerCommandSource)context.getSource())
                     .sendMessage(
                        Text.literal(
                           Utils.formatPlaceholders(
                              Gts.language.getInsufficientItems(), minPrice, item.toHoverableText().getString(), player.getDisplayName().getString(), null
                           )
                        )
                     );
                  return 1;
               } else if (stackSize > 0 && stackSize <= amount && amount % stackSize == 0) {
                  int numberOfStacks = amount / stackSize;

                  for (int i = 0; i < numberOfStacks; i++) {
                     int totalActiveListings = Gts.listings.getListingsByPlayer(player.getUuid()).size();
                     int totalExpiredListigs = Gts.listings.getExpiredListingsOfPlayer(player.getUuid()).size();
                     if (totalActiveListings + totalExpiredListigs >= Gts.config.getMaxListingsPerPlayer()) {
                        ((ServerCommandSource)context.getSource())
                           .sendMessage(
                              Text.literal(
                                 Utils.formatPlaceholders(
                                    Gts.language.getMaximumListings(),
                                    0.0,
                                    null,
                                    ((ServerCommandSource)context.getSource()).getPlayer().getDisplayName().getString(),
                                    null
                                 )
                              )
                           );
                        break;
                     }

                     ItemStack listingItem = item.copy();
                     listingItem.setCount(stackSize);
                     ItemListing listing = new ItemListing(player.getUuid(), player.getName().getString(), price, listingItem, currency);
                     boolean success = GtsAPI.addListing(listing, player, null);
                     if (success) {
                        ((ServerCommandSource)context.getSource())
                           .sendMessage(Text.literal(GtsTransactions.message(Gts.language.getMultiCurrency().listed, listing)));
                     } else {
                        ((ServerCommandSource)context.getSource())
                           .sendMessage(
                              Text.literal(
                                 Utils.formatPlaceholders(
                                    Gts.language.getListingFail(), minPrice, listing.getListingName(), player.getDisplayName().getString(), null
                                 )
                              )
                           );
                     }
                  }

                  return 1;
               } else {
                  ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("The stack size can not be divided by amount."));
                  return 1;
               }
            }
         } catch (NullPointerException var21) {
            ((ServerCommandSource)context.getSource())
               .sendMessage(
                  Text.literal(Utils.formatPlaceholders(Gts.language.getItemIdNotFound(), 0.0, null, player.getDisplayName().getString(), null))
               );
            Gts.LOGGER.error("Couldn't find Item ID\n Stacktrace: ");
            var21.printStackTrace();
            return 1;
         }
      }
   }

   public int showPokemonUsage(CommandContext<ServerCommandSource> context) {
      String usage = "§9Usage:\n§3- gts sell pokemon <slot> <price>";
      ((ServerCommandSource)context.getSource()).sendMessage(Text.literal(Utils.formatMessage(usage, ((ServerCommandSource)context.getSource()).isExecutedByPlayer())));
      return 1;
   }

   public int showItemUsage(CommandContext<ServerCommandSource> context) {
      String usage = "§9Usage:\n§3- gts sell item <price> <quantity> [stack size]";
      ((ServerCommandSource)context.getSource()).sendMessage(Text.literal(Utils.formatMessage(usage, ((ServerCommandSource)context.getSource()).isExecutedByPlayer())));
      return 1;
   }
}
