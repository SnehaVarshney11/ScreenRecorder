import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jcodec.api.awt.AWTSequenceEncoder;

public class ScreenRecorder extends JFrame{

    private final JButton startButton;
    private final JButton stopButton;
    private final JLabel statusLabel;
    private final List<BufferedImage> imgList;
    private final Rectangle screenRectangle;
    private final Robot robot;
    private File file;
    private AWTSequenceEncoder sequenceEncoder;
    private volatile boolean isRecording; //volatile indicates that the value can be modified by different threads. 

    public ScreenRecorder() throws AWTException {
        super("Screen Recorder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        statusLabel = new JLabel("Status: IDLE");
        imgList = new ArrayList<>();
        screenRectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        robot = new Robot();

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startRecording();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopRecording();
            }
        });

        // LAYOUTS COMPONENTS
        JPanel btnPanel = new JPanel();
        btnPanel.add(startButton);
        btnPanel.add(stopButton);

        JPanel statusPanel = new JPanel();
        statusPanel.add(statusLabel);

        setLayout(new BorderLayout());
        add(btnPanel, BorderLayout.NORTH);
        add(statusPanel, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(200, 100));

        pack();
        setResizable(false);
        setVisible(true);

    }

    private void startRecording() {
        if (!isRecording) {
            isRecording = true;
            file = new File("outputVideo.mp4");
            int counter = 1;
            while (file.exists()) {
                file = new File("outputVideo" + counter + ".mp4");
                counter++;
            }
            new Thread(() -> {
                try {
                    sequenceEncoder = AWTSequenceEncoder.createSequenceEncoder(file, 8);
                    
                    while (isRecording) {
                        BufferedImage img = robot.createScreenCapture(screenRectangle);
                        imgList.add(img);
                        sequenceEncoder.encodeImage(img);
                        statusLabel.setText("Status: Recording");
                        Thread.sleep(40); 
                    }
                    sequenceEncoder.finish();
                    statusLabel.setText("Status: Recording stopped");
                    isRecording = false;
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void stopRecording() {
        isRecording = false;
    }

    public static void main(String[] args) throws AWTException {
        new ScreenRecorder();
    }

}