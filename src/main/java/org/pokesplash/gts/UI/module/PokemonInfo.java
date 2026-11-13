package org.pokesplash.gts.UI.module;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Pokemon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.util.Utils;

public abstract class PokemonInfo {
   public static Collection<Text> parse(Pokemon pokemon) {
      List<Text> lore = new ArrayList<>();
      lore.add(label("Nature", Text.translatable(pokemon.getNature().getDisplayName())));
      MutableText ability = label("Ability", Text.translatable(pokemon.getAbility().getDisplayName()));
      if (Utils.isHA(pokemon)) ability.append(Text.literal(" (HA)").formatted(Formatting.AQUA));
      lore.add(ability);
      lore.add(separator());
      lore.addAll(ivLines(pokemon.getIvs()));
      lore.add(separator());
      lore.add(line("Moves:", Formatting.GRAY));
      for (Move move : pokemon.getMoveSet().getMoves()) {
         lore.add(line("  ", Formatting.WHITE).append(move.getTemplate().getDisplayName().copy().formatted(Formatting.WHITE)));
      }
      lore.add(separator());
      var ball = pokemon.getCaughtBall().item();
      lore.add(label("Ball", ball.getName(new ItemStack(ball))));
      lore.add(label("Tera", pokemon.getTeraType().getDisplayName()));
      lore.add(label("Friendship", Text.literal(Integer.toString(pokemon.getFriendship()))));
      if (!pokemon.getPersistentData().getString("size").isEmpty()) {
         lore.add(label("Size", Text.literal(Utils.capitaliseFirst(pokemon.getPersistentData().getString("size")))));
      }
      if (pokemon.getPersistentData().getBoolean("pokerus")) lore.add(label("Pokerus", Text.literal("Yes")));
      if (Gts.config != null && Gts.config.isShowBreedable()) {
         boolean breedable = !pokemon.getPersistentData().contains("breedable") || pokemon.getPersistentData().getBoolean("breedable");
         lore.add(line(breedable ? "Breedable" : "Unbreedable", breedable ? Formatting.AQUA : Formatting.RED));
      }
      lore.add(separator());
      return lore;
   }

   static List<Text> ivLines(com.cobblemon.mod.common.pokemon.IVs ivs) {
      Stats[] stats = {Stats.HP, Stats.ATTACK, Stats.DEFENCE, Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE, Stats.SPEED};
      String[] names = {"HP", "ATK", "DEF", "SPA", "SPD", "SPE"};
      int total = 0;
      for (Stats stat : stats) total += ivs.get(stat) == null ? 0 : ivs.get(stat);
      List<Text> lines = new ArrayList<>();
      lines.add(line("IVs (" + total + "/186)", Formatting.GRAY));
      for (int row = 0; row < 2; row++) {
         MutableText line = line("  ", Formatting.GRAY);
         for (int col = 0; col < 3; col++) {
            int index = row * 3 + col;
            int value = ivs.get(stats[index]) == null ? 0 : ivs.get(stats[index]);
            if (col > 0) line.append("  ");
            line.append(line(names[index] + " ", Formatting.DARK_GRAY));
            line.append(line(Integer.toString(value), value == 31 ? Formatting.GREEN : Formatting.WHITE));
         }
         lines.add(line);
      }
      return lines;
   }

   public static MutableText separator() {
      return line("────────────────────────", Formatting.DARK_GRAY);
   }

   private static MutableText label(String label, Text value) {
      return line(label + ": ", Formatting.GRAY).append(value.copy().styled(style -> style.withColor(Formatting.WHITE).withItalic(false)));
   }

   private static MutableText line(String text, Formatting color) {
      return Text.literal(text).styled(style -> style.withColor(color).withItalic(false));
   }

   public static Collection<Text> parse(PokemonListing listing) {
      return parse(listing.getListing());
   }
}
