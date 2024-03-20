package ladysnake.ratsmischief.mialeemisc.util;

public class MialeeMath {
	/**
	 * Clamps a value to a range, looping around if the value is outside the range.
	 * Inclusive of the min and exclusive of the max values.
	 * Only works for ints.
	 */
	public static int clampLoop(int input, int start, int end) {
		if (start - end == 0) {
			return start;
		}
		if (end < start) {
			int temp = start;
			start = end;
			end = temp;
		}
		if (input < start) {
			return end - ((start - input) % (end - start));
		}
		return start + ((input - start) % (end - start));
	}

	/**
	 * Clamps a value to a range, looping around if the value is outside the range.
	 * Inclusive of the min and exclusive of the max values.
	 * Use floats instead of ints.
	 */
	public static float clampLoop(float input, float start, float end) {
		if (start - end == 0) {
			return start;
		}
		if (end < start) {
			float temp = start;
			start = end;
			end = temp;
		}
		if (input < start) {
			return end - ((start - input) % (end - start));
		}
		return start + ((input - start) % (end - start));
	}

	/**
	 * Clamps a value to a range, looping around if the value is outside the range.
	 * Inclusive of the min and exclusive of the max values.
	 * Use doubles instead of ints.
	 */
	public static double clampLoop(double input, double start, double end) {
		if (start - end == 0) {
			return start;
		}
		if (end < start) {
			double temp = start;
			start = end;
			end = temp;
		}
		if (input < start) {
			return end - ((start - input) % (end - start));
		}
		return start + ((input - start) % (end - start));
	}
}
