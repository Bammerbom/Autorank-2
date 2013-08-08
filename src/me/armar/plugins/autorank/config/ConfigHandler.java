package me.armar.plugins.autorank.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

import com.google.common.collect.Lists;

/**
 * This class handles has all methods to get data from the config. This is now
 * handled by every class seperately, but should be organised soon.
 * 
 * @author Staartvin
 * 
 */
public class ConfigHandler {

	private Autorank plugin;
	private SimpleYamlConfiguration config;

	public ConfigHandler(Autorank instance) {
		plugin = instance;
		this.config = plugin.getAdvancedConfig();
	}

	/**
	 * Gets whether a requirement is optional for a certain group
	 * 
	 * @param requirement
	 * @param group
	 * @return true if optional; false otherwise
	 */
	public boolean isOptional(String requirement, String group) {
		boolean optional = config.getBoolean("ranks." + group
				+ ".requirements." + requirement + ".options.optional", false);

		return optional;
	}

	public Set<String> getRequirements(String group) {
		Set<String> requirements = config.getConfigurationSection(
				"ranks." + group + ".requirements").getKeys(false);

		return requirements;
	}

	public Set<String> getResults(String group) {
		Set<String> results = config.getConfigurationSection(
				"ranks." + group + ".results").getKeys(false);

		return results;
	}

	public Set<String> getRanks() {
		return config.getConfigurationSection("ranks").getKeys(false);
	}

	public String getRequirement(String requirement, String group) {

		// Correct config
		String result;
		result = (config.get("ranks." + group + ".requirements." + requirement
				+ ".value") != null) ? config.get(
				"ranks." + group + ".requirements." + requirement + ".value")
				.toString() : config.getString(
				"ranks." + group + ".requirements." + requirement).toString();

		return result;
	}

	public String getResult(String result, String group) {
		return config.get("ranks." + group + ".results." + result).toString();
	}

	public String getRankChange(String group) {
		return config.getString("ranks." + group + ".results.rank change");
	}

	public List<String> getResultsOfRequirement(String requirement, String group) {
		Set<String> results = new HashSet<String>();
		
		results = (Set<String>) ((config.getConfigurationSection("ranks." + group + ".requirements." + requirement + ".results") != null) ? config.getConfigurationSection("ranks." + group + ".requirements." + requirement + ".results").getKeys(false): new HashSet<String>());
		
		return Lists.newArrayList(results);
	}
	
	public String getResultOfRequirement(String requirement, String group, String result) {
		return config.get("ranks." + group + ".requirements." + requirement + ".results." + result).toString();
	}
	
	public boolean usePartialCompletion() {
		return config.getBoolean("use partial completion", false);
	}
	
	public boolean useMySQL() {
		return config.getBoolean("sql.enabled");
	}
}