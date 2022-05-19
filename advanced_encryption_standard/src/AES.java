import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class AES {

    private static final int PACKET_LENGTH_BY_BYTE = 16;
    private final SecretKey secretKey;

    public static void main(String[] args) {
        AES aes = new AES();
        aes.showSecretKey();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Please input the plaintext: ");
        String plaintext = scanner.nextLine();
        System.out.println(">> Plaintext: " + plaintext);

        String ciphertext = aes.encrypt(plaintext);
        System.out.println(">> Ciphertext: " + ciphertext);

        String recoveredText = aes.decrypt(ciphertext);
        System.out.println(">> Recovered Text with padding: " + recoveredText);
    }

    public AES() {
        secretKey = new SecretKey();
    }

    public List<Packet> stringToPacket(String str) {

        List<Packet> packets = new ArrayList<>();
        int charCount = 0;
        int[] bytes = new int[PACKET_LENGTH_BY_BYTE];

        for (; charCount < str.length(); charCount++) {
            int twoBytes = str.charAt(charCount);
            bytes[charCount * 2 % PACKET_LENGTH_BY_BYTE] = twoBytes >>> 8;
            bytes[charCount * 2 % PACKET_LENGTH_BY_BYTE + 1] = twoBytes & 0xFF;

            if (((charCount * 2) % PACKET_LENGTH_BY_BYTE) + 1 == (PACKET_LENGTH_BY_BYTE - 1)) {
                packets.add(new Packet(bytes));
            }
        }

        // Padding 100000....
        if (charCount * 2 % PACKET_LENGTH_BY_BYTE != 0) {
            bytes[charCount * 2 % PACKET_LENGTH_BY_BYTE] = 0x80;
            bytes[charCount++ * 2 % PACKET_LENGTH_BY_BYTE + 1] = 0;
            while (charCount * 2 % PACKET_LENGTH_BY_BYTE != 0) {
                bytes[charCount * 2 % PACKET_LENGTH_BY_BYTE] = 0;
                bytes[charCount++ * 2 % PACKET_LENGTH_BY_BYTE + 1] = 0;
            }
            packets.add(new Packet(bytes));
        }

        return packets;
    }

    public String packetToString(List<Packet> packets) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Packet packet: packets) {
            stringBuilder.append(packet.toString());
        }
        return stringBuilder.toString();
    }

    private void encryptPacket(Packet packet) {
        // Add round key 0
        packet.addRoundKey(secretKey, 0);

        for (int i = 1; i < SecretKey.ROUND; i++) {
            packet.substitute();
            packet.shift();
            packet.mix();
            packet.addRoundKey(secretKey, i);
        }
        packet.substitute();
        packet.shift();
        packet.addRoundKey(secretKey, 10);
    }

    private void decryptPacket(Packet packet) {
        packet.addRoundKey(secretKey, 10);

        for (int i = 9; i > 0; i--) {
            packet.revShift();
            packet.revSubstitute();
            packet.addRoundKey(secretKey, i);
            packet.revMix();
        }

        packet.revShift();
        packet.revSubstitute();
        packet.addRoundKey(secretKey, 0);
    }

    public String encrypt(String plaintext) {

        List<Packet> packets = stringToPacket(plaintext);

        for (Packet packet: packets) {
            encryptPacket(packet);
        }

        return packetToString(packets);
    }

    public String decrypt(String ciphertext) {

        List<Packet> packets = stringToPacket(ciphertext);

        for (Packet packet: packets) {
            decryptPacket(packet);
        }

        return packetToString(packets);
    }

    public void showSecretKey() {
        System.out.println(this.secretKey);
    }
}
