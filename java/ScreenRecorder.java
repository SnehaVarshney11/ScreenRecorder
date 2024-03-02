import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jcodec.api.awt.AWTSequenceEncoder;

public class ScreenRecorder {
    public static void main(String[] args) throws AWTException, IOException {
        List<BufferedImage> imgList = new ArrayList<>();

        Rectangle screeRectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        Robot robot = new Robot();

        File file = new File("outputVideo.mp4");

        System.out.println("Getting scan images...");

        int count = 0;

        while (count < 100) {
            BufferedImage img = robot.createScreenCapture(screeRectangle);
            imgList.add(img);

            count++;
        }

        makeVideoFromImage(imgList, file);
    }

    public static void makeVideoFromImage(List<BufferedImage> imgList, File file) throws IOException {
        AWTSequenceEncoder sequenceEncoder = AWTSequenceEncoder.createSequenceEncoder(file, 25);

        for(int i=0; i<imgList.size(); i++) {
            sequenceEncoder.encodeImage(imgList.get(i));

            System.out.println("List encode " + i);
        }
        sequenceEncoder.finish();
    }
}