import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RSA {

    static class PrivateKey {
        BigInteger p;
        BigInteger q;
        BigInteger d;

        public PrivateKey(BigInteger p, BigInteger q, BigInteger d) {
            this.p = p;
            this.q = q;
            this.d = d;
        }
    }

    static class PublicKey {
        BigInteger n;
        BigInteger e;

        public PublicKey(BigInteger n, BigInteger e) {
            this.n = n;
            this.e = e;
        }
    }

    static class KeyPair {
        PublicKey publicKey;
        PrivateKey privateKey;

        public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }


    public static void main(String[] args) {

        /* Initialization */
        RSA rsa = new RSA();
        Random rng = new Random();

        /* Generate the key pair */
        KeyPair keyPair = rsa.genKeyPair(rng);

        /* Plaintext */
        String plaintext = "math";
        System.out.println("[ Plaintext ] " + plaintext);

        /* Encoding */
        BigInteger ciphertext = rsa.encMessage(plaintext, keyPair.publicKey);
        System.out.println("[ Ciphertext ]\n " + new String(ciphertext.toByteArray(), StandardCharsets.US_ASCII));

        /* Decoding */
        String recoveredText = rsa.decMessage(ciphertext, keyPair.privateKey);
        System.out.println("[ Recovered Text ] " + recoveredText);

    }

    private BigInteger genE(BigInteger z, Random rng) {
        BigInteger e = new BigInteger(2048, rng);
        while (e.compareTo(BigInteger.ONE) == 0 || e.compareTo(z) >= 0 || e.gcd(z).compareTo(BigInteger.ONE) != 0) {
            e = new BigInteger(2048, rng);
        }
        return e;
    }

    /**
     * Use generalized Euclidean algorithm to compute d
     * @param e: e of the private key
     * @param z: phi(p * q)
     * @return the d of the public key
     */
    private BigInteger genD(BigInteger e, BigInteger z) {

        List<BigInteger> s = new ArrayList<>();
        s.add(new BigInteger("1"));
        s.add(new BigInteger("0"));

        List<BigInteger> t = new ArrayList<>();
        t.add(new BigInteger("0"));
        t.add(new BigInteger("1"));

        BigInteger[] list = z.divideAndRemainder(e); // (quotient, remainder)
        BigInteger quotient = list[0];

        BigInteger a = new BigInteger(e.toString());
        BigInteger b = list[1];

        while (b.compareTo(BigInteger.ZERO) != 0) {

            int lenS = s.size();
            s.add(
                    s.get(lenS - 2).subtract(
                            s.get(lenS - 1).multiply(quotient)
                    )
            );

            int lenT = t.size();
            t.add(
                    t.get(lenT - 2).subtract(
                            t.get(lenT - 1).multiply(quotient)
                    )
            );

            /* Update the variable */
            list = a.divideAndRemainder(b);
            quotient = list[0];
            a = b;
            b = list[1];

        }

        return t.get(t.size() - 1);
    }

    public KeyPair genKeyPair(Random rng) {

        /* Generate 1024 bit prime */
        BigInteger p = new BigInteger(1024, 4, rng);
        BigInteger q = new BigInteger(1024, 4, rng);

        BigInteger n = p.multiply(q);
        BigInteger phiN = p.subtract(new BigInteger("1")).multiply(q.subtract(new BigInteger("1")));
        BigInteger e = genE(phiN, rng);
        BigInteger d = genD(e, phiN);       // Can use this to replace: BigInteger d = e.modInverse(phiN);

        System.out.println("[ Check ed (mod phiN) ] " + e.multiply(d).mod(phiN));

        PrivateKey privateKey = new PrivateKey(p, q, d);
        PublicKey publicKey = new PublicKey(n, e);
        return new KeyPair(publicKey, privateKey);
    }

    public BigInteger encMessage(String plaintext, PublicKey key) {
        return new BigInteger(plaintext.getBytes(StandardCharsets.US_ASCII)).modPow(key.e, key.n);
    }

    public String decMessage(BigInteger c, PrivateKey key) {
        return new String(c.modPow(key.d, key.p.multiply(key.q)).toByteArray(), StandardCharsets.US_ASCII);
    }
}
