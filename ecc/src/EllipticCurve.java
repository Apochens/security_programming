import java.math.BigInteger;

public class EllipticCurve {

    BigInteger a;
    BigInteger b;
    BigInteger p;

    public EllipticCurve(BigInteger _a, BigInteger _b, BigInteger _p) {
        a = _a;
        b = _b;
        p = _p;
    }

    private boolean isEqual(ECPoint p1, ECPoint p2) {
        return p1.x.equals(p2.x) && p1.y.equals(p2.y);
    }

    private boolean isNegate(ECPoint p1, ECPoint p2) {
        return p1.x.equals(p2.x) && p1.y.add(p2.y).mod(this.p).equals(BigInteger.ZERO);
    }

    private ECPoint computeECPoint(BigInteger lambda, ECPoint p1, ECPoint p2) {
        BigInteger x = lambda.multiply(lambda).subtract(p1.x).subtract(p2.x).mod(this.p);
        BigInteger y = lambda.multiply(p1.x.subtract(x)).subtract(p1.y).mod(this.p);
        return new ECPoint(x, y);
    }

    public ECPoint addECPoint(ECPoint p1, ECPoint p2) {
        ECPoint result = null;
        if (p1.isO() && p2.isO()) {
            result = ECPoint.ZERO;
        } else if (p1.isO()) {
            result = p2;
        } else if (p2.isO()) {
            result = p1;
        } else {
            if (isEqual(p1, p2)) {
                // lambda = (3 * x1^2 + a) / (2 * y1)
                BigInteger lambda = p1.x.multiply(p1.x).multiply(new BigInteger("3")).add(this.a).multiply(
                        p1.y.multiply(new BigInteger("2")).modInverse(this.p)
                ).mod(this.p);

                result = computeECPoint(lambda, p1, p2);
            } else if (isNegate(p1, p2)) {
                result = ECPoint.ZERO;
            } else {
                BigInteger lambda = p1.y.subtract(p2.y).multiply(
                        p1.x.subtract(p2.x).modInverse(this.p)
                ).mod(this.p);

                result = computeECPoint(lambda, p1, p2);
            }
        }
        assert onCurve(result);
        return result;
    }

    public ECPoint dotMul(BigInteger times, ECPoint p) {
        ECPoint res = new ECPoint(p.x, p.y);
        assert onCurve(res);
        for (BigInteger i = new BigInteger("1"); !i.equals(times); i = i.add(BigInteger.ONE)) {
//            System.out.println(i + ": " + res.toString());
//            System.out.println(onCurve(res));
            res = addECPoint(res, p);
        }
        return res;
    }

    public boolean onCurve(ECPoint p) {
        return p == ECPoint.ZERO || p.y.modPow(new BigInteger("2"), this.p).equals(
                p.x.modPow(new BigInteger("3"), this.p).add(p.x.multiply(this.a)).add(this.b).mod(this.p)
        );
    }

}
