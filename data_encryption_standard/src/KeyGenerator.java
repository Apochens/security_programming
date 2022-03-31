public class KeyGenerator {

    public static final int[] KEY_POS_PERMUTATION = new int[]{
            57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4
    };

    public static final int[] KEY_LEFT_SHIFT = new int[]{
            1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };

    public static final int[] KEY_POS_COMPRESS_PERMUTATION = new int[]{
            14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32
    };

    public static long permuteKey(long key, int[] permutation, int beforeBit, int afterBit) {
        long validKey = 0L;
        for (int i = 0; i < permutation.length; i++) {
            int originalPos = beforeBit - permutation[i];
            validKey = (validKey << 1) | ((key >>> originalPos) & 1L);
        }
        return validKey;
    }

    private static long leftShiftKey(long key, int bit) {
        long maskRight = (1L << 28) - 1;
        long maskLeft = (1L << 56) - 1 - maskRight;
        long shiftedLeftKey = (key & maskLeft) << bit;
        long shiftedRightKey = (key & maskRight) << bit;
        return ((shiftedLeftKey | shiftedLeftKey >>> 28) & maskLeft)
                | ((shiftedRightKey | shiftedRightKey >>> 28) & maskRight);
    }

    public static long[] generateRoundKey(long masterKey, int round) {
        /* Key permutation */
        long permutedKey = permuteKey(masterKey, KEY_POS_PERMUTATION, 64, 56);

        /* Generate the round keys */
        long[] roundKeys = new long[16];
        for (int i = 0; i < 16; i++) {
            /* Key left shift */
            roundKeys[i] = leftShiftKey(permutedKey, KEY_LEFT_SHIFT[i]);
            /* Key compress permutation */
            roundKeys[i] = permuteKey(roundKeys[i], KEY_POS_COMPRESS_PERMUTATION, 56, 48);
        }

        return roundKeys;
    }
}
