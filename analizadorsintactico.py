# -*- coding: utf-8 -*-
import json



class Token:
    def __init__(self, token_type, value, line_no):
        self.token_type = token_type
        self.value = value
        self.line_no = line_no
errores_semantico = []
with open("salida.txt", "w"):
    pass

class Node:
    def __init__(
        self,
        value,
        line_no=None,
        children=None,
        identificator=False
    ):
        self.value = value
        self.children = children or []
        self.line_no = line_no or None
        self.type = None  # Nuevo atributo para el tipo
        self.identificator = False
        self.val = None  # Nuevo atributo para el valor

    def add_child(self, node):
        self.children.append(node)


class Parser:
    def __init__(self, tokens):
        self.tokens = tokens
        self.current_token = None
        self.token_index = -1
        self.errors = []
        self.advance()

    def advance(self):
        self.token_index += 1
        if self.token_index < len(self.tokens):
            self.current_token = self.tokens[self.token_index]
        else:
            self.current_token = None

    def match(self, token_type):
        if self.current_token and self.current_token.token_type == token_type:
            self.advance()
        else:
            expected_token = token_type if token_type else "fin de entrada"
            found_token = (
                self.current_token.token_type
                if self.current_token
                else "fin de entrada"
            )
            self.errors.append(
                f"Se esperaba {expected_token}, se encontró {found_token}"
            )
            self.advance()

    def program(self):
        root = Node("Programa")
        self.match("main")
        self.match("{")
        root.add_child(self.stmts())
        self.match("}")
        return root

    def stmts(self):
        root = Node("Sentencias")
        if self.current_token and self.current_token.token_type in [
            "int",
            "float",
            "id",
            "if",
            "while",
            "{",
            "cin",
            "cout",
        ]:
            if self.current_token and self.current_token.token_type == "id":
                self.identificator = True
            root.add_child(self.stmt())
        while (
            self.current_token
            and self.current_token.token_type != "}"
            and self.current_token.token_type != "end"
        ):
            if self.current_token.token_type == "do":
                root.add_child(self.do_while_stmt())
            elif (
                self.current_token.value == "end"
                or self.current_token.token_type == "else"
            ):
                return root
            else:
                root.add_child(self.stmt())
        return root

    def do_while_stmt(self):
        root = Node("SentenciaDo")
        self.match("do")
        root.add_child(self.stmt())  # Agregar la primera expresión dentro del do-while
        while self.current_token and self.current_token.token_type != "until":
            root.add_child(self.stmt())  # Agregar más expresiones dentro del do-while
        self.match("until")
        self.match("(")
        root.add_child(self.expr())
        self.match(")")
        self.match(";")
        return root

    def stmt(self):
        if self.current_token and self.current_token.token_type == "int":
            root = Node("DeclaraciónInt", self.current_token.line_no)
            self.match("int")
            root.add_child(self.idList())
            self.match(";")
        elif self.current_token and self.current_token.token_type == "boolean":
             root = Node("DeclaraciónBoolean", self.current_token.line_no)
             self.match("boolean")
             root.add_child(self.idList())
             self.match(";")
        elif self.current_token and self.current_token.token_type == "do":
            root = self.do_while_stmt()
        elif self.current_token and self.current_token.token_type == "float":
            root = Node("DeclaraciónFloat", self.current_token.line_no)
            self.match("float")
            root.add_child(self.idList())
            self.match(";")
        elif self.current_token and self.current_token.token_type == "id":
            root = Node("Asignación", self.current_token.line_no)
            id_node = Node(self.current_token.value, self.current_token.line_no)
            id_node.identificator = True
            self.match("id")
            root.add_child(id_node)
            if self.current_token and self.current_token.token_type in ["++", "--"]:
                op_node = None
                if self.current_token.token_type == "++":
                    op_node = Node("+")
                else:
                    op_node = Node("-")
                op_node.add_child(id_node)
                self.match(self.current_token.token_type)
                op_node.add_child(Node("1"))
                root.add_child(op_node)
            elif self.current_token and self.current_token.token_type in [
                "<",
                ">",
                "<=",
                ">=",
                "==",
                "!=",
            ]:
                op_node = Node(self.current_token.value, self.current_token.line_no)
                self.match(self.current_token.token_type)
                if self.current_token and self.current_token.token_type in [
                    "id",
                    "num",
                ]:
                    operand_node = Node(
                        self.current_token.value, self.current_token.line_no
                    )
                    if self.current_token and self.current_token.token_type == "id":
                        operand_node.identificator = True
                    root.add_child(operand_node)
                    self.match(self.current_token.token_type)
            else:
                self.match("=")
                expr_node = self.expr()
                root.add_child(expr_node)
            self.match(";")
        #      root.add_child(id_node)
        elif self.current_token and self.current_token.token_type == "if":
            root = Node("SentenciaIf", self.current_token.line_no)
            self.match("if")

            if self.current_token and self.current_token.token_type == "(":
                self.match("(")
                expr_node = self.expr()
                root.add_child(expr_node)
                self.match(")")

            stmt_node = self.stmts()

            if self.current_token and self.current_token.token_type == "{":
                self.match("{")
                root.add_child(stmt_node)
                self.match("}")
            else:
                root.add_child(stmt_node)

            if self.current_token and self.current_token.token_type == "else":
                self.match("else")
                else_stmt_node = self.stmts()
                root.add_child(else_stmt_node)

            self.match("end")

        elif self.current_token and self.current_token.token_type == "while":
            root = Node("SentenciaWhile", self.current_token.line_no)
            self.match("while")
            self.match("(")
            expr_node = self.expr()
            root.add_child(expr_node)
            self.match(")")
            stmt_node = self.stmt()
            root.add_child(stmt_node)
        elif self.current_token and self.current_token.token_type == "{":
            root = Node("Bloque", self.current_token.line_no)
            self.match("{")
            root.add_child(self.stmts())
            self.match("}")
        elif self.current_token and self.current_token.token_type == "cin":
            root = Node("SentenciaInput", self.current_token.line_no)
            self.match("cin")
            root.add_child(self.idList())
            self.match(";")
        elif self.current_token and self.current_token.token_type == "cout":
            root = Node("SentenciaOutput", self.current_token.line_no)
            self.match("cout")
            root.add_child(self.expr())
            self.match(";")
        else:
            root = Node("Error")
            error_token = self.current_token.value if self.current_token else None
            if error_token:
                self.errors.append(f"Error Linea {self.current_token.line_no}. Sentencia inválida: {error_token}")
            self.advance()
        return root

    def idList(self):
        root = Node("IdList")
        id_node = Node(self.current_token.value, self.current_token.line_no)
        root.add_child(id_node)
        self.match("id")
        while (
            self.current_token
            and self.current_token.value == ","
            and self.current_token.value != ";"
        ):
            self.match(",")
            id_node = Node(self.current_token.value, self.current_token.line_no)
            root.add_child(id_node)
            self.match("id")

        return root

    def expr(self):
        root = self.term()
        while self.current_token and self.current_token.token_type in ["+", "-"]:
            op_node = Node(self.current_token.value, self.current_token.line_no)
            self.match(self.current_token.token_type)
            op_node.add_child(root)
            root = op_node
            root.add_child(self.term())

        return root

    def term(self):
        root = self.factor()
        while self.current_token and self.current_token.token_type in ["*", "/"]:
            op_node = Node(self.current_token.value, self.current_token.line_no)
            self.match(self.current_token.token_type)
            op_node.add_child(root)
            root = op_node
            root.add_child(self.factor())

        return root

    def factor(self):
        root = self.primary()
        while self.current_token and self.current_token.token_type in [
            "<",
            ">",
            "<=",
            ">=",
            "==",
            "!=",
        ]:
            op_node = Node(self.current_token.value, self.current_token.line_no)
            self.match(self.current_token.token_type)
            op_node.add_child(root)
            root = op_node
            root.add_child(self.primary())

        return root

    def primary(self):
        if self.current_token and self.current_token.token_type == "(":
            self.match("(")
            root = self.expr()
            self.match(")")
        elif self.current_token and self.current_token.token_type in ["id", "num", "true", "false"]:
            root = Node(self.current_token.value, self.current_token.line_no)
            if self.current_token and self.current_token.token_type == "id":
                root.identificator = True
            self.match(self.current_token.token_type)


        else:
            root = Node("Error")
            error_token = self.current_token.value if self.current_token else None
            self.errors.append(f"Factor inválido: {error_token}")
            self.advance()

        return root

    def relational_expr(self):
        root = self.term()
        if self.current_token and self.current_token.token_type in [
            "<",
            ">",
            "<=",
            ">=",
            "==",
            "!=",
        ]:
            op_node = Node(self.current_token.value, self.current_token.line_no)
            self.match(self.current_token.token_type)
            root.add_child(op_node)
            if self.current_token and self.current_token.token_type in ["id", "num"]:
                operand_node = Node(
                    self.current_token.value, self.current_token.line_no
                )
                if self.current_token and self.current_token.token_type == "id":
                    operand_node.identificator = True
                root.add_child(operand_node)
                self.match(self.current_token.token_type)
        return root

    def parse(self):
        ast = self.program()

        if self.errors:
            print("Se encontraron errores de sintaxis. La compilación ha fallado.")
        else:
            print("La sintaxis es correcta. La compilación ha sido exitosa.")

        return ast


with open("lexico.txt", "r") as file:
    lines = file.readlines()

# Crear la lista de objetos Token
token_list = []
for line in lines:
    line = line.strip()
    if line:
        token_parts = line.split("--*")
        if token_parts[1].strip() == "identificador":
            token_type = "id"
            value = token_parts[0].strip()
        elif token_parts[1].strip() == "flotante":
            token_type = "num"
            value = token_parts[0].strip()
        elif token_parts[1].strip() == "entero":
            token_type = "num"
            value = token_parts[0].strip()
        else:
            token_type = token_parts[0].strip()
            value = token_parts[0].strip()
        line_no = token_parts[2].strip()
        token = Token(token_type, value, line_no)
        token_list.append(token)
# # Imprimir la lista de objetos Token
# for tok in token_list:
#    print (tok.token_type, tok.value, tok.line_no)


parser = Parser(token_list)
ast = parser.parse()


# Imprimir errores

f = open("erroresSintactico.txt", "w", encoding="utf-8")
if parser.errors:
    print("Errores de sintaxis:")
    # f.write("Errores de sintaxis:")
    for error in parser.errors:
        f.write(error + "\n")
        print(error)
f.close()

# Imprimir AST
f = open("arbolSintactico.txt", "w", encoding="utf-8")
f.write("Arbol Sintactico")


def print_ast(node, level=0, is_last_child=False, annotations=False):
    indent = " | " * level
    # child_prefix = " -- " if is_last_child else "| -- "
    print(
        f"{indent}{node.value} (Linea: {node.line_no}) [Tipo: {node.type}] [Valor: {node.val}]"
    )
    if not annotations:
        f.write(f"\n{indent}{node.value}")
    else: 
        if(node.val != "Error"):
            f.write( f"\n{indent}{node.value} (Linea: {node.line_no}) [Tipo: {node.type}] [Valor: {node.val}]")
    for i, child in enumerate(node.children):
        is_last = i == len(node.children) - 1
        print_ast(child, level + 1, is_last, annotations= annotations)


class SymbolTable:
    location = 0

    def __init__(self):
        self.symbols = {}

    def insertLine(self, name, line):
        var = self.lookup(name)
        if(var != None):
            self.symbols[name]["values"][-1] = (
                    var[0],
                    var[1],
                    var[2]
                )
            self.symbols[name]["lines"].append(line)
    def insert(self, name, value, datatype, location, line, shouldntExist=False):
        if name in self.symbols:
            if shouldntExist:
                errores_semantico.append(f"Error Linea {line}: La variable {name}, ya se declaro previamente")
                return -1
            # Resolución de colisiones por encadenamiento
            var = self.lookup(name)
            self.symbols[name]["values"][-1] = (
                value,
                datatype,
                var[2]
            )
            self.symbols[name]["lines"].append(line)
        else:
            self.symbols[name] = {
                "values": [(value, datatype, self.location)],
                "lines": [line],
            }
            self.location = self.location + 1

    def lookup(self, name):
        if name in self.symbols:
            return self.symbols[name]["values"][-1]
        else:
            return None

    def evaluate_expression(self, node):
        if node.value in ("+", "-", "*", "/", "%"):
            left_value = self.evaluate_expression(node.children[0])
            right_value = self.evaluate_expression(node.children[1])
            if left_value is None or right_value is None:
                #raise ValueError(f"Error in expression at line {node.line_no}")
                errores_semantico.append(f"Type error at line {node.line_no}")
                return
            if type(left_value) != type(right_value):
                node.type = "float"
#                raise ValueError(f"Type mismatch at line {node.line_no}")
            else:
                node.type = node.children[0].type
            if node.value == "+":
                node.val = left_value + right_value
                return left_value + right_value
            elif node.value == "-":
                node.val = left_value - right_value
                return left_value - right_value
            elif node.value == "*":
                if(node.type =="int"):
                    node.val = int(left_value * right_value)
                    return node.val
                else: 
                    node.val = left_value * right_value
                    return node.val
            elif node.value == "/":
                if(node.type =="int"):
                    node.val = int(left_value / right_value)
                    return node.val
                else: 
                    node.val = left_value / right_value
                    return node.val
            elif node.value == "%":
                if(node.type =="int"):
                    node.val = int(left_value % right_value)
                    return node.val
                else: 
                    node.val = left_value % right_value
                    return node.val
        elif node.value in ("==", "<=", "<", ">", ">=", "!="):
            left_value = self.evaluate_expression(node.children[0])
            right_value = self.evaluate_expression(node.children[1])
            if left_value is None or right_value is None:
                raise ValueError(f"Error in expression at line {node.line_no}")

            #if type(left_value) != type(right_value):
                #raise ValueError(f"Type mismatch at line {node.line_no}")
            node.type = "boolean"
            if node.value == "==":
                node.val = left_value == right_value
                return left_value == right_value
            elif node.value == "<=":
                node.val = left_value <= right_value
                return left_value <= right_value
            elif node.value == "<":
                node.val = left_value < right_value
                return left_value < right_value
            elif node.value == ">":
                node.val = left_value > right_value
                return left_value > right_value
            elif node.value == ">=":
                node.val = left_value >= right_value
                return left_value >= right_value
            elif node.value == "!=":
                node.val = left_value != right_value
                return left_value != right_value
        else:
            # Handle variables or constants
            if self.is_int(node.value):
                node.type = "int"
                node.val = int(node.value)
                return int(node.value)
            elif self.is_float(node.value):
                node.type = "float"
                node.val = float(node.value)
                return float(node.value)
            elif node.value == "true":
                node.type = "boolean"
                node.val = "true"
                return True
            elif node.value == "false":
                node.type = "boolean"
                node.val = "false"
                return False
            else:
                # Handle variable lookup here
                var_name = node.value
                var_info = self.lookup(var_name)
                if(var_info != None):
                   
                    symbol_table.insertLine(node.value, node.line_no)
                    if var_info[1] == "int":
                        node.val = var_info[0]
                        node.type = var_info[1]
                        return var_info[0]
                    elif var_info[1] == "float":
                        node.val = var_info[0]
                        node.type = var_info[1]
                        return var_info[0]
                    elif var_info[1] == "boolean":
                        node.val = var_info[0]
                        node.type = var_info[1] 
                        return var_info[0]
                    else:
                        errores_semantico.append(f"Type error at line {node.line_no}")
                        #aqui
                else: 
                    errores_semantico.append(f"Error linea {node.line_no}: La variable '{var_name}' no se declaró antes de usarse.")
                    return
    def is_float(self, value):
        try:
            float_value = float(value)
            # TODO: CHECAR SI FORZOSAMENTE DEBEN LLEVAR PUNTO O NO
            # if "." in value:
            #   return True
            # else:
            return True
        except ValueError:
            return False

    def is_int(self, value):
        try:
            int(value)
            return True
        except ValueError:
            return False

    def print_symbol_table(self):
        f.write("Nombre          | Tipo       | Valor             | # Registro (loc) | Líneas\n")
        f.write("--------------------------------------------------------------------------------\n")
        for name, symbol_info in self.symbols.items():
            for value, datatype, location in symbol_info["values"]:
                sorted_lines = sorted(symbol_info["lines"])
                lines = ", ".join(str(line) for line in sorted(sorted_lines, key=lambda x: int(x)))
                line_str = f"{name.ljust(15)} |  {datatype.ljust(9)} | {str(value).ljust(17)} | {str(location).ljust(16)} | {lines}\n"
                f.write(line_str)


def semantic_analysis(node, symbol_table):
    # Define una función para verificar si la expresión es booleana
    def check_boolean_expr(expr_node):
        # Esta función verifica si la expresión es booleana.
        comparison_operators = ["<", ">", "<=", ">=", "==", "!=", "true", "false"]

        if expr_node.value in comparison_operators:
            return True
        else:
            var_info = symbol_table.lookup(expr_node.value)
            if(var_info != None  and var_info[1] == "boolean"):
                return True
            else: return False

    if node is None:
        return

    if node.value == "DeclaraciónInt":
        datatype = "int"
    elif node.value == "DeclaraciónFloat":
        datatype = "float"
    elif node.value == "DeclaraciónBoolean":
        datatype = "boolean"
    else:
        datatype = None

    if datatype:
        id_list = node.children[0]
        for id_node in id_list.children:
            id_node.type = datatype
            if(datatype == "boolean"):
                id_node.val = False
            else: 
                id_node.val = 0
            status = symbol_table.insert(id_node.value, id_node.val , datatype, None, node.line_no, shouldntExist=True)
            if( status == -1):
                id_node.val = "Error"
    for child in node.children:
        semantic_analysis(child, symbol_table)

    if node.value == "Asignación":
        var_name = node.children[0].value
        symbol_info = symbol_table.lookup(var_name)
        if not symbol_info:
            print(f"Error linea {node.line_no}: La variable '{var_name}' no se declaró antes de usarse.")
            errores_semantico.append(f"Error linea {node.line_no}: La variable '{var_name}' no se declaró antes de usarse.")
        else:
            node.children[0].type = symbol_info[1]
            var_type = symbol_info[1]
            var_value = symbol_table.evaluate_expression(node.children[1])
            if(node.children[1].type != node.children[0].type):
                print(f"Error linea {node.line_no}: tipo erroneo")
                errores_semantico.append(f"Error linea {node.line_no}: tipo erroneo")
                symbol_table.insertLine(node.children[0].value, node.line_no)
            #TODO: SOLO SE ASIGNA Y SE GUARDA EN MEMORIA SI ES CORRECTO EL TIPADO, DLC, NO SE ACTUALIZA VARIABLE
            else: 
                node.val = var_value
                node.type = var_type
               # node.children[0].val = var_value
                symbol_table.insert(var_name, var_value, var_type, None, node.line_no)

    if node.value == "SentenciaIf" or node.value == "SentenciaWhile":
        # Verificar si la expresión es booleana
        expr_node = node.children[0]  # Asume que la expresión está en el primer hijo
        if not check_boolean_expr(expr_node):
            print(f"Error Línea {expr_node.line_no}: La expresión no es booleana.")
            errores_semantico.append(f"Error Línea {expr_node.line_no}: La expresión no es booleana.")
        else:
            var_value = symbol_table.evaluate_expression(node.children[0])
            node.val = var_value
    if node.value == "SentenciaDo":
        expr_node = node.children[-1]  # Asume que la expresión está en el 2ndo hijo
        if not check_boolean_expr(expr_node):
            print(
                f"Error Línea {expr_node.line_no}: La expresión en la sentencia Do-Until no es booleana."
            )
            errores_semantico.append(f"Error Línea {expr_node.line_no}: La expresión en la sentencia Do-Until no es booleana.")
        else:
            var_value = symbol_table.evaluate_expression(node.children[-1])
            node.val = var_value
    if node.value == "SentenciaOutput":
            
            var_value = symbol_table.evaluate_expression(node.children[0])
    
            node.val = var_value
    if node.value == "SentenciaInput":
             #se escribe en indentificador, debemos de buscar en tabla hash primero
             #aqui
            var_name = node.children[0].children[0].value
            symbol_info = symbol_table.lookup(var_name)
            if(symbol_info != None):
                symbol_table.insertLine(var_name, node.line_no)
            else:
                errores_semantico.append(f"Error linea {node.line_no}: La variable '{var_name}' no se declaró antes de usarse.")


root_node = ast

symbol_table = SymbolTable()
semantic_analysis(root_node, symbol_table)

def serialize_node(node):
    node_dict = {
        'value': node.value,
        'children': [serialize_node(child) for child in node.children],
	    'line_no': node.line_no,
	    'type': node.type,
	    'identificator': node.identificator,
	    'val': node.val
	}
    return node_dict

serialized_tree = serialize_node(root_node)
	
with open('arbol_sintactico.json', 'w') as file:
    json.dump(serialized_tree, file, indent=4)

print_ast(ast)
f.close()
if errores_semantico:
    for error in errores_semantico:
        pass
f.close()
# Imprimir arbol con anotaciones a txt
f = open("ArbolAnotaciones.txt", "w", encoding="utf-8")
print_ast(ast, annotations= True)
f.close()
#Imprimir tabla de simboloas a txt
f = open("TablaSimbolos.txt", "w", encoding="utf-8")
symbol_table.print_symbol_table()
f.close()
#Imprimir errores semantico a txt
f = open("ErroresSemantico.txt", "w", encoding="utf-8")
if errores_semantico:

    for error in errores_semantico:
        f.write(error + "\n")
f.close()
