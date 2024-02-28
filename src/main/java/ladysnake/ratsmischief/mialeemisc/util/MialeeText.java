package ladysnake.ratsmischief.mialeemisc.util;

import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;

public class MialeeText {
	/**
	 * Takes a text and returns the same text but with the given int color.
	 */
	public static Text withColor(Text text, int color) {
		Style style = text.getStyle().withColor(color);
		List<Text> styled = text.setStyle(style);
		if (!styled.isEmpty()) {
			return styled.get(0);
		}
		//MialeeMisc.LOGGER.error("Failed to set color of text: " + text.getString() + " to color: " + color);
		return text;
	}

	/**
	 * Takes a text and returns the same text but with without italics.
	 */
	public static Text withoutItalics(Text text) {
		List<Text> styled = text.setStyle(text.getStyle().withItalic(false));
		if (!styled.isEmpty()) {
			return styled.get(0);
		}
		//MialeeMisc.LOGGER.error("Failed to remove italics from text: " + text.getString());
		return text;
	}
}
