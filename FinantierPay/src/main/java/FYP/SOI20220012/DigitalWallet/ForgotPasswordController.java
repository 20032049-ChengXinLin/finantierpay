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
* Date created: 2022-May-05 2:56:18 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.bytebuddy.utility.RandomString;

/**
 * @author 20032049
 *
 */
@Controller
public class ForgotPasswordController {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private AccountDetailsService accountDetailsService;

	@GetMapping("/forgot_password")
	public String showForgotPasswordForm(Model model) {
		model.addAttribute("pageTitle", "Forget Password");

		return "forgot_password";
	}

	@PostMapping("/forgot_password")
	public String processForgotPassword(HttpServletRequest request, Model model,
			RedirectAttributes redirectAttributes) {
		String email = request.getParameter("email");
		String token = RandomString.make(45);
		if (email.equals("")) {
			redirectAttributes.addFlashAttribute("email", "Please enter your email");
			return "redirect:/forgot_password";
		}
		try {
			accountDetailsService.updateResetPasswordToken(token, email);
			// generate reset password link
			// send email
			String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
			sendEmail(email, resetPasswordLink);
			model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
			model.addAttribute("emailaddress", email);
		} catch (AccountNotFoundException ex) {
			model.addAttribute("error", ex.getMessage());
		} catch (UnsupportedEncodingException | MessagingException e) {
			model.addAttribute("error", "Error while sending email");
		}

		return "forgot_password";
	}

	public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("finantierpay@outlook.com", "FinantierPay Support");
		helper.setTo(recipientEmail);

		String subject = "Here's the link to reset your password";

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + link
				+ "\">Change my password</a></p>" + "<br>" + "<p>Ignore this email if you do remember your password, "
				+ "or you have not made the request.</p>";

		helper.setSubject(subject);

		helper.setText(content, true);

		mailSender.send(message);
	}

	@GetMapping("/reset_password")
	public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
		Account account = accountDetailsService.getByResetPasswordToken(token);
		model.addAttribute("token", token);

		if (account == null) {
			return "invalid_reset_password_token";
		}

		return "reset_password";
	}

	@PostMapping("/reset_password")
	public String processResetPassword(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		String confirmpassword = request.getParameter("confirmpassword");
		Account account = accountDetailsService.getByResetPasswordToken(token);

		if (account == null) {
			return "invalid_reset_password_token";
		} else {
			String errorpassword = "";
			String errorconfirmpassword = "";
			if (password.equals("")) {
				errorpassword = "Please enter a new password";
			}
			if (confirmpassword.equals("")) {
				errorconfirmpassword = "Please confirm password";
			}
			if (!errorpassword.equals("") || !errorconfirmpassword.equals("")) {
				redirectAttributes.addFlashAttribute("password", errorpassword);
				redirectAttributes.addFlashAttribute("confirmpassword", errorconfirmpassword);
				return "redirect:/reset_password?token=" + token;
			}
			String regularexpression = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
			boolean pattern = confirmpassword.matches(regularexpression);
			if (pattern == true) {
				if (!password.equals(confirmpassword)) {
					redirectAttributes.addFlashAttribute("matchpassword", "Password do not match");
					return "redirect:/reset_password?token=" + token;
				}
			} else {
				redirectAttributes.addFlashAttribute("patternpassword",  "Password must contains Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character");
				return "redirect:/reset_password?token=" + token;
			}

			accountDetailsService.updatePassword(account, password);
			redirectAttributes.addFlashAttribute("message", "Reset Password success");
		}

		return "redirect:/login";
	}
}
