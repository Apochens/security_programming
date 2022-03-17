### How to use
Open this directory using IDEA and compile to run.

### Structure
- `LFSR.java`: simple implementation of LFSR.
- `StreamCipher.java`: main sourcre code including main function and an implementation of stream cipher with *Enc* and *Dec*.

### Related parameters
- `initState`: the initial state of LFSR in StreamCipher. (No more than 32 bits and use decimal number to represent binary number)
- `coeffs`: the f(x) represent the structure of LFSR. (E.g, [0, 3, 4] represents `f(x) = x^4 + x^3 + 1`)
- `bitNumber`: to encrypt bitNumber bits for one time. (Default 16 bit, because Java uses Unicode.)