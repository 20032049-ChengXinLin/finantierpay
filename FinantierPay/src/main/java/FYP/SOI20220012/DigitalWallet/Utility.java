/**
*
* I declare that this code was written by me, 20009334.
* I will not copy or allow others to copy my code.
* I understand that copying code is considered as plagiarism.
*
* Student Name: Cheng Xin Lin (20032049), Koh Siew Gek (20008303), Chen Wan Ting (20009334)
* Team ID: SOI-2022-0012
* Team Project ID: SOI-2022-2210-0049
* Project: [IP] Digital Wallet
* Date created: 2022-May-18 3:48:36 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 20009334
 *
 */
public class Utility {
	
    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

}
