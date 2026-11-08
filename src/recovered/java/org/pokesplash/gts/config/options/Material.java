package org.pokesplash.gts.config.options;

public class Material {
   private String material;
   private String nbt;

   public Material(String material, String nbt) {
      this.material = material;
      this.nbt = nbt;
   }

   public String getMaterial() {
      return this.material;
   }

   public String getNBT() {
      return this.nbt;
   }
}
