package br.com.instamc.sponge.vip.data;

import br.com.instamc.sponge.library.SpongeLib;
import br.com.instamc.sponge.library.utils.encode.Base64;
import br.com.instamc.sponge.vip.pacotes.Pacote;
import com.google.common.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.item.inventory.ItemStack;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.json.simple.JSONObject;

public class DataSerializer {

    public static String serializePlayerData(Pacote playerData) {
        ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(playerData.toContainer());
        StringWriter stringWriter = new StringWriter();
        try {
            HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringWriter.toString();
    }

    public static Optional<Pacote> deserializePlayerData(String item) {
        ConfigurationNode node = null;
        try {
            node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataView dataView = DataTranslators.CONFIGURATION_NODE.translate(node);

        return Sponge.getDataManager().deserialize(Pacote.class, dataView);
    }
}
