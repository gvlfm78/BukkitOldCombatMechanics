package gvlfm78.plugin.OldCombatMechanics;

import com.codingforcookies.armourequip.ArmourListener;
import gvlfm78.plugin.OldCombatMechanics.hooks.PlaceholderAPIHook;
import gvlfm78.plugin.OldCombatMechanics.hooks.api.Hook;
import gvlfm78.plugin.OldCombatMechanics.module.*;
import gvlfm78.plugin.OldCombatMechanics.updater.ModuleUpdateChecker;
import gvlfm78.plugin.OldCombatMechanics.utilities.Config;
import gvlfm78.plugin.OldCombatMechanics.utilities.Messenger;
import gvlfm78.plugin.OldCombatMechanics.utilities.damage.EntityDamageByEntityListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OCMMain extends JavaPlugin {

    private static OCMMain INSTANCE;
    private Logger logger = getLogger();
    private OCMConfigHandler CH = new OCMConfigHandler(this);
    private List<Runnable> disableListeners = new ArrayList<>();
    private List<Runnable> enableListeners = new ArrayList<>();
    private List<Hook> hooks = new ArrayList<>();

    public static OCMMain getInstance(){
        return INSTANCE;
    }

    @Override
    public void onEnable(){
        INSTANCE = this;

        PluginDescriptionFile pdfFile = this.getDescription();

        // Setting up config.yml
        CH.setupConfig();

        // Initialise ModuleLoader utility
        ModuleLoader.initialise(this);

        // Register all the modules
        registerModules();

        // Register all hooks for integrating with other plugins
        registerHooks();

        // Initialize all the hooks
        hooks.forEach(hook -> hook.init(this));

        // Set up the command handler
        getCommand("OldCombatMechanics").setExecutor(new OCMCommandHandler(this, this.getFile()));

        // Initialise the Messenger utility
        Messenger.initialise(this);

        // Initialise Config utility
        Config.initialise(this);

        // MCStats Metrics
        try{
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch(IOException e){
            // Failed to submit the stats
        }

        //BStats Metrics
        Metrics metrics = new Metrics(this);

        metrics.addCustomChart(
                new Metrics.SimpleBarChart(
                        "enabled_modules",
                        () -> ModuleLoader.getModules().stream()
                                .filter(Module::isEnabled)
                                .collect(Collectors.toMap(Module::toString, module -> 1))
                )
        );

        enableListeners.forEach(Runnable::run);

        // Logging to console the enabling of OCM
        logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " has been enabled");

    }

    @Override
    public void onDisable(){

        PluginDescriptionFile pdfFile = this.getDescription();

        disableListeners.forEach(Runnable::run);

        //if (task != null) task.cancel();

        // Logging to console the disabling of OCM
        logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " has been disabled");
    }

    private void registerModules(){
        // Update Checker (also a module so we can use the dynamic registering/unregistering)
        ModuleLoader.addModule(new ModuleUpdateChecker(this, this.getFile()));

        // Module listeners
        ModuleLoader.addModule(new ArmourListener(this));
        ModuleLoader.addModule(new ModuleAttackCooldown(this));
        ModuleLoader.addModule(new ModulePlayerCollisions(this));

        //Listeners registered after with same priority appear to be called later
        ModuleLoader.addModule(new ModuleOldToolDamage(this));
        ModuleLoader.addModule(new ModuleSwordSweep(this));

        ModuleLoader.addModule(new ModuleOldPotionEffects(this));
        ModuleLoader.addModule(new EntityDamageByEntityListener(this));
        ModuleLoader.addModule(new ModuleGoldenApple(this));
        ModuleLoader.addModule(new ModuleFishingKnockback(this));
        ModuleLoader.addModule(new ModulePlayerRegen(this));
        ModuleLoader.addModule(new ModuleSwordBlocking(this));
        ModuleLoader.addModule(new ModuleOldArmourStrength(this));
        ModuleLoader.addModule(new ModuleDisableCrafting(this));
        ModuleLoader.addModule(new ModuleDisableOffHand(this));
        ModuleLoader.addModule(new ModuleOldBrewingStand(this));
        ModuleLoader.addModule(new ModuleDisableElytra(this));
        ModuleLoader.addModule(new ModuleDisableProjectileRandomness(this));
        ModuleLoader.addModule(new ModuleDisableBowBoost(this));
        ModuleLoader.addModule(new ModuleProjectileKnockback(this));
        ModuleLoader.addModule(new ModuleNoLapisEnchantments(this));
        ModuleLoader.addModule(new ModuleDisableEnderpearlCooldown(this));
    }

    private void registerHooks(){
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            hooks.add(new PlaceholderAPIHook());
        }
    }

    public void upgradeConfig(){
        CH.upgradeConfig();
    }

    public boolean doesConfigExist(){
        return CH.doesConfigExist();
    }

    /**
     * Registers a runnable to run when the plugin gets disabled.
     *
     * @param action the {@link Runnable} to run when the plugin gets disabled
     */
    public void addDisableListener(Runnable action){
        disableListeners.add(action);
    }
    /**
     * Registers a runnable to run when the plugin gets enabled.
     *
     * @param action the {@link Runnable} to run when the plugin gets enabled
     */
    public void addEnableListener(Runnable action){
        enableListeners.add(action);
    }
}