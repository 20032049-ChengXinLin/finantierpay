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
* Date created: 2022-Jul-03 3:50:15 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.braintreegateway.BraintreeGateway;

/**
 * @author 20032049
 *
 */
public class BraintreeGatewayFactory {
    public static BraintreeGateway fromConfigMapping(Map<String, String> mapping) {
        return new BraintreeGateway(
            mapping.get("BT_ENVIRONMENT"),
            mapping.get("BT_MERCHANT_ID"),
            mapping.get("BT_PUBLIC_KEY"),
            mapping.get("BT_PRIVATE_KEY")
        );
    }

    public static BraintreeGateway fromConfigFile(File configFile) {
        InputStream inputStream = null;
        Properties properties = new Properties();

        try {
            inputStream = new FileInputStream(configFile);
            properties.load(inputStream);
        } catch (Exception e) {
            System.err.println("Exception: " + e);
        } finally {
            try { inputStream.close(); }
            catch (IOException e) { System.err.println("Exception: " + e); }
        }

        return new BraintreeGateway(
            properties.getProperty("BT_ENVIRONMENT"),
            properties.getProperty("BT_MERCHANT_ID"),
            properties.getProperty("BT_PUBLIC_KEY"),
            properties.getProperty("BT_PRIVATE_KEY")
        );
    }
}