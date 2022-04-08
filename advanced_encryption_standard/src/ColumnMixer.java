public class ColumnMixer {

    public static final int[][] MIX_ARRAY = new int[][]
            {
                    new int[]{0x02, 0x03, 0x01, 0x01},
                    new int[]{0x01, 0x02, 0x03, 0x01},
                    new int[]{0x01, 0x01, 0x02, 0x03},
                    new int[]{0x03, 0x01, 0x01, 0x02}
            };

    public static final int[][] MIX_ARRAY_REVERSE = new int[][]
            {
                    new int[]{0x0E, 0x0B, 0x0D, 0x09},
                    new int[]{0x09, 0x0E, 0x0B, 0x0D},
                    new int[]{0x0D, 0x09, 0x0E, 0x0B},
                    new int[]{0x0B, 0x0D, 0x09, 0x0E}
            };

    private static int leftShiftMod(int b, int coff) {
        while (coff-- > 0) {
            b = b << 1;
            int high = (b >>> 8) & 1;
            if (high == 1) b ^= 0x1B;
            b &= 0xff;
        }
        return b;
    }

    private static int multipleByte(int b1, int b2) {
        int coff = 0;
        int res = 0;
        while (b1 != 0) {
            if ((b1 & 1) == 1) {
                res ^= leftShiftMod(b2, coff);
            }
            coff++;
            b1 >>>= 1;
        }
        return res;
    }

    private static int manipulateWord(int[] word1, int[] word2) {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res ^= multipleByte(word1[i], word2[i]);
        }
        return res;
    }

    private static int[][] mixCore(int[][] packet, int[][] mixTable) {
        int[][] res = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                res[i][j] = manipulateWord(mixTable[j], packet[i]);
            }
        }
        return res;
    }

    public static int[][] mixColumn(int[][] packet) {
        return mixCore(packet, MIX_ARRAY);
    }

    public static int[][] revMixColumn(int[][] packet) {
        return mixCore(packet, MIX_ARRAY_REVERSE);
    }
}
