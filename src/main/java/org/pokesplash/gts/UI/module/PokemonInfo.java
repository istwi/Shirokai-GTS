package org.pokesplash.gts.UI.module;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.util.ColorUtil;
import org.pokesplash.gts.util.Utils;

public abstract class PokemonInfo {
   public static Collection<Text> parse(Pokemon pokemon) {
      Style style = Style.EMPTY.withItalic(false);
      Collection<Text> lore = new ArrayList<>();
      Style dark_aqua = style.withColor((TextColor)TextColor.parse("dark_aqua").getOrThrow());
      Style dark_green = style.withColor((TextColor)TextColor.parse("dark_green").getOrThrow());
      Style dark_purple = style.withColor((TextColor)TextColor.parse("dark_purple").getOrThrow());
      Style gold = style.withColor((TextColor)TextColor.parse("gold").getOrThrow());
      Style gray = style.withColor((TextColor)TextColor.parse("gray").getOrThrow());
      Style green = style.withColor((TextColor)TextColor.parse("green").getOrThrow());
      Style red = style.withColor((TextColor)TextColor.parse("red").getOrThrow());
      Style light_purple = style.withColor((TextColor)TextColor.parse("light_purple").getOrThrow());
      Style yellow = style.withColor((TextColor)TextColor.parse("yellow").getOrThrow());
      Style white = style.withColor((TextColor)TextColor.parse("white").getOrThrow());
      Item ball = pokemon.getCaughtBall().item();
      lore.add(
         Text.empty()
            .setStyle(style)
            .append(ColorUtil.parse(Gts.language.getPokemonBall()))
            .append(Text.translatable(ball.getName(new ItemStack(ball)).getString()).setStyle(green))
      );
      lore.add(
         Text.translatable("cobblemon.ui.info.species")
            .setStyle(dark_green)
            .append(": ")
            .append(pokemon.getSpecies().getTranslatedName().setStyle(green))
      );
      MutableText types = Text.empty().setStyle(green);

      for (ElementalType type : pokemon.getSpecies().getTypes()) {
         types.append(" ").append(type.getDisplayName());
      }

      lore.add(Text.translatable("cobblemon.ui.info.type").setStyle(dark_green).append(":").append(types));
      lore.add(
         Text.translatable("cobblemon.ui.info.nature")
            .setStyle(dark_green)
            .append(": ")
            .append(Text.translatable(pokemon.getNature().getDisplayName()).setStyle(green))
      );
      MutableText ability = Text.translatable("cobblemon.ui.info.ability")
         .setStyle(dark_green)
         .append(": ")
         .append(Text.translatable(pokemon.getAbility().getDisplayName()).setStyle(green));
      if (Utils.isHA(pokemon)) {
         ability.append(Text.literal(" §b(HA)"));
      }

      lore.add(ability);
      if (!pokemon.getPersistentData().getString("size").isEmpty()) {
         lore.add(
            Text.literal("Size")
               .setStyle(dark_green)
               .append(": ")
               .append(Text.literal(Utils.capitaliseFirst(pokemon.getPersistentData().getString("size"))).setStyle(green))
         );
      }

      if (pokemon.getPersistentData().getBoolean("pokerus")) {
         lore.add(
            Text.literal("Pokerus")
               .setStyle(dark_green)
               .append(": ")
               .append(Text.translatable("cobblemon.ui.generic.yes").setStyle(green))
         );
      }

      lore.add(Text.translatable("cobblemon.ui.stats").setStyle(gray).append(": "));
      lore.add(
         Text.translatable("cobblemon.ui.stats.hp")
            .setStyle(light_purple)
            .append(
               " §8- §3IV: §a"
                  + (pokemon.getIvs().get(Stats.HP) == null ? "0" : pokemon.getIvs().get(Stats.HP))
                  + " §cEV: §a"
                  + (pokemon.getEvs().get(Stats.HP) == null ? "0" : pokemon.getEvs().get(Stats.HP))
            )
      );
      lore.add(
         Text.translatable("cobblemon.ui.stats.atk")
            .setStyle(red)
            .append(
               " §8- §3IV: §a"
                  + (pokemon.getIvs().get(Stats.ATTACK) == null ? "0" : pokemon.getIvs().get(Stats.ATTACK))
                  + " §cEV: §a"
                  + (pokemon.getEvs().get(Stats.ATTACK) == null ? "0" : pokemon.getEvs().get(Stats.ATTACK))
            )
      );
      lore.add(
         Text.translatable("cobblemon.ui.stats.def")
            .setStyle(gold)
            .append(
               " §8- §3IV: §a"
                  + (pokemon.getIvs().get(Stats.DEFENCE) == null ? "0" : pokemon.getIvs().get(Stats.DEFENCE))
                  + " §cEV: §a"
                  + (pokemon.getEvs().get(Stats.DEFENCE) == null ? "0" : pokemon.getEvs().get(Stats.DEFENCE))
            )
      );
      lore.add(
         Text.translatable("cobblemon.ui.stats.sp_atk")
            .setStyle(dark_purple)
            .append(
               " §8- §3IV: §a"
                  + (pokemon.getIvs().get(Stats.SPECIAL_ATTACK) == null ? "0" : pokemon.getIvs().get(Stats.SPECIAL_ATTACK))
                  + " §cEV: §a"
                  + (pokemon.getEvs().get(Stats.SPECIAL_ATTACK) == null ? "0" : pokemon.getEvs().get(Stats.SPECIAL_ATTACK))
            )
      );
      lore.add(
         Text.translatable("cobblemon.ui.stats.sp_def")
            .setStyle(yellow)
            .append(
               " §8- §3IV: §a"
                  + (pokemon.getIvs().get(Stats.SPECIAL_DEFENCE) == null ? "0" : pokemon.getIvs().get(Stats.SPECIAL_DEFENCE))
                  + " §cEV: §a"
                  + (pokemon.getEvs().get(Stats.SPECIAL_DEFENCE) == null ? "0" : pokemon.getEvs().get(Stats.SPECIAL_DEFENCE))
            )
      );
      lore.add(
         Text.translatable("cobblemon.ui.stats.speed")
            .setStyle(dark_aqua)
            .append(
               " §8- §3IV: §a"
                  + (pokemon.getIvs().get(Stats.SPEED) == null ? "0" : pokemon.getIvs().get(Stats.SPEED))
                  + " §cEV: §a"
                  + (pokemon.getEvs().get(Stats.SPEED) == null ? "0" : pokemon.getEvs().get(Stats.SPEED))
            )
      );
      lore.add(Text.translatable("cobblemon.ui.stats.friendship").setStyle(dark_green).append(": §a" + pokemon.getFriendship()));
      lore.add(Text.translatable("cobblemon.ui.moves").setStyle(gold).append(": "));

      for (Move move : pokemon.getMoveSet().getMoves()) {
         lore.add(Text.translatable(move.getTemplate().getDisplayName().getString()).setStyle(white));
      }

      if (Gts.config.isShowBreedable()) {
         if (pokemon.getPersistentData().contains("breedable") && !pokemon.getPersistentData().getBoolean("breedable")) {
            lore.add(Text.literal("§cUnbreedable"));
         } else {
            lore.add(Text.literal("§bBreedable"));
         }
      }

      return lore;
   }

   public static Collection<Text> parse(PokemonListing listing) {
      Pokemon pokemon = listing.getListing();
      return parse(pokemon);
   }
}
