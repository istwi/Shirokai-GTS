package org.pokesplash.gts.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClientCommandTreeTest {
    @Test void hidesAllSubcommandsWhilePreservingManualExecutionAndOtherCommands() throws Exception {
        var server = new CommandDispatcher<Object>();
        server.register(LiteralArgumentBuilder.<Object>literal("gts").executes(ctx -> 1)
                .then(LiteralArgumentBuilder.<Object>literal("history").executes(ctx -> 2))
                .then(LiteralArgumentBuilder.<Object>literal("reload").requires(source -> false).executes(ctx -> 3)));
        server.register(LiteralArgumentBuilder.<Object>literal("help").executes(ctx -> 4));
        Object source = new Object();
        var client = new CommandDispatcher<>(ClientCommandTree.hideGtsSubcommands(server.getRoot()));
        assertEquals(List.of("gts"), client.getCompletionSuggestions(client.parse("gt", source)).get().getList().stream().map(s -> s.getText()).toList());
        for (String input : List.of("gts ", "gts h", "gts reload ")) {
            assertTrue(client.getCompletionSuggestions(client.parse(input, source)).get().isEmpty());
        }
        assertEquals(0, client.execute("gts history", source));
        assertEquals(2, server.execute("gts history", source));
        assertThrows(com.mojang.brigadier.exceptions.CommandSyntaxException.class, () -> server.execute("gts reload", source));
        assertNotNull(server.getRoot().getChild("gts").getChild("history"));
        assertSame(server.getRoot().getChild("help"), client.getRoot().getChild("help"));
        assertEquals(4, client.execute("help", source));
    }
}
