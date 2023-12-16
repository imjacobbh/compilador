# -*- coding: utf-8 -*-

from optparse import OptionParser

parser = OptionParser(version="%prog 1.0.3")
parser.add_option(
    "-f",
    "--*file",
    action="store",
    dest="file",
    default="test.txt",
    type="string",
    help="specify a file to load. Created by Jacob & Sergio",
)
options, args = parser.parse_args()
file = open(options.file, "r")
contenidodecodigo = file.read()
# Diccionarios de tokens
palabras_reservadas = {
    "main": "palabra reservada",
    "then": "palabra reservada",
    "if": "palabra reservada",
    "else": "palabra reservada",
    "end": "palabra reservada",
    "do": "palabra reservada",
    "while": "palabra reservada",
    "repeat": "palabra reservada",
    "until": "palabra reservada",
    "cin": "palabra reservada",
    "cout": "palabra reservada",
    "real": "palabra reservada",
    "int": "palabra reservada",
    "boolean": "palabra reservada",
    "true": "palabra reservada",
    "false": "palabra reservada",
    "float":"palabra reservada",
}
simbolos_especiales = {
    "(": "PAR_IZQ",
    ")": "PAR_DER",
    "{": "LLAVE_IZQ",
    "}": "LLAVE_DER",
    ";": "PUNTO_COMA",
    ",": "COMA",
}
operadores_aritmeticos = {
    "+": "SUMA",
    "-": "RESTA",
    "*": "MULTIPLICACION",
    "/": "DIVISION",
    "=": "IGUALACION",
}
operadores_relacionales = {
    "==": "IGUALDAD",
    "!=": "DIFERENTE",
    "<>": "DIFERENTE2",
    "<": "MENOR_QUE",
    ">": "MAYOR_QUE",
    "<=": "MENOR_IGUAL_QUE",
    ">=": "MAYOR_IGUAL_QUE",
}
operadores_logicos = {"&&": "AND", "||": "OR", "!": "NOT"}
operadores_dobles = {"++": "INCREMENTO", "--": "DECREMENTO"}
tokens = []
errors = []
linea = 1
col = 1

i = 0
while i < len(contenidodecodigo):
    # Ignorar espacios en blanco
    if contenidodecodigo[i].isspace():
        if contenidodecodigo[i] == "\n":
            linea += 1
            col = 1
        else:
            col += 1
        i += 1
        continue
    # Identificar comentarios de una línea
    if contenidodecodigo[i : i + 2] == "//":
        i = contenidodecodigo.index("\n", i)
        continue
    # Identificar comentarios multilinea
    if contenidodecodigo[i : i + 2] == "/*":
        aux = contenidodecodigo.index("*/", i) + 2
        j = i
        while j < aux:
            if contenidodecodigo[j] == "\n":
                linea += 1
            j += 1
        i = aux
        continue
    # Identificar palabras reservadas, identificadores y números
    if contenidodecodigo[i].isalpha():
        j = i + 1
        while j < len(contenidodecodigo) and (
            contenidodecodigo[j].isalnum() or contenidodecodigo[j] == "_"
        ):
            j += 1
        token = contenidodecodigo[i:j]
        if token in palabras_reservadas:
            tokens.append(" " + token + "  --* " + palabras_reservadas[token] + " --* " + str(linea))
        else:
            tokens.append(" " + token + "  --* identificador --*" + str(linea))
        col += j - i
        i = j
        continue
    elif contenidodecodigo[i].isdigit():
        j = i + 1
        while j < len(contenidodecodigo) and contenidodecodigo[j].isdigit():
            j += 1
        if j < len(contenidodecodigo) and contenidodecodigo[j] == ".":
            j += 1
            while j < len(contenidodecodigo) and contenidodecodigo[j].isdigit():
                j += 1
            tokens.append(" " + contenidodecodigo[i:j] + " --* flotante --* "+ str(linea))
        else:
            tokens.append(" " + contenidodecodigo[i:j] + " --* entero --* " + str(linea))
        col += j - i
        i = j
        continue
    # Identificar símbolos especiales
    if contenidodecodigo[i] in simbolos_especiales:
        tokens.append(" " + contenidodecodigo[i] + "  --* simbolo especial --*" + str(linea))
        i += 1
        col += 1
        continue
    # Identificar operadores aritméticos y relacionales
    if contenidodecodigo[i : i + 2] in operadores_relacionales:
        tokens.append(" " + contenidodecodigo[i : i + 2] + " --* operador relacional --*" + str(linea))
        i += 2
        col += 2
        continue
    elif contenidodecodigo[i] in operadores_relacionales:
        tokens.append(" " + contenidodecodigo[i] + " --* operador relacional --*" + str(linea))
        i += 1
        continue
    if contenidodecodigo[i : i + 2] in operadores_dobles:
        tokens.append(" " + contenidodecodigo[i : i + 2] + "--* operador aritmetico --*" + str(linea))
        i += 2
        col += 1
        continue
    elif contenidodecodigo[i] in operadores_aritmeticos:
        tokens.append(" " + contenidodecodigo[i] + "  --* operador aritmetico --*"+ str(linea))
        i += 1
        col += 1
        continue
    else:
        errors.append(
            "error:'"
            + contenidodecodigo[i]
            + "'(linea:"
            + str(linea)
            + ", columna: "
            + str(col)
            + ")"
        )
        i += 1
        col += 1
        continue
# Ingresa lo tokens a txt
f = open("lexico.txt", "w")
for item in tokens:
    print(item)
    f.write(item + "\n")
f.close()
# Ingresa los errores a un txt
f = open("errors.txt", "w")
for error in errors:
    print(error)
    f.write(error + "\n")
f.close()
