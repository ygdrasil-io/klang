

package jclang.util;

import org.jetbrains.annotations.NotNull;

public class Util {
    private Util() {}

    public static <E extends Enum<E>> int buildOptionsMask(@NotNull E... values) {
        int result = 0;
        for (E value : values) {
            result |= 1 << value.ordinal();
        }
        return result;
    }
}
