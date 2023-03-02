package doctor4t.ratsmischief.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RatsMischiefUtils {

	public static final boolean IS_WORLD_RAT_DAY = DateTimeFormatter.ofPattern("dd/MM").format(LocalDateTime.now()).equals("04/04");

	private static final LocalDate today = LocalDate.now();

	public static final boolean IS_RAT_BIRTHDAY = LocalDate.of(today.getYear(), 7, 19).compareTo(today) * today.compareTo(LocalDate.of(today.getYear(), 7, 25)) >= 0;

	public static final boolean IS_MISCHIEF_BIRTHDAY = (LocalDate.of(today.getYear(), 12, 28).compareTo(today) * today.compareTo(LocalDate.of(today.getYear(), 12, 31)) >= 0)
			|| (LocalDate.of(today.getYear(), 1, 1).compareTo(today) * today.compareTo(LocalDate.of(today.getYear(), 1, 3)) >= 0);

	public static final boolean IS_BIRTHDAY = IS_RAT_BIRTHDAY || IS_MISCHIEF_BIRTHDAY;
}
