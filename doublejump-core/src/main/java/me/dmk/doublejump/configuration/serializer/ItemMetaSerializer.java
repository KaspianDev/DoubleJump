package me.dmk.doublejump.configuration.serializer;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemMetaSerializer implements ObjectSerializer<ItemMeta> {

    private final MiniMessage miniMessage;

    public ItemMetaSerializer(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public boolean supports(@NonNull Class<? super ItemMeta> type) {
        return ItemMeta.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull ItemMeta itemMeta, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        if (itemMeta.hasDisplayName()) {
            Component displayName = this.deserialize(itemMeta.getDisplayName());
            data.add("display-name", this.serialize(displayName), String.class);
        }

        if (itemMeta.hasLore()) {
            List<String> lore = itemMeta.getLore().stream()
                    .map(this::deserialize)
                    .map(this::serialize)
                    .toList();

            data.addCollection("lore", lore, String.class);
        }

        if (itemMeta.hasEnchants()) {
            data.addAsMap("enchantments", itemMeta.getEnchants(), Enchantment.class, Integer.class);
        }

        if (!itemMeta.getItemFlags().isEmpty()) {
            data.addCollection("item-flags", itemMeta.getItemFlags(), ItemFlag.class);
        }
    }

    @Override
    public ItemMeta deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        Optional<String> displayNameOptional = Optional.ofNullable(data.get("display-name", String.class));
        Optional<List<String>> loreOptional = Optional.ofNullable(data.getAsList("lore", String.class));

        Optional<Map<Enchantment, Integer>> enchantmentsOptional = Optional.ofNullable(data.getAsMap("enchantments", Enchantment.class, Integer.class));
        Optional<List<ItemFlag>> itemFlagsOptional = Optional.ofNullable(data.getAsList("item-flags", ItemFlag.class));

        ItemBuilder itemBuilder = ItemBuilder.from(Material.STONE);

        displayNameOptional.ifPresent(displayName -> itemBuilder.name(this.deserialize(displayName)));
        loreOptional.ifPresent(lore -> itemBuilder.lore(this.deserialize(lore)));

        enchantmentsOptional.ifPresent(itemBuilder::enchant);
        itemFlagsOptional.ifPresent(itemFlags -> itemBuilder.flags(itemFlags.toArray(new ItemFlag[0])));

        return itemBuilder.build().getItemMeta();
    }

    private Component deserialize(String text) {
        if (text.contains("§")) {
            return LegacyComponentSerializer.legacySection().deserialize(text);
        }

        return this.miniMessage.deserialize(text);
    }

    private List<Component> deserialize(List<String> strings) {
        return strings.stream()
                .map(this::deserialize)
                .toList();
    }

    private String serialize(Component component) {
        return this.miniMessage.serialize(component);
    }
}
