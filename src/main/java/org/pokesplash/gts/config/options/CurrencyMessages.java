package org.pokesplash.gts.config.options;

import com.google.gson.JsonObject;

public final class CurrencyMessages {
   public String selectionTitle = "GTS Shirokai — escolha a moeda";
   public String confirmationTitle = "GTS Shirokai — confirmar anúncio";
   public String confirm = "§aConfirmar publicação";
   public String cancel = "§cCancelar";
   public String changeCurrency = "§eTrocar moeda";
   public String minimumPrice = "§7Preço mínimo: {price}";
   public String tokensBelowOne = "§cUse um preço de pelo menos 1 PokéToken.";
   public String selected = "§7Moeda: {currency}";
   public String price = "§7Preço: {price}";
   public String invalidPrice = "§cPreço inválido. Use um valor positivo com até duas casas decimais, dentro do limite configurado.";
   public String unavailable = "§cEste anúncio não está mais disponível ou aguarda verificação administrativa.";
   public String insufficientFunds = "§cVocê não possui {currency} suficientes.";
   public String economyError = "§cNão foi possível concluir a operação. Contate a equipe se houve alteração de saldo.";
   public String currencyUnavailable = "§cMoeda indisponível ou sem permissão de uso.";
   public String bought = "§aVocê comprou {listing} por {price}.";
   public String sold = "§aVocê vendeu {listing} por {price}.";
   public String listed = "§aVocê anunciou {listing} por {price}.";
   public String changed = "§cO item ou Pokémon selecionado mudou. Inicie o anúncio novamente.";

   public static CurrencyMessages fromLegacy(JsonObject language) {
      CurrencyMessages messages = new CurrencyMessages();
      if (language.has("purchaseMessageBuyer")) {
         messages.bought = language.get("purchaseMessageBuyer").getAsString() + " " + messages.price;
      }

      if (language.has("listingBought")) {
         messages.sold = language.get("listingBought").getAsString() + " " + messages.price;
      }

      if (language.has("listingSuccess")) {
         messages.listed = language.get("listingSuccess").getAsString() + " " + messages.price;
      }

      if (language.has("insufficientFunds")) {
         messages.insufficientFunds = language.get("insufficientFunds").getAsString() + " " + messages.selected;
      }

      return messages;
   }
}
