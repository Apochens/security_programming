## Data Encryption Standard (DES) Algorithm

### 1. Basic Parameter
* PACKET_SIZE: 64 bit
* KEY_LENGTH: 56 bit
* ROUND: 16 times
* POS_PERMUTATION & POS_REVERSE: arrays used to complete the *permutation* before and after encryption, respectively.

### 2. Workflow
- Follow the DES workflow

### 3. Module
* DES: The main module the implement DES algorithm
* KeyGenerator: To generate round keys used in DES through the master key
* SBox: Implementation of S-Box in DES
