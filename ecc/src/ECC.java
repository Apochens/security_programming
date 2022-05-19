import java.math.BigInteger;
import java.util.Random;

public class ECC {

    public static void main(String[] args) {
        BigInteger a = new BigInteger("20");
        BigInteger b = new BigInteger("4");
        BigInteger p = new BigInteger("29");

        EllipticCurve ec = new EllipticCurve(a, b, p);
        ECPoint g = new ECPoint(new BigInteger("13"), new BigInteger("24"));
        System.out.println("[ ECC ] The generator g is: " + g);
        assert ec.onCurve(g);

        /* PRIVATE KEY */
        BigInteger privateKey = BigInteger.valueOf(new Random().nextInt(4) + 1);
        System.out.println("[ ECC ] The private key s is: " + privateKey);

        /* PUBLIC KEY */
        ECPoint publicKey = ec.dotMul(privateKey, g);
        assert ec.onCurve(publicKey);
        System.out.println("[ ECC ] The public key p is: " + publicKey.toString());

        /* ENCRYPTION */
        ECPoint message = ec.addECPoint(g, g);
        System.out.println("[ ECC ] The message m is: " + message);

        BigInteger k = BigInteger.valueOf(3);
        System.out.println("[ ECC ] The random k is: " + k);

        ECPoint ciphertext1 = ec.dotMul(k, g);   // kg
        ECPoint ciphertext2 = ec.addECPoint(message, ec.dotMul(k, publicKey)); // m+kq
        System.out.println("[ ECC ] The ciphertext (kg, m+kp) is: [" + ciphertext1 + ", " + ciphertext2 + "]");

        /* DECRYPTION */
        ECPoint recoveredMessage = ec.addECPoint(ciphertext2, ec.dotMul(privateKey, ciphertext1).negate(p));
        System.out.println("[ ECC ] The recovered message is: " + recoveredMessage);

    }

}
