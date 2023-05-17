# -*- coding: utf-8 -*-
import sys
import re

a=''

for i in range(0,len(sys.argv)):
    a += str(sys.argv[i])
    a+='\n'


operators = {'=' : 'Assignment op','+' : 'Addition op','-' : 'Subtraction op','*' : 'Multiplication op','<' : 'Lessthan op','>' : 'Greaterthan op','%':'Mod op',
'<=':'Lessthanorequal op','>=':'Greaterthanorequal op','==':'Equal op','!=':'Different op','(':'Parentright op',')':'Parentleft op','{':'Leftbracket op','}':'Rightbracket op', 
'++':'Increment op','--':'Decrement op' }
operators_key = operators.keys()

punctuation_symbol = { ':' : 'colon', ';' : 'semi-colon', '.' : 'dot' , ',' : 'comma' }
punctuation_symbol_key = punctuation_symbol.keys()

palabras_reservadas = { 'main' : 'palabra reservada', 'if' : 'palabra reservada', 'then' : 'palabra reservada', 'else' : 'palabra reservada', 'do' : 'palabra reservada',
 'while' : 'palabra reservada', 'repeat' : 'palabra reservada', 'until' : 'palabra reservada', 'cin': 'palabra reservada', 'cout' : 'palabra reservada' }

data_type = {'int':'tipo integer','real':'tipo flotante','boolean':'tipo boleano','char':'tipo char'}
data_type_key = data_type.keys()

identifier = {'[a-zA-Z]':'identificador','[0-9]':'Digit'}
identifier_key = identifier.keys()

count = 0


# Utilizamos una expresión regular para encontrar todos los tokens en la línea
tokensunalinea = re.findall(r'//.*', a)
tokensmultilinea = re.findall(r'/\*(?:.|\n)*?\*/', a, re.DOTALL)
tokens = re.findall(r'//.*|/\*(?:.|\n)*?\*/|[\w]+|[-+*/%=<>!&|^~]=|--|\+\+|&&|\|\||[-+*/%=<>!&|^~]|\n', a)
print("Los tokens son ", tokens)

print('Line#', count, "Propiedades \n")
for token in tokens:
    if token in operators_key:
        print(token, operators[token])
    elif token in punctuation_symbol_key:
        print(token, punctuation_symbol[token])
    elif token in data_type_key:
        print(token, data_type[token])
    elif re.match(r'/\*(?:.|\n)*?\*/', token):
        print(token, "Comentario de múltiples líneas")
    elif re.match(r'/', token):
        if re.match(r'//',token):
            print(token, "Comentario simple")
        else:
            print(token, "Operador Division")
    elif re.match(r'\n', token):
        print(token, "Salto de linea")
    else:
        for key in identifier_key:
            if re.match(key, token[0]):
                print(token, identifier[key])
                break