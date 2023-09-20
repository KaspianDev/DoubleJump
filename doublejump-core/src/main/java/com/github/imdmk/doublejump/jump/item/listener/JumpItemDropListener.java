package com.github.imdmk.doublejump.jump.item.listener;

import com.github.imdmk.doublejump.jump.JumpPlayerService;
import com.github.imdmk.doublejump.jump.item.JumpItemService;
import com.github.imdmk.doublejump.jump.item.configuration.JumpItemConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class JumpItemDropListener implements Listener {

    private final JumpItemConfiguration jumpItemConfiguration;
    private final JumpItemService jumpItemService;
    private final JumpPlayerService jumpPlayerService;

    public JumpItemDropListener(JumpItemConfiguration jumpItemConfiguration, JumpItemService jumpItemService, JumpPlayerService jumpPlayerService) {
        this.jumpItemConfiguration = jumpItemConfiguration;
        this.jumpItemService = jumpItemService;
        this.jumpPlayerService = jumpPlayerService;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        ItemStack itemDrop = event.getItemDrop().getItemStack();

        if (!this.jumpItemService.compare(itemDrop)) {
            return;
        }

        if (this.jumpItemConfiguration.dropConfiguration.delete) {
            event.setCancelled(true);
            player.getInventory().removeItem(itemDrop);
        }

        if (this.jumpItemConfiguration.dropConfiguration.disableDoubleJumpMode) {
            this.jumpPlayerService.disable(player);
        }

        if (this.jumpItemConfiguration.dropConfiguration.cancel) {
            event.setCancelled(true);
        }
    }
}
