# caesar cipher program
# enter messag and step and receive encoded or decoded message

uppercases = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
        'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
lowercases = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
        'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']



def main():
    to_encode = ''
    while to_encode != 'e' and to_encode != 'd':
        to_encode = input('e for encode, d for decode: ')
    step = int(input("step: ")) if to_encode == 'e' else 0 - int(input("step: "))
    msg = input("message: ")
    encoded = ""
    for char in msg:
        encoded += swapChar(char, step) 
    print(encoded)

def swapChar(char, step):
    usingArr = []
    if char.isalpha():
        usingArr = uppercases.copy() if char.isupper() else lowercases.copy()
        i = 0
        i = usingArr.index(char)
        i = (i + step) % 26 # in case step + i goes above max index      
        return usingArr[i]
    return char

if __name__ == "__main__":
    main()
