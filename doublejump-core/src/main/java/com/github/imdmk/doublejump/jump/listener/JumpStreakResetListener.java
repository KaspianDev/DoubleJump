package com.github.imdmk.doublejump.jump.listener;

import com.github.imdmk.doublejump.jump.JumpPlayer;
import com.github.imdmk.doublejump.jump.JumpPlayerManager;
import com.github.imdmk.doublejump.jump.JumpSettings;
import com.github.imdmk.doublejump.jump.event.reset.JumpStreakResetEvent;
import com.github.imdmk.doublejump.jump.event.reset.JumpStreakResetReason;
import com.github.imdmk.doublejump.notification.NotificationSender;
import com.github.imdmk.doublejump.notification.NotificationSettings;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

public class JumpStreakResetListener implements Listener {

    private final Server server;
    private final JumpSettings jumpSettings;
    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;
    private final JumpPlayerManager jumpPlayerManager;

    public JumpStreakResetListener(Server server, JumpSettings jumpSettings, NotificationSettings notificationSettings, NotificationSender notificationSender, JumpPlayerManager jumpPlayerManager) {
        this.server = server;
        this.jumpSettings = jumpSettings;
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
        this.jumpPlayerManager = jumpPlayerManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!this.jumpSettings.streakSettings.resetOnDeath) {
            return;
        }

        Optional<JumpPlayer> jumpPlayerOptional = this.jumpPlayerManager.getJumpPlayer(player.getUniqueId());
        if (jumpPlayerOptional.isEmpty()) {
            return;
        }

        JumpPlayer jumpPlayer = jumpPlayerOptional.get();

        if (jumpPlayer.getStreak() == 0) {
            return;
        }

        this.resetStreak(player, jumpPlayer, JumpStreakResetReason.PLAYER_DEATH);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!this.jumpSettings.streakSettings.resetOnGround) {
            return;
        }

        if (!player.isOnGround()) {
            return;
        }

        Optional<JumpPlayer> jumpPlayerOptional = this.jumpPlayerManager.getJumpPlayer(player.getUniqueId());
        if (jumpPlayerOptional.isEmpty()) {
            return;
        }

        JumpPlayer jumpPlayer = jumpPlayerOptional.get();

        if (jumpPlayer.getStreak() == 0) {
            return;
        }

        this.resetStreak(player, jumpPlayer, JumpStreakResetReason.PLAYER_ON_GROUND);
    }

    private void resetStreak(Player player, JumpPlayer jumpPlayer, JumpStreakResetReason streakResetReason) {
        JumpStreakResetEvent streakResetEvent = new JumpStreakResetEvent(player, jumpPlayer, streakResetReason);

        this.server.getPluginManager().callEvent(streakResetEvent);

        if (streakResetEvent.isCancelled()) {
            return;
        }

        jumpPlayer.setStreak(0);

        this.notificationSender.send(player, this.notificationSettings.jumpStreakResetNotification);
    }
}
