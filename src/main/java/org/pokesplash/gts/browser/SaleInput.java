package org.pokesplash.gts.browser;

import java.math.BigDecimal;

public record SaleInput(BigDecimal price, int amount) {
    public static SaleInput parse(String text, boolean item, int available) {
        String[] parts = text.trim().split("\\s+");
        if (parts.length < 1 || parts.length > (item ? 2 : 1)) throw new IllegalArgumentException("Digite o preço" + (item ? " e, opcionalmente, a quantidade." : "."));
        BigDecimal price;
        try { price = new BigDecimal(parts[0].replace(',', '.')).stripTrailingZeros(); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Preço inválido."); }
        if (price.signum() < 0 || price.scale() > 2 || !Double.isFinite(price.doubleValue())) throw new IllegalArgumentException("Use um preço válido com até duas casas decimais.");
        int amount;
        try { amount = parts.length == 2 ? Integer.parseInt(parts[1]) : 1; }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Quantidade inválida."); }
        if (amount < 1 || amount > available) throw new IllegalArgumentException("Quantidade deve estar entre 1 e " + available + ".");
        return new SaleInput(price, amount);
    }
}
