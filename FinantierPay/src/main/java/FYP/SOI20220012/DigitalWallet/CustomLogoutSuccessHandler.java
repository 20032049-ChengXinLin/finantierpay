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
* Date created: 2022-May-16 1:19:31 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

/**
 * @author 20032049
 *
 */
@Service
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	@Autowired
	private AccountRepository accountRepo;

	@Override
	public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Authentication authentication) throws IOException, ServletException {


		if (authentication != null && authentication.getDetails() != null) {
			try {
				httpServletRequest.getSession().invalidate();
				System.out.println("User Successfully Logout");
				AccountDetails loggedInAccount = (AccountDetails) authentication.getPrincipal();
				LocalDateTime dateTime = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				loggedInAccount.getAccount().setLast_login_date_time(dateTime);
				accountRepo.save(loggedInAccount.getAccount());
				// you can add more codes here when the user successfully logs out,
				// such as updating the database for last active.
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		// redirect to login
		httpServletResponse.sendRedirect("/login");
	}
	
}