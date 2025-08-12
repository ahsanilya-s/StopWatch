import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RoundedTimer extends JPanel {
    private long endMillis = 0;
    private boolean running = false;
    private final Timer tick;
    private long repeatMillis = 0; // 0 = no repeat
    private long lastDuration = 0;

    public RoundedTimer() {
        setPreferredSize(new Dimension(300, 300));
        setBackground(Color.WHITE);
        tick = new Timer(100, e -> {
            if (running) {
                long rem = endMillis - System.currentTimeMillis();
                if (rem <= 0) {
                    // time finished
                    Toolkit.getDefaultToolkit().beep(); // simple beep
                    if (repeatMillis > 0) {
                        // schedule next alarm after repeatMillis
                        endMillis = System.currentTimeMillis() + repeatMillis;
                    } else {
                        running = false;
                        endMillis = 0;
                    }
                }
            }
            repaint();
        });
        tick.start();
    }

    // paint round watch and time text
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int size = Math.min(w, h) - 20;

        // background circle
        g2.setColor(new Color(230, 240, 255));
        g2.fillRoundRect((w - size) / 2, (h - size) / 2, size, size, 200, 200);

        // inner circle
        g2.setColor(new Color(180, 200, 245));
        int inner = size - 20;
        g2.fillOval((w - inner) / 2, (h - inner) / 2, inner, inner);

        // border
        g2.setStroke(new BasicStroke(6f));
        g2.setColor(new Color(60, 80, 160));
        g2.drawOval((w - inner) / 2, (h - inner) / 2, inner, inner);

        // time text
        String timeText = getTimeText();
        g2.setFont(new Font("SansSerif", Font.BOLD, 32));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(timeText)) / 2;
        int ty = h / 2 + fm.getAscent() / 2 - 10;
        g2.setColor(Color.WHITE);
        g2.drawString(timeText, tx, ty);

        g2.dispose();
    }

    private String getTimeText() {
        long rem = running ? Math.max(0, endMillis - System.currentTimeMillis()) : lastDuration;
        long totalSeconds = rem / 1000;
        long mm = totalSeconds / 60;
        long ss = totalSeconds % 60;
        return String.format("%02d:%02d", mm, ss);
    }

    // UI builder
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Rounded Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        RoundedTimer panel = new RoundedTimer();

        // Controls
        JTextField minutesField = new JTextField("0", 3);
        JTextField secondsField = new JTextField("10", 3); // default 10s
        JButton startBtn = new JButton("Start");
        JButton stopBtn = new JButton("Stop");
        JButton resetBtn = new JButton("Reset");

        // Repeat controls
        JCheckBox repeatCheck = new JCheckBox("Repeat every");
        JTextField rmin = new JTextField("0", 3);
        JTextField rsec = new JTextField("30", 3); // default repeat 30s
        JLabel secLabel = new JLabel("sec");

        JPanel top = new JPanel();
        top.add(new JLabel("Min"));
        top.add(minutesField);
        top.add(new JLabel("Sec"));
        top.add(secondsField);
        top.add(startBtn);
        top.add(stopBtn);
        top.add(resetBtn);

        JPanel bottom = new JPanel();
        bottom.add(repeatCheck);
        bottom.add(new JLabel("Min"));
        bottom.add(rmin);
        bottom.add(new JLabel("Sec"));
        bottom.add(rsec);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(top, BorderLayout.NORTH);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().add(bottom, BorderLayout.SOUTH);

        // button actions
        startBtn.addActionListener(e -> {
            try {
                int m = Integer.parseInt(minutesField.getText().trim());
                int s = Integer.parseInt(secondsField.getText().trim());
                long dur = (m * 60L + s) * 1000L;
                if (dur <= 0) return;
                panel.lastDuration = dur;
                panel.endMillis = System.currentTimeMillis() + dur;
                panel.running = true;

                if (repeatCheck.isSelected()) {
                    int rm = Integer.parseInt(rmin.getText().trim());
                    int rs = Integer.parseInt(rsec.getText().trim());
                    long rdur = (rm * 60L + rs) * 1000L;
                    panel.repeatMillis = Math.max(0, rdur);
                } else {
                    panel.repeatMillis = 0;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter valid numbers.");
            }
        });

        stopBtn.addActionListener(e -> {
            panel.running = false;
            panel.endMillis = 0;
        });

        resetBtn.addActionListener(e -> {
            panel.running = false;
            panel.endMillis = 0;
            panel.lastDuration = 0;
            panel.repaint();
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoundedTimer::createAndShowGUI);
    }
}
