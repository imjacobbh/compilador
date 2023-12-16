package compilador;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

 class IntermediateCodeInterpreter {

    private Map<String, Double> variables = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);
    private List<String> code;
    private int currenIndex = 0;
    private String rutaActual = System.getProperty("user.dir");

    public static void execute() {
        IntermediateCodeInterpreter interpreter = new IntermediateCodeInterpreter();
        interpreter.executeIntermediateCode();
    }

    private void executeIntermediateCode() {
        try {
            // Nombre del archivo de salida
            String nombreArchivoSalida = "EjecucionIntermedio.txt";
            Path rutaArchivoSalida = Paths.get(rutaActual, nombreArchivoSalida);

            // Redirigir la salida estándar a un archivo
            PrintStream originalOut = System.out;
            PrintStream fileOut = new PrintStream(new FileOutputStream(rutaArchivoSalida.toFile()));
            System.setOut(fileOut);

            Path rutaArchivo = Paths.get(rutaActual, "codigo_intermedio.txt");
            code = Files.readAllLines(rutaArchivo);

            while (currenIndex < code.size()) {
                try {
                    executeInstruction(code.get(currenIndex));
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("FIN");
                    break;
                }
            }

            // Restaurar la salida estándar
            System.setOut(originalOut);

        } catch (IOException ex) {
            Logger.getLogger(IntermediateCodeInterpreter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void executeInstruction(String instruction) {

        String[] parts = instruction.split("[,\\s]+");
        String opCode = parts[0];

        if (opCode.endsWith(":")) {
            // Es una etiqueta, no hacemos nada
        } else {
            switch (opCode) {
                case "LDC":
                    ldc(parts[1], Double.parseDouble(parts[2]));
                    break;
                case "ST":
                    st(parts[1], parts[2]);
                    break;
                case "IN":
                    in(parts[1]);
                    break;
                case "MUL":
                    mul(parts[1], parts[2], parts[3]);
                    break;
                case "DIV":
                    div(parts[1], parts[2], parts[3]);
                    break;
                case "SUB":
                    sub(parts[1], parts[2], parts[3]);
                    break;
                case "JLE":
                case "JEQ":
                case "JGT":
                case "JLT":
                case "JNE":
                    currenIndex = this.jumpConditional(parts[0], parts[1], parts[2], this.currenIndex);
                    break;
                case "OUT":
                    out(parts[1]);
                    break;
                case "ADD":
                    add(parts[1], parts[2], parts[3]);
                    break;
                case "JMP":
                    currenIndex = jump(parts[1], code, this.currenIndex);
                    break;
                // Agrega más casos según sea necesario
                default:
                    System.out.println("Instrucción no reconocida: " + instruction);
            }
        }
        currenIndex++;
    }

    private void mul(String operand1, String operand2, String destination) {
        double value1 = variables.get(operand1);
        double value2 = variables.get(operand2);
        double result = value1 * value2;
        variables.put(destination, result);
    }

    private void div(String operand1, String operand2, String destination) {
        double value1 = variables.get(operand1);
        double value2 = variables.get(operand2);
        try {
            double result = value1 / value2;
            variables.put(destination, result);
        } catch (NullPointerException e) {
            System.out.println("Error: División por cero");
        }
    }

    private int findLabelIndex(String label) {
        for (int i = 0; i < code.size(); i++) {
            if (code.get(i).startsWith(label)) {
                // System.out.println("Saltando a la etiqueta: " + label);
                return i;
            }
        }
        System.out.println("Error: Etiqueta no encontrada - " + label);
        return -1; // Manejar el error de etiqueta no encontrada según sea necesario
    }

    private int jump(String label, List<String> code, int currentIndex) {
        for (int i = 0; i < code.size(); i++) {
            if (code.get(i).startsWith(label)) {
                //  System.out.println("Saltando a la etiqueta: " + label);
                return i;
            }
        }
        System.out.println("Etiqueta no encontrada: " + label);
        return currentIndex;
    }

    private void ldc(String destination, double value) {
        variables.put(destination, value);
    }

    private void st(String source, String destination) {
        if (source.startsWith("T")) {
            // Si la fuente es un registro temporal, obten el valor de ese registro
            int sourceIndex = getRegisterIndex(source);
            double value = variables.get("T" + sourceIndex);
            variables.put(destination, value);
        } else {
            // Si la fuente es una variable, simplemente copia su valor
            double value = variables.get(source);
            variables.put(destination, value);
        }
    }

    private void in(String variable) {
        boolean validInput = false;
        double valueD = 0.0;

        while (!validInput) {
            String value = JOptionPane.showInputDialog("Ingrese valor para " + variable + ":");

            // Verificar si el usuario presionó "Cancelar" o dejó el campo vacío
            if (value == null) {
                // Tratar el caso de cancelación
                System.out.println("Operación cancelada.");

                return;
            }

            try {
                // Intentar convertir el valor a double
                valueD = Double.parseDouble(value);
                validInput = true;
            } catch (NumberFormatException e) {
                // Manejar la excepción si el valor no es un número válido
                System.out.println("Ingrese un número válido.");
            }
        }

        // Almacenar el valor en el mapa de variables
        variables.put(variable, valueD);
    }

    private void sub(String operand1, String operand2, String destination) {
        double value1 = variables.get(operand1);
        double value2 = variables.get(operand2);
        double result = value1 - value2;
        variables.put(destination, result);
    }

    private int jgt(String operand1, String destination, int currentIndex) {
        double value1 = variables.get(operand1);
        if (value1 > 0) {
            return findLabelIndex(destination);
        }
        return currentIndex;
    }

    private int jumpConditional(String opCode, String operand1, String destination, int currentIndex) {
        double value1 = variables.get(operand1);

        boolean jumpCondition = false;

        switch (opCode) {
            case "JEQ":
                jumpCondition = value1 == 0;
                break;
            case "JGT":
                jumpCondition = value1 > 0;
                break;
            case "JLT":
                jumpCondition = value1 < 0;
                break;
            case "JLE":
                jumpCondition = value1 <= 0;
                break;
            case "JNE":
                jumpCondition = value1 != 0;
                break;
        }

        if (jumpCondition) {
            return findLabelIndex(destination);
        } else {
            currenIndex++;
        }
        return currentIndex;
    }

    private void out(String variable) {
        double value = variables.get(variable);

        // Verificar si el valor tiene decimales distintos de 0
        if (value != Math.floor(value)) {
            System.out.println(value);
        } else {
            // Intentar convertir el valor double a int
            try {
                int intValue = (int) value;
                System.out.println(intValue);
            } catch (ArithmeticException e) {
                System.out.println(value);
            }
        }
    }

    private void add(String operand1, String operand2, String destination) {
        double value1 = variables.get(operand1);
        double value2 = variables.get(operand2);
        double result = value1 + value2;
        variables.put(destination, result);
    }

    private int getRegisterIndex(String register) {
        return Integer.parseInt(register.substring(1));
    }
}
