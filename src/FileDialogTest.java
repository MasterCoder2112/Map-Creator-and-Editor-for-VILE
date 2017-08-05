import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** @see http://stackoverflow.com/questions/2914733 */
public class FileDialogTest {

    public static void main(String[] args) {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(0, 1));
        frame.add(new JButton(new AbstractAction("Load") {

            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fd = new FileDialog(frame, "Test", FileDialog.LOAD);
                fd.setVisible(true);
                System.out.println(fd.getFiles()[0].getParent());
            }
        }));
        frame.add(new JButton(new AbstractAction("Save") {

            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fd = new FileDialog(frame, "Test", FileDialog.SAVE);
                fd.setVisible(true);
                System.out.println(fd.getFiles()[0].getParent());
            }
        }));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
