import java.util.Random;

public class SecretKey {

    public static final int ROUND = 10;
    public static final int WORD_LENGTH = 4;
    private final int[][] secretKeys;

    private static final int[][] CONSTANT = new int[][]
            {
                    new int[]{0x01, 0x00, 0x00, 0x00},
                    new int[]{0x02, 0x00, 0x00, 0x00},
                    new int[]{0x04, 0x00, 0x00, 0x00},
                    new int[]{0x08, 0x00, 0x00, 0x00},
                    new int[]{0x10, 0x00, 0x00, 0x00},
                    new int[]{0x20, 0x00, 0x00, 0x00},
                    new int[]{0x40, 0x00, 0x00, 0x00},
                    new int[]{0x80, 0x00, 0x00, 0x00},
                    new int[]{0x1B, 0x00, 0x00, 0x00},
                    new int[]{0x36, 0x00, 0x00, 0x00}
            };

    public SecretKey() {

        // init
        Random rng = new Random();
        secretKeys = new int[ROUND * 4 + 4][];
        int wordCount = 0;

        // Randomly generate the master key
        for (; wordCount < 4; wordCount++) {
            secretKeys[wordCount] = new int[4];
            for (int i = 0; i < 4; i++) {
                secretKeys[wordCount][i] = rng.nextInt(1 << 8);
            }
        }

        // Secret key extension
        for (; wordCount < ROUND * 4 + 4; wordCount++) {

            secretKeys[wordCount] = new int[4];
            System.arraycopy(secretKeys[wordCount - 1], 0, secretKeys[wordCount], 0, 4);

            if (wordCount % 4 == 0) {
                rotateWord(secretKeys[wordCount]);
                substituteWord(secretKeys[wordCount]);
                addModule2(secretKeys[wordCount], CONSTANT[wordCount % 4]);
            }

            addModule2(secretKeys[wordCount], secretKeys[wordCount - 3]);
            secretKeys[wordCount] = secretKeys[wordCount];
        }

    }

    public int[] getRoundWord(int round, int pos) {
        return this.secretKeys[round * 4 + pos];
    }

    private void rotateWord(int[] word) {
        assert word.length == WORD_LENGTH;

        int temp = word[0];
        word[0] = word[1];
        word[1] = word[2];
        word[2] = word[3];
        word[3] = temp;
    }

    private void substituteWord(int[] word) {
        assert  word.length == WORD_LENGTH;

        for (int i = 0; i < WORD_LENGTH; i++) {
            word[i] = SBox.byteSubstitution(word[i]);
        }
    }

    public static void addModule2(int[] word1, int[] word2) {
        assert  word1.length == WORD_LENGTH;
        assert  word2.length == WORD_LENGTH;

        for (int i = 0; i < WORD_LENGTH; i++) {
            word1[i] = word1[i] ^ word2[i];
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-------------------------Round keys-------------------------------\n");
        for (int i = 0; i < ROUND + 1; i++) {
            stringBuilder.append("[").append(i).append("]: ");
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    stringBuilder.append(secretKeys[i * 4 + j][k]).append(" ");
                }
            }
            stringBuilder.append('\n');
        }
        stringBuilder.append("-----------------------------------------------------------------");
        return stringBuilder.toString();
    }
}
