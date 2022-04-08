import com.sun.rowset.internal.Row;

public class Packet {

    public static final int PACKET_LENGTH = 16;
    private int[][] packet;

    public Packet(int[] bytes) {
        assert bytes.length == PACKET_LENGTH;

        this.packet = new int[4][4];
        for (int i = 0; i < PACKET_LENGTH; i++) {
            this.packet[i / 4][i % 4] = bytes[i];
        }
    }

    public void addRoundKey(SecretKey secretKey, int round) {
        for (int i = 0; i < 4; i++) {
            SecretKey.addModule2(packet[i], secretKey.getRoundWord(round, i));
        }
    }

    public void substitute() {
        for (int[] word: this.packet) {
            for (int i = 0; i < word.length; i++) {
                word[i] = SBox.byteSubstitution(word[i]);
            }
        }
    }

    public void revSubstitute() {
        for (int[] word: this.packet) {
            for (int i = 0; i < word.length; i++) {
                word[i] = SBox.byteSubstitutionReverse(word[i]);
            }
        }
    }

    public void shift() {
        RowShifter.shiftPacket(this.packet);
    }

    public void revShift() {
        RowShifter.revShiftPacket(this.packet);
    }

    public void mix() {
        this.packet = ColumnMixer.mixColumn(this.packet);
    }

    public void revMix() {
        this.packet = ColumnMixer.revMixColumn(this.packet);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] word: packet) {
            stringBuilder.append((char)((word[0] << 8) + word[1]));
            stringBuilder.append((char)((word[2] << 8) + word[3]));
        }
        return stringBuilder.toString();
    }
}
