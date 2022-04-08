public class RowShifter {

    private static void swapRow2(int[][] packet, int pos1, int pos2) {
        int temp = packet[pos1][2];
        packet[pos1][2] = packet[pos2][2];
        packet[pos2][2] = temp;
    }

    private static void leftShift(int[][] packet, int col) {
        int temp = packet[0][col];
        for (int i = 0; i < 3; i++) {
            packet[i][col] = packet[i+1][col];
        }
        packet[3][col] = temp;
    }

    private static void rightShift(int[][] packet, int col) {
        int temp = packet[3][col];
        for (int i = 3; i > 0; i--) {
            packet[i][col] = packet[i-1][col];
        }
        packet[0][col] = temp;
    }

    public static void shiftPacket(int[][] packet) {

        // Row0 do nothing

        // Row1 << 1
        leftShift(packet, 1);

        // Row2 << 2
        swapRow2(packet, 0, 2);
        swapRow2(packet, 1, 3);

        // Row3 << 3 => Row >> 1
        rightShift(packet, 3);

    }

    public static void revShiftPacket(int[][] packet) {
        // Row0 do nothing

        // Row1 >> 1
        rightShift(packet, 1);

        // Row2 >> 2
        swapRow2(packet, 0, 2);
        swapRow2(packet, 1, 3);

        // Row3 >> 3 => Row3 << 1
        leftShift(packet, 3);
    }

}
