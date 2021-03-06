package me.armar.plugins.autorank.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.armar.plugins.autorank.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/*
 * AutorankTools is a bunch of static methods, I put sendColoredMessage
 * there so that if I ever wanted to change the message formatting I can just do that here.
 *
 */
public class AutorankTools {

    public static enum Time {

        DAYS,
        HOURS,
        MINUTES,
        SECONDS
    }

    private static List<String> reqTypes = new ArrayList<String>();

    /**
     * This will return the correct type of the requirement. As admins might want to use multiple
     * requirements of the same type, they only have to specify the name of it with a unique
     * identifier. E.g. time1, time2 or exp1, exp2, etc.
     *
     * @param oldName Name of the requirement to search for.
     * @return correct requirement name or old name if none was found.
     */
    public static String getCorrectName(String oldName) {

        // Remove all numbers from string
        oldName = oldName.replaceAll("[^a-zA-Z\\s]", "").trim();

        for (final String type : reqTypes) {
            if (!oldName.contains(type)) {
                continue;
            }
            // Contains word

            if (type.length() == oldName.length()) {
                //System.out.print(type + " & " + oldName + " are equal.");

                return type;
            }

            // Did not match correctly, search for next word.
            continue;

        }

        //System.out.print("Returned: null");
        return null;
    }

    /**
     * Elaborate method to check whether a player is excluded from ranking.
     * <p>
     * When a player has a wildcard permission but is an OP, it will return false; When a player
     * has a wildcard permission but is not an OP, it will return true; When a player only has
     * autorank.exclude, it will return true;
     *
     * @param player Player to check for
     * @return whether a player is excluded from ranking or not.
     */
    public static boolean isExcluded(final Player player) {
        if (player.hasPermission("autorank.askdjaslkdj")) {
            // Op's have all permissions, but if he is a OP, he isn't excluded
            if (player.isOp()) {
                return false;
            }

            // Player uses wildcard permission, so excluded
            return true;
        }

        if (player.hasPermission("autorank.exclude")) {
            return true;
        }

        return false;
    }

    /**
     * Register requirement name so it can be used to get the correct name. If a requirement is not
     * passed through this method, it will not show up in {@link #getCorrectName(String)}.
     *
     * @param type Requirement name
     */
    public static void registerRequirement(final String type) {
        if (!reqTypes.contains(type)) {
            reqTypes.add(type);
        }
    }

    public static void sendColoredMessage(final CommandSender sender,
            final String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                ChatColor.GREEN + msg));
    }

    public static void sendColoredMessage(final Player player, final String msg) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                ChatColor.GREEN + msg));
    }

    public static double stringtoDouble(final String string)
            throws NumberFormatException {
        double res = 0;

        if (string != null) {
            res = Double.parseDouble(string);
        }

        return res;
    }

    public static int stringtoInt(final String string)
            throws NumberFormatException {
        int res = 0;

        if (string != null) {
            res = Integer.parseInt(string);
        }

        return res;
    }

    /**
     * Convert a string to an integer.
     *
     * @param string input; this must be in the format '10d 14h 15m'
     * @param time the time type of the output
     * @return the integer representing the number of seconds/minutes/hours/days
     */
    public static int stringToTime(String string, final Time time) {
        int res = 0;

        string = string.trim();

        final Pattern pattern = Pattern
                .compile("((\\d+)d)?((\\d+)h)?((\\d+)m)?");
        final Matcher matcher = pattern.matcher(string);

        matcher.find();
        final String days = matcher.group(2);
        final String hours = matcher.group(4);
        final String minutes = matcher.group(6);

        res += stringtoDouble(minutes);
        res += stringtoDouble(hours) * 60;
        res += stringtoDouble(days) * 60 * 24;

        // Res time is in minutes
        if (time.equals(Time.SECONDS)) {
            return res * 60;
        } else if (time.equals(Time.MINUTES)) {
            return res;
        } else if (time.equals(Time.HOURS)) {
            return res / 60;
        } else if (time.equals(Time.DAYS)) {
            return res / 1440;
        } else {
            return 0;
        }
    }

    /**
     * Convert an integer to a string. <br>
     * Format of the returned string: <b>x days, y hours, z minutes and r seconds</b>
     *
     * @param count the value to convert
     * @param time the type of time of the value given
     * @return string in given format
     */
    public static String timeToString(int count, final Time time) {
        final StringBuilder b = new StringBuilder();

        int days = 0, hours = 0, minutes = 0, seconds = 0;

        if (time.equals(Time.DAYS)) {
            days = count;
        } else if (time.equals(Time.HOURS)) {
            days = count / 24;

            hours = count - (days * 24);
        } else if (time.equals(Time.MINUTES)) {
            days = count / 1440;

            count = count - (days * 1440);

            hours = count / 60;

            minutes = count - (hours * 60);
        } else if (time.equals(Time.SECONDS)) {
            days = count / 86400;

            count = count - (days * 86400);

            hours = count / 3600;

            count = count - (hours * 3600);

            minutes = count / 60;

            seconds = count - (minutes * 60);
        }

        if (days != 0) {
            b.append(days);
            b.append(" ");
            if (days != 1) {
                b.append(Lang.DAY_PLURAL.getConfigValue());
            } else {
                b.append(Lang.DAY_SINGULAR.getConfigValue());
            }

            if (hours != 0 || minutes != 0) {
                b.append(" ");
            }
        }

        if (hours != 0) {
            b.append(hours);
            b.append(" ");
            if (hours != 1) {
                b.append(Lang.HOUR_PLURAL.getConfigValue());
            } else {
                b.append(Lang.HOUR_SINGULAR.getConfigValue());
            }

            if (minutes != 0) {
                b.append(" ");
            }
        }

        if (minutes != 0 || (hours == 0 && days == 0)) {
            b.append(minutes);
            b.append(" ");
            if (minutes != 1) {
                b.append(Lang.MINUTE_PLURAL.getConfigValue());
            } else {
                b.append(Lang.MINUTE_SINGULAR.getConfigValue());
            }

            if (seconds != 0) {
                b.append(" ");
            }
        }

        if (seconds != 0) {
            b.append(seconds);
            b.append(" ");
            if (seconds != 1) {
                b.append(Lang.SECOND_PLURAL.getConfigValue());
            } else {
                b.append(Lang.SECOND_SINGULAR.getConfigValue());
            }
        }

        return b.toString();
    }

    /**
     * Create a string that shows all elements of the given list <br>
     * The end divider is the last word used for the second last element. <br>
     * Example: a list with {1,2,3,4,5,6,7,8,9,0} and end divider 'or'. <br>
     * Would show: 1, 2, 3, 4, 5, 6, 7, 8, 9 or 0.
     *
     * @param array Array to get the elements from.
     * @param endDivider Last word used for dividing the second last and last word.
     * @return string with all elements.
     */
    public static String seperateList(final Collection<?> c,
            final String endDivider) {
        final Object[] array = c.toArray();
        if (array.length == 1) {
            return array[0].toString();
        }

        if (array.length == 0) {
            return null;
        }

        final StringBuilder string = new StringBuilder("");

        for (int i = 0; i < array.length; i++) {

            if (i == (array.length - 1)) {
                string.append(array[i]);
            } else if (i == (array.length - 2)) {
                // Second last
                string.append(array[i] + " " + endDivider + " ");
            } else {
                string.append(array[i] + ", ");
            }
        }

        return string.toString();
    }

    public static String makeProgressString(final Collection<?> c,
            final String wordBetween, final Object currentValue) {
        final Object[] array = c.toArray();

        String extraSpace = " ";

        if (wordBetween == null || wordBetween.equals("")) {
            extraSpace = "";
        }

        String progress = "";

        for (int i = 0; i < c.size(); i++) {

            final String object = array[i].toString();

            if (i == 0) {
                progress += currentValue + extraSpace + wordBetween + "/"
                        + object + extraSpace + wordBetween;
            } else {
                progress += " or " + currentValue + extraSpace + wordBetween
                        + "/" + object + extraSpace + wordBetween;
            }
        }

        return progress;
    }

    /**
     * Split a string with .split() and then get the given element in the array.
     *
     * @param splitString String to split
     * @param splitterCharacter character to split the string with
     * @param element element to get
     * @return String that was at the given splitted element
     */
    public static String getStringFromSplitString(final String splitString,
            final String splitterCharacter, final int element) {
        final String[] split = splitString.split(splitterCharacter);

        String returnString = null;

        try {
            returnString = split[element];

            if (returnString.trim().equals("")) {
                return null;
            }

        } catch (final ArrayIndexOutOfBoundsException e) {
            return null;
        }

        return returnString;
    }

    public static boolean containsAtLeast(Player player, ItemStack item, int amount, String displayName) {
        // Check if player has at least the x of an item WITH proper displayname

        int count = 0;

        // Otherwise we'll not find any colour codes
        displayName = displayName.replace("&", "§");

        // Check every slot
        for (ItemStack itemFound : player.getInventory().getContents()) {

            if (itemFound == null) {
                continue;
            }

            // Not the same item
            if (!itemFound.getType().equals(item.getType())) {
                continue;
            }

            // Check display name
            if (!itemFound.hasItemMeta() || !itemFound.getItemMeta().hasDisplayName()) {
                continue;
            }

            if (itemFound.getItemMeta().getDisplayName().equals(displayName)) {
                count += itemFound.getAmount();
            }
        }

        return count >= amount;
    }
}
