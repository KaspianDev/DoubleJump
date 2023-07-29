package me.dmk.doublejump.configuration.serializer;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import me.dmk.doublejump.notification.Notification;
import me.dmk.doublejump.notification.NotificationType;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NotificationSerializer implements ObjectSerializer<Notification> {

    @Override
    public boolean supports(@NonNull Class<? super Notification> type) {
        return Notification.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull Notification notification, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("type", notification.type());
        data.add("message", notification.message());
    }

    @Override
    public Notification deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        NotificationType type = data.get("type", NotificationType.class);
        String message = data.get("message", String.class);

        return new Notification(type, message);
    }
}
