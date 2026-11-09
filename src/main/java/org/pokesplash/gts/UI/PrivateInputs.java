package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/** Captured input never enters public chat; no assets move at this stage. */
public final class PrivateInputs {
    private record Pending(String prompt, Consumer<String> accept, long expires) {}
    private static final Map<UUID, Pending> PENDING = new ConcurrentHashMap<>();
    public static void cancel(UUID id) { PENDING.remove(id); }
    public static void request(ServerPlayerEntity player, String prompt, Consumer<String> accept) {
        PENDING.put(player.getUuid(), new Pending(prompt, accept, System.currentTimeMillis() + 120_000));
        UIManager.closeUI(player);
        player.sendMessage(Text.literal("§e" + prompt + " §7Digite cancelar para voltar. Prazo: 2 minutos. Sua resposta não será publicada no chat."));
    }
    public static boolean capture(ServerPlayerEntity player, String input) {
        Pending pending = PENDING.get(player.getUuid());
        if (pending == null) return false;
        player.getServer().execute(() -> {
            if (!PENDING.remove(player.getUuid(), pending) || player.isDisconnected()) return;
            if (System.currentTimeMillis() > pending.expires()) {
                player.sendMessage(Text.literal("§cA entrada expirou. Abra o livro de vendas novamente.")); return;
            }
            if (input.trim().equalsIgnoreCase("cancelar") || input.trim().equalsIgnoreCase("cancel")) { ListingBrowser.open(player); return; }
            try { pending.accept().accept(input); }
            catch (IllegalArgumentException error) {
                player.sendMessage(Text.literal("§c" + error.getMessage()));
                request(player, pending.prompt(), pending.accept());
            }
        });
        return true;
    }
}
