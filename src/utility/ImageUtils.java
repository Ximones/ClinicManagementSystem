package utility;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Chok Chun Fai
 */
public class ImageUtils {

    // A private, static field to hold the cached logo image.
    private static ImageIcon imageIcon;
    private static JLabel imageLabel;
    private static final String IMAGE_DIR = "/resources/";

    public static JLabel getImageLabel(String imageName, JLabel jLabel) {

        imageLabel = jLabel;

        if (imageLabel != null) {
            try {
                imageLabel.setIcon(getLogoIcon(IMAGE_DIR + imageName, 100));
                imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                imageLabel.setBorder(new javax.swing.border.EmptyBorder(10, 0, 0, 0));
            } catch (Exception ex) {
                imageLabel.setText("Logo not found!");
            }
        }

        return imageLabel;
    }

    /**
     * Gets the shared logo ImageIcon. The image is loaded from the disk only
     * the first time this method is called. Subsequent calls return the cached
     * instance.
     *
     * @param imageDir
     * @param imageSize
     * @return The ImageIcon for the logo.
     */
    public static ImageIcon getLogoIcon(String imageDir, int imageSize) {
        // Check if the icon has been loaded yet
        if (imageIcon == null) {
            // If not, load it from the disk
            try {
                ImageIcon originalIcon = new ImageIcon(ImageUtils.class.getResource(imageDir));
                Image resizedImage = originalIcon.getImage().getScaledInstance(-1, imageSize, Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(resizedImage); // Store the resized icon in our static field
            } catch (Exception ex) {
                System.err.println("Image not found: " + ex.getMessage());
                // Optionally return a placeholder or null
            }
        }
        // Return the cached icon
        return imageIcon;
    }
}
