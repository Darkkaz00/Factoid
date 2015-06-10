/**
 * Source from https://forums.bukkit.org/threads/util-colored-console-output.168889/
 */
package me.tabinol.factoid.utilities;

import java.util.EnumMap;
import java.util.Map;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.Ansi.Attribute;

public class ColoredConsole {

	private static final Map<ChatStyle, String> ansicolors = new EnumMap<ChatStyle, String>(
			ChatStyle.class);
	private static final ChatStyle[] colors = ChatStyle.values();

	private static String colorize(String msg) {
		ansicolors.put(ChatStyle.BLACK,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).boldOff()
						.toString());
		ansicolors.put(ChatStyle.DARK_BLUE,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).boldOff()
						.toString());
		ansicolors.put(ChatStyle.DARK_GREEN,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).boldOff()
						.toString());
		ansicolors.put(ChatStyle.DARK_AQUA,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).boldOff()
						.toString());
		ansicolors.put(ChatStyle.DARK_RED,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).boldOff()
						.toString());
		ansicolors.put(ChatStyle.DARK_PURPLE, Ansi.ansi().a(Attribute.RESET)
				.fg(Ansi.Color.MAGENTA).boldOff().toString());
		ansicolors.put(ChatStyle.GOLD,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff()
						.toString());
		ansicolors.put(ChatStyle.GRAY,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).boldOff()
						.toString());
		ansicolors.put(ChatStyle.DARK_GRAY,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).bold()
						.toString());
		ansicolors.put(ChatStyle.BLUE,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).bold()
						.toString());
		ansicolors.put(ChatStyle.GREEN,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).bold()
						.toString());
		ansicolors.put(ChatStyle.AQUA,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).bold()
						.toString());
		ansicolors.put(ChatStyle.RED,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).bold()
						.toString());
		ansicolors.put(ChatStyle.LIGHT_PURPLE, Ansi.ansi().a(Attribute.RESET)
				.fg(Ansi.Color.MAGENTA).bold().toString());
		ansicolors.put(ChatStyle.YELLOW,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).bold()
						.toString());
		ansicolors.put(ChatStyle.WHITE,
				Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).bold()
						.toString());
		ansicolors.put(ChatStyle.MAGIC, Ansi.ansi().a(Attribute.BLINK_SLOW)
				.toString());
		ansicolors.put(ChatStyle.BOLD, Ansi.ansi()
				.a(Attribute.UNDERLINE_DOUBLE).toString());
		ansicolors.put(ChatStyle.STRIKETHROUGH,
				Ansi.ansi().a(Attribute.STRIKETHROUGH_ON).toString());
		ansicolors.put(ChatStyle.UNDERLINE, Ansi.ansi().a(Attribute.UNDERLINE)
				.toString());
		ansicolors.put(ChatStyle.ITALIC, Ansi.ansi().a(Attribute.ITALIC)
				.toString());
		ansicolors.put(ChatStyle.RESET, Ansi.ansi().a(Attribute.RESET)
				.toString());

		for (ChatStyle c : colors) {
			if (!ansicolors.containsKey(c)) {
				msg = msg.replaceAll(c.toString(), "");
			} else {
				msg = msg.replaceAll(c.toString(), ansicolors.get(c));
			}
		}
		return msg;
	}

	private static String OS = System.getProperty("os.name").toLowerCase();

	public static void info(String msg) {
		if (OS.indexOf("win") >= 0) {
			AnsiConsole.out.print(colorize(msg)
					+ Ansi.ansi().reset().toString());
		} else {
			System.out.println(colorize(msg) + Ansi.ansi().reset().toString());
		}

	}
}
