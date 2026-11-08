package org.pokesplash.gts.config.options;

public class PokemonPrices {
   private PokemonAspects pokemon = new PokemonAspects();
   private double price = 10000.0;

   public PokemonAspects getPokemon() {
      return this.pokemon;
   }

   public double getPrice() {
      return this.price;
   }
}
