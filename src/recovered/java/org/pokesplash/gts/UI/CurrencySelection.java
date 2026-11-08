package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_7923;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.button.Filler;
import org.pokesplash.gts.config.options.CurrencyMessages;
import org.pokesplash.gts.economy.CurrencySelectionSession;
import org.pokesplash.gts.economy.CurrencySettings;
import org.pokesplash.gts.economy.GtsTransactions;
import org.pokesplash.gts.economy.MinimumPricePolicy;
import org.pokesplash.gts.impactor.ImpactorService;
import org.pokesplash.gts.util.ColorUtil;

public final class CurrencySelection {
   private CurrencySelection() {
   }

   public static void open(class_3222 player, class_1799 asset, double price, Function<String, MinimumPricePolicy.Quote> validate, Consumer<String> publish) {
      CurrencySettings settings = Gts.config.getMultiCurrency();
      CurrencyMessages lang = Gts.language.getMultiCurrency();
      CurrencySelectionSession session = new CurrencySelectionSession(validate);
      Builder template = ChestTemplate.builder(3).fill(Filler.getButton());
      int slot = 10;
      if (settings.currencies == null) {
         throw new IllegalStateException(lang.currencyUnavailable);
      } else {
         for (Entry<String, CurrencySettings.Display> entry : settings.currencies.entrySet()) {
            String id = entry.getKey();
            CurrencySettings.Display display = entry.getValue();

            try {
               settings.requireSelectable(id);
               new ImpactorService(id).currency();
               if (!GtsTransactions.permitted(player, id)) {
                  continue;
               }
            } catch (RuntimeException var17) {
               continue;
            }

            if (slot > 16) {
               break;
            }

            class_2960 location = class_2960.method_12829(display.icon);
            class_1799 icon = location == null
               ? new class_1799(class_1802.field_8407)
               : new class_1799((class_1935)class_7923.field_41178.method_10223(location));
            if (icon.method_7960()) {
               icon = new class_1799(class_1802.field_8407);
            }

            template.set(
               slot++,
               GooeyButton.builder()
                  .display(icon)
                  .with(class_9334.field_49631, ColorUtil.parse("<" + display.color + ">" + display.displayName + "</" + display.color + ">"))
                  .with(class_9334.field_49632, new class_9290(List.of(ColorUtil.parse(lang.price.replace("{price}", settings.colored(id, price))))))
                  .onClick(
                     action -> {
                        MinimumPricePolicy.Quote quote = session.select(id);
                        if (quote != null) {
                           Builder confirm = ChestTemplate.builder(3).fill(Filler.getButton());
                           ArrayList<class_2561> lore = new ArrayList<>();
                           lore.add(ColorUtil.parse(lang.selected.replace("{currency}", display.displayName)));
                           lore.add(ColorUtil.parse(lang.price.replace("{price}", settings.colored(id, price))));
                           if (quote.enforcesAutomaticMinimum() && quote.minimum() > 0.0) {
                              lore.add(ColorUtil.parse(lang.minimumPrice.replace("{price}", settings.colored(id, quote.minimum()))));
                           }

                           confirm.set(13, GooeyButton.builder().display(asset.method_7972()).with(class_9334.field_49632, new class_9290(lore)).build());
                           confirm.set(
                              11,
                              GooeyButton.builder()
                                 .display(new class_1799(class_1802.field_8131))
                                 .with(class_9334.field_49631, ColorUtil.parse(lang.confirm))
                                 .onClick(
                                    click -> {
                                       if (!session.isConsumed() && !click.getPlayer().method_14239()) {
                                          if (!session.confirm(id, selected -> {
                                             UIManager.closeUI(click.getPlayer());
                                             publish.accept(selected);
                                          })) {
                                             UIManager.openUIForcefully(
                                                click.getPlayer(), GooeyPage.builder().template(template.build()).title(lang.selectionTitle).build()
                                             );
                                          }
                                       }
                                    }
                                 )
                                 .build()
                           );
                           confirm.set(
                              15,
                              GooeyButton.builder()
                                 .display(new class_1799(class_1802.field_8264))
                                 .with(class_9334.field_49631, ColorUtil.parse(lang.cancel))
                                 .onClick(click -> {
                                    session.cancel();
                                    UIManager.closeUI(click.getPlayer());
                                 })
                                 .build()
                           );
                           confirm.set(
                              22,
                              GooeyButton.builder()
                                 .display(new class_1799(class_1802.field_8107))
                                 .with(class_9334.field_49631, ColorUtil.parse(lang.changeCurrency))
                                 .onClick(click -> {
                                    session.clearSelection();
                                    UIManager.openUIForcefully(
                                       click.getPlayer(), GooeyPage.builder().template(template.build()).title(lang.selectionTitle).build()
                                    );
                                 })
                                 .build()
                           );
                           UIManager.openUIForcefully(action.getPlayer(), GooeyPage.builder().template(confirm.build()).title(lang.confirmationTitle).build());
                        }
                     }
                  )
                  .build()
            );
         }

         template.set(
            22,
            GooeyButton.builder().display(new class_1799(class_1802.field_8077)).with(class_9334.field_49631, ColorUtil.parse(lang.cancel)).onClick(action -> {
               session.cancel();
               UIManager.closeUI(action.getPlayer());
            }).build()
         );
         UIManager.openUIForcefully(player, GooeyPage.builder().template(template.build()).title(lang.selectionTitle).build());
      }
   }
}
