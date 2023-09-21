package com.github.imdmk.doublejump.jump.item.command;

import com.github.imdmk.doublejump.jump.item.configuration.JumpItemSettings;
import com.github.imdmk.doublejump.notification.Notification;
import com.github.imdmk.doublejump.notification.NotificationSender;
import com.github.imdmk.doublejump.notification.NotificationSettings;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Route(name = "doublejump")
public class DoubleJumpItemCommand {

    private final JumpItemSettings jumpItemSettings;
    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;

    public DoubleJumpItemCommand(JumpItemSettings jumpItemSettings, NotificationSettings notificationSettings, NotificationSender notificationSender) {
        this.jumpItemSettings = jumpItemSettings;
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
    }

    @Execute(route = "item-give", required = 1)
    void giveItem(CommandSender sender, @Arg @Name("target") Player target) {
        if (!this.jumpItemSettings.enabled) {
            this.notificationSender.send(sender, this.notificationSettings.jumpItemDisabledNotification);
            return;
        }

        ItemStack jumpItem = this.jumpItemSettings.item;
        Inventory targetInventory = target.getInventory();

        if (targetInventory.firstEmpty() == -1) {
            this.notificationSender.send(sender, this.notificationSettings.targetFullInventoryNotification);
            return;
        }

        targetInventory.addItem(jumpItem);

        Notification notification = Notification.builder()
                .fromNotification(this.notificationSettings.jumpItemAddedNotification)
                .placeholder("{PLAYER}", target.getName())
                .build();

        this.notificationSender.send(sender, notification);
    }

    @Execute(route = "item-remove", required = 1)
    void removeItem(CommandSender sender, @Arg @Name("target") Player target) {
        ItemStack jumpItem = this.jumpItemSettings.item;

        Inventory targetInventory = target.getInventory();
        Inventory targetEnderChest = target.getEnderChest();

        if (!targetInventory.contains(jumpItem) && !targetEnderChest.contains(jumpItem)) {
            this.notificationSender.send(sender, this.notificationSettings.targetHasNoJumpItemNotification);
            return;
        }

        targetInventory.remove(jumpItem);
        targetEnderChest.remove(jumpItem);

        Notification notification = Notification.builder()
                .fromNotification(this.notificationSettings.jumpItemRemovedNotification)
                .placeholder("{PLAYER}", target.getName())
                .build();

        this.notificationSender.send(sender, notification);
    }
}
