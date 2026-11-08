package org.pokesplash.gts.UI.module;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.util.ColorUtil;
import org.pokesplash.gts.util.Utils;

public abstract class PokemonInfo {
   public static Collection<class_2561> parse(Pokemon pokemon) {
      class_2583 style = class_2583.field_24360.method_10978(false);
      Collection<class_2561> lore = new ArrayList<>();
      class_2583 dark_aqua = style.method_27703((class_5251)class_5251.method_27719("dark_aqua").getOrThrow());
      class_2583 dark_green = style.method_27703((class_5251)class_5251.method_27719("dark_green").getOrThrow());
      class_2583 dark_purple = style.method_27703((class_5251)class_5251.method_27719("dark_purple").getOrThrow());
      class_2583 gold = style.method_27703((class_5251)class_5251.method_27719("gold").getOrThrow());
      class_2583 gray = style.method_27703((class_5251)class_5251.method_27719("gray").getOrThrow());
      class_2583 green = style.method_27703((class_5251)class_5251.method_27719("green").getOrThrow());
      class_2583 red = style.method_27703((class_5251)class_5251.method_27719("red").getOrThrow());
      class_2583 light_purple = style.method_27703((class_5251)class_5251.method_27719("light_purple").getOrThrow());
      class_2583 yellow = style.method_27703((class_5251)class_5251.method_27719("yellow").getOrThrow());
      class_2583 white = style.method_27703((class_5251)class_5251.method_27719("white").getOrThrow());
      class_1792 ball = pokemon.getCaughtBall().item();
      lore.add(
         class_2561.method_43473()
            .method_10862(style)
            .method_10852(ColorUtil.parse(Gts.language.getPokemonBall()))
            .method_10852(class_2561.method_43471(ball.method_7864(new class_1799(ball)).getString()).method_10862(green))
      );
      lore.add(
         class_2561.method_43471("cobblemon.ui.info.species")
            .method_10862(dark_green)
            .method_27693(": ")
            .method_10852(pokemon.getSpecies().getTranslatedName().method_10862(green))
      );
      class_5250 types = class_2561.method_43473().method_10862(green);

      for (ElementalType type : pokemon.getSpecies().getTypes()) {
         types.method_27693(" ").method_10852(type.getDisplayName());
      }

      lore.add(class_2561.method_43471("cobblemon.ui.info.type").method_10862(dark_green).method_27693(":").method_10852(types));
      lore.add(
         class_2561.method_43471("cobblemon.ui.info.nature")
            .method_10862(dark_green)
            .method_27693(": ")
            .method_10852(class_2561.method_43471(pokemon.getNature().getDisplayName()).method_10862(green))
      );
      class_5250 ability = class_2561.method_43471("cobblemon.ui.info.ability")
         .method_10862(dark_green)
         .method_27693(": ")
         .method_10852(class_2561.method_43471(pokemon.getAbility().getDisplayName()).method_10862(green));
      if (Utils.isHA(pokemon)) {
         ability.method_10852(class_2561.method_43470(" §b(HA)"));
      }

      lore.add(ability);
      if (!pokemon.getPersistentData().method_10558("size").isEmpty()) {
         lore.add(
            class_2561.method_43470("Size")
               .method_10862(dark_green)
               .method_27693(": ")
               .method_10852(class_2561.method_43470(Utils.capitaliseFirst(pokemon.getPersistentData().method_10558("size"))).method_10862(green))
         );
      }

      if (pokemon.getPersistentData().method_10577("pokerus")) {
         lore.add(
            class_2561.method_43470("Pokerus")
               .method_10862(dark_green)
               .method_27693(": ")
               .method_10852(class_2561.method_43471("cobblemon.ui.generic.yes").method_10862(green))
         );
      }

      lore.add(class_2561.method_43471("cobblemon.ui.stats").method_10862(gray).method_27693(": "));
      lore.add(
         class_2561.method_43471("cobblemon.ui.stats.hp")
            .method_10862(light_purple)
            .method_27693(
               " §8- §3IV: §a"
                  + (pokemon.getIvs().get(Stats.HP) == null ? "0" : pokemon.getIvs().get(Stats.HP))
                  + " §cEV: §a"
                  + (pokemon.getEvs().get(Stats.HP) == null ? "0" : pokemon.getEvs().get(Stats.HP))
            )
      );
      lore.add(
         class_2561.method_43471("cobblemon.ui.stats.atk")
            .method_10862(red)
            .method_27693(
               " §8- §3IV: §a"
                  + (pokemon.getIvs().get(Stats.ATTACK) == null ? "0" : pokemon.getIvs().get(Stats.ATTACK))
                  + " §cEV: §a"
                  + (pokemon.getEvs().get(Stats.ATTACK) == null ? "0" : pokemon.getEvs().get(Stats.ATTACK))
            )
      );
      lore.add(
         class_2561.method_43471("cobblemon.ui.stats.def")
            .method_10862(gold)
            .method_27693(
               " §8- §3IV: §a"
                  + (pokemon.getIvs().get(Stats.DEFENCE) == null ? "0" : pokemon.getIvs().get(Stats.DEFENCE))
                  + " §cEV: §a"
                  + (pokemon.getEvs().get(Stats.DEFENCE) == null ? "0" : pokemon.getEvs().get(Stats.DEFENCE))
            )
      );
      lore.add(
         class_2561.method_43471("cobblemon.ui.stats.sp_atk")
            .method_10862(dark_purple)
            .method_27693(
               " §8- §3IV: §a"
                  + (pokemon.getIvs().get(Stats.SPECIAL_ATTACK) == null ? "0" : pokemon.getIvs().get(Stats.SPECIAL_ATTACK))
                  + " §cEV: §a"
                  + (pokemon.getEvs().get(Stats.SPECIAL_ATTACK) == null ? "0" : pokemon.getEvs().get(Stats.SPECIAL_ATTACK))
            )
      );
      lore.add(
         class_2561.method_43471("cobblemon.ui.stats.sp_def")
            .method_10862(yellow)
            .method_27693(
               " §8- §3IV: §a"
                  + (pokemon.getIvs().get(Stats.SPECIAL_DEFENCE) == null ? "0" : pokemon.getIvs().get(Stats.SPECIAL_DEFENCE))
                  + " §cEV: §a"
                  + (pokemon.getEvs().get(Stats.SPECIAL_DEFENCE) == null ? "0" : pokemon.getEvs().get(Stats.SPECIAL_DEFENCE))
            )
      );
      lore.add(
         class_2561.method_43471("cobblemon.ui.stats.speed")
            .method_10862(dark_aqua)
            .method_27693(
               " §8- §3IV: §a"
                  + (pokemon.getIvs().get(Stats.SPEED) == null ? "0" : pokemon.getIvs().get(Stats.SPEED))
                  + " §cEV: §a"
                  + (pokemon.getEvs().get(Stats.SPEED) == null ? "0" : pokemon.getEvs().get(Stats.SPEED))
            )
      );
      lore.add(class_2561.method_43471("cobblemon.ui.stats.friendship").method_10862(dark_green).method_27693(": §a" + pokemon.getFriendship()));
      lore.add(class_2561.method_43471("cobblemon.ui.moves").method_10862(gold).method_27693(": "));

      for (Move move : pokemon.getMoveSet().getMoves()) {
         lore.add(class_2561.method_43471(move.getTemplate().getDisplayName().getString()).method_10862(white));
      }

      if (Gts.config.isShowBreedable()) {
         if (pokemon.getPersistentData().method_10545("breedable") && !pokemon.getPersistentData().method_10577("breedable")) {
            lore.add(class_2561.method_43470("§cUnbreedable"));
         } else {
            lore.add(class_2561.method_43470("§bBreedable"));
         }
      }

      return lore;
   }

   public static Collection<class_2561> parse(PokemonListing listing) {
      Pokemon pokemon = listing.getListing();
      return parse(pokemon);
   }
}
