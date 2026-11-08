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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemConvertible;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.DataComponentTypes;
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

   public static void open(ServerPlayerEntity player, ItemStack asset, double price, Function<String, MinimumPricePolicy.Quote> validate, Consumer<String> publish) {
      CurrencySettings settings = Gts.config.getMultiCurrency();
      CurrencyMessages lang = Gts.language.getMultiCurrency();
      CurrencySelectionSession session = new CurrencySelectionSession(validate);
      Builder template = ReusableSelectionTemplate.builder(3).fill(Filler.getButton());
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

            Identifier location = Identifier.tryParse(display.icon);
            ItemStack icon = location == null
               ? new ItemStack(Items.PAPER)
               : new ItemStack((ItemConvertible)Registries.ITEM.get(location));
            if (icon.isEmpty()) {
               icon = new ItemStack(Items.PAPER);
            }

            template.set(
               slot++,
               GooeyButton.builder()
                  .display(icon)
                  .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse("<" + display.color + ">" + display.displayName + "</" + display.color + ">"))
                  .with(DataComponentTypes.LORE, new LoreComponent(List.of(ColorUtil.parse(lang.price.replace("{price}", settings.colored(id, price))))))
                  .onClick(
                     action -> {
                        MinimumPricePolicy.Quote quote = session.select(id);
                        if (quote != null) {
                           Builder confirm = ChestTemplate.builder(3).fill(Filler.getButton());
                           ArrayList<Text> lore = new ArrayList<>();
                           lore.add(ColorUtil.parse(lang.selected.replace("{currency}", display.displayName)));
                           lore.add(ColorUtil.parse(lang.price.replace("{price}", settings.colored(id, price))));
                           if (quote.enforcesAutomaticMinimum() && quote.minimum() > 0.0) {
                              lore.add(ColorUtil.parse(lang.minimumPrice.replace("{price}", settings.colored(id, quote.minimum()))));
                           }

                           confirm.set(13, GooeyButton.builder().display(asset.copy()).with(DataComponentTypes.LORE, new LoreComponent(lore)).build());
                           confirm.set(
                              11,
                              GooeyButton.builder()
                                 .display(new ItemStack(Items.LIME_DYE))
                                 .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(lang.confirm))
                                 .onClick(
                                    click -> {
                                       if (!session.isConsumed() && !click.getPlayer().isDisconnected()) {
                                          if (!session.confirm(id, selected -> {
                                             UIManager.closeUI(click.getPlayer());
                                             publish.accept(selected);
                                          })) {
                                             UIManager.openUIForcefully(
                                                click.getPlayer(), GooeyPage.builder().template(Layouts.apply("sell_currency", template.build())).title(Layouts.title("sell_currency", lang.selectionTitle)).build()
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
                                 .display(new ItemStack(Items.RED_DYE))
                                 .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(lang.cancel))
                                 .onClick(click -> {
                                    session.cancel();
                                    UIManager.closeUI(click.getPlayer());
                                 })
                                 .build()
                           );
                           confirm.set(
                              22,
                              GooeyButton.builder()
                                 .display(new ItemStack(Items.ARROW))
                                 .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(lang.changeCurrency))
                                 .onClick(click -> {
                                    session.clearSelection();
                                    UIManager.openUIForcefully(
                                       click.getPlayer(), GooeyPage.builder().template(Layouts.apply("sell_currency", template.build())).title(Layouts.title("sell_currency", lang.selectionTitle)).build()
                                    );
                                 })
                                 .build()
                           );
                           UIManager.openUIForcefully(action.getPlayer(), GooeyPage.builder().template(Layouts.apply("sell_confirm", confirm.build())).title(Layouts.title("sell_confirm", lang.confirmationTitle)).build());
                        }
                     }
                  )
                  .build()
            );
         }

         template.set(
            22,
            GooeyButton.builder().display(new ItemStack(Items.BARRIER)).with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(lang.cancel)).onClick(action -> {
               session.cancel();
               UIManager.closeUI(action.getPlayer());
            }).build()
         );
         UIManager.openUIForcefully(player, GooeyPage.builder().template(Layouts.apply("sell_currency", template.build())).title(Layouts.title("sell_currency", lang.selectionTitle)).build());
      }
   }
}
