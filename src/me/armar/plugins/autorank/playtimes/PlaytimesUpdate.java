package me.armar.plugins.autorank.playtimes;

import java.util.UUID;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.entity.Player;

/*
 * PlaytimesUpdate does an update on all online players
 * every 5 minutes (set lower atm for debugging).
 *
 */
public class PlaytimesUpdate implements Runnable {

    private final Playtimes playtimes;
    private final Autorank plugin;

    public PlaytimesUpdate(final Playtimes playtimes, final Autorank plugin) {
        this.playtimes = playtimes;

        this.plugin = plugin;

    }

    @Override
    public void run() {
        updateMinutesPlayed();
    }

    private void updateMinutesPlayed(final Player player) {
        // Changed this so it is readable ;)
        // OP's should also get time added.
        // When a player has a wildcard permission ('*') it should still update.

        if (player.hasPermission("autorank.rsefrxsgtse")
                || !player.hasPermission("autorank.timeexclude")) {

            final DependencyManager depManager = plugin.getDependencyManager();

            // Check to see if player is afk
            if (depManager.isAFK(player)) {
                return;
            }

            final UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());

            // Modify local time
            playtimes.modifyLocalTime(uuid, Playtimes.INTERVAL_MINUTES);

            // Modify global time
            if (playtimes.isMySQLEnabled()) {
                playtimes.modifyGlobalTime(uuid, Playtimes.INTERVAL_MINUTES);
            }

            // Check if player meets requirements
            plugin.getPlayerChecker().checkPlayer(player);

        }
    }

    @SuppressWarnings("deprecation")
    private void updateMinutesPlayed() {
        plugin.debugMessage("Checking players for automatic ranking");

        for (final Player player : plugin.getServer().getOnlinePlayers()) {

            if (player.getPlayer() == null) {
                plugin.debugMessage("Could not update play time of "
                        + player.getName() + " as (s)he is not online!");
                continue;
            }

            updateMinutesPlayed(player);
        }
    }

}
