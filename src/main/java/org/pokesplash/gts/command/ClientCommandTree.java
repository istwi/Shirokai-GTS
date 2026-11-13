package org.pokesplash.gts.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

/** Hide GTS subcommands only in the client copy; server parsing and permissions stay intact. */
public final class ClientCommandTree {
    private ClientCommandTree() {}

    public static <S> RootCommandNode<S> hideGtsSubcommands(RootCommandNode<S> original) {
        RootCommandNode<S> result = new RootCommandNode<>();
        for (var child : original.getChildren()) {
            if (child instanceof LiteralCommandNode<S> literal && literal.getLiteral().equals("gts")) {
                var gts = new LiteralCommandNode<S>("gts", literal.getCommand(), literal.getRequirement(), null, null, false);
                // Accept manually typed arguments on the client without exposing literal names.
                gts.addChild(RequiredArgumentBuilder.<S, String>argument("comando", StringArgumentType.greedyString())
                        .executes(context -> 0).build());
                result.addChild(gts);
            } else {
                result.addChild(child);
            }
        }
        return result;
    }
}
