package org.pokesplash.gts.config.options;

import com.cobblemon.mod.common.pokemon.Pokemon;

public class PokemonAspects {
   private String species;
   private String form;
   private String ability;
   private String gender;

   public PokemonAspects() {
      this.species = "blaziken";
      this.form = "";
      this.ability = "speedboost";
      this.gender = "male";
   }

   public PokemonAspects(String species) {
      this.species = species;
      this.form = "";
      this.ability = "";
      this.gender = "";
   }

   public String getSpecies() {
      return this.species;
   }

   public String getForm() {
      return this.form;
   }

   public String getAbility() {
      return this.ability;
   }

   public String getGender() {
      return this.gender;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o instanceof PokemonAspects other) {
         return this.species.equals(other.getSpecies())
            && this.form.equals(other.getForm())
            && this.ability.equals(other.getAbility())
            && this.gender.equals(other.getGender());
      } else if (!(o instanceof Pokemon other)) {
         return false;
      } else {
         boolean isSameSpecies = false;
         boolean isSameForm = false;
         boolean isSameAbility = false;
         boolean isSameGender = false;
         if (this.species.trim().isBlank() || this.species.equalsIgnoreCase(other.getSpecies().getName())) {
            isSameSpecies = true;
         }

         if (this.form.trim().isBlank() || this.form.equalsIgnoreCase(other.getForm().getName())) {
            isSameForm = true;
         }

         if (this.ability.trim().isBlank() || this.ability.equalsIgnoreCase(other.getAbility().getName())) {
            isSameAbility = true;
         }

         if (this.gender.trim().isBlank() || this.gender.equalsIgnoreCase(other.getGender().name())) {
            isSameGender = true;
         }

         return isSameSpecies && isSameForm && isSameAbility && isSameGender;
      }
   }
}
