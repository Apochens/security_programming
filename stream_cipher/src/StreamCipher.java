import java.util.Scanner;

public class StreamCipher {

    private final LFSR encLfsr;
    private final LFSR decLfsr;
    private final int bitNumber;

    public static void main(String[] args) {

        int initState = 1; // Initial lfsr state is 0001 (use decimal to represent binary).
        int[] coeffs = new int[]{0, 3, 4}; // Represent f(x) = x^4 + x^3 + 1.
        int bitNumber = 16; // Using 16 as a block because that Java uses Unicode.

        StreamCipher streamCipher = new StreamCipher(initState, coeffs, bitNumber);
        System.out.println(streamCipher);

        Scanner scanner = new Scanner(System.in);

        System.out.print("Please input the message you want to encrypt: ");
        String msg = scanner.nextLine();
        System.out.println("The original message:\n>> " + msg);

        String ct = streamCipher.enc(msg);
        System.out.println("The ciphertext:\n>> " + ct);

        String reMsg = streamCipher.dec(ct);
        System.out.println("The message recovered is:\n>> " + reMsg);
    }

    public StreamCipher(int initArray, int[] coeffs, int _bitNumber) {
        this.encLfsr = new LFSR(initArray, coeffs.clone());
        this.decLfsr = new LFSR(initArray, coeffs.clone());
        this.bitNumber = _bitNumber;
    }

    private int getMask(LFSR lfsr, int bitNumber) {
        /**
         * Get bitNumber bits key once
         */
        int mask = 0;
        for (int i = 0; i < bitNumber; i++) {
            mask |= lfsr.getNextBit();
            mask <<= 1;
        }
        return mask;
    }

    public String enc(String msg) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < msg.length(); i++) {
            stringBuilder.append((char) (getMask(this.encLfsr, this.bitNumber) ^ msg.charAt(i)));
        }
        return stringBuilder.toString();
    }

    public String dec(String cipher) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < cipher.length(); i++) {
            stringBuilder.append((char) (getMask(this.decLfsr, this.bitNumber) ^ cipher.charAt(i)));
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "The LFSR used to Enc: (" + this.encLfsr.toString()
                + ")\nThe LFSR used to Dec: (" + this.decLfsr.toString() + ")";
    }

}
