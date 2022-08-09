#transposition cipher program
import math
def main():
    message = input("message: ")
    col_size = int(input("col size: "))
    en_or_de = input("e for encrypt, d for decrypt: ")
    print('*' * 50)
    print("original  message: ", message)
    
    print("encrypted message: ", encrypt(message, col_size)) if(en_or_de == 'e') else print("decrypted message: ", decrypt(message, col_size))

def encrypt(message, col_size):
    cols = []
    encrypted = ''
    while message:
        cols.append(message[:col_size])
        message = message[col_size:]
    for i in range(len(cols[0])):
        encrypted += vertical_add(cols, i)
    return encrypted

def decrypt(message, col_size):
    result = ''
    col = 0
    row = 0
    num_cols = math.ceil(len(message) / col_size)
    decrypted = [''] * num_cols
    num_blanks = num_cols * col_size - len(message)
    for c in message:
        decrypted[col] += c
        col += 1
        if(col == num_cols - 1 and row >= col_size - num_blanks) or (col == num_cols):
            row += 1
            col = 0
    for col_result in decrypted:
        result += col_result
    return result


def vertical_add(cols, i):
    result = ''
    for col in cols:
        if(len(col) > i):
            result += col[i]
    return result

if __name__ == "__main__":
    main()
