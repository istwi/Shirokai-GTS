package org.pokesplash.gts.command.subcommand;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;
import org.pokesplash.gts.economy.GtsTransactions;

public final class Recover extends Subcommand {
    public Recover() { super("§e/gts recuperar [confirmar]"); }
    @Override public LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager.literal("recuperar")
                .requires(source -> !source.isExecutedByPlayer() || Gts.permissions.hasPermission(source.getPlayer(), "recover"))
                .executes(this::run)
                .then(CommandManager.literal("confirmar").executes(this::confirm)).build();
    }
    @Override public int run(CommandContext<ServerCommandSource> context) {
        var report = GtsTransactions.recoveryReport();
        long eligible = report.stream().filter(GtsTransactions.RecoveryEntry::eligible).count();
        context.getSource().sendMessage(Text.literal("§eAnúncios retidos: " + report.size() + ". Criações concluídas compatíveis para recuperação: " + eligible + "."));
        report.stream().limit(20).forEach(entry -> context.getSource().sendMessage(Text.literal(
                (entry.eligible() ? "§a" : "§c") + entry.id() + " | " + entry.phase() + " | " + (entry.eligible() ? "compatível" : "exige análise"))));
        if (eligible > 0) context.getSource().sendMessage(Text.literal("§ePara liberar essas criações concluídas: /gts recuperar confirmar. Será salvo backup de cada registro. Compras/devoluções incertas continuam retidas."));
        return 1;
    }
    private int confirm(CommandContext<ServerCommandSource> context) {
        String actor = context.getSource().isExecutedByPlayer() ? context.getSource().getPlayer().getUuid().toString() : "console";
        int restored = GtsTransactions.recoverCompletedCreations(actor);
        context.getSource().sendMessage(Text.literal("§aCriações recuperadas: " + restored + ". Abra Meu GTS novamente. Registros incompatíveis ou de operações incertas não foram liberados."));
        return 1;
    }
}
