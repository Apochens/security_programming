use num::{Integer, One, ToPrimitive, Zero};
use num_primes::{RandBigInt, BigUint, Generator, Verification};

fn rewrite(candidate: &BigUint) -> (BigUint, BigUint) {
    let mut s = BigUint::zero();
    let one = BigUint::one();
    let two = &one + &one;

    let mut t = candidate - &one;

    while t.is_even() {
        t /= &two;
        s += &one;
    }

    (t, s)
}

/// The Miller-Rabin prime testing
fn miller_rabin(candidate: &BigUint, limit: usize) -> bool {
    let one = BigUint::one();
    let two = &one + &one;

    /* Given the odd number n = candidate, security parameter k = limit (the times of testing) */
    /* rewrite n-1 = t*(2^s) */
    let (t, s) = rewrite(candidate);
    let mut rng = rand::thread_rng();

    for _ in 0..limit {

        /* 1. Get a random number b, 2 <= b <= n-2. */
        let b = rng.gen_biguint_range(&two, &(candidate - &one));

        /* 2. Compute r_0 = b^t mod n. */
        let mut r = b.modpow(&t, &candidate);

        /* 3. If r_0 = 1 or r_0 = n-1, then pass and goto 1.;
            or compute r_(i+1) = r_i ^ 2 mod n from r_1 to r_(s-1) incursively. */
        if r == one || r == candidate - &one {
            continue;
        }

        let mut early_break = false;
        for _ in 1..s.to_usize().unwrap() {
            r = r.modpow(&two, &candidate);

            /* 4. If r_i = n-1 then pass this testing and goto 1.; or continue the loop； */
            if r == one {
                return false;
            }
            if r == candidate - &one {
                early_break = true;
                break;
            }

        }
        
        /* early_break is false means that testing fails for all r_i, so we return false directly. */
        if early_break == false { 
            return false;
        }
        
    }

    true
}

fn main() {

    let mut num= Generator::new_uint(160);

    while miller_rabin(&num, 10) != true {
        num = Generator::new_uint(160);
    }

    if Verification::is_prime(&num) {   // Use Verification to double check
        println!("The 160 bits number passed the Miller-Rabin verifacation is:\n    {}", num);
    } else {
        panic!("Error in generating large prime of 160 bits");
    }
}
