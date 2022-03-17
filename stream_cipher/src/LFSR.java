public class LFSR {

    int state;      // The state of this LFSR
    int mask;       // Get the bits to xor
    int highestBit; // Record the highest bit

    public LFSR(int initState, int[] coeffs) {
        /**
         * Init a LFSR with initState and coeffs
         */
        this.state = initState;
        this.mask = 0;
        this.highestBit = 0;
        for (int coeff: coeffs) {
            this.mask |= (1 << coeff) >> 1;
            this.highestBit = Math.max(this.highestBit, coeff);
        }
    }

    public int getNextBit() {
        /**
         * Get the next bit in the stream
         */
        int temp = mask & state, newBit = temp & 1;
        for (int i = 1; i < highestBit; i++) {
            temp >>= 1;
            newBit ^= temp & 1;
        }
        state <<= 1;
        int res = state >> highestBit;
        state = (state | newBit) & ((1 << highestBit) - 1);
        return res;
    }

    @Override
    public String toString() {
        return "State: " + Integer.toBinaryString(state)
                + "; Mask: " + Integer.toBinaryString(mask)
                + "; Highest Bit: " + highestBit;
    }



}
