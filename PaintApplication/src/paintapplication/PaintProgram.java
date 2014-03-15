/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paintapplication;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

// Höfundur: Rúnar Már Magnússon 
// Þýðing og keyrsla: 
// Opna src möppu í cmd og gera 
// javac -cp . paintapplication\PaintProgram.java 
// java paintapplication.PaintProgram (eða nota jar skrá í PaintApplication\dist)
// 
// Þróunarumhverfi: Netbeans 
// Lýsing: Forritið PaintProgram er teikniforrit sem býður upp á aðgerðirnar: 
// teikna með blýant, stroka út, fylla svæði með lit, teikna beinar línur, 
// teikna rétthyrninga, teikna hringi/ellipsur, velja lit og setja inn texta. 
// Einnig er hægt að búa til nýja mynd, vista mynd, opna mynd, undo og redo.
//
// Myndir sem forritið notar eru 500x500 af stærð og eru með endinguna ".png".
public class PaintProgram extends javax.swing.JFrame {

    private ArrayList<Point> points;
    private int currentTool = 0;
    private int currentStrokeSize = 1;
    private int pencilStrokeSize = 1;
    private int eraserStrokeSize = 5;
    private Point startPoint;
    private Point dragPoint;
    private Point endPoint;
    private Color currentColor;
    private boolean dragged;
    private boolean released;

    /* 
     * Data Invariant for variables that are not Swing components
     * points - holds the points that the cursor went over when it was dragged
     *          and the tool was either Eraser or Pencil
     * currentTool - Holds the currently used tool in GUI, currentTool=:
     *                  0 if no tool is in use
     *                  1 if the pencil tool is in use
     *                  2 if the eraser tool is in use
     *                  3 if the line tool is in use
     *                  4 if the rectangle tool is in use
     *                  5 if the oval tool is in use
     *                  6 if the fill tool is in use
     *                  7 if the text tool is in use
     * currentStrokeSize - The current size of the stroke to use. 
     *          If the tool in use is eraser then 
     *          currentStrokeSize = eraserStrokeSize 
     *          else currentStrokeSize = pencilStrokeSize
     * pencilStrokeSize - The stroke size of the line, pencil, rectangle and
     *          oval tools.
     * eraserStrokeSize - The stroke size of the eraser.
     * startPoint - The point on the drawing panel where the cursor is first
     *          pressed relative to the origin of the panel.
     * endPoint - The point where the cursor is released on the drawing panel 
     *          relative to the origin of the panel.
     * dragPoint - The most current point the cursor went over when it was 
     *          dragged. The point is relative to the origin of the drawing 
     *          panel.
     * currentColor - The current color in use in the application. If the 
     *          tool in use is an eraser, then the color is Color.WHITE 
     *          but if the tool in use is not an eraser then currentColor is 
     *          a user specified color.     
     */
    // Usage : JFrame frame = new PaintProgram();
    // Before: Nothing
    // After : A new Paint Program has been initilized
    public PaintProgram() {
        points = new ArrayList<>();
        currentTool = 0;
        currentStrokeSize = 1;
        pencilStrokeSize = 1;
        eraserStrokeSize = 5;
        startPoint = null;
        dragPoint = null;
        endPoint = null;
        currentColor = Color.BLACK;
        dragged = false;
        released = false;
        initComponents();
    }

    // Usage : Color c = getCurrentColor();
    // Before: Nothing
    // After : c holds the current color in use in the program. If the tool
    //         in use is the eraser then the color is by default Color.WHITE.
    private Color getCurrentColor() {
        if (!isCurrentTool("Eraser")) {
            return currentColor;
        } else {
            return Color.WHITE;
        }
    }

    // Usage : setCurrentColor(newColor)
    // Before: Nothing
    // After : The current color in use is newColor
    private void setCurrentColor(Color newColor) {
        currentColor = newColor;
    }

    // Usage : int tool = getCurrentTool()
    // Before: Nothing
    // After : tool holds the int representation of the current tool in use.
    private int getCurrentTool() {
        return currentTool;
    }

    // Usage : setCurrentTool(newTool)
    // Before: newTool is an int representing a tool that the application uses
    //         that is newToole is in the range 0 to 7
    // After : The current tool in use is newTool
    private void setCurrentTool(int newTool) {
        currentTool = newTool;
    }

    // Usage : boolean b = isCurrentTool(tool)
    // Before: tool is a string representation of a tool the application uses:
    //         that is tool is in {"None", "Pencil", "Eraser", "Line", 
    //         "Rectangle", "Oval", "Fill", "Text"}
    // After : b is true if the current tool is tool, false otherwise
    private boolean isCurrentTool(String tool) {
        boolean result;
        switch (tool) {
            case "None":
                result = (0 == currentTool);
                break;
            case "Pencil":
                result = (1 == currentTool);
                break;
            case "Eraser":
                result = (2 == currentTool);
                break;
            case "Line":
                result = (3 == currentTool);
                break;
            case "Rectangle":
                result = (4 == currentTool);
                break;
            case "Oval":
                result = (5 == currentTool);
                break;
            case "Fill":
                result = (6 == currentTool);
                break;
            case "Text":
                result = (7 == currentTool);
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    // Usage : int strokeSize = getCurrentStrokeSize()
    // Before: nothing
    // After : strokeSize holds the stroke size in use.
    private int getCurrentStrokeSize() {
        return currentStrokeSize;
    }

    // Usage : Font f = getCurrentFont()
    // Before: Nothing
    // After : f is the current font in use determined by the 
    //         user choices in the text panel.
    private Font getCurrentFont() {
        // Get the font name from the font combo box
        String fontName = (String) jFontComboBox.getSelectedItem();

        // Get the font size from the font size combo box
        Integer fontSize = (Integer) jFontSizeComboBox.getSelectedItem();

        // Gets the current status of the bold and italic toggle buttons
        boolean bold = jBoldButton.isSelected();
        boolean italic = jItalicButton.isSelected();

        // Determine the style
        int style;
        if (bold && italic) {
            style = Font.BOLD | Font.ITALIC;
        } else if (bold) {
            style = Font.BOLD;
        } else if (italic) {
            style = Font.ITALIC;
        } else {
            style = Font.PLAIN;
        }

        // Construct a font using the font name, style and font size.
        Font currentFont = new Font(fontName, style, fontSize.intValue());
        return currentFont;
    }

    // Usage : String text = getCurrentText()
    // Before: Nothing
    // After : text holds current the text from the text field in the 
    //         text panel.
    private String getCurrentText() {
        String text = jTextField.getText();
        String result;
        if (text == null) {
            result = "";
        } else {
            result = text;
        }
        return result;
    }

    // Usage : removePanels(chooser)
    // Before: chooser is a color chooser
    // After : The panels RGB, HSV, HSL and CMYK have been removed from 
    //         chooser.
    private void removePanels(JColorChooser chooser) {
        AbstractColorChooserPanel[] panels = chooser.getChooserPanels();
        for (AbstractColorChooserPanel panel : panels) {
            switch (panel.getDisplayName()) {
                case "RGB":
                    chooser.removeChooserPanel(panel);
                    break;
                case "HSV":
                    chooser.removeChooserPanel(panel);
                    break;
                case "HSL":
                    chooser.removeChooserPanel(panel);
                    break;
                case "CMYK":
                    chooser.removeChooserPanel(panel);
                    break;
                default:
                    break;
            }
        }
    }

    // Usage : toggleButtons(toolToggled)
    // Before: toolToggled is a the int representation of a tool used by the 
    //         program from 0 to 7
    // After : All tool toggle buttons except tooToggled have been un selected
    //         because only one tool can be used at a time. 
    //         The GUI has been updated to represent the tool that was toggled.
    private void toggleButtons(int toolToggled) {
        JToggleButton[] buttons = {jPencilButton, jEraserButton,
            jLineButton, jRectangleButton, jOvalButton, jFillButton,
            jTextButton};

        // Reset all variables used by the repaint function.
        startPoint = null;
        dragPoint = null;
        endPoint = null;
        released = false;
        dragged = false;

        // Change the current tool used
        setCurrentTool(toolToggled);

        // Un select all buttons except toolToogled button
        if (toolToggled != 0) {
            for (JToggleButton button : buttons) {
                if (button != buttons[toolToggled - 1]) {
                    button.setSelected(false);
                }
            }
        } else {
            for (JToggleButton button : buttons) {
                button.setSelected(false);
            }
        }

        // Change the stroke size slider
        if (!isCurrentTool("Eraser")) {
            jStrokeSizeSlider.setValue(pencilStrokeSize);
        } else {
            jStrokeSizeSlider.setValue(eraserStrokeSize);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        colorChooser = new javax.swing.JColorChooser();
        colorChooser.setPreviewPanel(new JPanel());
        removePanels(colorChooser);
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png", "PNG"));
        jToolsPanel = new javax.swing.JPanel();
        jPencilButton = new javax.swing.JToggleButton();
        jEraserButton = new javax.swing.JToggleButton();
        jLineButton = new javax.swing.JToggleButton();
        jRectangleButton = new javax.swing.JToggleButton();
        jOvalButton = new javax.swing.JToggleButton();
        jFillButton = new javax.swing.JToggleButton();
        jDrawingPanel = new DrawingPanel();
        jCanvasLabel = new JLabel(new ImageIcon(((DrawingPanel) jDrawingPanel).getCanvas()));
        jSizePanel = new javax.swing.JPanel();
        jStrokeSizeSlider = new javax.swing.JSlider();
        jColorPanel = new javax.swing.JPanel();
        jSetColorButton = new javax.swing.JButton();
        jCurrentColorPanel = new javax.swing.JPanel();
        jTextPanel = new javax.swing.JPanel();
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        jFontComboBox = new JComboBox(g.getAvailableFontFamilyNames());
        jFontSizeComboBox = new JComboBox(new Integer[] { 7, 8, 9, 10, 11, 12, 14, 18, 20, 22, 24, 36 });
        jTextButton = new javax.swing.JToggleButton();
        jBoldButton = new javax.swing.JToggleButton();
        jItalicButton = new javax.swing.JToggleButton();
        jTextField = new javax.swing.JTextField();
        jMenuBar = new javax.swing.JMenuBar();
        jFileMenu = new javax.swing.JMenu();
        jMenuNew = new javax.swing.JMenuItem();
        jMenuOpen = new javax.swing.JMenuItem();
        jMenuSave = new javax.swing.JMenuItem();
        jMenuExit = new javax.swing.JMenuItem();
        jEditMenu = new javax.swing.JMenu();
        jMenuUndo = new javax.swing.JMenuItem();
        jMenuRedo = new javax.swing.JMenuItem();

        fileChooser.setDialogTitle("File extension: \".png\", Maximum dimension of images: 500x500");

        colorChooser.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Teikniforrit");
        setResizable(false);

        jToolsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Tools"));
        jToolsPanel.setPreferredSize(new java.awt.Dimension(150, 4));

        jPencilButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/pencil_icon.png"))); // NOI18N
        jPencilButton.setPreferredSize(new java.awt.Dimension(55, 55));
        jPencilButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jPencilButtonItemStateChanged(evt);
            }
        });

        jEraserButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/eraser_icon.png"))); // NOI18N
        jEraserButton.setPreferredSize(new java.awt.Dimension(55, 55));
        jEraserButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jEraserButtonItemStateChanged(evt);
            }
        });

        jLineButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/line_icon.png"))); // NOI18N
        jLineButton.setPreferredSize(new java.awt.Dimension(55, 55));
        jLineButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jLineButtonItemStateChanged(evt);
            }
        });

        jRectangleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/box_icon.png"))); // NOI18N
        jRectangleButton.setPreferredSize(new java.awt.Dimension(55, 55));
        jRectangleButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRectangleButtonItemStateChanged(evt);
            }
        });

        jOvalButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/ring_icon.png"))); // NOI18N
        jOvalButton.setPreferredSize(new java.awt.Dimension(55, 55));
        jOvalButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jOvalButtonItemStateChanged(evt);
            }
        });

        jFillButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/bucket_icon.png"))); // NOI18N
        jFillButton.setPreferredSize(new java.awt.Dimension(55, 55));
        jFillButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jFillButtonItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jToolsPanelLayout = new javax.swing.GroupLayout(jToolsPanel);
        jToolsPanel.setLayout(jToolsPanelLayout);
        jToolsPanelLayout.setHorizontalGroup(
            jToolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jToolsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jToolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jToolsPanelLayout.createSequentialGroup()
                        .addComponent(jLineButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRectangleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jOvalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jToolsPanelLayout.createSequentialGroup()
                        .addComponent(jPencilButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jEraserButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFillButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jToolsPanelLayout.setVerticalGroup(
            jToolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jToolsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jToolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jEraserButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPencilButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFillButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jToolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLineButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRectangleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jOvalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jDrawingPanel.setBackground(new java.awt.Color(255, 255, 255));
        jDrawingPanel.setForeground(new java.awt.Color(240, 240, 240));
        jDrawingPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        jDrawingPanel.setFocusable(false);
        jDrawingPanel.add(jCanvasLabel,null);
        jDrawingPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jDrawingPanelMouseReleased(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jDrawingPanelMousePressed(evt);
            }
        });
        jDrawingPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jDrawingPanelMouseDragged(evt);
            }
        });

        jCanvasLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jCanvasLabel.setAlignmentX(0.5F);
        jCanvasLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        jCanvasLabel.setDoubleBuffered(true);
        jCanvasLabel.setPreferredSize(new java.awt.Dimension(500, 500));

        javax.swing.GroupLayout jDrawingPanelLayout = new javax.swing.GroupLayout(jDrawingPanel);
        jDrawingPanel.setLayout(jDrawingPanelLayout);
        jDrawingPanelLayout.setHorizontalGroup(
            jDrawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jCanvasLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jDrawingPanelLayout.setVerticalGroup(
            jDrawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jCanvasLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jSizePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Size"));

        jStrokeSizeSlider.setMajorTickSpacing(1);
        jStrokeSizeSlider.setMaximum(10);
        jStrokeSizeSlider.setMinimum(1);
        jStrokeSizeSlider.setMinorTickSpacing(1);
        jStrokeSizeSlider.setPaintLabels(true);
        jStrokeSizeSlider.setPaintTicks(true);
        jStrokeSizeSlider.setSnapToTicks(true);
        jStrokeSizeSlider.setToolTipText("");
        jStrokeSizeSlider.setValue(1);
        jStrokeSizeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jStrokeSizeSliderStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jSizePanelLayout = new javax.swing.GroupLayout(jSizePanel);
        jSizePanel.setLayout(jSizePanelLayout);
        jSizePanelLayout.setHorizontalGroup(
            jSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSizePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jStrokeSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jSizePanelLayout.setVerticalGroup(
            jSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSizePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jStrokeSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jColorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Color"));

        jSetColorButton.setText("Set the color");
        jSetColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSetColorButtonActionPerformed(evt);
            }
        });

        jCurrentColorPanel.setBackground(new java.awt.Color(0, 0, 0));
        currentColor = jCurrentColorPanel.getBackground();
        jCurrentColorPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jCurrentColorPanelLayout = new javax.swing.GroupLayout(jCurrentColorPanel);
        jCurrentColorPanel.setLayout(jCurrentColorPanelLayout);
        jCurrentColorPanelLayout.setHorizontalGroup(
            jCurrentColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 75, Short.MAX_VALUE)
        );
        jCurrentColorPanelLayout.setVerticalGroup(
            jCurrentColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jColorPanelLayout = new javax.swing.GroupLayout(jColorPanel);
        jColorPanel.setLayout(jColorPanelLayout);
        jColorPanelLayout.setHorizontalGroup(
            jColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jColorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSetColorButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCurrentColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jColorPanelLayout.setVerticalGroup(
            jColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jColorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSetColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCurrentColorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jTextPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Text"));

        jFontComboBox.setEnabled(false);

        jFontSizeComboBox.setSelectedIndex(5);
        jFontSizeComboBox.setEnabled(false);

        jTextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/Text_icon.png"))); // NOI18N
        jTextButton.setPreferredSize(new java.awt.Dimension(55, 55));
        jTextButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jTextButtonItemStateChanged(evt);
            }
        });

        jBoldButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/Bold_icon.png"))); // NOI18N
        jBoldButton.setEnabled(false);
        jBoldButton.setPreferredSize(new java.awt.Dimension(30, 30));

        jItalicButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/Italic_icon.png"))); // NOI18N
        jItalicButton.setEnabled(false);
        jItalicButton.setPreferredSize(new java.awt.Dimension(30, 30));

        jTextField.setText("Enter your text here...");
        jTextField.setEnabled(false);

        javax.swing.GroupLayout jTextPanelLayout = new javax.swing.GroupLayout(jTextPanel);
        jTextPanel.setLayout(jTextPanelLayout);
        jTextPanelLayout.setHorizontalGroup(
            jTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTextPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jTextPanelLayout.createSequentialGroup()
                        .addComponent(jTextButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBoldButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jItalicButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFontSizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField)
                    .addComponent(jFontComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jTextPanelLayout.setVerticalGroup(
            jTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTextPanelLayout.createSequentialGroup()
                .addGroup(jTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jTextPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jTextButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jTextPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jItalicButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFontSizeComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBoldButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(7, 7, 7)
                .addComponent(jFontComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField)
                .addContainerGap())
        );

        jFileMenu.setText("File");

        jMenuNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/document_plain_new.png"))); // NOI18N
        jMenuNew.setText("New");
        jMenuNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNewActionPerformed(evt);
            }
        });
        jFileMenu.add(jMenuNew);

        jMenuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/folder_out.png"))); // NOI18N
        jMenuOpen.setText("Open");
        jMenuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuOpenActionPerformed(evt);
            }
        });
        jFileMenu.add(jMenuOpen);

        jMenuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/save_as.png"))); // NOI18N
        jMenuSave.setText("Save");
        jMenuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveActionPerformed(evt);
            }
        });
        jFileMenu.add(jMenuSave);

        jMenuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/close.png"))); // NOI18N
        jMenuExit.setText("Exit");
        jMenuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuExitActionPerformed(evt);
            }
        });
        jFileMenu.add(jMenuExit);

        jMenuBar.add(jFileMenu);

        jEditMenu.setText("Edit");

        jMenuUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMenuUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/Undo_icon.png"))); // NOI18N
        jMenuUndo.setText("Undo");
        jMenuUndo.setEnabled(false);
        jMenuUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuUndoActionPerformed(evt);
            }
        });
        jEditMenu.add(jMenuUndo);

        jMenuRedo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        jMenuRedo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paintapplication/Redo_icon.png"))); // NOI18N
        jMenuRedo.setText("Redo");
        jMenuRedo.setEnabled(false);
        jMenuRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuRedoActionPerformed(evt);
            }
        });
        jEditMenu.add(jMenuRedo);

        jMenuBar.add(jEditMenu);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jDrawingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jColorPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSizePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jToolsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .addComponent(jTextPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jDrawingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToolsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSizePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Usage : Called after an mouse button is pressed on the drawing panel
    // Before: Nothing
    // After : The start point has been set.
    private void jDrawingPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jDrawingPanelMousePressed
        startPoint = evt.getPoint();

        // Add the next point used to draw the pencil and eraser
        if (isCurrentTool("Pencil") || isCurrentTool("Eraser")) {
            points.add(startPoint);
        }
    }//GEN-LAST:event_jDrawingPanelMousePressed

    // Usage : Called after an mouse button is released
    // Before: Nothing
    // After : The end point has been set and the drawing panel has 
    //         been repainted.
    private void jDrawingPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jDrawingPanelMouseReleased
        endPoint = evt.getPoint();

        // Add the next point used to draw the pencil and eraser
        if (isCurrentTool("Pencil") || isCurrentTool("Eraser")) {
            points.add(endPoint);
        }


        // Set the flags used by the repaint function.
        dragged = false;
        released = true;
        jDrawingPanel.repaint();
    }//GEN-LAST:event_jDrawingPanelMouseReleased

    // Usage : Called after an mouse cursor was dragged
    // Before: Nothing
    // After : The gui has been updated to show the current result of the tool
    //         used.
    private void jDrawingPanelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jDrawingPanelMouseDragged
        dragPoint = evt.getPoint();

        // Add the next point used to draw the pencil and eraser
        if (isCurrentTool("Pencil") || isCurrentTool("Eraser")) {
            points.add(dragPoint);
        }

        // Set the flags used by the repaint function.
        dragged = true;
        jDrawingPanel.repaint();
    }//GEN-LAST:event_jDrawingPanelMouseDragged

    // Usage : Called after the pencil button was toggled
    // Before: Nothing
    // After : The current tool has been updated and all other buttons have
    //         been untoggled.
    private void jPencilButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jPencilButtonItemStateChanged
        if (evt.getStateChange() == 1) {
            toggleButtons(1);
        } else if (isCurrentTool("Pencil")) {
            toggleButtons(0);
        }
    }//GEN-LAST:event_jPencilButtonItemStateChanged

    // Usage : Called after the exit menu action was used
    // Before: Nothing
    // After : The application has closed
    private void jMenuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuExitActionPerformed
        // Display yes no dialog
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure "
                + ", this will destroy your current image if it is not saved",
                "Warning", dialogButton);
        if (dialogResult == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_jMenuExitActionPerformed

    // Usage : Called after the save menu action was used
    // Before: Nothing
    // After : The user has gone through the file chooser dialog
    private void jMenuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveActionPerformed
        // Set the default file name
        fileChooser.setSelectedFile(new File("Untitled.png"));

        // Show the file chooser save dialog
        int returnVal = fileChooser.showSaveDialog(null);

        // Save the file
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File imageFile = fileChooser.getSelectedFile();
            try {
                ImageIO.write(((DrawingPanel) jDrawingPanel).getCanvas(), "png", imageFile);
            } catch (IOException ex) {
                Logger.getLogger(PaintProgram.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuSaveActionPerformed

    // Usage : Called after the new menu action was used
    // Before: Nothing
    // After : The drawing panel has been emptied
    private void jMenuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuNewActionPerformed
        // Create a new empty BufferedImage with a white background and the 
        // same dimensions as the current image
        BufferedImage newImage = new BufferedImage(DrawingPanel.MAX_WIDTH,
                DrawingPanel.MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = newImage.createGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, newImage.getWidth(), newImage.getHeight());

        // Set the new image in the panel and reset all buttons.
        ((DrawingPanel) jDrawingPanel).setNewCanvas(newImage);
        toggleButtons(0);
        jDrawingPanel.repaint();

    }//GEN-LAST:event_jMenuNewActionPerformed

    // Usage : Called after the stroke slider has changed
    // Before: Nothing
    // After : The current Stroke size has been set with the value in the
    //         the slider
    private void jStrokeSizeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jStrokeSizeSliderStateChanged
        JSlider slider = (JSlider) evt.getSource();

        // Only change the stroke size if the slider has 
        // reached it's final value
        if (!slider.getValueIsAdjusting()) {
            currentStrokeSize = slider.getValue();
            if (!isCurrentTool("Eraser")) {
                pencilStrokeSize = currentStrokeSize;
            } else {
                eraserStrokeSize = currentStrokeSize;
            }
        }
    }//GEN-LAST:event_jStrokeSizeSliderStateChanged

    // Usage : Called after the eraser button was toggled
    // Before: Nothing
    // After : The current tool has been updated and all other buttons have
    //         been untoggled.
    private void jEraserButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jEraserButtonItemStateChanged
        // TODO add your handling code here:
        if (evt.getStateChange() == 1) {
            toggleButtons(2);
        } else if (isCurrentTool("Eraser")) {
            toggleButtons(0);
        }
    }//GEN-LAST:event_jEraserButtonItemStateChanged

    // Usage : Called after the line button was toggled
    // Before: Nothing
    // After : The current tool has been updated and all other buttons have
    //         been untoggled.
    private void jLineButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jLineButtonItemStateChanged
        // TODO add your handling code here:
        if (evt.getStateChange() == 1) {
            toggleButtons(3);
        } else if (isCurrentTool("Line")) {
            toggleButtons(0);
        }
    }//GEN-LAST:event_jLineButtonItemStateChanged

    // Usage : Called after the rectangle button was toggled
    // Before: Nothing
    // After : The current tool has been updated and all other buttons have
    //         been untoggled.
    private void jRectangleButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRectangleButtonItemStateChanged
        // TODO add your handling code here:
        if (evt.getStateChange() == 1) {
            toggleButtons(4);
        } else if (isCurrentTool("Rectangle")) {
            toggleButtons(0);
        }
    }//GEN-LAST:event_jRectangleButtonItemStateChanged

    // Usage : Called after the oval button was toggled
    // Before: Nothing
    // After : The current tool has been updated and all other buttons have
    //         been untoggled.
    private void jOvalButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jOvalButtonItemStateChanged
        // TODO add your handling code here:
        if (evt.getStateChange() == 1) {
            toggleButtons(5);
        } else if (isCurrentTool("Oval")) {
            toggleButtons(0);
        }
    }//GEN-LAST:event_jOvalButtonItemStateChanged

    // Usage : Called when the set color button is used
    // Before: Nothing
    // After : The user has gone through the color chooser dialog
    private void jSetColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSetColorButtonActionPerformed
        // Set the current color of the chooser
        colorChooser.setColor(currentColor);

        // Create the dialog with an action listener
        JDialog dialog = JColorChooser.createDialog(PaintProgram.this,
                "Choose a color", true, colorChooser, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = colorChooser.getColor();
                // If a color was choosen the current color used is set
                if (newColor != null) {
                    setCurrentColor(newColor);

                    // Set the color of the current color panel with the 
                    // new color
                    jCurrentColorPanel.setBackground(currentColor);
                }
            }
        }, null);

        // Display the dialog
        dialog.setVisible(true);
    }//GEN-LAST:event_jSetColorButtonActionPerformed

    // Usage : Called after the open menu action was used
    // Before: Nothing
    // After : The user has gone through the open file chooser dialog
    private void jMenuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuOpenActionPerformed
        // Display yes no dialog
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure "
                + ", this will destroy your current image if it is not saved",
                "Warning", dialogButton);
        if (dialogResult == JOptionPane.YES_OPTION) {
            // Show the open file dialog
            int returnVal = fileChooser.showOpenDialog(null);

            // If a valid file was choosen place it in a BufferedImage
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File openFile = fileChooser.getSelectedFile();
                BufferedImage fileImage = new BufferedImage(DrawingPanel.MAX_WIDTH,
                        DrawingPanel.MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);
                try {
                    fileImage = ImageIO.read(openFile);
                } catch (IOException ex) {
                    Logger.getLogger(PaintProgram.class.getName()).log(Level.SEVERE, null, ex);
                }

                // Set the the chosen image in the panel and repaint the panel
                toggleButtons(0);
                ((DrawingPanel) jDrawingPanel).setNewCanvas(fileImage);
                jDrawingPanel.repaint();
            }
        }

    }//GEN-LAST:event_jMenuOpenActionPerformed

    // Usage : Called after the menu undo action was used
    // Before: Nothing
    // After : The last operation on the drawing panel has been undone
    private void jMenuUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuUndoActionPerformed
        ((DrawingPanel) jDrawingPanel).undo();
    }//GEN-LAST:event_jMenuUndoActionPerformed

    // Usage : Called after the menu redo action was used
    // Before: Nothing
    // After : The last undo operation has been undone
    private void jMenuRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuRedoActionPerformed
        ((DrawingPanel) jDrawingPanel).redo();
    }//GEN-LAST:event_jMenuRedoActionPerformed

    // Usage : Called after the fill button was toggled
    // Before: Nothing
    // After : The current tool has been updated and all other buttons have
    //         been untoggled.
    private void jFillButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jFillButtonItemStateChanged
        if (evt.getStateChange() == 1) {
            toggleButtons(6);
        } else if (isCurrentTool("Fill")) {
            toggleButtons(0);
        }
    }//GEN-LAST:event_jFillButtonItemStateChanged

    // Usage : Called after the text button was toggled
    // Before: Nothing
    // After : The current tool has been updated and all other buttons have
    //         been untoggled.
    private void jTextButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jTextButtonItemStateChanged
        if (evt.getStateChange() == 1) {
            toggleButtons(7);

            // Enable all the text options in the text panel
            jBoldButton.setEnabled(true);
            jItalicButton.setEnabled(true);
            jFontComboBox.setEnabled(true);
            jFontSizeComboBox.setEnabled(true);
            jTextField.setEnabled(true);
        } else {
            if (isCurrentTool("Text")) {
                toggleButtons(0);
            }

            // Disable all the text options in the text panel
            jBoldButton.setEnabled(false);
            jItalicButton.setEnabled(false);
            jFontComboBox.setEnabled(false);
            jFontSizeComboBox.setEnabled(false);
            jTextField.setEnabled(false);
        }
    }//GEN-LAST:event_jTextButtonItemStateChanged

    /*
     * Internal class that stores the canvas that the user draws on and
     * holds the operations that change the canvas
     */
    private class DrawingPanel extends JPanel {

        /*
         * Data Invariant
         * canvas - used to store the current image displayed in the program
         * UNDO_HISTORY_LENGTH - the number of undo operations the user can do
         * undos - Holds the images that the user can revert to
         * redos - Holds the images that the user has undone
         * MAX_WIDTH - the maximum width of the canvas
         * MAX_HEIGHT - the maximum height of the canvas
         * 
         * Before every change to the canvas a copy is taken and placed in 
         * the undos array. If a change is undone it is placed in the redos
         * array and the user can redo changes that where undone.
         */
        private BufferedImage canvas;
        private final int UNDO_HISTORY_LENGTH = 5;
        private BufferedImage[] undos = new BufferedImage[UNDO_HISTORY_LENGTH];
        private BufferedImage[] redos = new BufferedImage[UNDO_HISTORY_LENGTH];
        public static final int MAX_WIDTH = 500;
        public static final int MAX_HEIGHT = 500;

        // Usage : drawingPanel = new DrawingPanel()
        // Before: nothing
        // After : A new Drawing panel has been initilized
        public DrawingPanel() {
            // Create a new canvas and paint the background white
            canvas = new BufferedImage(MAX_WIDTH, MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = canvas.createGraphics();
            g2.setBackground(Color.WHITE);
            g2.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }

        // Usage : canvas = drawingPanel.getCanvas()
        // Before: Nothing
        // After : canvas holds a bufferedImage of the current displayed canvas
        public BufferedImage getCanvas() {
            return canvas;
        }

        // Usage : drawingPanel.setCanvas(newCanvas)
        // Before: Nothing
        // After : The canvas has been changed to newCanvas       
        public void setNewCanvas(BufferedImage newCanvas) {
            Graphics2D g2 = canvas.createGraphics();
            g2.setBackground(Color.WHITE);
            g2.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            undos = new BufferedImage[UNDO_HISTORY_LENGTH];
            redos = new BufferedImage[UNDO_HISTORY_LENGTH];
            g2.drawImage(newCanvas, 0, 0, null);
            g2.dispose();
        }

        // Usage : Called by the repaint method
        // Before: Nothing
        // After : The drawing panel has been updated.
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Disables the undo menu action if there are no undos
            if (undos[0] == null) {
                jMenuUndo.setEnabled(false);
            }

            // Disables the redo menu action if there are no redoes
            if (redos[0] == null) {
                jMenuRedo.setEnabled(false);
            }

            // Update the screen if there is a dragged tool in use
            if (dragged) {
                // Set the startPoint if there was no mouse pressed event
                if (startPoint == null) {
                    startPoint = dragPoint;
                }

                // Draw the canvas on the panel
                Graphics2D g2 = (Graphics2D) g;
                g2.drawImage(canvas, 0, 0, null);

                if (isCurrentTool("Pencil") || isCurrentTool("Eraser")) {
                    g2.setColor(getCurrentColor());
                    g2.setStroke(new BasicStroke(getCurrentStrokeSize()));
                    for (int i = 0; i < points.size() - 1; i++) {
                        Point p1 = points.get(i);
                        Point p2 = points.get(i + 1);
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                } else {
                    // Display the current shape of the line, rectangle or oval
                    // that is being drawn

                    // Set the stroke and color of the underline
                    g2.setStroke(new BasicStroke(1));
                    g2.setColor(Color.WHITE);

                    // Set the rendering to make smooth shapes
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    // Calculations for oval and rectangle
                    int width = Math.abs(startPoint.x - dragPoint.x);
                    int height = Math.abs(startPoint.y - dragPoint.y);
                    int x = Math.min(startPoint.x, dragPoint.x);
                    int y = Math.min(startPoint.y, dragPoint.y);

                    if (isCurrentTool("Line")) {
                        g2.drawLine(startPoint.x, startPoint.y, dragPoint.x, dragPoint.y);
                    } else if (isCurrentTool("Rectangle")) {
                        g2.drawRect(x, y, width, height);
                    } else if (isCurrentTool("Oval")) {
                        g2.drawOval(x, y, width, height);
                    }

                    // Set the color and Stroke of the overline
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(1F, BasicStroke.CAP_SQUARE,
                            BasicStroke.JOIN_MITER, 2F, new float[]{5F, 5F}, 0F));
                    if (isCurrentTool("Line")) {
                        g2.drawLine(startPoint.x, startPoint.y, dragPoint.x, dragPoint.y);
                    } else if (isCurrentTool("Rectangle")) {
                        g2.drawRect(x, y, width, height);
                    } else if (isCurrentTool("Oval")) {
                        g2.drawOval(x, y, width, height);
                    }
                }
                // We don't want to draw the dragged shape again
                g2.dispose();
                dragged = false;
            }

            // Draw the final shape on the canvas
            if (released && !isCurrentTool("None")) {
                drawShape();

                // If something is drawn then all redos are discarded
                redos = new BufferedImage[UNDO_HISTORY_LENGTH];
                released = false;
            }

            // Enable the undo menu action if something can be undone
            if (undos[0] != null) {
                jMenuUndo.setEnabled(true);
            }

            // Enable the redo menu action if an undo can be redone
            if (redos[0] != null) {
                jMenuRedo.setEnabled(true);
            }
        }

        // Usage : drawShape()
        // Before: The mouse has been released
        // After : The current tool has been used on the canvas
        private void drawShape() {
            // Before any change is done we make an undo point so we
            // can revert to it
            setUndoPoint();

            // Get the graphics of the canvas and set stroke and color
            Graphics2D g2 = canvas.createGraphics();
            g2.drawImage(canvas, 0, 0, null);
            g2.setColor(getCurrentColor());
            g2.setStroke(new BasicStroke(getCurrentStrokeSize()));
            if (isCurrentTool("Pencil") || isCurrentTool("Eraser")) {
                // Go through all the points in points and draw
                // a line between every two adjacent points
                for (int i = 0; i < points.size() - 1; i++) {
                    Point p1 = points.get(i);
                    Point p2 = points.get(i + 1);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
                points.clear();
            } else if (isCurrentTool("Line")) {
                g2.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
            } else if (isCurrentTool("Rectangle")) {
                int width = Math.abs(startPoint.x - endPoint.x);
                int height = Math.abs(startPoint.y - endPoint.y);
                int x = Math.min(startPoint.x, endPoint.x);
                int y = Math.min(startPoint.y, endPoint.y);
                g2.drawRect(x, y, width, height);
            } else if (isCurrentTool("Oval")) {
                int width = Math.abs(startPoint.x - endPoint.x);
                int height = Math.abs(startPoint.y - endPoint.y);
                int x = Math.min(startPoint.x, endPoint.x);
                int y = Math.min(startPoint.y, endPoint.y);
                g2.drawOval(x, y, width, height);
            } else if (isCurrentTool("Fill")) {
                // Create a array list of points that have to be colored
                ArrayList<Point> pointsToColor = new ArrayList<>();
                pointsToColor.add(startPoint);

                // Copy the contents of the current canvas
                BufferedImage newImage = new BufferedImage(MAX_WIDTH, MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);
                Graphics g = newImage.createGraphics();
                g.drawImage(canvas, 0, 0, MAX_WIDTH, MAX_HEIGHT, this);

                // Initialize the current color and the fillColor
                int initialColor = newImage.getRGB(startPoint.x, startPoint.y);
                int fillColor = getCurrentColor().getRGB();

                while (pointsToColor.size() > 0) {
                    // Remove the first point in the array list
                    Point p = pointsToColor.remove(0);
                    int x = p.x;
                    int y = p.y;
                    if (newImage.getRGB(x, y) == initialColor && initialColor != fillColor) {
                        // Change the color to fillColor at the point p
                        newImage.setRGB(x, y, fillColor);

                        int north = y + 1;
                        int south = y - 1;
                        int east = x - 1;
                        int west = x + 1;

                        if (east >= 0) {
                            pointsToColor.add(new Point(east, y));
                        }
                        if (west < MAX_WIDTH) {
                            pointsToColor.add(new Point(west, y));
                        }
                        if (south >= 0) {
                            pointsToColor.add(new Point(x, south));
                        }
                        if (north < MAX_HEIGHT) {
                            pointsToColor.add(new Point(x, north));
                        }
                    }

                }

                // Draw the image with fill to the canvas
                g = canvas.createGraphics();
                g.drawImage(newImage, 0, 0, MAX_WIDTH, MAX_HEIGHT, this);
            } else if (isCurrentTool("Text")) {
                g2.setFont(getCurrentFont());
                g2.drawString(getCurrentText(), endPoint.x, endPoint.y);
            }

        }

        // Usage : drawingPanel.setUndoPoint()
        // Before: Nothing
        // After : The current canvas has been added to drawingPanel's undos.
        public void setUndoPoint() {
            // Make room for the current canvas
            for (int i = undos.length - 2; i >= 0; i--) {
                undos[i + 1] = undos[i];
            }

            // Add the current canvas to the front of 
            undos[0] = new BufferedImage(MAX_WIDTH, MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics g = undos[0].createGraphics();
            g.drawImage(canvas, 0, 0, MAX_WIDTH, MAX_HEIGHT, this);
        }

        // Usage : drawingPanel.undo()
        // Before: Nothing
        // After : The last change to drawingPanel has been undone
        public void undo() {
            if (undos[0] == null) {
                return;
            }

            // Make space for the new redo
            for (int i = redos.length - 2; i >= 0; i--) {
                redos[i + 1] = redos[i];
            }

            // Add the current canvas to redoes
            redos[0] = new BufferedImage(MAX_WIDTH, MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics g = redos[0].createGraphics();
            g.drawImage(canvas, 0, 0, MAX_WIDTH, MAX_HEIGHT, this);

            // Draw the the the canvas before the change
            g = canvas.createGraphics();
            g.drawImage(undos[0], 0, 0, MAX_WIDTH, MAX_HEIGHT, this);

            // Move all undos to the front
            for (int i = 0; i < undos.length - 1; i++) {
                undos[i] = undos[i + 1];
            }

            // Delete the last undo.
            undos[undos.length - 1] = null;

            // Repaint the panel.
            repaint();
        }

        // Usage : drawingPanle.redo()
        // Before: Nothing
        // After : The last undo has been redone.
        public void redo() {
            if (redos[0] == null) {
                return;
            }
            // Set a new undo point
            setUndoPoint();

            // Draw the redo to the canvas
            Graphics g = canvas.createGraphics();
            g.drawImage(redos[0], 0, 0, MAX_WIDTH, MAX_HEIGHT, this);

            // Delete the redo
            for (int i = 0; i < redos.length - 1; i++) {
                redos[i] = redos[i + 1];
            }
            redos[redos.length - 1] = null;

            // Repaint the canvas
            repaint();
        }
    }

    // Usage : main(args) 
    // Before: Nothing 
    // After : The gui window is running 
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PaintProgram.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PaintProgram().setVisible(true);
            }
        });
    }
    /*
     * Data invariant for swing components
     * colorChooser - the color chooser for the colors the program uses
     * fileChooser - the file chooser that that is used to open and save files
     * jBoldButton - used to set the text from the text field bold
     * jCanvasLabel - the label that displays the canvas 
     * jColorPanle - the panel that holds the color options components
     * jCurrentColorPanel - displays the current color in use
     * jDrawingPanel - the panel that holds the drawing area
     * jEditMenu - the edit menu holds the undo and redo operations
     * jEraserButton - used to toggle the eraser tool on and off
     * jFileMenu - the file menu holds the new, open, save, and exit operations
     * jFillButton - used to toggle the fill tool on and off
     * jFontComboBox - used to choose the font family of the current font
     * jFontSizeComboBox - used to choose the font size of the current font
     * jItalicButton - used to set the text from the text field bold
     * jLineButton - used to toggle the line tool on and off
     * jMenuBar - holds the edit and file menus
     * jMenuExit - the exit operation
     * jMenuNew - the new operation
     * jMenuOpen - the open operation
     * jMenuRedo - the redo operation
     * jMenuSave - the save operation
     * jMenuUndo - the undo operation
     * jOvalButton - used to toggle the oval tool on and off
     * jPencilButton - used to toggle the pencil tool on and off
     * jRectangleButton - used to toggle the rectangle tool on and off
     * jSetColorButton - used to call the color chooser
     * jSizePanel - holds the size slider
     * jStrokeSizeSlider - used to change the current stroke size
     * jTextButton - used to toggle the text tool on and off
     * jTextField - used to set the text that is drawn to the canvas
     * jTextPanel - holds all the text options
     * jToolsPanel - holds all the tools 
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JColorChooser colorChooser;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JToggleButton jBoldButton;
    private javax.swing.JLabel jCanvasLabel;
    private javax.swing.JPanel jColorPanel;
    private javax.swing.JPanel jCurrentColorPanel;
    private javax.swing.JPanel jDrawingPanel;
    private javax.swing.JMenu jEditMenu;
    private javax.swing.JToggleButton jEraserButton;
    private javax.swing.JMenu jFileMenu;
    private javax.swing.JToggleButton jFillButton;
    private javax.swing.JComboBox jFontComboBox;
    private javax.swing.JComboBox jFontSizeComboBox;
    private javax.swing.JToggleButton jItalicButton;
    private javax.swing.JToggleButton jLineButton;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenuItem jMenuExit;
    private javax.swing.JMenuItem jMenuNew;
    private javax.swing.JMenuItem jMenuOpen;
    private javax.swing.JMenuItem jMenuRedo;
    private javax.swing.JMenuItem jMenuSave;
    private javax.swing.JMenuItem jMenuUndo;
    private javax.swing.JToggleButton jOvalButton;
    private javax.swing.JToggleButton jPencilButton;
    private javax.swing.JToggleButton jRectangleButton;
    private javax.swing.JButton jSetColorButton;
    private javax.swing.JPanel jSizePanel;
    private javax.swing.JSlider jStrokeSizeSlider;
    private javax.swing.JToggleButton jTextButton;
    private javax.swing.JTextField jTextField;
    private javax.swing.JPanel jTextPanel;
    private javax.swing.JPanel jToolsPanel;
    // End of variables declaration//GEN-END:variables
}
