package org.pokesplash.gts.UI.module;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.IVs;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PokemonInfoTest {
    @BeforeAll static void bootstrap() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @Test void perfectIvsUseTwoRowsWithCorrectTotalAndGreenValues() {
        IVs ivs = new IVs();
        for (Stats stat : new Stats[]{Stats.HP, Stats.ATTACK, Stats.DEFENCE, Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE, Stats.SPEED}) ivs.set(stat, 31);
        var lines = PokemonInfo.ivLines(ivs);
        assertEquals(3, lines.size());
        assertEquals("IVs (186/186)", lines.get(0).getString());
        assertEquals("  HP 31  ATK 31  DEF 31", lines.get(1).getString());
        assertEquals("  SPA 31  SPD 31  SPE 31", lines.get(2).getString());
        Text value = lines.get(1).getSiblings().get(1);
        assertEquals(Formatting.GREEN.getColorValue(), value.getStyle().getColor().getRgb());
        assertFalse(value.getStyle().isItalic());
    }

    @Test void mixedIvsKeepZerosAndComputeRawTotal() {
        IVs ivs = new IVs();
        for (Stats stat : new Stats[]{Stats.HP, Stats.ATTACK, Stats.DEFENCE, Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE, Stats.SPEED}) ivs.set(stat, 0);
        ivs.set(Stats.ATTACK, 19);
        ivs.set(Stats.SPEED, 12);
        var lines = PokemonInfo.ivLines(ivs);
        assertEquals("IVs (31/186)", lines.get(0).getString());
        assertEquals("  HP 0  ATK 19  DEF 0", lines.get(1).getString());
        assertEquals("  SPA 0  SPD 0  SPE 12", lines.get(2).getString());
    }
}
