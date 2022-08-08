#transposition cipher program

def main():
    message = input("message: ")
    col_size = int(input("col size: "))
    print('*' * 50)
    print("original  message: ", message)
    print("encrypted message: ", encrypt(message, col_size)) 

def encrypt(message, col_size):
    cols = []
    encrypted = ''
    while message:
        cols.append(message[:col_size])
        message = message[col_size:]
    for i in range(len(cols[0])):
        encrypted += vertical_add(cols, i)
    return encrypted

def vertical_add(cols, i):
    result = ''
    for col in cols:
        if(len(col) > i):
            result += col[i]
    return result

if __name__ == "__main__":
    main()
