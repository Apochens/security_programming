## Advanced Encryption Standard (AES)

### Modules
- **AES**: The main module to show this implementation of AES
- **ColumnMixer**: To implement the function of mixing columns of the packet
- **Packet**: The implementation of the message packet
- **RowShifter**: To implement the function of shifting rows of the packet
- **SBox**: The implementation of S-Box
- **SecretKey**: The implementation of the secret key including _randomly_ generating the master secret key and all round keys 

### About the recovered text
Due to the padding (0x8000...), the recovered text may be some unexpected characters after the original plaintext. This is easy to handle the extra chars by removing the suffix starting with byte 0x80.