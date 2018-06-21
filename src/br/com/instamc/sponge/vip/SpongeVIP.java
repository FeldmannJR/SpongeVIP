package br.com.instamc.sponge.vip;

import br.com.instamc.sponge.library.apis.ComandoAPI;
import br.com.instamc.sponge.library.menu.MenuButton;
import br.com.instamc.sponge.library.menu.menus.venda.moedas.Moeda;
import br.com.instamc.sponge.library.menu.menus.venda.moedas.MoedaType;
import br.com.instamc.sponge.vip.data.PacoteDB;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.vip.cmds.CmdCriarPacote;
import br.com.instamc.sponge.vip.cmds.CmdCriarPagina;
import br.com.instamc.sponge.vip.cmds.CmdShop;
import br.com.instamc.sponge.vip.cmds.CmdShopAdm;
import br.com.instamc.sponge.vip.pacotes.Pacote;
import br.com.instamc.sponge.vip.pacotes.PacoteType;
import br.com.instamc.sponge.vip.provisorio.CustomCoins;
import br.com.instamc.sponge.vip.provisorio.MCustomCoins;
import com.google.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;

/**
 *
 * @author Gabriel
 *
 */
@Plugin(id = "instavip", name = "VIP-ITMC", authors = "Feldmann", dependencies
        = {
            @Dependency(id = "instalibrary")
        }, description = "Plugin de Vips e Pacotes", version = "0.1")
public class SpongeVIP {

    public static List<ComprouPacote> comprapacote = new ArrayList();

    public static Iterable<MenuButton> getButtons() {
        return botoespermanentes;
    }

   
    public static Moeda moedashop;
   // public static CustomCoins coins;
    ConfigurationNode config;
    private static List<MenuButton> botoespermanentes = new ArrayList();
    public static String banco = "pokemon2_vips";
    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;
    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    public Path getDefaultConfig() {
        return defaultConfig;
    }
    Game game;
    Logger logger;
    public static SpongeVIP instancia;

    @Inject
    public SpongeVIP(Game game, Logger logger) {
        instancia = this;
        this.game = game;
        this.logger = logger;
    }

    public static void addButtonToMenu(MenuButton but) {
        botoespermanentes.add(but);
    }

    public void loadConfig() {
        if (!getDefaultConfig().toFile().exists()) {
            try {
                getDefaultConfig().toFile().createNewFile();
                config = configManager.load();
                config.getNode("DB", "banco").setValue("pokemon2_vips");
                configManager.save(config);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                config = configManager.load();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        banco = config.getNode("DB", "banco").getString();
    }

    public static PermissionService getPermissions() {
        return Sponge.getGame().getServiceManager().getRegistration(PermissionService.class).get().getProvider();
    }

    public static void sendMessageComprou(Player comprou, String oq, int preco) {
        for (Player p : Sponge.getServer().getOnlinePlayers()) {
            p.sendMessage(Txt.f("§6§l[Loja] §eO jogador §a" + comprou.getName() + " §ecomprou §c" + oq + " §epor §a§l" + preco + " " + moedashop.getName(preco) + " §e!"));
        }
    }

 //   public static MCustomCoins custommoeda = new MCustomCoins();

    @Listener
    public void gameStart(GameStartedServerEvent ev) {
        instancia = this;
        Sponge.getDataManager().registerBuilder(Pacote.class, new Pacote.Builder());
        PacoteDB.init();
        PacoteManager.init();
        registerCmds();
        //coins = new CustomCoins();
        moedashop = MoedaType.GEMA.m;
        Sponge.getEventManager().registerListeners(this, new PacoteListener());

    }

    public void registerCmds() {
        HashMap<String, PacoteType> map = new HashMap();
        map.put("vip", PacoteType.VIP);
        map.put("itens", PacoteType.ITENS);
        map.put("permissoes", PacoteType.PERMISSION);
        Sponge.getCommandManager().register(this, CommandSpec.builder().arguments(
                GenericArguments.choices(Text.of("tipo"), map),
                GenericArguments.integer(Text.of("preco")),
                GenericArguments.remainingJoinedStrings(Text.of("nome"))
        ).description(Text.of("Cria pacote.")).executor(new CmdCriarPacote()).build(), "criarpacote");
        Sponge.getCommandManager().register(this, CommandSpec.builder().description(Txt.f("Comando loja de gemas!")).executor(new CmdShop()).build(), "shop");
        Sponge.getCommandManager().register(this, CommandSpec.builder().permission(getPermission()).description(Txt.f("Comando loja de gemas de OPS!")).executor(new CmdShopAdm()).build(), "shopadm");
        ComandoAPI.enable(new CmdCriarPagina());
    }

    private static String getPermission() {
        return "instamc.shop.staff";
    }

    public static boolean hasPermission(Player p) {
        return p.hasPermission(getPermission());
    }

    public static PluginContainer getPlugin() {
        return Sponge.getPluginManager().getPlugin("instaprotection").get();
    }

    public static void sendMessage(Player p, String msg) {
        p.sendMessage(Txt.f("§7[§f§lVIP§7] §r§e" + msg));
    }
}
