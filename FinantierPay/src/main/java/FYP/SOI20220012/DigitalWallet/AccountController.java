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
* Date created: 2022-May-05 2:52:34 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import net.bytebuddy.utility.RandomString;

/**
 * @author 20032049
 *
 */
@Controller
public class AccountController {
	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private NotificationsService notificationsService;
	
	@Autowired
	private JavaMailSender javaMailSender;

	@GetMapping("/register")
	public String registerAccount(Model model) {
		model.addAttribute("account", new Account());

		List<Country> countryList = countryRepository.findAll();
		model.addAttribute("countryList", countryList);

		// Date of Birth
		LocalDate currentdate = LocalDate.now();
		int currentYear = currentdate.getYear();
		int birthYear = currentYear - 12;
		String minAge = Integer.toString(birthYear) + "-12-31";
		model.addAttribute("minAge", minAge);

		// JavaScript Check For uniqueness
		List<Account> listAccount = accountRepository.findAll();
		model.addAttribute("listAccount", listAccount);

		return "register";
	}

	@PostMapping("/process_register")
	public String processRegister(@Valid Account account, BindingResult bindingResult,
			@RequestParam("account_password") String password, RedirectAttributes redirectAttribute,
			HttpServletRequest request, Model model) throws UnsupportedEncodingException, MessagingException {

		if (bindingResult.hasErrors()) {
			List<Country> countryList = countryRepository.findAll();
			model.addAttribute("countryList", countryList);
			return "register";
		}
		String phone = "";
		String postal = "";
		if (account.getCountry().getCountryId() == 1) {
			String regexPhone = "^$|^[689]\\d{7}$";
			boolean patternPhone = account.getPhone().matches(regexPhone);
			if (patternPhone == false) {
				phone = "Mobile Number Wrong Format!";
			}
			String regexPostal = "^$|^\\d{6}$";
			boolean patternPostal = account.getPostal_code().matches(regexPostal);
			if (patternPostal == false) {
				postal = "Postal Code Wrong Format!";
			}
		} else if (account.getCountry().getCountryId() == 2) {
			String regexPhone = "^(\\+?6?01)[02-46-9][-][0-9]{7}$|^(\\+?6?01)[1][-][0-9]{8}$";
			boolean patternPhone = account.getPhone().matches(regexPhone);
			if (patternPhone == false) {
				phone = "Mobile Number Wrong Format!";
			}
			String regexPostal = "^$|^\\d{5}$";
			boolean patternPostal = account.getPostal_code().matches(regexPostal);
			if (patternPostal == false) {
				postal = "Postal Code Wrong Format!";
			}
		} else if (account.getCountry().getCountryId() == 3) {
			String regexPhone = "^(08)(\\d{3,4}-?){2}\\d{2,3}$";
			boolean patternPhone = account.getPhone().matches(regexPhone);
			if (patternPhone == false) {
				phone = "Mobile Number Wrong Format!";
			}
			String regexPostal = "^$|^\\d{5}$";
			boolean patternPostal = account.getPostal_code().matches(regexPostal);
			if (patternPostal == false) {
				postal = "Postal Code Wrong Format!";
			}
		}

		List<Account> listAccount = accountRepository.findAll();
		String username = "";
		String email = "";
		for (int i = 0; i < listAccount.size(); i++) {
			if (listAccount.get(i).getUsername().equals(account.getUsername())) {
				username = "Username has already been taken. Please choose another one. ";
			}
			if (listAccount.get(i).getEmail().equals(account.getEmail())) {
				email = "Email has already been taken";
			}
		}
		String matchpassword = "";
		String patternpassword = "";
		String regularexpression = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
		boolean pattern = account.getPassword().matches(regularexpression);
		if (pattern == true) {
			if (password.equals(account.getPassword())) {
				if (account.getRole().equalsIgnoreCase("ROLE_USER")) {
					account.setMembership_levels("ROOKIE");
					account.setCashback_voucher(0.03);
				} else if (account.getRole().equalsIgnoreCase("ROLE_MERCHANT")) {
					account.setMembership_levels("NONE");
					account.setCashback_voucher(0.00);
				}

				String encodedPassword = passwordEncoder.encode(account.getPassword());

				account.setPassword(encodedPassword);

				String randomCode = RandomString.make(64);
				account.setVerificationCode(randomCode);
				account.setEnabled(false);

			} else {
				matchpassword += "Password do not match \n";
			}

		} else {
			patternpassword = "Password must contains Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character";
		}
		if (username != "" || matchpassword != "" || patternpassword != "" || email != "" || phone != ""
				|| postal != "") {
			List<Country> countryList = countryRepository.findAll();
			model.addAttribute("countryList", countryList);

			model.addAttribute("username", username);
			model.addAttribute("pattern", patternpassword);
			model.addAttribute("matchpassword", matchpassword);
			model.addAttribute("email", email);
			model.addAttribute("postal", postal);
			model.addAttribute("phone", phone);
			return "register";
		} else {
			accountRepository.save(account);
		}
		sendVerificationEmail(account, getSiteURL(request));

		if (account.getRole().equalsIgnoreCase("ROLE_MERCHANT")) {
			// Current Date Time
			LocalDateTime currentDateTime = LocalDateTime.now();
			
			Wallet wallet = new Wallet();
			wallet.setPaymentLimit(1000);
			wallet.setAccount(account);
			wallet.setDateTimeofCreation(currentDateTime);
			walletRepository.save(wallet);
			
			Notifications notifications = new Notifications();
			notifications.setAccount(account);
			notifications.setDateTime(currentDateTime);
			notifications.setTitle("Created Wallet ID Success!");

			notifications.setMessage(
					"You have successfully created your wallet. Your wallet ID is " + wallet.getWalletId() + ".");

			notificationsRepository.save(notifications);
		}

		return "register_success";
	}

	private void sendVerificationEmail(Account account, String siteURL)
			throws MessagingException, UnsupportedEncodingException {
		String toAddress = account.getEmail();
		String fromAddress = "finantierpay@outlook.com";
		String senderName = "FinantierPay";
		String subject = "Please verify your registration";
		String content = "Dear [[name]],<br>" + "Please click the link below to verify your registration:<br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>" + "Thank you,<br>" + "FinantierPay";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);

		content = content.replace("[[name]]", account.getFirst_name());
		String verifyURL = siteURL + "/verify?code=" + account.getVerificationCode();

		content = content.replace("[[URL]]", verifyURL);

		helper.setText(content, true);

		mailSender.send(message);

	}

	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

	@GetMapping("/verify")
	public String verifyUser(@Param("code") String code, Model model) {
		if (verify(code)) {
//			int unread = notificationsService.unreadNotificiations();
//			model.addAttribute("unread", unread);

			return "verify_success";
		} else {
//			int unread = notificationsService.unreadNotificiations();
//			model.addAttribute("unread", unread);

			return "verify_fail";
		}
	}

	public boolean verify(String verificationCode) {
		Account account = accountRepository.findByVerificationCode(verificationCode);

		if (account == null || account.isEnabled()) {
			return false;
		} else {
			account.setVerificationCode(verificationCode);
			account.setEnabled(true);
			accountRepository.save(account);
			LocalDateTime dateTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			Notifications notifications = new Notifications();
			notifications.setAccount(account);
			notifications.setDateTime(dateTime);
			notifications.setTitle("Welcome to FinantierPay!");
			if (account.getRole().equals("ROLE_USER")) {
				account.setMembership_levels("ROOKIE");
				account.setCashback_voucher(0.03);

				notifications.setMessage(
						"You can now create your new wallet, top-up and make payment. You are also entitled to membership levels. Your membership levels is ROOKIE.");
				notificationsRepository.save(notifications);
			}
			return true;
		}

	}

	@GetMapping("/account")
	public String ViewAccount(Model model) {
		// Get currently logged in user
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);
		model.addAttribute("account", account);

		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "view_account";
	}

	@GetMapping("/account/edit/{id}")
	public String UpdateAccount(@PathVariable("id") Integer id, Model model) {
		Account account = accountRepository.getById(id);
		model.addAttribute("account", account);

		int country = account.getCountry().getCountryId();
		model.addAttribute("country", country);

		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "edit_account";
	}

	@PostMapping("/account/edit/{id}")
	public String saveAccount(@PathVariable("id") int accountId, @RequestParam("phone") String phone,
			@RequestParam("address") String address, @RequestParam("postal_code") String postal_code, Model model,
			RedirectAttributes redirectAttributes) {

		// Get account object by account's id
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);
		String phoneError = "";
		String postalError = "";
		if (account.getCountry().getCountryId() == 1) {
			String regexPhone = "^$|^[689]\\d{7}$";
			boolean patternPhone = phone.matches(regexPhone);
			if (patternPhone == false) {
				phoneError = "Mobile Number Wrong Format!";
			}
			String regexPostal = "^$|^\\d{6}$";
			boolean patternPostal = postal_code.matches(regexPostal);
			if (patternPostal == false) {
				postalError = "Postal Code Wrong Format!";
			}
		} else if (account.getCountry().getCountryId() == 2) {
			String regexPhone = "^(\\+?6?01)[02-46-9][-][0-9]{7}$|^(\\+?6?01)[1][-][0-9]{8}$";
			boolean patternPhone = phone.matches(regexPhone);
			if (patternPhone == false) {
				phoneError = "Mobile Number Wrong Format!";
			}
			String regexPostal = "^$|^\\d{5}$";
			boolean patternPostal = postal_code.matches(regexPostal);
			if (patternPostal == false) {
				postalError = "Postal Code Wrong Format!";
			}
		} else if (account.getCountry().getCountryId() == 3) {
			String regexPhone = "^(08)(\\d{3,4}-?){2}\\d{2,3}$";
			boolean patternPhone = phone.matches(regexPhone);
			if (patternPhone == false) {
				phoneError = "Mobile Number Wrong Format!";
			}
			String regexPostal = "^$|^\\d{5}$";
			boolean patternPostal = postal_code.matches(regexPostal);
			if (patternPostal == false) {
				postalError = "Postal Code Wrong Format!";
			}
		}
		if (phoneError != "" || postalError != "") {
			List<Country> countryList = countryRepository.findAll();
			model.addAttribute("countryList", countryList);
			model.addAttribute("postal", postalError);
			model.addAttribute("phone", phoneError);
			model.addAttribute("account", account);
			return "edit_account";
		}
		// Set these of the account object retrieved
		account.setPhone(phone);
		account.setAddress(address);
		account.setPostal_code(postal_code);

		// Save the account back to the accountRepository
		accountRepository.save(account);

		redirectAttributes.addFlashAttribute("success", "You have successfully updated your account");

		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "update_account_success";
	}

	@GetMapping("/account/password/edit/{id}")
	public String UpdateAccountPassword(@PathVariable("id") Integer id, Model model) {
		Account account = accountRepository.getById(id);
		model.addAttribute("account", account);

		String password = account.getPassword();
		model.addAttribute("password", password);

		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "edit_password";
	}

	@PostMapping("/account/password/edit/{id}")
	public String saveAccountPassword(@PathVariable("id") Integer id, Model model,
			@RequestParam("current_password") String current_password,
			@RequestParam("new_password") String new_password,
			@RequestParam("confirm_password") String confirm_password) {

		Account account = accountRepository.getById(id);
		String matchpassword = "";
		String patternpassword = "";
		String currentpassword = "";
		if (passwordEncoder.matches(current_password, account.getPassword()) == true) {
			String regularexpression = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
			boolean pattern = new_password.matches(regularexpression);
			if (pattern == true) {
				if (confirm_password.equals(new_password)) {
					String encodedNewPassword = passwordEncoder.encode(new_password);
					account.setPassword(encodedNewPassword);

				} else {
					matchpassword += "Password do not match \n";
				}

			} else {
				patternpassword = "Password must contains Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character";
			}
		} else {
			currentpassword = "Wrong Current Password";
		}

		if (matchpassword != "" || patternpassword != "" || currentpassword != "") {
			model.addAttribute("account", account);

			String password = account.getPassword();
			model.addAttribute("currentError", currentpassword);
			model.addAttribute("password", password);
			model.addAttribute("matchpassword", matchpassword);
			model.addAttribute("patternpassword", patternpassword);

			return "edit_password";
		}
		accountRepository.save(account);

		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "update_password_success";
	}

	// delete
	@GetMapping("/account/delete/{id}")
	public String deleteAccount(@PathVariable("id") int accountId, Model model) {
		Account account = accountRepository.getById(accountId);
		model.addAttribute("account", account);

		accountRepository.save(account);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "request_delete_account";
	}

	@PostMapping("/account/delete/{id}")
	public String delete(@PathVariable("id") int accountId, Model model) {

		List<Wallet> walletList = walletRepository.findByAccount_AccountId(accountId);
		int totalAmount = 0;
		for (int i = 0; i < walletList.size(); i++) {
			totalAmount += walletList.get(i).getTotalAmount();
		}

		if (totalAmount != 0) {
			Account account = accountRepository.getById(accountId);
			LocalDateTime currentdateTime = LocalDateTime.now();
			Notifications notifications = new Notifications();
			notifications.setAccount(account);
			notifications.setDateTime(currentdateTime);
			notifications.setTitle("Sorry Your account will not be deleted!");
			notifications.setMessage(
					"Sorry! Your account will not be deleted. You have existing money inside your wallet" + ".");
			notificationsRepository.save(notifications);
			int unread = notificationsService.unreadNotificiations();
			model.addAttribute("unread", unread);
			return "delete_account_fail"; // cannot delete account message display
		} else {
			Account account = accountRepository.getById(accountId);
			LocalDate currentdate = LocalDate.now();
			account.setDate_deleted(currentdate);
			LocalDate plus30days = currentdate.plusDays(30);
			accountRepository.save(account);
			String subject = "Requested to deleted your FinantierPay Account!";
			String body = "Dear " + account.getUsername()
					+ ",\n\nYou have requested to deleted your account.\n Your account will be deleted on " + plus30days
					+ ".\n If you wish to continue to access your account, you will be able to do so within the 30 days and your account will not be deleted. \n\nThank you! \n\nBest Regards, \nFinantierPay";
			String to = account.getEmail();
			sendEmail(to, subject, body);
		}
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "delete_account_success";
	}
	
	@GetMapping("/deleted/account/{id}")
	public String ConfirmDelete(@PathVariable("id") int accountId, Model model) {
		Account account = accountRepository.getById(accountId);
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDate plus30days =  currentDateTime.toLocalDate().plusDays(30);
		Notifications notifications = new Notifications();
		notifications.setAccount(account);
		notifications.setDateTime(currentDateTime);
		notifications.setTitle("Your account will not be deleted!");
		notifications.setMessage("You have choose not to deleted your account on " + plus30days + ". Your account will not be deleted.");
		notificationsRepository.save(notifications);

		account.setDate_deleted(null);
		accountRepository.save(account);
		return "redirect:/";
	}
	
	@GetMapping("/deleted/account/no/{id}")
	public String NotDeleted(@PathVariable("id") int accountId, Model model) {

		Account account = accountRepository.getById(accountId);
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDate plus30days =  currentDateTime.toLocalDate().plusDays(30);
		Notifications notifications = new Notifications();
		notifications.setAccount(account);
		notifications.setDateTime(currentDateTime);
		notifications.setTitle("Account will be deleted!");
		notifications.setMessage("Your account will be deleted on " + plus30days + ".");
		notificationsRepository.save(notifications);

		return "redirect:/";
	}
	
	public void sendEmail(String to, String subject, String body) {
		SimpleMailMessage msg = new SimpleMailMessage();
		String fromAddress = "finantierpay@outlook.com";
		msg.setFrom(fromAddress);
		msg.setTo(to);
		msg.setSubject(subject);
		msg.setText(body);
		javaMailSender.send(msg);
	}
}
