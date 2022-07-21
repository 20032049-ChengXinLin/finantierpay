/**
*
* I declare that this code was written by me, 20032049.
* I will not copy or allow others to copy my code.
* I understand that copying code is considered as plagiarism.
*
* Student Name: Cheng Xin Lin (20032049), Koh Siew Gek (20008303), Chen Wan Ting (20009334)
* Team ID: SOI-2022-0012
* Team Project ID: SOI-2022-2210-0049
* Project: [IP] Digital Wallet
* Date created: 2022-May-12 1:11:16 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

/**
 * @author 20032049
 *
 */
@Service
public class QRCodeReader {
	 public static String decodeQRCode(File qrCodeimage) throws IOException {
	        BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
	        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
	        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

	        try {
	            Result result = new MultiFormatReader().decode(bitmap);
	            return result.getText();
	        } catch (NotFoundException e) {
	            System.out.println("There is no QR code in the image");
	            return null;
	        }
	    }

	    public static void main(String[] args) {
	        try {
	            File file = new File("./MyQRCode.png");
	            String decodedText = decodeQRCode(file);
	            if(decodedText == null) {
	                System.out.println("No QR Code found in the image");
	            } else {
	                System.out.println("Decoded text = " + decodedText);
	            }
	        } catch (IOException e) {
	            System.out.println("Could not decode QR Code, IOException :: " + e.getMessage());
	        }
	    }
}
