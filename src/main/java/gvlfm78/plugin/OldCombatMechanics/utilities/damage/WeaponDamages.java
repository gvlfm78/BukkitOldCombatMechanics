package gvlfm78.plugin.OldCombatMechanics.utilities.damage;

import gvlfm78.plugin.OldCombatMechanics.OCMMain;
import gvlfm78.plugin.OldCombatMechanics.utilities.ConfigUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class WeaponDamages {

    private static Map<String, Double> damages;

    private static OCMMain plugin;

    public static void initialise(OCMMain plugin){
        WeaponDamages.plugin = plugin;
        reload();
    }

    private static void reload(){
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("old-tool-damage.damages");

        damages = ConfigUtils.loadDoubleMap(section);
    }

    public static double getDamage(Material mat){
        //Replace 1.14 material names to ones used in config.yml
        String name = mat.name().replace("GOLDEN", "GOLD").replace("WOODEN", "WOOD");
        return damages.getOrDefault(name, -1.0);
    }
}
