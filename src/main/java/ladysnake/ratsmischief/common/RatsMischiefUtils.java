package ladysnake.ratsmischief.common;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class RatsMischiefUtils {
	// DATES
	public static final boolean IS_WORLD_RAT_DAY = DateTimeFormatter.ofPattern("dd/MM").format(LocalDateTime.now()).equals("04/04");
	// RENDER
	public static final Identifier REMY_TEXTURE = RatsMischief.id("textures/entity/named/remy.png");
	private static final LocalDate today = LocalDate.now();
	public static final boolean IS_RAT_BIRTHDAY = LocalDate.of(today.getYear(), 7, 19).compareTo(today) * today.compareTo(LocalDate.of(today.getYear(), 7, 25)) >= 0;
	public static final boolean IS_MISCHIEF_BIRTHDAY = (LocalDate.of(today.getYear(), 12, 28).compareTo(today) * today.compareTo(LocalDate.of(today.getYear(), 12, 31)) >= 0)
		|| (LocalDate.of(today.getYear(), 1, 1).compareTo(today) * today.compareTo(LocalDate.of(today.getYear(), 1, 3)) >= 0);
	public static final boolean IS_BIRTHDAY = IS_RAT_BIRTHDAY || IS_MISCHIEF_BIRTHDAY;
	public static Identifier[] RAT_KID_TEXTURES;

	public static Identifier getRatTexture(RatEntity.Type ratType, DyeColor ratColor) {
		// initializing
		if (RAT_KID_TEXTURES == null) {
			RAT_KID_TEXTURES = new Identifier[16];
			for (DyeColor color : DyeColor.values()) {
				RAT_KID_TEXTURES[color.getId()] = RatsMischief.id("textures/entity/rat_kid/rat_kid_" + color.getName().toLowerCase(Locale.ROOT) + ".png");
			}
		}

		if (ratType == RatEntity.Type.RAT_KID) {
			return RAT_KID_TEXTURES[ratColor.getId()];
		} else {
			return ratType.ratTexture;
		}
	}
}
