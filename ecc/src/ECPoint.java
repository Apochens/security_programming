import java.math.BigInteger;

public class ECPoint {

    static ECPoint ZERO = new ECPoint();
    BigInteger x;
    BigInteger y;

    private ECPoint() { }

    public ECPoint(BigInteger _x, BigInteger _y) {
        this.x = _x;
        this.y = _y;
    }

    public boolean isO() {
        return ECPoint.ZERO == this;
    }

    public ECPoint negate(BigInteger mod) {
        return new ECPoint(this.x, this.y.negate().mod(mod));
    }

    @Override
    public String toString() {
        if (this == ECPoint.ZERO) return "The O";
        return "(" + this.x + ", " + this.y + ")";
    }
}
