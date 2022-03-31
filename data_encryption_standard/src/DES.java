import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DES {

    public static final int PACKET_SIZE = 64;
    public static final int KEY_LENGTH = 56;
    public static final int ROUND = 16;
    private final long masterKey;
    private final long[] encKeys;
    private final long[] decKeys;

    public static final int[] POS_PERMUTATION = new int[]{
            58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7};

    public static final int[] POS_REVERSE = new int[]{
            40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25};

    public static final int[] POS_EXPANSION = new int[]{
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };

    public static final int[] P_BOX = new int[] {
            16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25
    };

    public static void main(String[] args) {

        long masterKey = 0x012345678;
        System.out.format("The master key is: %s\nPlease input the messages: ", masterKey);
        DES des = new DES(masterKey);

        Scanner scanner = new Scanner(System.in);
        String plainText = scanner.nextLine();
        System.out.format("The original messages is: %s\n", plainText);

        String cipherText = des.encodeString(plainText);
        System.out.format("The ciphertext is: %s\n", cipherText);

        String recoveredText = des.decodeString(cipherText);
        System.out.format("The recovered plaintext is: %s\n", recoveredText);
    }

    public DES(long key) {
        this.masterKey = key;
        this.encKeys = KeyGenerator.generateRoundKey(this.masterKey, ROUND);
        long[] temp = new long[this.encKeys.length];
        for (int i = 0; i < temp.length; i++) {
            temp[i] =  this.encKeys[temp.length - 1 - i];
        }
        this.decKeys = temp;
    }

    /** Round function */
    private long roundFn(long message, long key) {
        /** Extension permutation and key xor */
        message = KeyGenerator.permuteKey(message, POS_EXPANSION, 32, 48) ^ key;
        /** S-Box replacement */
        message = SBox.sBoxTransition(message);
        /** P-Box permutation */
        message = KeyGenerator.permuteKey(message, P_BOX, 32, 32);
        return message;
    }

    /** Convert string to packets (64 bit) */
    private long[] stringToPacket(String str) {
        long[] packets = new long[(str.length() / 4) + (str.length() % 4 == 0 ? 0 : 1)];
        long packet = 0;
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            packet = (packet << 16) | (long) str.charAt(i);
            if (i == str.length() - 1 && i % 4 != 3) {
                packet <<= (3 - (i % 4)) * 16;
                packets[count] = packet;
            }
            if (i % 4 == 3) {
                packets[count++] = packet;
                packet = 0;
            }
        }
        return packets;
    }

    /** Convert packets (64 bit) to string */
    private String packetToString(long[] packets) {
        long mask = (1L << 16) - 1;
        StringBuilder stringBuilder = new StringBuilder();
        for (long packet: packets) {
            int shift = 48;
            for (int i = 0; i < 4; i++) {
                stringBuilder.append((char) ((packet >>> shift) & mask));
                shift -= 16;
            }
        }
        return stringBuilder.toString();
    }

    /** Encryption and decryption module */
    private long encAndDec(long packet, long[] keys) {

        /* Initial permutation */
        packet = KeyGenerator.permuteKey(packet, POS_PERMUTATION, PACKET_SIZE, PACKET_SIZE);
        long left = packet >>> 32;
        long right = packet & ((1L << 32) - 1);

        /* 16 time round function */
        for (int i = 0; i < ROUND; i++) {
            long tempRight = right;
            right = left ^ roundFn(right, keys[i]);
            left = tempRight;
        }

        /* Swap the left and right */
        packet = (right << 32) | left;

        /* The reverse of initial permutation*/
        packet = KeyGenerator.permuteKey(packet, POS_REVERSE, 64, 64);

        return packet;
    }

    /** Encryption function */
    public String encodeString(String plaintext) {

        /* Turn plaintext to packets*/
        long[] packets = stringToPacket(plaintext);

        /* Encryption by packet */
        for (int i = 0; i < packets.length; i++) {
            packets[i] = encAndDec(packets[i], this.encKeys);
        }

        /* Turn packets to string and return it */
        return packetToString(packets);
    }

    /** Decryption function */
    public String decodeString(String ciphertext) {

        /* Turn ciphertext to packets*/
        long[] packets = stringToPacket(ciphertext);

        /* Decryption by packet */
        for (int i = 0; i < packets.length; i++) {
            packets[i] = encAndDec(packets[i], this.decKeys);
        }

        /* Turn packets to string and return it */
        return packetToString(packets);
    }
}
