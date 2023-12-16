# -*- coding: utf-8 -*-
import json


class Node:
    def __init__(
        self,
        value,
        children=None,
        line_no=None,
        type=None,
        identificator=False,
        val=None,
    ):
        self.value = value
        self.children = children if children is not None else []
        self.line_no = line_no
        self.type = type
        self.identificator = identificator
        self.val = val  # Agregar el manejo del parámetro val


def leer_entradas(archivo):
    entradas = {}
    with open(archivo, "r") as f:
        for linea in f:
            partes = linea.strip().split()
            if len(partes) == 2:
                var_name, value = partes
                try:
                    entradas[var_name] = float(value)
                except ValueError:
                    print(f"Error: valor no numérico para la variable '{var_name}'.")
                    return
    return entradas


def deserialize_node(node_dict):
    node = Node(
        value=node_dict["value"],
        line_no=node_dict["line_no"],
        type=node_dict["type"],
        identificator=node_dict["identificator"],
        val=node_dict["val"],
    )
    node.children = [deserialize_node(child) for child in node_dict["children"]]
    return node


with open("arbol_sintactico.json", "r") as file:
    tree_data = json.load(file)
    root_node = deserialize_node(tree_data)


class CodeGenerator:
    def __init__(self):
        self.code = []
        self.temp_count = 0
        self.output_file = "salida.txt"
        self.label_count = 0

    def new_label(self):
        self.label_count += 1
        return f"LABEL_{self.label_count}"

    def get_temp(self):
        """Obtiene un nuevo registro temporal"""
        self.temp_count += 1
        return f"T{self.temp_count}"

    def generate_code(self, node, parent=None):
        """Genera código a partir del nodo del árbol sintáctico"""
        if node.value in ["Programa", "Sentencias", "Bloque"]:
            for child in node.children:
                self.generate_code(child)

        elif node.value == "Asignación":
            dest = node.children[0].value
            src_code = self.generate_expression_code(node.children[1])
            self.code.append(f"ST {src_code}, {dest}")

        if node.value == "SentenciaIf":
            if parent and parent.value == "SentenciaIf":
                is_nested = True
            else:
                is_nested = False
            if_body_label = self.new_label()
            else_body_label = self.new_label() if len(node.children) > 2 else None
            end_if_label = self.new_label()

            # Genera código para la condición
            condition_code = self.generate_expression_code(
                node.children[0], if_body_label
            )

            if not is_nested:
                if else_body_label:
                    self.code.append(f"JMP {else_body_label}")
                else:
                    self.code.append(f"JMP {end_if_label}")

            # Cuerpo del if
            self.code.append(f"{if_body_label}:")
            self.generate_code(node.children[1], parent=node)

            if else_body_label:
                self.code.append(f"JMP {end_if_label}")

            # Cuerpo del else si existe
            if else_body_label:
                self.code.append(f"{else_body_label}:")
                self.generate_code(node.children[2], parent=node)

            # Etiqueta de fin del if
            self.code.append(f"{end_if_label}:")
        elif node.value == "SentenciaOutput":
            expr_code = self.generate_expression_code(node.children[0])
            self.code.append(f"OUT {expr_code}")
            with open(self.output_file, "a") as output_file:
                output_file.write(f"{node.children[0].val}\n")
        # Implementar casos para otros tipos de nodos (DoWhileStatement, OutputStatement, etc.)

        elif node.value == "SentenciaWhile":
            compararison_label = self.new_label()
            start_label = self.new_label()
            end_label = self.new_label()
            # Genera código para la condición
            # aquiiii
            self.code.append(f"{compararison_label}:")
            condition_code = self.generate_expression_code(
                node.children[0], start_label, is_while=True
            )
            # si falso salta a fin
            self.code.append(f"JMP {end_label}")
            # Etiqueta de inicio del bucle
            self.code.append(f"{start_label}:")
            # Cuerpo del bucle
            self.generate_code(node.children[1], parent=node)
            # Salto condicional al inicio del bucle
           # self.code.append(f"{condition_code}, {start_label}")
            # Etiqueta de fin del bucle
            self.code.append(f"JMP {compararison_label}")
            self.code.append(f"{end_label}:")

        elif node.value == "SentenciaDo":
            start_label = self.new_label()
            end_label = self.new_label()
            self.code.append(f"{start_label}:")
            for child in node.children[:-1]:  # Todos los hijos excepto la condición
                self.generate_code(child)
            condition_code = self.generate_expression_code(
                node.children[-1], start_label
            )
            # self.code.append(f"JNE {condition_code}, {start_label}")
            self.code.append(f"{end_label}:")  # Fin del bucle
        elif node.value == "SentenciaInput":
            var_name = (
                node.children[0].children[0].value
            )  # Asume que el primer hijo es el nombre de la variable
            self.code.append(f"IN {var_name}")
            entradas = leer_entradas("archivo_entrada.txt")
            temp = self.get_temp()
            if var_name in entradas:
                valor = entradas[var_name]
                # Aquí agregamos la asignación si existe un valor en el archivo de entradas
                self.code.append(f"LDC {temp}, {valor}")
                self.code.append(f"ST {temp}, {var_name}")

    def generate_expression_code(self, node, label=None, is_while=False):
        if node.value in ["+", "-", "*", "/", "%"]:
            left_code = self.generate_expression_code(node.children[0])
            right_code = self.generate_expression_code(node.children[1])
            result = self.get_temp()
            if node.value == "+":
                self.code.append(f"ADD {left_code}, {right_code}, {result}")
            elif node.value == "-":
                self.code.append(f"SUB {left_code}, {right_code}, {result}")
            elif node.value == "*":
                self.code.append(f"MUL {left_code}, {right_code}, {result}")
            elif node.value == "/":
                self.code.append(f"DIV {left_code}, {right_code}, {result}")
            elif node.value == "%":
                self.code.append(f"MOD {left_code}, {right_code}, {result}")
            return result
        elif self.is_int(node.value):
            temp = self.get_temp()
            self.code.append(f"LDC {temp}, {node.value}")
            return temp

        elif self.is_float(node.value):
            temp = self.get_temp()
            self.code.append(f"LDC {temp}, {node.value}")
            return temp

        elif node.identificator:
            return node.value  # Retorna el nombre de la variable
        # Dentro de generate_expression_code

        elif node.value in ["<", ">", "<=", ">=", "==", "!="]:
            left_code = self.generate_expression_code(node.children[0])
            right_code = self.generate_expression_code(node.children[1])
            result = self.get_temp()
            self.code.append(f"SUB {left_code}, {right_code}, {result}")

            jump_instructions = {
                "<": "JLT",
                ">": "JGT",
                "<=": "JLE",
                ">=": "JGE",
                "==": "JEQ",
                "!=": "JNE",
            }
            jump_instruction = jump_instructions[
                node.value
            ]  # if is_while else f"JNE {result}, {label}"
            self.code.append(f"{jump_instruction} {result}, {label}")
            return result
        # Implementar casos para otros tipos de nodos (relaciones, booleanos, etc.)

    def is_int(self, value):
        """Verifica si un valor es un entero"""
        try:
            int(value)
            return True
        except ValueError:
            return False

    def is_float(self, value):
        """Verifica si un valor es un flotante"""
        try:
            float(value)
            return True
        except ValueError:
            return False


code_generator = CodeGenerator()
code_generator.generate_code(root_node)
generated_code = code_generator.code

# Opcional: guardar en un archivo
with open("codigo_intermedio.txt", "w") as file:
    for line in generated_code:
        file.write(line + "\n")
