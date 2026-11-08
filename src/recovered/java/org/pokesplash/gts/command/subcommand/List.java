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
import net.minecraft.class_1799;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
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

   public LiteralCommandNode<class_2168> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("sell")
                     .requires(ctx -> ctx.method_43737() ? Gts.permissions.hasPermission(ctx.method_44023(), "sell") : true))
                  .executes(this::showUsage))
               .then(
                  ((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("pokemon")
                           .requires(ctx -> ctx.method_43737() ? Gts.config.isEnablePokemonSales() : false))
                        .executes(this::showPokemonUsage))
                     .then(((RequiredArgumentBuilder)class_2170.method_9244("slot", IntegerArgumentType.integer()).suggests((ctx, builder) -> {
                        for (int x = 0; x < 6; x++) {
                           builder.suggest(x + 1);
                        }

                        return builder.buildFuture();
                     }).executes(this::showPokemonUsage)).then(class_2170.method_9244("price", DoubleArgumentType.doubleArg()).suggests((ctx, builder) -> {
                        for (double price : Gts.config.getAllPokemonPrices()) {
                           if (price > 0.0) {
                              builder.suggest((int)price);
                           }
                        }

                        return builder.buildFuture();
                     }).executes(this::run)))
               ))
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("item")
                        .requires(ctx -> ctx.method_43737() ? Gts.config.isEnableItemSales() : false))
                     .executes(this::showItemUsage))
                  .then(
                     ((RequiredArgumentBuilder)class_2170.method_9244("price", DoubleArgumentType.doubleArg()).suggests((ctx, builder) -> {
                           for (int i = 1; i <= 11; i++) {
                              builder.suggest(i * 100);
                           }

                           return builder.buildFuture();
                        }).executes(this::showItemUsage))
                        .then(((RequiredArgumentBuilder)class_2170.method_9244("amount", IntegerArgumentType.integer()).suggests((ctx, builder) -> {
                           for (int i = 0; i <= 64; i++) {
                              builder.suggest(i + 1);
                           }

                           return builder.buildFuture();
                        }).executes(this::run)).then(class_2170.method_9244("stackSize", IntegerArgumentType.integer()).suggests((ctx, builder) -> {
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
   public int run(CommandContext<class_2168> context) {
      if (!((class_2168)context.getSource()).method_43737()) {
         return this.showUsage(context);
      } else {
         class_3222 player = ((class_2168)context.getSource()).method_44023();
         if (!Gts.permissions.hasPermission(player, "sell")) {
            return 1;
         } else {
            double price = DoubleArgumentType.getDouble(context, "price");

            try {
               MinimumPricePolicy.beforeSelection(price, Gts.config.getMultiCurrency().allowZeroPrices, Gts.config.getMaximumPrice());
            } catch (IllegalArgumentException var12) {
               player.method_43496(class_2561.method_43470(Gts.language.getMultiCurrency().invalidPrice));
               return 1;
            }

            boolean isPokemon = context.getInput().contains("pokemon");
            UUID pokemonId = null;
            class_1799 preview = player.method_6047().method_7972();
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
               preview = new PokemonListing(player.method_5667(), player.method_5477().getString(), price, pokemon).getIcon();
            } else if (preview.method_7960()) {
               player.method_43496(class_2561.method_43470(Gts.language.getNoItemInHand()));
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
                  preview.method_7939(stack);
               }
            }

            UUID expectedPokemon = pokemonId;
            class_1799 expectedItem = player.method_6047().method_7972();
            Function<String, MinimumPricePolicy.Quote> validate = currency -> {
               if (player.method_14239()) {
                  return null;
               } else {
                  String assetName = expectedItem.method_7964().getString();

                  try {
                     Gts.config.getMultiCurrency().requireSelectable(currency);
                     if (!Gts.permissions.hasPermission(player, "sell") || !GtsTransactions.permitted(player, currency)) {
                        player.method_43496(class_2561.method_43470(Gts.language.getMultiCurrency().currencyUnavailable));
                        return null;
                     } else if (isPokemon) {
                        Pokemon current = Cobblemon.INSTANCE.getStorage().getParty(player).get(IntegerArgumentType.getInteger(context, "slot") - 1);
                        if (current != null && current.getUuid().equals(expectedPokemon)) {
                           assetName = current.getDisplayName(Gts.showPokemonDisplayName).getString();
                           return ListingPrices.pokemon(current, currency, price);
                        } else {
                           player.method_43496(class_2561.method_43470(Gts.language.getMultiCurrency().changed));
                           return null;
                        }
                     } else if (!class_1799.method_31577(expectedItem, player.method_6047())) {
                        player.method_43496(class_2561.method_43470(Gts.language.getMultiCurrency().changed));
                        return null;
                     } else {
                        return ListingPrices.item(player.method_6047(), currency, price);
                     }
                  } catch (MinimumPricePolicy.Rejected var10x) {
                     player.method_43496(class_2561.method_43470(ListingPrices.rejection(var10x, currency, assetName, player.method_5477().getString())));
                     return null;
                  } catch (IllegalArgumentException var11x) {
                     player.method_43496(class_2561.method_43470(Gts.language.getMultiCurrency().currencyUnavailable));
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

   private int runSelected(CommandContext<class_2168> context, String currency) {
      class_3222 selectedPlayer = ((class_2168)context.getSource()).method_44023();
      if (selectedPlayer != null
         && !selectedPlayer.method_14239()
         && Gts.permissions.hasPermission(selectedPlayer, "sell")
         && GtsTransactions.permitted(selectedPlayer, currency)) {
         try {
            Gts.config.getMultiCurrency().requireSelectable(currency);
         } catch (IllegalArgumentException var9) {
            return 1;
         }

         if (!((class_2168)context.getSource()).method_43737()) {
            ((class_2168)context.getSource()).method_45068(class_2561.method_43470("This command must be ran by a player."));
            return 1;
         } else if (!Gts.timeouts.hasTimeoutExpired(((class_2168)context.getSource()).method_44023().method_5667())) {
            long endTime = Gts.timeouts.getTimeout(((class_2168)context.getSource()).method_44023().method_5667());
            ((class_2168)context.getSource())
               .method_45068(class_2561.method_43470("§cYou have been timed out for " + Utils.parseLongDate(endTime - new Date().getTime())));
            return 1;
         } else {
            try {
               PokemonBattle battle = BattleRegistry.getBattleByParticipatingPlayer(((class_2168)context.getSource()).method_44023());
               if (battle != null) {
                  ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cYou can not list to GTS while in a battle."));
                  return 1;
               } else {
                  int totalListings = Gts.listings.getListingsByPlayer(((class_2168)context.getSource()).method_44023().method_5667()).size();
                  java.util.List<Listing> expiredListings = Gts.listings
                     .getExpiredListingsOfPlayer(((class_2168)context.getSource()).method_44023().method_5667());
                  int totalExpiredListings = expiredListings == null ? 0 : expiredListings.size();
                  if (totalListings + totalExpiredListings >= Gts.config.getMaxListingsPerPlayer()) {
                     ((class_2168)context.getSource())
                        .method_45068(
                           class_2561.method_43470(
                              Utils.formatPlaceholders(
                                 Gts.language.getMaximumListings(), 0.0, null, ((class_2168)context.getSource()).method_44023().method_5476().getString(), null
                              )
                           )
                        );
                     return 1;
                  } else {
                     return context.getInput().contains("pokemon") ? this.runPokemon(context, currency) : this.runItem(context, currency);
                  }
               }
            } catch (Exception var8) {
               ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cSomething went wrong."));
               var8.printStackTrace();
               return 1;
            }
         }
      } else {
         return 1;
      }
   }

   public int runPokemon(CommandContext<class_2168> context) {
      return this.runPokemon(context, Gts.config.getMultiCurrency().defaultCurrency);
   }

   private int runPokemon(CommandContext<class_2168> context, String currency) {
      if (!Gts.config.isEnablePokemonSales()) {
         return 1;
      } else {
         class_3222 player = ((class_2168)context.getSource()).method_44023();
         int slot = IntegerArgumentType.getInteger(context, "slot") - 1;
         double price = DoubleArgumentType.getDouble(context, "price");
         PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
         Pokemon pokemon = party.get(slot);
         if (pokemon == null) {
            ((class_2168)context.getSource())
               .method_45068(
                  class_2561.method_43470(Utils.formatPlaceholders(Gts.language.getNoPokemonInSlot(), 0.0, null, player.method_5476().getString(), null))
               );
            return 1;
         } else if (!pokemon.getTradeable()) {
            ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cThis Pokemon is not tradeable."));
            return 1;
         } else if (party.occupied() < 2) {
            ((class_2168)context.getSource())
               .method_45068(
                  class_2561.method_43470(Utils.formatPlaceholders(Gts.language.getOnlyOnePokemonInParty(), 0.0, null, player.method_5476().getString(), null))
               );
            return 1;
         } else {
            double minPrice;
            try {
               minPrice = ListingPrices.pokemon(pokemon, currency, price).minimum();
            } catch (MinimumPricePolicy.Rejected var14) {
               player.method_43496(
                  class_2561.method_43470(
                     ListingPrices.rejection(var14, currency, pokemon.getDisplayName(Gts.showPokemonDisplayName).getString(), player.method_5477().getString())
                  )
               );
               return 1;
            }

            if (price > Gts.config.getMaximumPrice()) {
               ((class_2168)context.getSource())
                  .method_45068(
                     class_2561.method_43470(
                        Utils.formatPlaceholders(
                           Gts.language.getMaximumListingPrice(),
                           minPrice,
                           pokemon.getDisplayName(Gts.showPokemonDisplayName).getString(),
                           player.method_5476().getString(),
                           null
                        )
                     )
                  );
               return 1;
            } else {
               for (PokemonAspects bannedMon : Gts.config.getBannedPokemon()) {
                  if (bannedMon.equals(pokemon)) {
                     ((class_2168)context.getSource())
                        .method_45068(
                           class_2561.method_43470(
                              Utils.formatPlaceholders(
                                 Gts.language.getBannedPokemon(), 0.0, pokemon.getSpecies().getName(), player.method_5476().getString(), null
                              )
                           )
                        );
                     return 1;
                  }
               }

               PokemonListing listing = new PokemonListing(player.method_5667(), player.method_5477().getString(), price, pokemon, currency);
               boolean success = GtsAPI.addListing(listing, player, slot);
               if (success) {
                  ((class_2168)context.getSource())
                     .method_45068(class_2561.method_43470(GtsTransactions.message(Gts.language.getMultiCurrency().listed, listing)));
               } else {
                  ((class_2168)context.getSource())
                     .method_45068(
                        class_2561.method_43470(
                           Utils.formatPlaceholders(
                              Gts.language.getListingFail(),
                              minPrice,
                              pokemon.getDisplayName(Gts.showPokemonDisplayName).getString(),
                              player.method_5476().getString(),
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

   public int runItem(CommandContext<class_2168> context) {
      return this.runItem(context, Gts.config.getMultiCurrency().defaultCurrency);
   }

   private int runItem(CommandContext<class_2168> context, String currency) {
      if (!Gts.config.isEnableItemSales()) {
         return 1;
      } else {
         class_3222 player = ((class_2168)context.getSource()).method_44023();
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
            class_1799 item = ((class_2168)context.getSource()).method_44023().method_6047();
            if (item == null) {
               ((class_2168)context.getSource())
                  .method_45068(
                     class_2561.method_43470(Utils.formatPlaceholders(Gts.language.getNoItemInHand(), 0.0, null, player.method_5476().getString(), null))
                  );
               return 1;
            } else if (amount <= 0) {
               ((class_2168)context.getSource())
                  .method_45068(
                     class_2561.method_43470(
                        Utils.formatPlaceholders(Gts.language.getZeroItemAmount(), 0.0, item.method_7954().getString(), player.method_5476().getString(), null)
                     )
                  );
               return 1;
            } else {
               for (JsonElement bannedItem : bannedItems) {
                  class_1799 banned = CodecUtils.decodeItem(bannedItem);
                  if (banned.method_7909().equals(item.method_7909()) && class_1799.method_31577(banned, item)) {
                     ((class_2168)context.getSource())
                        .method_45068(
                           class_2561.method_43470(
                              Utils.formatPlaceholders(
                                 Gts.language.getBannedItem(), 0.0, item.method_7954().getString(), player.method_5476().getString(), null
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
                  player.method_43496(
                     class_2561.method_43470(ListingPrices.rejection(var19, currency, item.method_7954().getString(), player.method_5477().getString()))
                  );
                  return 1;
               }

               if (price > Gts.config.getMaximumPrice()) {
                  ((class_2168)context.getSource())
                     .method_45068(
                        class_2561.method_43470(
                           Utils.formatPlaceholders(
                              Gts.language.getMaximumListingPrice(), minPrice, item.method_7954().getString(), player.method_5476().getString(), null
                           )
                        )
                     );
                  return 1;
               } else if (item.method_7947() < amount) {
                  ((class_2168)context.getSource())
                     .method_45068(
                        class_2561.method_43470(
                           Utils.formatPlaceholders(
                              Gts.language.getInsufficientItems(), minPrice, item.method_7954().getString(), player.method_5476().getString(), null
                           )
                        )
                     );
                  return 1;
               } else if (stackSize > 0 && stackSize <= amount && amount % stackSize == 0) {
                  int numberOfStacks = amount / stackSize;

                  for (int i = 0; i < numberOfStacks; i++) {
                     int totalActiveListings = Gts.listings.getListingsByPlayer(player.method_5667()).size();
                     int totalExpiredListigs = Gts.listings.getExpiredListingsOfPlayer(player.method_5667()).size();
                     if (totalActiveListings + totalExpiredListigs >= Gts.config.getMaxListingsPerPlayer()) {
                        ((class_2168)context.getSource())
                           .method_45068(
                              class_2561.method_43470(
                                 Utils.formatPlaceholders(
                                    Gts.language.getMaximumListings(),
                                    0.0,
                                    null,
                                    ((class_2168)context.getSource()).method_44023().method_5476().getString(),
                                    null
                                 )
                              )
                           );
                        break;
                     }

                     class_1799 listingItem = item.method_7972();
                     listingItem.method_7939(stackSize);
                     ItemListing listing = new ItemListing(player.method_5667(), player.method_5477().getString(), price, listingItem, currency);
                     boolean success = GtsAPI.addListing(listing, player, null);
                     if (success) {
                        ((class_2168)context.getSource())
                           .method_45068(class_2561.method_43470(GtsTransactions.message(Gts.language.getMultiCurrency().listed, listing)));
                     } else {
                        ((class_2168)context.getSource())
                           .method_45068(
                              class_2561.method_43470(
                                 Utils.formatPlaceholders(
                                    Gts.language.getListingFail(), minPrice, listing.getListingName(), player.method_5476().getString(), null
                                 )
                              )
                           );
                     }
                  }

                  return 1;
               } else {
                  ((class_2168)context.getSource()).method_45068(class_2561.method_43470("The stack size can not be divided by amount."));
                  return 1;
               }
            }
         } catch (NullPointerException var21) {
            ((class_2168)context.getSource())
               .method_45068(
                  class_2561.method_43470(Utils.formatPlaceholders(Gts.language.getItemIdNotFound(), 0.0, null, player.method_5476().getString(), null))
               );
            Gts.LOGGER.error("Couldn't find Item ID\n Stacktrace: ");
            var21.printStackTrace();
            return 1;
         }
      }
   }

   public int showPokemonUsage(CommandContext<class_2168> context) {
      String usage = "§9Usage:\n§3- gts sell pokemon <slot> <price>";
      ((class_2168)context.getSource()).method_45068(class_2561.method_43470(Utils.formatMessage(usage, ((class_2168)context.getSource()).method_43737())));
      return 1;
   }

   public int showItemUsage(CommandContext<class_2168> context) {
      String usage = "§9Usage:\n§3- gts sell item <price> <quantity> [stack size]";
      ((class_2168)context.getSource()).method_45068(class_2561.method_43470(Utils.formatMessage(usage, ((class_2168)context.getSource()).method_43737())));
      return 1;
   }
}
