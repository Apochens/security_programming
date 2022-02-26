use std::ops::{Add, Sub, Div, Mul, Index, IndexMut, Shl, Shr};

#[derive(Debug, Clone)]
pub struct BigUint(Vec<u64>);

impl BigUint {
    pub fn zero() -> Self {
        BigUint(vec![0u64; 5])
    }
}

impl From<Vec<u64>> for BigUint {
    fn from(vec: Vec<u64>) -> Self {
        assert!(vec.len() == 5);
        BigUint(vec)
    }
}

impl PartialEq for BigUint {
    fn eq(&self, other: &Self) -> bool {
        self.0 == other.0
    }
}

impl PartialOrd for BigUint {
    fn partial_cmp(&self, other: &Self) -> Option<std::cmp::Ordering> {
        for i in (0..5).rev() {
            if self[i] > other[i] {
                return Some(std::cmp::Ordering::Greater);
            } 
            if self[i] < other[i] {
                return Some(std::cmp::Ordering::Less);
            }
        }
        Some(std::cmp::Ordering::Equal)
    }
}

impl Index<usize> for BigUint {
    type Output = u64;

    fn index(&self, index: usize) -> &Self::Output {
        assert!(index < 5);
        &(self.0)[index]
    }
}

impl IndexMut<usize> for BigUint {
    fn index_mut(&mut self, index: usize) -> &mut Self::Output {
        &mut (self.0)[index]
    }
}

impl Add<&BigUint> for BigUint {
    type Output = BigUint;

    fn add(self, rhs: &BigUint) -> Self::Output {
        let mut res = BigUint::zero();
        let mut carry = 0u64;
        for i in 0..5 {
            res[i] = self[i] + rhs[i] + carry;
            carry = res[i] >> 32;
            res[i] = res[i] ^ (carry << 32);
        }
        res
    }
}

impl Sub<&BigUint> for BigUint {
    type Output = BigUint;

    fn sub(self, rhs: &Self) -> Self::Output {
        let mut res = BigUint::zero();
        let mut borrow = 0u64;

        for i in 0..5 {
            if self[i] >= rhs[i] + borrow {
                res[i] = self[i] - rhs[i] - borrow;
                borrow = 0;
            } else {
                res[i] = self[i] + (1u64 << 32) - rhs[i] - borrow;
                borrow = 1;
            }
        }

        res
    }
}

impl Div<&BigUint> for BigUint {
    type Output = BigUint;

    fn div(self, rhs: &Self) -> Self::Output {
        let mut quotient = BigUint::zero();
        let one = BigUint::from(vec![1u64, 0u64, 0u64, 0u64, 0u64]);

        if self < *rhs {
            return quotient;
        }
        if self == *rhs {
            return one;
        }

        let mut dividend = self;
        let mut divisor = (*rhs).clone();
        let mut count = 0;

        while dividend >= divisor {
            divisor = divisor << 1;
            count += 1;
        }

        divisor = divisor >> 1;

        for _ in 1..=count {
            while dividend >= divisor && dividend != BigUint::zero() {
                dividend = dividend - &divisor;
                quotient = quotient + &one;
            }
            divisor = divisor >> 1;
            quotient = quotient << 1;
            println!("dividend: {:?}, divisor: {:?}, quotient: {:?}", &dividend, &divisor, &quotient);
        }

        quotient >> 1
    }
}

impl Mul<&BigUint> for BigUint {
    type Output = BigUint;

    fn mul(self, rhs: &Self) -> Self::Output {
        let mut product = BigUint::zero();

        product
    }
}

impl Shl<usize> for BigUint {
    type Output = BigUint;

    fn shl(self, rhs: usize) -> Self::Output {
        let mut carry = 0u64;
        let mut res = BigUint::zero();
        for i in 0..5 {
            res[i] = (self[i] << rhs) + carry;
            carry = res[i] >> 32;
            res[i] = res[i] ^ (carry << 32);
        }
        res
    }
}

impl Shr<usize> for BigUint {
    type Output = BigUint;

    fn shr(self, rhs: usize) -> Self::Output {
        let mark = (1u64 << rhs) - 1;
        let mut remain = 0u64;
        let mut res = BigUint::zero();
        for i in (0..5).rev() {
            let temp = remain;
            remain = (self[i] & mark) << (32 - rhs);
            res[i] = (self[i] >> rhs) + temp;
        }
        res
    }
}


#[cfg(test)]
mod test {
    use crate::BigUint;
    #[test]
    fn test_sub() {
        let num1 = BigUint::from(vec![0u64, 0u64, 0u64, 0u64, 1u64]);
        let num2 = BigUint::from(vec![1u64, 0u64, 0u64, 0u64, 0u64]);

        let difference = BigUint::from(vec![(1u64 << 32) - 1, (1u64 << 32) - 1, (1u64 << 32) - 1, (1u64 << 32) - 1, 0u64]);

        assert_eq!(num1 - &num2, difference);
    }

    #[test]
    fn test_add() {
        let num1 = BigUint::from(vec![(1u64 << 32) - 1, (1u64 << 32) - 1, (1u64 << 32) - 1, (1u64 << 32) - 1, 0u64]);
        let num2 = BigUint::from(vec![1u64, 0u64, 0u64, 0u64, 0u64]);

        let sum = BigUint::from(vec![0u64, 0u64, 0u64, 0u64, 1u64]);

        assert_eq!(num1 + &num2, sum);

        let num3 = BigUint::from(vec![0u64, 0u64, 0u64, 0u64, (1u64 << 32) - 1]);
        let num4 = BigUint::from(vec![0u64, 0u64, 0u64, 0u64, 1u64]);

        assert_eq!(num3 + &num4, BigUint::zero());
    }

    #[test]
    fn test_div() {
        let num1 = BigUint::from(vec![0u64, 1u64, 0u64, 0u64, 0u64]);
        let num2 = BigUint::from(vec![2u64, 0u64, 0u64, 0u64, 0u64]);

        assert_eq!(num1 / &num2, BigUint::from(vec![1u64 << 31, 0u64, 0u64, 0u64, 0u64]));
    }

    #[test]
    fn test_shl_and_shr() {
        let num1 = BigUint::from(vec![0u64, 0u64, 1u64, 0u64, 0u64]);
        let num2 = BigUint::from(vec![1u64, 0u64, 0u64, 0u64, 0u64]);
        let res = BigUint::from(vec![0u64, 1u64, 0u64, 0u64, 0u64]);

        assert_eq!(num1 >> 32, res);
        assert_eq!(num2 << 32, res);
    }
}





// use std::{fmt::Display, ops::{Add, Rem, Sub, Div}, cmp::Ordering};

// #[derive(PartialEq, Eq, Debug, Clone)]
// pub struct BigUint {
//     data: Vec<u8>,
// }

// impl BigUint {

//     /// Create a new zero BigUint with security_parameter long
//     pub fn zero(security_parameter: usize) -> Self {
//         BigUint { data: vec![0; security_parameter] }
//     }

//     pub fn new(num_string: String, len: usize) -> Self {
//         let mut num = BigUint::zero(len);
//         num.set(num_string);
//         num
//     }

//     /// Set BigUint to a specific number within set length
//     pub fn set(&mut self, num_string: String) {
//         assert!(num_string.len() <= self.data.len());
//         num_string.chars().rev().fold(0, |acc, c| {
//             if c.is_ascii_digit() {
//                 self.data[acc] = c as u8 - '0' as u8;
//                 acc + 1
//             } else {
//                 panic!("Non-digit number char in input number string");
//             }
//         });
//     }

//     pub fn len(&self) -> usize {
//         self.data.len()
//     }

//     pub fn highest_bit(&self) -> Option<usize> {
//         for i in 0..self.data.len() {
//             if self.data[i] == 0 {
//                 if i == 0 {
//                     return None;
//                 } else {
//                     return Some(i + 1);
//                 }
//             }
//         }

//         Some(self.data.len())
//     }
// }

// impl Add for BigUint {

//     type Output = Self;

//     fn add(self, other: Self) -> Self {
//         let mut overflow = 0u8;
//         let mut sum = BigUint::zero(self.len());

//         for i in 0..self.data.len() {
//             let temp_sum = self.data[i] + other.data[i] + overflow;
//             sum.data[i] = temp_sum % 10;
//             overflow = temp_sum / 10;
//         }

//         if overflow != 0 {
//             panic!("BigUint addition overflows!");
//         }

//         sum
//     } 
// }

// impl Sub for BigUint {
//     type Output = Self;

//     fn sub(self, other: Self) -> Self::Output {
//         let mut difference = BigUint::zero(self.len());
//         let mut borrow = 0u8;
        
//         let (minuend, subtrahend) = if self > other {
//             (self, other)
//         } else {
//             (other, self)
//         };

//         for i in 0..minuend.len() {
//             if minuend.data[i] < subtrahend.data[i] + borrow {
//                 difference.data[i] = minuend.data[i] + 10 - subtrahend.data[i] - borrow;
//                 borrow = 1;
//             } else {
//                 difference.data[i] = minuend.data[i] - subtrahend.data[i] - borrow;
//                 borrow = 0;
//             }
//         }

//         difference
//     }
// }

// impl Div for BigUint {
//     type Output = Self;

//     fn div(self, other: Self) -> Self::Output {
//         let mut divisor = self;
//         let mut quotient = BigUint::zero(divisor.len());
//         while divisor > other || divisor == other {
//             divisor = divisor - other.clone();
//             quotient = quotient + BigUint::new(1.to_string(), divisor.len());
//         }

//         quotient
//     }
// }

// impl Rem for BigUint {
//     type Output = Self;

//     fn rem(self, other: Self) -> Self::Output {
//         let mut remainder = BigUint::zero(self.len());
        
//         remainder
//     }
// }

// impl PartialOrd for BigUint {
//     fn partial_cmp(&self, other: &Self) -> Option<Ordering> {

//         let hb_self = self.highest_bit();
//         let hb_other = self.highest_bit();

//         match (hb_self, hb_other) {
//             (None, None) => Some(Ordering::Equal),
//             (None, Some(_)) => Some(Ordering::Less),
//             (Some(_), None) => Some(Ordering::Greater),
//             (Some(hb_1), Some(hb_2)) => {
//                 if hb_1 > hb_2 {
//                     Some(Ordering::Greater)
//                 } else if hb_1 < hb_2 {
//                     Some(Ordering::Less)
//                 } else {
//                     for i in (0..hb_1).rev() {
//                         if self.data[i] > other.data[i] {
//                             return Some(Ordering::Greater);
//                         }
//                         if self.data[i] < other.data[i] {
//                             return Some(Ordering::Less);
//                         }
//                     }
//                     Some(Ordering::Equal)
//                 }
//             }
//         }

//     }
// }

// // impl Ord for BigUint {
// //     fn cmp(&self, other: &Self) -> Ordering {

// //     }
// // }

// impl Display for BigUint {
//     fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        
//         let mut num_string: String = String::new();
//         for &num in self.data.iter().rev() {
//             num_string.push((num + '0' as u8) as char);
//         }

//         write!(f, "{}", &num_string)
//     }
// }


// #[cfg(test)]
// mod test {
//     use crate::BigUint;

//     #[test]
//     fn test_add() {
//         let mut num1 = BigUint::zero(160);
//         let mut num2 = BigUint::zero(160);
//         num1.set("123456".to_string());
//         num2.set("123".to_string());

//         let mut sum = BigUint::zero(160);
//         sum.set((123456 + 123).to_string());

//         assert_eq!(num1 + num2, sum);
//     }

//     #[test]
//     fn test_sub() {
//         let mut num1 = BigUint::zero(160);
//         let mut num2 = BigUint::zero(160);
//         num1.set("123456".to_string());
//         num2.set("123".to_string());

//         let mut difference = BigUint::zero(160);
//         difference.set((123456 - 123).to_string());

//         assert_eq!(num1 - num2, difference);
//     }

//     #[test]
//     fn test_div_and_rem() {
//         let num1 = BigUint::new(64.to_string(), 160);
//         let num2 = BigUint::new(16.to_string(), 160);

//         assert_eq!(num1 / num2, BigUint::new(4.to_string(), 160));
//     }
// }