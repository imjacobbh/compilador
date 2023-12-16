/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.plaf.basic.BasicMenuBarUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**
 *
 * @author Usuario
 */
public class Interfaz extends javax.swing.JFrame {

    public String RutaActual = "";
    private LineNumber lineNumber;
    private boolean isNightMode;
    String rutaActual = System.getProperty("user.dir");
    private String rutaArchivo = "";

    /**
     * Creates new form Interfaz
     */
    public Interfaz() {
        initComponents();
        jPanel1 = new javax.swing.JPanel();
        JSplitPane jSplitPane1 = new JSplitPane();
        JSplitPane jSplitPane2 = new JSplitPane(); // New split pane for jTabbedPane1
        jPanel1.setLayout(new java.awt.GridBagLayout());
        // Set up the left split pane
        jSplitPane1.setLeftComponent(jScrollPane8);
        // Create a panel for the right side
        JPanel rightPanel = new JPanel(new java.awt.GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        // Add jTabbedPane7 to the right panel
        rightPanel.add(this.jTabbedPane7, gbc);
        // Add the right panel to the right side of the split pane
        jSplitPane1.setRightComponent(rightPanel);
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1202;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 0, 12);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jSplitPane1, gridBagConstraints);
        // Set up the second split pane for jTabbedPane1 and additional jTabbedPane1
        jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setTopComponent(jSplitPane1);
        // Create another jTabbedPane1 below the first one
        jSplitPane2.setBottomComponent(jTabbedPane1);
        // Add the second split pane to the panel
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1202;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 12, 13, 12);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jSplitPane2, gridBagConstraints);
        setContentPane(jPanel1);
        jSplitPane1.setDividerLocation(1000);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane2.setContinuousLayout(true);
        pack();
        inicializar();
        colors();
    }

    private void inicializar() {
        setTitle("Nuevo archivo");
        lineNumber = new LineNumber(this.TextAreaCodigo);
        this.jScrollPane8.setRowHeaderView(this.lineNumber);
        this.jCheckBoxTheme.setSelected(false);
        this.isNightMode = false;
        this.TextAreaCodigo.addCaretListener(new CaretListener() {
            // Each time the caret is moved, it will trigger the listener and its method caretUpdate.
            // It will then pass the event to the update method including the source of the event (which is our textarea control)
            @Override
            public void caretUpdate(CaretEvent e) {
                int dot = e.getDot();
                int line;
                try {
                    line = getLineOfOffset(TextAreaCodigo, dot);
                    int positionInLine = dot - getLineStartOffset(TextAreaCodigo, line);
                    jLabelLine.setText("linea: " + (line + 1) + ", columna: " + (positionInLine + 1));
                } catch (BadLocationException ex) {
                    Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    static int getLineOfOffset(JTextComponent comp, int offset) throws BadLocationException {
        Document doc = comp.getDocument();
        if (offset < 0) {
            throw new BadLocationException("Can't translate offset to line", -1);
        } else if (offset > doc.getLength()) {
            throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
        } else {
            Element map = doc.getDefaultRootElement();
            return map.getElementIndex(offset);
        }
    }

    static int getLineStartOffset(JTextComponent comp, int line) throws BadLocationException {
        Element map = comp.getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= map.getElementCount()) {
            throw new BadLocationException("No such line", comp.getDocument().getLength() + 1);
        } else {
            Element lineElem = map.getElement(line);
            return lineElem.getStartOffset();
        }
    }

    //METODO PARA PINTAS LAS PALABRAS 
    private void colors() {

        final StyleContext cont = StyleContext.getDefaultStyleContext();

        //COLORES 
        final AttributeSet attred = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(255, 0, 35));
        final AttributeSet attgreen = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 255, 54));
        final AttributeSet attblue = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 147, 255));
        final AttributeSet attpink = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(255, 192, 203));
        final AttributeSet attblack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 0, 0));
        final AttributeSet attgray = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(155, 155, 155));
        final AttributeSet attOperadores = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 57, 128));
        final AttributeSet attwhite = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.WHITE);
        //STYLO 
        DefaultStyledDocument doc = new DefaultStyledDocument() {
            @Override
            public void postRemoveUpdate(DefaultDocumentEvent chng) {
                try {
                    super.postRemoveUpdate(chng);
                    String text = getText(0, getLength());
                    //reset text
                    if (isNightMode) {
                        setCharacterAttributes(0, getLength(), attwhite, true);
                    } else {
                        setCharacterAttributes(0, getLength(), attblack, true);
                    }
                    //match palabras reservaadas
                    Pattern palabrasReservadas = Pattern.compile("\\b(main|if|IF|else|ELSE|end|END|do|DO|while|WHILE|then|THEN|repeat|REPEAT|until|UNTIL|cin|cout)\\b");
                    Matcher matcher = palabrasReservadas.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attblue, true);
                    }
                    //match NUMEROS
                    Pattern numerosPattern = Pattern.compile("\\b(-?\\d+(\\.\\d+)?)\\b");
                    matcher = numerosPattern.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attred, true);
                    }
                    //match tipo de datos
                    Pattern tipoDeDatos = Pattern.compile("\\b(int|INT|real|REAL|boolean|float|FLOAT|BOOLEAN)\\b");
                    matcher = tipoDeDatos.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attgreen, true);
                    }
                    //MATCH VALORES BOOLEANOS
                    Pattern booleanPattern = Pattern.compile("\\b(true|TRUE|false|FALSE)\\b");
                    matcher = booleanPattern.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attpink, true);
                    }
                    //MATCH OPERADORES
                    Pattern operatorsPattern = Pattern.compile("[-+*/=<>!]");
                    matcher = operatorsPattern.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attOperadores, true);
                    }
                    //DETECTAR COMETARIOS
                    Pattern singleLinecommentsPattern = Pattern.compile("\\/\\/.*");
                    matcher = singleLinecommentsPattern.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attgray, false);
                    }

                    Pattern multipleLinecommentsPattern = Pattern.compile("\\/\\*.*?\\*\\/",
                            Pattern.DOTALL);
                    matcher = multipleLinecommentsPattern.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attgray, false);

                    }
                } catch (BadLocationException ex) {
                    Logger.getLogger(Interfaz.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }

            @Override
            public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offset, str, a);

                String text = getText(0, getLength());
                //reset text
                if (isNightMode) {
                    setCharacterAttributes(0, getLength(), attwhite, true);
                } else {
                    setCharacterAttributes(0, getLength(), attblack, true);
                }
                //match palabras reservaadas
                Pattern palabrasReservadas = Pattern.compile("\\b(main|if|IF|else|ELSE|end|END|do|DO|while|then|THEN|WHILE|repeat|REPEAT|until|UNTIL|cin|cout)\\b");
                Matcher matcher = palabrasReservadas.matcher(text);
                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attblue, true);
                }
                //match NUMEROS
                Pattern numerosPattern = Pattern.compile("\\b(-?\\d+(\\.\\d+)?)\\b");
                matcher = numerosPattern.matcher(text);
                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attred, true);
                }
                //match tipo de datos
                Pattern tipoDeDatos = Pattern.compile("\\b(int|INT|real|REAL|boolean|float|BOOLEAN)\\b");
                matcher = tipoDeDatos.matcher(text);
                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attgreen, true);
                }
                //MATCH VALORES BOOLEANOS
                Pattern booleanPattern = Pattern.compile("\\b(true|TRUE|false|FALSE)\\b");
                matcher = booleanPattern.matcher(text);
                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attpink, true);
                }
                //MATCH OPERADORES
                Pattern operatorsPattern = Pattern.compile("[-+*/=<>!]");
                matcher = operatorsPattern.matcher(text);
                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attOperadores, true);
                }
                //DETECTAR COMETARIOS
                Pattern singleLinecommentsPattern = Pattern.compile("\\/\\/.*");
                matcher = singleLinecommentsPattern.matcher(text);
                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attgray, false);
                }

                Pattern multipleLinecommentsPattern = Pattern.compile("\\/\\*.*?\\*\\/",
                        Pattern.DOTALL);
                matcher = multipleLinecommentsPattern.matcher(text);

                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attgray, false);
                }
            }

        };

        JTextPane txt = new JTextPane(doc);
        String temp = this.TextAreaCodigo.getText();
        this.TextAreaCodigo.setStyledDocument(txt.getStyledDocument());
        this.TextAreaCodigo.setText(temp);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabelLine = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaResultados = new javax.swing.JTextArea();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextAreaTablaSimbolos = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaErrores = new javax.swing.JTextArea();
        jTabbedPane7 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaLexico = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextAreaSintacticp = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextAreaSemantico = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextAreaCodigoIntermedio = new javax.swing.JTextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        TextAreaCodigo = new javax.swing.JTextPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        jCheckBoxTheme = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabelLine.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelLine.setForeground(new java.awt.Color(255, 255, 255));
        jLabelLine.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLine.setText("Bienvenido");
        jLabelLine.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabelLine, gridBagConstraints);

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));

        jTextAreaResultados.setEditable(false);
        jTextAreaResultados.setColumns(20);
        jTextAreaResultados.setRows(5);
        jTextAreaResultados.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane2.setViewportView(jTextAreaResultados);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1319, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1295, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 89, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jTabbedPane1.addTab("Resultado", jPanel2);

        jPanel8.setBackground(new java.awt.Color(153, 153, 153));

        jTextAreaTablaSimbolos.setEditable(false);
        jTextAreaTablaSimbolos.setColumns(20);
        jTextAreaTablaSimbolos.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        jTextAreaTablaSimbolos.setForeground(new java.awt.Color(102, 102, 102));
        jTextAreaTablaSimbolos.setRows(5);
        jTextAreaTablaSimbolos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane9.setViewportView(jTextAreaTablaSimbolos);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1319, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 1295, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 89, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jTabbedPane1.addTab("Tabla de Simbolos", jPanel8);

        jPanel3.setBackground(new java.awt.Color(153, 153, 153));
        jPanel3.setAlignmentX(0.0F);
        jPanel3.setAlignmentY(0.0F);
        jPanel3.setAutoscrolls(true);

        jTextAreaErrores.setEditable(false);
        jTextAreaErrores.setColumns(20);
        jTextAreaErrores.setFont(new java.awt.Font("Monospaced", 0, 16)); // NOI18N
        jTextAreaErrores.setForeground(new java.awt.Color(255, 0, 0));
        jTextAreaErrores.setRows(5);
        jTextAreaErrores.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane3.setViewportView(jTextAreaErrores);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1319, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1295, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 89, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jTabbedPane1.addTab("Errores", jPanel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1202;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 12, 13, 12);
        jPanel1.add(jTabbedPane1, gridBagConstraints);

        jPanel4.setBackground(new java.awt.Color(153, 153, 153));

        jTextAreaLexico.setEditable(false);
        jTextAreaLexico.setColumns(20);
        jTextAreaLexico.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        jTextAreaLexico.setRows(5);
        jTextAreaLexico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane4.setViewportView(jTextAreaLexico);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 523, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 610, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jTabbedPane7.addTab("Lexico", jPanel4);

        jPanel5.setBackground(new java.awt.Color(153, 153, 153));

        jTextAreaSintacticp.setEditable(false);
        jTextAreaSintacticp.setColumns(20);
        jTextAreaSintacticp.setRows(5);
        jTextAreaSintacticp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane5.setViewportView(jTextAreaSintacticp);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 533, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 610, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jTabbedPane7.addTab("Sintactico", jPanel5);

        jPanel6.setBackground(new java.awt.Color(153, 153, 153));

        jTextAreaSemantico.setEditable(false);
        jTextAreaSemantico.setColumns(20);
        jTextAreaSemantico.setRows(5);
        jTextAreaSemantico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane6.setViewportView(jTextAreaSemantico);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 533, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 610, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jTabbedPane7.addTab("Semantico", jPanel6);

        jPanel7.setBackground(new java.awt.Color(153, 153, 153));

        jTextAreaCodigoIntermedio.setEditable(false);
        jTextAreaCodigoIntermedio.setColumns(20);
        jTextAreaCodigoIntermedio.setRows(5);
        jTextAreaCodigoIntermedio.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane7.setViewportView(jTextAreaCodigoIntermedio);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 533, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 610, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jTabbedPane7.addTab("Codigo Intermedio", jPanel7);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 417;
        gridBagConstraints.ipady = 416;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 0, 12);
        jPanel1.add(jTabbedPane7, gridBagConstraints);

        TextAreaCodigo.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        TextAreaCodigo.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N
        TextAreaCodigo.setMinimumSize(new java.awt.Dimension(255, 255));
        TextAreaCodigo.setPreferredSize(new java.awt.Dimension(286, 116));
        TextAreaCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TextAreaCodigoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TextAreaCodigoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TextAreaCodigoKeyTyped(evt);
            }
        });
        jScrollPane8.setViewportView(TextAreaCodigo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 699;
        gridBagConstraints.ipady = 508;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 0, 0);
        jPanel1.add(jScrollPane8, gridBagConstraints);

        jMenuBar1.setBackground(new java.awt.Color(255, 255, 255));
        jMenuBar1.setPreferredSize(new java.awt.Dimension(291, 35));

        jMenu1.setText("Archivo");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Abrir");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Guardar");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Guardar Como...");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Cerrar");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Editar");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Formato");
        jMenuBar1.add(jMenu3);

        jMenu4.setText("Compilar");
        jMenu4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu4MouseClicked(evt);
            }
        });
        jMenu4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu4ActionPerformed(evt);
            }
        });
        jMenuBar1.add(jMenu4);

        jMenu5.setText("Ayuda");
        jMenu5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu5MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu5);

        jMenu6.setText("Tema");

        jCheckBoxTheme.setSelected(true);
        jCheckBoxTheme.setText("Modo noche");
        jCheckBoxTheme.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCheckBoxThemeMouseClicked(evt);
            }
        });
        jCheckBoxTheme.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxThemeStateChanged(evt);
            }
        });
        jCheckBoxTheme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxThemeActionPerformed(evt);
            }
        });
        jMenu6.add(jCheckBoxTheme);

        jMenuBar1.add(jMenu6);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1348, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 813, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        JFileChooser selectorArchivos = new JFileChooser();
        selectorArchivos.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        if (!rutaArchivo.isEmpty()) {
            selectorArchivos.setCurrentDirectory(new File(rutaArchivo));
        }

        selectorArchivos.showOpenDialog(this);
        rutaArchivo = selectorArchivos.getSelectedFile().getPath();
        AbrirTxt(rutaArchivo);


    }//GEN-LAST:event_jMenuItem1ActionPerformed

    public void AbrirTxt(String Ruta) {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        RutaActual = Ruta;
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File(Ruta);

            TextAreaCodigo.setText(getTextFile(archivo));
            setTitle(archivo.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        saveFile();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void saveFile() {
        if (RutaActual != "") {
            try {
                String ruta = RutaActual;

                File file = new File(ruta);
                // Si el archivo no existe es creado
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(TextAreaCodigo.getText());
                setTitle(file.getName());
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JFileChooser guardar = new JFileChooser();
            guardar.showSaveDialog(null);
            guardar.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            File archivo = guardar.getSelectedFile();
            System.out.println(guardar.getSelectedFile().getPath());
            guardarFichero(TextAreaCodigo.getText(), archivo);
            RutaActual = guardar.getSelectedFile().getPath();
        }
    }

    public String getTextFile(File file) {
        String text = "";
        try {

            BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            while (true) {
                int b = entrada.read();
                if (b != -1) {
                    text += (char) b;
                } else {
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("El archivo no pudo ser encontrado... " + ex.getMessage());
            return null;
        } catch (IOException ex) {
            System.out.println("Error al leer el archivo... " + ex.getMessage());
            return null;
        }
        return text;
    }


    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        JFileChooser guardar = new JFileChooser();
        guardar.showSaveDialog(null);
        guardar.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        File archivo = guardar.getSelectedFile();
        System.out.println(guardar.getSelectedFile().getPath());
        RutaActual = guardar.getSelectedFile().getPath();
        guardarFichero(TextAreaCodigo.getText(), archivo);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        TextAreaCodigo.setText("");
        RutaActual = "";
        setTitle("Nuevo archivo");
        jTextAreaErrores.setText("");
        jTextAreaLexico.setText("");
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void TextAreaCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextAreaCodigoKeyReleased
        this.tecla(evt);
    }//GEN-LAST:event_TextAreaCodigoKeyReleased

    private void TextAreaCodigoKeyTyped(java.awt.event.KeyEvent evt) {

    }

    private void TextAreaCodigoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextAreaCodigoKeyPressed
    }//GEN-LAST:event_TextAreaCodigoKeyPressed

    private void jCheckBoxThemeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxThemeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxThemeActionPerformed

    //change theme :)))))
    private void jCheckBoxThemeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBoxThemeStateChanged
        if (this.jCheckBoxTheme.isSelected()) {
            jMenuBar1.setUI(new BasicMenuBarUI() {
                public void paint(Graphics g, JComponent c) {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(0, 0, c.getWidth(), c.getHeight());
                }
            });

            this.TextAreaCodigo.setBackground(Color.DARK_GRAY);
            this.TextAreaCodigo.setForeground(Color.WHITE);
            this.TextAreaCodigo.setCaretColor(Color.WHITE);
            isNightMode = true;
            this.lineNumber.updateColor(isNightMode);
            this.jMenuBar1.setBackground(Color.black);
            jMenuBar1.setOpaque(true);

            this.jMenu1.setBackground(Color.DARK_GRAY);
            this.jMenu1.setForeground(Color.WHITE);
            jMenu1.setOpaque(true);
            this.jMenuItem1.setBackground(Color.DARK_GRAY);
            this.jMenuItem1.setForeground(Color.WHITE);
            jMenuItem1.setOpaque(true);

            this.jMenuItem2.setBackground(Color.DARK_GRAY);
            this.jMenuItem2.setForeground(Color.WHITE);
            jMenuItem2.setOpaque(true);
            this.jMenuItem3.setBackground(Color.DARK_GRAY);
            this.jMenuItem3.setForeground(Color.WHITE);
            jMenuItem3.setOpaque(true);

            this.jMenuItem4.setBackground(Color.DARK_GRAY);
            this.jMenuItem4.setForeground(Color.WHITE);
            jMenuItem4.setOpaque(true);

            this.jCheckBoxTheme.setBackground(Color.DARK_GRAY);
            this.jCheckBoxTheme.setForeground(Color.WHITE);
            jCheckBoxTheme.setOpaque(true);

            this.jMenu2.setBackground(Color.DARK_GRAY);
            this.jMenu2.setForeground(Color.WHITE);
            jMenu2.setOpaque(true);
            this.jMenu3.setBackground(Color.DARK_GRAY);
            this.jMenu3.setForeground(Color.WHITE);
            jMenu3.setOpaque(true);
            this.jMenu4.setBackground(Color.DARK_GRAY);
            this.jMenu4.setForeground(Color.WHITE);
            jMenu4.setOpaque(true);
            this.jMenu5.setBackground(Color.DARK_GRAY);
            this.jMenu5.setForeground(Color.WHITE);
            jMenu5.setOpaque(true);
            this.jMenu6.setBackground(Color.DARK_GRAY);
            this.jMenu6.setForeground(Color.WHITE);

            jMenu6.setOpaque(true);
        } else {
            jMenuBar1.setUI(new BasicMenuBarUI() {
                public void paint(Graphics g, JComponent c) {
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, c.getWidth(), c.getHeight());
                }
            });
            this.TextAreaCodigo.setBackground(Color.WHITE);
            this.TextAreaCodigo.setForeground(Color.BLACK);
            this.TextAreaCodigo.setCaretColor(Color.BLACK);
            isNightMode = false;
            this.lineNumber.updateColor(isNightMode);
            this.jMenuBar1.setBackground(Color.black);
            jMenuBar1.setOpaque(true);

            this.jMenu1.setBackground(Color.WHITE);
            this.jMenu1.setForeground(Color.BLACK);
            jMenu1.setOpaque(true);
            this.jMenuItem1.setBackground(Color.WHITE);
            this.jMenuItem1.setForeground(Color.BLACK);
            jMenuItem1.setOpaque(true);

            this.jMenuItem2.setBackground(Color.WHITE);
            this.jMenuItem2.setForeground(Color.BLACK);
            jMenuItem2.setOpaque(true);
            this.jMenuItem3.setBackground(Color.WHITE);
            this.jMenuItem3.setForeground(Color.BLACK);
            jMenuItem3.setOpaque(true);

            this.jMenuItem4.setBackground(Color.WHITE);
            this.jMenuItem4.setForeground(Color.BLACK);
            jMenuItem4.setOpaque(true);

            this.jCheckBoxTheme.setBackground(Color.WHITE);
            this.jCheckBoxTheme.setForeground(Color.BLACK);
            jCheckBoxTheme.setOpaque(true);

            this.jMenu2.setBackground(Color.WHITE);
            this.jMenu2.setForeground(Color.BLACK);
            jMenu2.setOpaque(true);
            this.jMenu3.setBackground(Color.WHITE);
            this.jMenu3.setForeground(Color.BLACK);
            jMenu3.setOpaque(true);
            this.jMenu4.setBackground(Color.WHITE);
            this.jMenu4.setForeground(Color.BLACK);
            jMenu4.setOpaque(true);
            this.jMenu5.setBackground(Color.WHITE);
            this.jMenu5.setForeground(Color.BLACK);
            jMenu5.setOpaque(true);
            this.jMenu6.setBackground(Color.WHITE);
            this.jMenu6.setForeground(Color.BLACK);

            jMenu6.setOpaque(true);
        }

    }//GEN-LAST:event_jCheckBoxThemeStateChanged


    private void jMenu4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu4ActionPerformed
        saveFile();
        executeLexico();

    }//GEN-LAST:event_jMenu4ActionPerformed

    private void jMenu4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu4MouseClicked
        saveFile();
        executeLexico();
    }//GEN-LAST:event_jMenu4MouseClicked

    private void jCheckBoxThemeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBoxThemeMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxThemeMouseClicked

    private void jMenu5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu5MouseClicked
        JOptionPane.showMessageDialog(
                this,
                "Proyecto desarrollado por Jacob Bonilla y Sergio Solis\n"
                + "ISC 8°B ");
    }//GEN-LAST:event_jMenu5MouseClicked

    private void executeLexico() {
        PySystemState state = new PySystemState();
        state.argv.append(new PyString("-f"));
        state.argv.append(new PyString(RutaActual));
        PythonInterpreter interpreter = new PythonInterpreter(null, state);

        interpreter.execfile(rutaActual + "\\AnalizadorLexico.py");

        //abrir archivo lexemas
        jTextAreaLexico.setText("");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(rutaActual + "\\lexico.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                jTextAreaLexico.append(str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
        //abrir archivo de errores lexemas
        jTextAreaErrores.setText("Errores Lexico:\n");
        try {
            in = new BufferedReader(new FileReader(rutaActual + "\\errors.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                jTextAreaErrores.append("\t" + str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
        this.Sintactico();
        this.CodigoIntermedio();
        this.ejecucion();
    }

    private void ejecucion() {

        try {
            IntermediateCodeInterpreter.execute();
            Font consolasFont = new Font("Arial", Font.PLAIN, 24);
            this.jTextAreaResultados.setFont(consolasFont);
            Path rutaArchivo = Paths.get(rutaActual, "EjecucionIntermedio.txt");
            List<String> out = Files.readAllLines(rutaArchivo);
            for (String linea : out) {
                this.jTextAreaResultados.append(linea + "\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void limpiarArchivo(String archivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write("");  // Escribe una cadena vacía para limpiar el archivo
        }
    }

    public static JSONObject leerArbolJson(String rutaArchivo) {
        JSONParser parser = new JSONParser();

        try {
            String contenido = new String(Files.readAllBytes(Paths.get(rutaArchivo)));
            Object obj = parser.parse(contenido);
            return (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void CodigoIntermedio() {
        this.jTextAreaResultados.setText("");
        try {
            String rutaActual = System.getProperty("user.dir");
            Path rutaarchivo = Paths.get(rutaActual, "arbol_sintactico.json");
            Path rutaentrada = Paths.get(rutaActual, "archivo_entrada.txt");
            limpiarArchivo(rutaentrada.toString());
            JSONObject arbol = leerArbolJson(rutaarchivo.toString());
            //manejarInputStatements(arbol, rutaentrada.toString());

            Path rutaScript = Paths.get(rutaActual, "ejecucion3.py");

            try {
                PythonRunner.ejecutarScriptPython(rutaScript.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.jTextAreaCodigoIntermedio.setText("");

            Path rutaArchivo = Paths.get(rutaActual, "codigo_intermedio.txt");

            Font consolasFont = new Font("Arial", Font.PLAIN, 24);
            this.jTextAreaCodigoIntermedio.setFont(consolasFont);

            List<String> lineas = Files.readAllLines(rutaArchivo);

            for (String linea : lineas) {
                this.jTextAreaCodigoIntermedio.append(linea + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();  // Manejo de excepciones, puedes personalizarlo según tus necesidades.
        }
    }

    public void Sintactico() {
        String rutaActual = System.getProperty("user.dir");
        System.out.println("Ruta actual: " + rutaActual);
        Path rutaNueva = Paths.get(rutaActual);
        System.out.println("Ruta nueva: " + rutaNueva.toString());
        //Path ruta = Paths.get(rutaNueva.toString(),"AnalizadorLexico");
        //System.out.println(ruta);
        Path rutaScript = Paths.get(rutaNueva.toString()).resolve("analizadorsintactico.py");

        try {
            String salidaPython = PythonRunner.ejecutarScriptPython(rutaScript.toString());
            this.jTextAreaSintacticp.setText(salidaPython);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //abrir archivo sintactico
        jTextAreaSintacticp.setText("");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(rutaActual + "\\ArbolSintactico.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                jTextAreaSintacticp.append(str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
        //abrir archivo de errores sintactico
        try {
            jTextAreaErrores.append("\nErrores análisis sintactico: " + '\n');
            in = new BufferedReader(new FileReader(rutaActual + "\\erroresSintactico.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                jTextAreaErrores.append("\t" + str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {

            }

        }
        //abrir archivo analisis semantico
        this.jTextAreaSemantico.setText("");
        try {
            in = new BufferedReader(new FileReader(rutaActual + "\\ArbolAnotaciones.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                jTextAreaSemantico.append(str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
        //abrir archivo tabla de simbolos
        this.jTextAreaTablaSimbolos.setText("");
        try {
            in = new BufferedReader(new FileReader(rutaActual + "\\TablaSimbolos.txt"));
            String str;
            jTextAreaTablaSimbolos.append("\n");
            while ((str = in.readLine()) != null) {
                jTextAreaTablaSimbolos.append(str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }

        //abrir archivo de errores semantico
        try {
            in = new BufferedReader(new FileReader(rutaActual + "\\ErroresSemantico.txt"));
            String str;
            jTextAreaErrores.append("\nErrores análisis semantico: " + '\n');
            while ((str = in.readLine()) != null) {
                jTextAreaErrores.append("\t" + str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {

            }

        }
    }

    private void tecla(java.awt.event.KeyEvent evt) {
        int keyCode = evt.getKeyCode();
        if ((keyCode >= 65 && keyCode <= 90) || (keyCode >= 48 && keyCode <= 57)
                || (keyCode >= 97 && keyCode <= 122) || (keyCode != 27 && !(keyCode >= 37
                && keyCode <= 40) && !(keyCode >= 16
                && keyCode <= 18) && keyCode != 524
                && keyCode != 20)) {

            if (!getTitle().contains("*")) {
                setTitle(getTitle() + "*");
            }
        }

    }

    /**
     * @param args the command line arguments
     */
    public void guardarFichero(String cadena, File archivo) {

        FileWriter escribir;
        try {

            escribir = new FileWriter(archivo, true);
            escribir.write(cadena);
            escribir.close();
            setTitle(archivo.getName());
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar, ponga nombre al archivo");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar, en la salida");
        }
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Interfaz().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane TextAreaCodigo;
    private javax.swing.JCheckBoxMenuItem jCheckBoxTheme;
    private javax.swing.JLabel jLabelLine;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane7;
    private javax.swing.JTextArea jTextAreaCodigoIntermedio;
    private javax.swing.JTextArea jTextAreaErrores;
    private javax.swing.JTextArea jTextAreaLexico;
    private javax.swing.JTextArea jTextAreaResultados;
    private javax.swing.JTextArea jTextAreaSemantico;
    private javax.swing.JTextArea jTextAreaSintacticp;
    private javax.swing.JTextArea jTextAreaTablaSimbolos;
    // End of variables declaration//GEN-END:variables
}
