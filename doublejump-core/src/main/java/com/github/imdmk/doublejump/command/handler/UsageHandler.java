package com.github.imdmk.doublejump.command.handler;

import com.github.imdmk.doublejump.notification.NotificationSender;
import com.github.imdmk.doublejump.notification.configuration.NotificationSettings;
import com.github.imdmk.doublejump.text.Formatter;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;

public class UsageHandler implements InvalidUsageHandler<CommandSender> {

    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;

    public UsageHandler(NotificationSettings notificationSettings, NotificationSender notificationSender) {
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, Schematic schematic) {
        if (schematic.isOnlyFirst()) {
            Formatter formatter = new Formatter()
                    .placeholder("{USAGE}", schematic.first());

            this.notificationSender.send(sender, this.notificationSettings.invalidUsage, formatter);
            return;
        }

        this.notificationSender.send(sender, this.notificationSettings.invalidUsageListFirst);

        for (String schema : schematic.getSchematics()) {
            Formatter formatter = new Formatter()
                    .placeholder("{USAGE}", schema);

            this.notificationSender.send(sender, this.notificationSettings.invalidUsageList, formatter);
        }
    }
}
