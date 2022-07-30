/**
*
* I declare that this code was written by me, 20008303.
* I will not copy or allow others to copy my code.
* I understand that copying code is considered as plagiarism.
*
* Student Name: Cheng Xin Lin (20032049), Koh Siew Gek (20008303), Chen Wan Ting (20009334)
* Team ID: SOI-2022-0012
* Team Project ID: SOI-2022-2210-0049
* Project: [IP] Digital Wallet
* Date created: 2022-May-22 12:37:53 am
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.Customer;
import com.braintreegateway.Result;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;
import com.braintreegateway.Transaction.Status;

/**
 * @author 20008303
 *
 */

@Controller
public class WalletController {

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private NotificationsService notificationsService;

	@GetMapping("/wallet")
	public String viewWallets(Model model) {

		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();

		List<Wallet> listWallets = walletRepository.findByAccount_AccountIdAndIsDeleted(loggedInAccountId, false);

		model.addAttribute("listWallets", listWallets);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "view_wallets";

	}

	@GetMapping("/wallet/create")
	public String createWallet(Model model) {
		model.addAttribute("wallet", new Wallet());

		List<Wallet> walletList = walletRepository.findAll();
		model.addAttribute("walletList", walletList);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "setup_walletpin";
	}

	@PostMapping("/wallet/confirmpin")
	public String saveWallet(@Valid Wallet wallet, Model model, BindingResult bindingResult,
			@RequestParam("d1") String digit1, @RequestParam("d2") String digit2, @RequestParam("d3") String digit3,
			@RequestParam("d4") String digit4, @RequestParam("d5") String digit5, @RequestParam("d6") String digit6,
			Principal principal, RedirectAttributes redirectAttribute) {

		if (bindingResult.hasErrors()) {
			List<Wallet> walletList = walletRepository.findAll();
			model.addAttribute("walletList", walletList);
			int unread = notificationsService.unreadNotificiations();
			model.addAttribute("unread", unread);
			return "setup_walletpin";
		}

		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();

		Account account = accountRepository.getById(loggedInAccountId);

		String pin = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;

		String regularexpression = "^\\d{6}$";
		String errorMsg = "";

		boolean pattern = pin.matches(regularexpression);

		if (pattern != true) {
			errorMsg = "Invalid Pin. Pin should include numeric numbers only.";
		}

		if (errorMsg != "") {
			model.addAttribute("errorMsg", errorMsg);
			model.addAttribute("verifyPin", pin);
			int unread = notificationsService.unreadNotificiations();
			model.addAttribute("unread", unread);
			return "setup_walletpin";
		}

		model.addAttribute("verifyPin", pin);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "confirm_walletpin";
	}

	@PostMapping("/wallet/merchant/save")
	public String saveMerchantWallet(Model model) {
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();

		Account account = accountRepository.getById(loggedInAccountId);

		Wallet wallet = new Wallet();
		wallet.setPaymentLimit(1000);
		wallet.setAccount(account);
		walletRepository.save(wallet);

		// Current Date Time
		LocalDateTime currentDateTime = LocalDateTime.now();
		Notifications notifications = new Notifications();
		notifications.setAccount(account);
		notifications.setDateTime(currentDateTime);
		notifications.setTitle("Created Wallet ID Success!");

		notifications.setMessage(
				"You have successfully created your wallet. Your wallet ID is " + wallet.getWalletId() + ".");

		notificationsRepository.save(notifications);
		model.addAttribute("wallet", wallet);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "success_wallet_creation";
	}

	@PostMapping("/wallet/save")
	public String checkConfirmPin(@Valid Wallet wallet, Model model, BindingResult bindingResult,
			@RequestParam("d1") String digit1, @RequestParam("d2") String digit2, @RequestParam("d3") String digit3,
			@RequestParam("d4") String digit4, @RequestParam("d5") String digit5, @RequestParam("d6") String digit6,
			@RequestParam("verifyPin") String verifyPin, Principal principal, RedirectAttributes redirectAttribute) {

		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();

		Account account = accountRepository.getById(loggedInAccountId);

		String confirmPin = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;
		String matchMsg = "";
		if (!confirmPin.equals(verifyPin)) {
			matchMsg = "Pin do not match.";
		}

		if (matchMsg != "") {
			model.addAttribute("matchMsg", matchMsg);
			model.addAttribute("verifyPin", verifyPin);
			int unread = notificationsService.unreadNotificiations();
			model.addAttribute("unread", unread);
			return "confirm_walletpin";
		}

		String encodedConfirmPin = passwordEncoder.encode(confirmPin);
		LocalDateTime currentDateTime = LocalDateTime.now();
		wallet.setPin(encodedConfirmPin);
		wallet.setAccount(account);
		wallet.setPaymentLimit(1000);
		wallet.setDateTimeofCreation(currentDateTime);
		walletRepository.save(wallet);
		// Current Date Time

		Notifications notifications = new Notifications();
		notifications.setAccount(account);
		notifications.setDateTime(currentDateTime);
		notifications.setTitle("Created Wallet ID Success!");

		notifications.setMessage(
				"You have successfully created your wallet. Your wallet ID is " + wallet.getWalletId() + ".");

		notificationsRepository.save(notifications);
		model.addAttribute("walletId", wallet.getWalletId());
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "success_wallet_creation";

	}

	@GetMapping("/wallet/{id}/topup")
	public String topUpWallet(@PathVariable("id") Integer id, Model model) {

		Wallet wallet = walletRepository.getById(id);
		model.addAttribute("wallet", wallet);
		model.addAttribute("walletId", wallet.getWalletId());
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "topup_wallet";
	}

	@PostMapping("/wallet/{id}/topup")
	public String saveTopUpAmt(@PathVariable("id") Integer id, @RequestParam("topUpAmt") double amount, Model model, RedirectAttributes redirectAttributes) {


		Wallet wallet = walletRepository.getById(id);
		
		String errorAmt = "";
		
		if (amount <= 0) {
			errorAmt = "Top up amount must not be a negative amount and should not be 0.";
		}
		if (errorAmt != "") {
			redirectAttributes.addFlashAttribute("errorAmt", errorAmt);
			return "redirect:/wallet/{id}/topup";
		}
		
		model.addAttribute("wallet", wallet);
		model.addAttribute("walletId", wallet.getWalletId());
		model.addAttribute("topUpAmt", amount);

		
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "topup_wallet";
	}

	@PostMapping("/wallet/process_topup")
	public String processTopUp(Model model, @RequestParam("formtopupTotal") double topUpTotal,
			@RequestParam("walletId") int walletId, @RequestParam("transactionId") String transactionId) {
		Wallet wallet = walletRepository.getById(walletId);

		// Create Transaction
		Transaction transaction = new Transaction();

		// Current Date Time
		LocalDateTime currentDateTime = LocalDateTime.now();
		transaction.setWallet(wallet);
		transaction.setActivity("Deposit");
		transaction.setStatus("Success");
		transaction.setArchive("No");
		transaction.setTransactionId(transactionId);
		transaction.setDateTime(currentDateTime);
		transaction.setAmount(topUpTotal);
		transaction.setDescription("Successfully Top-Up Your Wallet");
		transactionRepository.save(transaction);

		// Saving Wallet Repository
		double currentWalletAmt = wallet.getTotalAmount();
		double updateWalletAmt = currentWalletAmt + topUpTotal;
		double newupdatedValue = (double) Math.round(updateWalletAmt * 100d) / 100d;
		wallet.setTotalAmount(newupdatedValue);
		walletRepository.save(wallet);

		// Sending Success Top Up Notifications
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);

		Notifications notifications = new Notifications();
		notifications.setAccount(account);
		notifications.setDateTime(currentDateTime);
		notifications.setTitle("Top Up Wallet ID Success!");

		notifications.setMessage("You have successfully top up $"
				+ topUpTotal + " to your Wallet ID " + wallet.getWalletId()
				+ ". Total balance for Wallet ID " + wallet.getWalletId() + " is now $" + newupdatedValue + ".");

		notificationsRepository.save(notifications);
		String walletEmail = wallet.getAccount().getEmail();
		String username = wallet.getAccount().getUsername();
		// Pass info to view to display success page
		model.addAttribute("topUpTotal", topUpTotal);
		model.addAttribute("wallet", wallet);
		model.addAttribute("transactionId", transactionId);
		model.addAttribute("currentDateTime", currentDateTime);
		model.addAttribute("updateWalletAmt", newupdatedValue);
		model.addAttribute("walletEmail", walletEmail);
		// Send email
		String subject = "Top-Up Successfully!";
		String body = "Dear " + username + ",\n\n" + "Transaction ID: " + transactionId + "\n"
				+ "You have successfully topped up $" + topUpTotal + " to Wallet ID " + wallet.getWalletId() + "\n"
				+ "Total Amount in this wallet: $" + newupdatedValue + "\n\nBest Regards, \nFinantierPay";
		String to = walletEmail;
		sendEmail(to, subject, body);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "topup_success";
	}

	public void sendEmail(String to, String subject, String body) {
		SimpleMailMessage msg = new SimpleMailMessage();
		String fromAddress = "finantierpay@outlook.com";
		String senderName = "FinantierPay";
		msg.setFrom(fromAddress);
		msg.setTo(to);
		msg.setSubject(subject);
		msg.setText(body);
		System.out.println("Sending");
		javaMailSender.send(msg);
		System.out.println("Sent");
	}

	@GetMapping("/wallet/{id}")
	public String ViewIndividualWallet(Model model, @PathVariable("id") Integer id) {

		Wallet wallet = walletRepository.getById(id);

		List<Transaction> transactionList = transactionRepository
				.findByWallet_WalletIdAndIsDeletedAndArchiveOrderByDateTimeDesc(id, false, "No");

		// Month Calendar
		List<String> allDates = new ArrayList<>();
		List<String> allMonth = new ArrayList<>();
		List<String> allyear = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat monthDate = new SimpleDateFormat("M yyyy");
		SimpleDateFormat calmonth = new SimpleDateFormat("MMM yy");
		SimpleDateFormat yearDate = new SimpleDateFormat("yyyy");
		for (int i = 1; i <= 12; i++) {
			String month_name1 = monthDate.format(cal.getTime());
			allDates.add(month_name1);
			String month_name2 = calmonth.format(cal.getTime());
			allMonth.add(month_name2);
			cal.add(Calendar.MONTH, -1);
		}
		for (int i = 1; i <= 2; i++) {
			String year_name = yearDate.format(cal.getTime());
			allyear.add(year_name);
			cal.add(Calendar.YEAR, 1);
		}
		Calendar calenderMonth = Calendar.getInstance();

		Collections.reverse(allDates);
		Collections.reverse(allMonth);
		// Deposit Amount List
		List<Double> MonthlyDepositsAmountList = new ArrayList<>(12);
		double DepositsAmt1 = 0;
		double DepositsAmt2 = 0;
		double DepositsAmt3 = 0;
		double DepositsAmt4 = 0;
		double DepositsAmt5 = 0;
		double DepositsAmt6 = 0;
		double DepositsAmt7 = 0;
		double DepositsAmt8 = 0;
		double DepositsAmt9 = 0;
		double DepositsAmt10 = 0;
		double DepositsAmt11 = 0;
		double DepositsAmt12 = 0;

		// Credited Amount List
		List<Double> MonthlyCreditedAmountList = new ArrayList<>(12);
		double CreditedAmt1 = 0;
		double CreditedAmt2 = 0;
		double CreditedAmt3 = 0;
		double CreditedAmt4 = 0;
		double CreditedAmt5 = 0;
		double CreditedAmt6 = 0;
		double CreditedAmt7 = 0;
		double CreditedAmt8 = 0;
		double CreditedAmt9 = 0;
		double CreditedAmt10 = 0;
		double CreditedAmt11 = 0;
		double CreditedAmt12 = 0;

		// Sum the total for each month based on past 1 year from today's month
		for (int a = 0; a < allDates.size(); a++) {
			for (int i = 0; i < transactionList.size(); i++) {
				LocalDateTime dateTime = transactionList.get(i).getDateTime();
				int month = dateTime.getMonthValue();
				int year = dateTime.getYear();
				String monthYear = String.valueOf(month + " " + year);
				if (transactionList.get(i).getStatus().equalsIgnoreCase("Success")) {
					if (monthYear.equals(allDates.get(a))) {
						if (month == 1) {
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Received")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Deposit")) {
								DepositsAmt1 += transactionList.get(i).getAmount();
							}
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Sent")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Withdraw")) {
								CreditedAmt1 += transactionList.get(i).getAmount();
							}
						}
						if (month == 2) {
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Received")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Deposit")) {
								DepositsAmt2 += transactionList.get(i).getAmount();
							}
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Sent")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Withdraw")) {
								CreditedAmt2 += transactionList.get(i).getAmount();
							}
						}
						if (month == 3) {
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Received")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Deposit")) {
								DepositsAmt3 += transactionList.get(i).getAmount();
							}
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Sent")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Withdraw")) {
								CreditedAmt3 += transactionList.get(i).getAmount();
							}
						}
						if (month == 4) {
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Received")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Deposit")) {
								DepositsAmt4 += transactionList.get(i).getAmount();
							}
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Sent")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Withdraw")) {
								CreditedAmt4 += transactionList.get(i).getAmount();
							}
						}
						if (month == 5) {
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Received")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Deposit")) {
								DepositsAmt5 += transactionList.get(i).getAmount();
							}
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Sent")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Withdraw")) {
								CreditedAmt5 += transactionList.get(i).getAmount();
							}
						}
						if (month == 6) {
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Received")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Deposit")) {
								DepositsAmt6 += transactionList.get(i).getAmount();
							}
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Sent")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Withdraw")) {
								CreditedAmt6 += transactionList.get(i).getAmount();
							}
						}
						if (month == 7) {
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Received")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Deposit")) {
								DepositsAmt7 += transactionList.get(i).getAmount();
							}
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Sent")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Withdraw")) {
								CreditedAmt7 += transactionList.get(i).getAmount();
							}
						}
						if (month == 8) {
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Received")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Deposit")) {
								DepositsAmt8 += transactionList.get(i).getAmount();
							}
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Sent")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Withdraw")) {
								CreditedAmt8 += transactionList.get(i).getAmount();
							}
						}
						if (month == 9) {
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Received")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Deposit")) {
								DepositsAmt9 += transactionList.get(i).getAmount();
							}
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Sent")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Withdraw")) {
								CreditedAmt9 += transactionList.get(i).getAmount();
							}
						}
						if (month == 10) {
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Received")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Deposit")) {
								DepositsAmt10 += transactionList.get(i).getAmount();
							}
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Sent")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Withdraw")) {
								CreditedAmt10 += transactionList.get(i).getAmount();
							}
						}
						if (month == 11) {
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Received")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Deposit")) {
								DepositsAmt11 += transactionList.get(i).getAmount();
							}
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Sent")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Withdraw")) {
								CreditedAmt11 += transactionList.get(i).getAmount();
							}
						}
						if (month == 12) {
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Received")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Deposit")) {
								DepositsAmt12 += transactionList.get(i).getAmount();
							}
							if (transactionList.get(i).getActivity().equalsIgnoreCase("Sent")
									|| transactionList.get(i).getActivity().equalsIgnoreCase("Withdraw")) {
								CreditedAmt12 += transactionList.get(i).getAmount();
							}
						}
					}
				}
			}
		}
		List<String> Dates = new ArrayList<>();
		Calendar calender = Calendar.getInstance();
		SimpleDateFormat monthInt = new SimpleDateFormat("M");
		for (int i = 1; i <= 12; i++) {
			String month_name1 = monthInt.format(calender.getTime());
			Dates.add(month_name1);
			calender.add(Calendar.MONTH, -1);
		}
		Collections.reverse(Dates);

		// Adding the Deposits Amt and Credited Amt based on past 1 year Month index
		// from today's month
		for (int i = 0; i < Dates.size(); i++) {
			if (Dates.get(i).equals("1")) {
				MonthlyDepositsAmountList.add(i, DepositsAmt1);
				MonthlyCreditedAmountList.add(i, CreditedAmt1);
			} else if (Dates.get(i).equals("2")) {
				MonthlyDepositsAmountList.add(i, DepositsAmt2);
				MonthlyCreditedAmountList.add(i, CreditedAmt2);
			} else if (Dates.get(i).equals("3")) {
				MonthlyDepositsAmountList.add(i, DepositsAmt3);
				MonthlyCreditedAmountList.add(i, CreditedAmt3);
			} else if (Dates.get(i).equals("4")) {
				MonthlyDepositsAmountList.add(i, DepositsAmt4);
				MonthlyCreditedAmountList.add(i, CreditedAmt4);
			} else if (Dates.get(i).equals("5")) {
				MonthlyDepositsAmountList.add(i, DepositsAmt5);
				MonthlyCreditedAmountList.add(i, CreditedAmt5);
			} else if (Dates.get(i).equals("6")) {
				MonthlyDepositsAmountList.add(i, DepositsAmt6);
				MonthlyCreditedAmountList.add(i, CreditedAmt6);
			} else if (Dates.get(i).equals("7")) {
				MonthlyDepositsAmountList.add(i, DepositsAmt7);
				MonthlyCreditedAmountList.add(i, CreditedAmt7);
			} else if (Dates.get(i).equals("8")) {
				MonthlyDepositsAmountList.add(i, DepositsAmt8);
				MonthlyCreditedAmountList.add(i, CreditedAmt8);
			} else if (Dates.get(i).equals("9")) {
				MonthlyDepositsAmountList.add(i, DepositsAmt9);
				MonthlyCreditedAmountList.add(i, CreditedAmt9);
			} else if (Dates.get(i).equals("10")) {
				MonthlyDepositsAmountList.add(i, DepositsAmt10);
				MonthlyCreditedAmountList.add(i, CreditedAmt10);
			} else if (Dates.get(i).equals("11")) {
				MonthlyDepositsAmountList.add(i, DepositsAmt11);
				MonthlyCreditedAmountList.add(i, CreditedAmt11);
			} else if (Dates.get(i).equals("12")) {
				MonthlyDepositsAmountList.add(i, DepositsAmt12);
				MonthlyCreditedAmountList.add(i, CreditedAmt12);
			}

		}

		// Convert to 2 decimal Places
		for (int i = 0; i < MonthlyDepositsAmountList.size(); i++) {
			double newValue = (double) Math.round(MonthlyDepositsAmountList.get(i) * 100d) / 100d;
			MonthlyDepositsAmountList.set(i, newValue);
		}
		for (int i = 0; i < MonthlyCreditedAmountList.size(); i++) {
			double newValue = (double) Math.round(MonthlyCreditedAmountList.get(i) * 100d) / 100d;
			MonthlyCreditedAmountList.set(i, newValue);
		}

		model.addAttribute("wallet", wallet);
		model.addAttribute("MonthlyDepositsAmountList", MonthlyDepositsAmountList);
		model.addAttribute("MonthlyCreditedAmountList", MonthlyCreditedAmountList);
		model.addAttribute("MonthList", allMonth);
		model.addAttribute("yearList", allyear);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "view_individual_wallet";
	}

	@GetMapping("/wallet/{id}/send")
	public String TransferMoney(@PathVariable("id") Integer id, Model model) {
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		List<Wallet> walletList = walletRepository.findByAccount_AccountIdAndIsDeleted(loggedInAccountId, false);
		if (walletList.size() < 2) {
			model.addAttribute("NoWallet", "No Wallet Found");
		} else {
			Wallet wallet = walletRepository.getById(id);
			model.addAttribute("WalletFound", "Wallet Found");
			model.addAttribute("walletList", walletList);
			model.addAttribute("wallet", wallet);
			model.addAttribute("walletId", wallet.getWalletId());
		}
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "send_wallet_money";
	}

	@PostMapping("/wallet/{id}/confirm")
	public String confirmTransferMoney(@PathVariable("id") Integer id, Model model,
			@RequestParam("walletId") String walletId, @RequestParam("totalAmt") double totalAmt,
			RedirectAttributes redirectAttributes) {

		Wallet wallet = walletRepository.getById(id);
		String errorWalletId = "";
		String errorAmt = "";
		if (walletId == "") {
			errorWalletId = "Please Select Wallet ID You would like to send.";
		}
		if (totalAmt <= 0 || totalAmt > wallet.getTotalAmount()) {
			errorAmt = "Amount must be more than 0 and less than " + wallet.getTotalAmount();
		}
		if (errorWalletId != "" || errorAmt != "") {
			redirectAttributes.addFlashAttribute("errorWalletId", errorWalletId);
			redirectAttributes.addFlashAttribute("errorAmt", errorAmt);
			return "redirect:/wallet/{id}/send";
		}
		model.addAttribute("wallet", wallet);
		model.addAttribute("walletId", walletId);
		model.addAttribute("totalAmt", totalAmt);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "confirm_send_wallet_money";
	}

	@PostMapping("/wallet/{id}/success")
	public String successTransferMoney(@PathVariable("id") Integer id, Model model,
			@RequestParam("walletId") int walletId, @RequestParam("totalAmt") double totalAmt) {
		// From Wallet
		Wallet Fromwallet = walletRepository.getById(id);

		// To Wallet
		Wallet ToWallet = walletRepository.getById(walletId);

		// Calculate Total Amount of Transaction in a day based on current DateTime
		List<Transaction> transaction = transactionRepository
				.findByWallet_WalletIdAndIsDeletedOrderByDateTimeDesc(id, false);
		double total = 0;
		LocalDate currentdate = LocalDate.now();
		for (int i = 0; i < transaction.size(); i++) {
			LocalDate transactiondate = transaction.get(i).getDateTime().toLocalDate();
			if (currentdate.compareTo(transactiondate) == 0) {
				if (transaction.get(i).getStatus().equalsIgnoreCase("Success")
						&& transaction.get(i).getActivity().equalsIgnoreCase("Sent")) {
					total += transaction.get(i).getAmount();
					System.out.println(transaction.get(i).getAmount());
					System.out.println(transactiondate);
				}
			}
		}
		System.out.println(total);
		double amount = total + totalAmt;
		if (amount > Fromwallet.getPaymentLimit()) {
			// Transaction DateTime
			LocalDateTime dateTime = LocalDateTime.now();

			// Generating Random Unique Transaction Id
			String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXY";
			String number = "0123456789";
			Random Fromrandom = new Random();
			StringBuilder sbalpha = new StringBuilder(11);
			StringBuilder sbnumber = new StringBuilder(6);
			for (int i = 0; i < 11; i++) {
				sbnumber.append(number.charAt(Fromrandom.nextInt(number.length())));
			}
			for (int i = 0; i < 6; i++) {
				sbalpha.append(alpha.charAt(Fromrandom.nextInt(alpha.length())));
			}
			// Sorting the Transaction Order
			String Fromtransaction = sbalpha.toString() + sbnumber.toString();
			char[] Fromarray = Fromtransaction.toCharArray();
			for (int i = 0; i < Fromarray.length; i++) {
				int randomIndex = (int) (Math.random() * Fromarray.length);
				char temp = Fromarray[i];
				Fromarray[i] = Fromarray[randomIndex];
				Fromarray[randomIndex] = temp;
			}
			String FailtransactionId = String.valueOf(Fromarray);
			// Transaction Fail - From
			Transaction newFailTransaction = new Transaction();
			newFailTransaction.setTransactionId(FailtransactionId);
			newFailTransaction.setActivity("Sent");
			newFailTransaction.setAmount(totalAmt);
			newFailTransaction.setDateTime(dateTime);
			newFailTransaction.setStatus("Unsuccess");
			newFailTransaction.setArchive("No");
			newFailTransaction.setWallet(Fromwallet);
			newFailTransaction.setDescription("Failed To Sent Money to Your Wallet ID " + ToWallet.getWalletId());
			transactionRepository.save(newFailTransaction);

			model.addAttribute("wallet", Fromwallet);
			model.addAttribute("walletId", walletId);
			model.addAttribute("totalAmt", totalAmt);
			model.addAttribute("FailtransactionId", FailtransactionId);
//			model.addAttribute("TotransactionId", TotransactionId);
			model.addAttribute("currentDateTime", dateTime);
			model.addAttribute("status", newFailTransaction.getStatus());
			int unread = notificationsService.unreadNotificiations();
			model.addAttribute("unread", unread);
			return "send_wallet_money_fail";

		} else {
			// Transaction DateTime
			LocalDateTime dateTime = LocalDateTime.now();

			// From Transaction
			// Generating Random Unique Transaction Id
			String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXY";
			String number = "0123456789";
			Random Fromrandom = new Random();
			StringBuilder sbalpha = new StringBuilder(11);
			StringBuilder sbnumber = new StringBuilder(6);
			for (int i = 0; i < 11; i++) {
				sbnumber.append(number.charAt(Fromrandom.nextInt(number.length())));
			}
			for (int i = 0; i < 6; i++) {
				sbalpha.append(alpha.charAt(Fromrandom.nextInt(alpha.length())));
			}
			// Sorting the Transaction Order
			String Fromtransaction = sbalpha.toString() + sbnumber.toString();
			char[] Fromarray = Fromtransaction.toCharArray();
			for (int i = 0; i < Fromarray.length; i++) {
				int randomIndex = (int) (Math.random() * Fromarray.length);
				char temp = Fromarray[i];
				Fromarray[i] = Fromarray[randomIndex];
				Fromarray[randomIndex] = temp;
			}
			String FromtransactionId = String.valueOf(Fromarray);
			// Transaction - From User
			Transaction newFromTransaction = new Transaction();
			newFromTransaction.setTransactionId(FromtransactionId);
			newFromTransaction.setActivity("Sent");
			newFromTransaction.setAmount(totalAmt);
			newFromTransaction.setDateTime(dateTime);
			newFromTransaction.setStatus("Success");
			newFromTransaction.setArchive("No");
			newFromTransaction.setWallet(Fromwallet);
			newFromTransaction.setDescription("Successfully Sent Money to Your Wallet ID " + ToWallet.getWalletId());
			transactionRepository.save(newFromTransaction);

			// Transaction - To User
			Random Torandom = new Random();
			StringBuilder Tosbalpha = new StringBuilder(11);
			StringBuilder Tosbnumber = new StringBuilder(6);
			for (int i = 0; i < 11; i++) {
				Tosbnumber.append(number.charAt(Torandom.nextInt(number.length())));
			}
			for (int i = 0; i < 6; i++) {
				Tosbalpha.append(alpha.charAt(Torandom.nextInt(alpha.length())));
			}
			String Totransaction = Tosbalpha.toString() + Tosbnumber.toString();
			// Sorting the Transaction Order
			char[] Toarray = Totransaction.toCharArray();
			for (int i = 0; i < Toarray.length; i++) {
				int randomIndex = (int) (Math.random() * Toarray.length);
				char temp = Toarray[i];
				Toarray[i] = Toarray[randomIndex];
				Toarray[randomIndex] = temp;
			}
			String TotransactionId = String.valueOf(Toarray);
			Transaction newToTransaction = new Transaction();
			newToTransaction.setTransactionId(TotransactionId);
			newToTransaction.setActivity("Received");
			newToTransaction.setAmount(totalAmt);
			newToTransaction.setDateTime(dateTime);
			newToTransaction.setStatus("Success");
			newToTransaction.setArchive("No");
			newToTransaction.setWallet(ToWallet);
			newToTransaction
					.setDescription("Successfully Received Money From Your Wallet ID " + ToWallet.getWalletId());
			transactionRepository.save(newToTransaction);

			// From Wallet
			double FromTotalAmount = Fromwallet.getTotalAmount();
			double deductedAmount = FromTotalAmount - totalAmt;
			double newdeductedValue = (double) Math.round(deductedAmount * 100d) / 100d;
			Fromwallet.setTotalAmount(newdeductedValue);
			walletRepository.save(Fromwallet);

			// To Wallet
			double ToWalletAmount = ToWallet.getTotalAmount();
			double AddedAmount = ToWalletAmount + totalAmt;
			double newAddedValue = (double) Math.round(AddedAmount * 100d) / 100d;
			ToWallet.setTotalAmount(newAddedValue);
			walletRepository.save(ToWallet);

			model.addAttribute("wallet", Fromwallet);
			model.addAttribute("walletId", walletId);
			model.addAttribute("totalAmt", totalAmt);
			model.addAttribute("FromtransactionId", FromtransactionId);
			model.addAttribute("TotransactionId", TotransactionId);
			model.addAttribute("currentDateTime", dateTime);
			int unread = notificationsService.unreadNotificiations();
			model.addAttribute("unread", unread);
		}

		return "send_wallet_money_success";
	}

	@GetMapping("/wallet/{id}/withdraw")
	public String withdrawMoney(@PathVariable("id") Integer id, Model model) {
		
		Wallet wallet = walletRepository.getById(id);
		
			
		model.addAttribute("wallet", wallet);
		model.addAttribute("withdrawAmt", 0);
		model.addAttribute("walletId", wallet.getWalletId());
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "withdraw_money";
	}
	
	@PostMapping("/wallet/{id}/withdraw")
	public String saveWithdrawMoneyAmt(@PathVariable("id") Integer id, Model model, @RequestParam("withdrawAmt") double withdrawAmt,
			RedirectAttributes redirectAttributes) {

		Wallet wallet = walletRepository.getById(id);
		
		model.addAttribute("wallet", wallet);
		model.addAttribute("withdrawAmt", withdrawAmt);
		model.addAttribute("walletId", wallet.getWalletId());
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "confirm_withdraw";
	}


	@PostMapping("/wallet/process_withdraw")
	public String processWithdraw(Model model, @RequestParam("formWithdrawTotal") double withdrawTotal,
			@RequestParam("walletId") int walletId, @RequestParam("transactionId") String transactionId) {
		Wallet wallet = walletRepository.getById(walletId);

		// Create Transaction
		Transaction transaction = new Transaction();

		// Current Date Time
		LocalDateTime currentDateTime = LocalDateTime.now();
		transaction.setWallet(wallet);
		transaction.setActivity("Withdraw");
		transaction.setStatus("Success");
		transaction.setArchive("No");
		transaction.setTransactionId(transactionId);
		transaction.setDateTime(currentDateTime);
		transaction.setAmount(withdrawTotal);
		transaction.setDescription("Successfully Wallet Withdraw");

		transactionRepository.save(transaction);

		// Saving Wallet Repository
		double currentWalletAmt = wallet.getTotalAmount();
		double newValue = (double) Math.round(withdrawTotal * 100d) / 100d;
		double updateWalletAmt = currentWalletAmt - newValue;
		double newupdateWalletAmt = (double) Math.round(updateWalletAmt * 100d) / 100d;
		wallet.setTotalAmount(updateWalletAmt);

		walletRepository.save(wallet);

		// Sending Success Top Up Notifications
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);
		Notifications notifications = new Notifications();
		notifications.setAccount(account);
		notifications.setDateTime(currentDateTime);
		notifications.setTitle("Withdraw Wallet ID Success!");

		notifications.setMessage("You have successfully withdraw $" + newupdateWalletAmt + " from your wallet ID "
				+ wallet.getWalletId()
				+ ". Total balance for Wallet ID " + wallet.getWalletId() + " is now $" + newupdateWalletAmt + ".");

		notificationsRepository.save(notifications);
		String walletEmail = wallet.getAccount().getEmail();
		String username = wallet.getAccount().getUsername();
		// Pass info to view to display success page
		model.addAttribute("withdrawTotal", withdrawTotal);
		model.addAttribute("wallet", wallet);
		model.addAttribute("transactionId", transactionId);
		model.addAttribute("currentDateTime", currentDateTime);
		model.addAttribute("updateWalletAmt", updateWalletAmt);
		model.addAttribute("walletEmail", walletEmail);
		// Send email
		String subject = "Withdraw Successfully!";
		String body = "Dear " + username + ",\n\n" + "Transaction ID: " + transactionId + "\n"
				+ "You have successfully withdraw $" + newValue + " from Wallet ID " + wallet.getWalletId() + "\n"
				+ "Total Amount in this wallet: $" + newupdateWalletAmt + "\n\nBest Regards, \nFinantierPay";
		String to = walletEmail;
		sendEmail(to, subject, body);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "withdraw_success";
	}
	
	@PostMapping("/wallet/merchant/process_withdraw")
	public String saveWithdraw(Model model, @RequestParam("formWithdrawTotal") double withdrawTotal,
			@RequestParam("walletId") int walletId, @RequestParam("transactionId") String transactionId) {
		Wallet wallet = walletRepository.getById(walletId);

		// Create Transaction
		Transaction transaction = new Transaction();

		// Current Date Time
		LocalDateTime currentDateTime = LocalDateTime.now();
		transaction.setWallet(wallet);
		transaction.setActivity("Withdraw");
		transaction.setStatus("Success");
		transaction.setArchive("No");
		transaction.setTransactionId(transactionId);
		transaction.setDateTime(currentDateTime);
		transaction.setAmount(withdrawTotal);
		transaction.setDescription("Successfully Wallet Withdraw");

		transactionRepository.save(transaction);

		// Saving Wallet Repository
		double currentWalletAmt = wallet.getTotalAmount();
		double newValue = (double) Math.round(withdrawTotal * 100d) / 100d;
		double updateWalletAmt = currentWalletAmt - newValue;
		double newupdateWalletAmt = (double) Math.round(updateWalletAmt * 100d) / 100d;
		wallet.setTotalAmount(updateWalletAmt);

		walletRepository.save(wallet);

		// Sending Success Top Up Notifications
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);
		Notifications notifications = new Notifications();
		notifications.setAccount(account);
		notifications.setDateTime(currentDateTime);
		notifications.setTitle("Withdraw Wallet ID Success!");

		notifications.setMessage("You have successfully withdrawn $" + newupdateWalletAmt + " from your wallet ID "
				+ wallet.getWalletId()
				+ ". Total balance for Wallet ID " + wallet.getWalletId() + " is now $" + newupdateWalletAmt + ".");

		notificationsRepository.save(notifications);
		String walletEmail = wallet.getAccount().getEmail();
		String username = wallet.getAccount().getUsername();
		// Pass info to view to display success page
		model.addAttribute("withdrawTotal", withdrawTotal);
		model.addAttribute("wallet", wallet);
		model.addAttribute("transactionId", transactionId);
		model.addAttribute("currentDateTime", currentDateTime);
		model.addAttribute("updateWalletAmt", updateWalletAmt);
		model.addAttribute("walletEmail", walletEmail);
		// Send email
		String subject = "Withdraw Successfully!";
		String body = "Dear " + username + ",\n\n" + "Transaction ID: " + transactionId + "\n"
				+ "You have successfully withdraw $" + newValue + " from Wallet ID " + wallet.getWalletId() + "\n"
				+ "Total Amount in this wallet: $" + newupdateWalletAmt + "\n\nBest Regards, \nFinantierPay";
		String to = walletEmail;
		sendEmail(to, subject, body);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "withdraw_success";
	}


	@GetMapping("/wallet/{id}/delete")
	public String deleteWallet(@PathVariable("id") Integer id, RedirectAttributes RedirectAttributes) {

		Wallet wallet = walletRepository.getById(id);
		LocalDateTime currentDateTime = LocalDateTime.now();
		wallet.setDeleted(true);
		wallet.setDateTimeofDeletion(currentDateTime);
		Notifications notifications = new Notifications();
		notifications.setAccount(wallet.getAccount());
		notifications.setDateTime(currentDateTime);
		notifications.setTitle("Successfully Deleted Wallet ID!");

		notifications.setMessage("You have successfully deleted your wallet ID: " + id + ".");

		RedirectAttributes.addFlashAttribute("deleteSuccess", "Wallet ID: " + id + " successfully deleted.");

		notificationsRepository.save(notifications);

		return "redirect:/wallet";
	}

	@PostMapping("/wallet/{id}/checkouts")
	public String postForm(@PathVariable("id") int Id, @RequestParam("amount") double amount,
			@RequestParam("payment_method_nonce") String nonce, Model model,
			final RedirectAttributes redirectAttributes) {
		BigDecimal decimalAmount;
		try {
			decimalAmount = new BigDecimal(amount);
		} catch (NumberFormatException e) {
			redirectAttributes.addFlashAttribute("errorDetails", "Error: 81503: Amount is an invalid format.");
			return "redirect:/wallet/{id}/topup";
		}

		TransactionRequest request = new TransactionRequest().amount(decimalAmount).paymentMethodNonce(nonce).options()
				.submitForSettlement(true).done();
		System.out.println(amount);
//		Result<Transaction> result = gateway.transaction().sale(request);
//
//		if (result.isSuccess()) {
//			Transaction transaction = result.getTarget();
//			return "redirect:/checkouts/" + transaction.getId();
//		} else if (result.getTransaction() != null) {
//			Transaction transaction = result.getTransaction();
//			return "redirect:/checkouts/" + transaction.getId();
//		} else {
//			StringBuilder errorString = new StringBuilder();
//			for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
//				errorString.append("Error: ").append(error.getCode()).append(": ").append(error.getMessage())
//						.append("\n");
//			}
//			redirectAttributes.addFlashAttribute("errorDetails", errorString.toString());
//			return "redirect:/checkouts";
//		}

		return "redirect:/";
	}

	@GetMapping("/wallet/{id}/walletLimit")
	public String viewWalletLimit(@PathVariable("id") Integer id, Model model) {
		Wallet wallet = walletRepository.getById(id);
		model.addAttribute("wallet", wallet);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "view_wallet_limit";
	}

	@GetMapping("/wallet/{id}/updateWalletLimit")
	public String updateWalletLimit(@PathVariable("id") Integer id, Model model) {
		Wallet wallet = walletRepository.getById(id);
		model.addAttribute("wallet", wallet);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "update_wallet_limit";
	}

	@PostMapping("/wallet/{id}/updateWalletLimit")
	public String saveWalletLimit(@PathVariable("id") Integer id,
			@RequestParam("paymentLimit") double updatedWalletLimit, RedirectAttributes RedirectAttributes) {

		Wallet wallet = walletRepository.getById(id);
		LocalDateTime currentDateTime = LocalDateTime.now();
		double oldWalletLimit = wallet.getPaymentLimit();
		wallet.setPaymentLimit(updatedWalletLimit);
		Notifications notifications = new Notifications();
		notifications.setAccount(wallet.getAccount());
		notifications.setDateTime(currentDateTime);
		notifications.setTitle("Successfully Updated Wallet Limit!");

		notifications.setMessage("You have successfully updated wallet limit for your wallet ID: " + id + ".");

		if (updatedWalletLimit != oldWalletLimit) {
			RedirectAttributes.addFlashAttribute("success", "Wallet ID: " + id
					+ " successfully updated wallet limit from $" + oldWalletLimit + " to $" + updatedWalletLimit);
		}

		if (updatedWalletLimit == oldWalletLimit) {
			RedirectAttributes.addFlashAttribute("warning", "No update was made.");
		}

		notificationsRepository.save(notifications);

		return "redirect:/wallet/{id}/walletLimit";
	}

	@GetMapping("/wallet/current/{id}")
	public String updateWallet(@PathVariable("id") Integer id, Model model) {
		Wallet wallet = walletRepository.getById(id);
		model.addAttribute("wallet", wallet);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "current_walletpin";
	}

	@PostMapping("/wallet/current/{id}")
	public String saveWallet(@PathVariable("id") Integer id, Model model, @RequestParam("d1") String digit1,
			@RequestParam("d2") String digit2, @RequestParam("d3") String digit3, @RequestParam("d4") String digit4,
			@RequestParam("d5") String digit5, @RequestParam("d6") String digit6,
			RedirectAttributes redirectAttributes) {

		// Get account object by account's id
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();

		String pin = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;

		String regularexpression = "^\\d{6}$";
		String errorMsg = "";

		boolean pattern = pin.matches(regularexpression);
		Wallet wallet = walletRepository.getById(id);

		if (pattern != true) {
			errorMsg = "Invalid Pin. Pin should include numeric numbers only.";
		}

		if (errorMsg != "") {
			int unread = notificationsService.unreadNotificiations();
			model.addAttribute("unread", unread);
			model.addAttribute("errorMsg", errorMsg);
			model.addAttribute("wallet", wallet);
			return "current_walletpin";
		}

		String encodedConfirmPin = passwordEncoder.encode(pin);

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		if (passwordEncoder.matches(pin, wallet.getPin()) == false) {
			String wrongmsg = "Wrong current pin";
			model.addAttribute("errorMsg", wrongmsg);
			model.addAttribute("wallet", wallet);
			int unread = notificationsService.unreadNotificiations();
			model.addAttribute("unread", unread);
			return "current_walletpin";
		}
		model.addAttribute("wallet", wallet);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "redirect:/wallet/updatepin/{id}";

	}

	@GetMapping("/wallet/updatepin/{id}")
	public String saveWalletPin(@PathVariable("id") Integer id, Model model) {

		Wallet wallet = walletRepository.getById(id);

		model.addAttribute("wallet", wallet);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "update_walletpin";
	}

	@PostMapping("/wallet/confirmupdatepin/{id}")
	public String confirmUpdateWalletPin(@PathVariable("id") Integer id, Model model, @RequestParam("d1") String digit1,
			@RequestParam("d2") String digit2, @RequestParam("d3") String digit3, @RequestParam("d4") String digit4,
			@RequestParam("d5") String digit5, @RequestParam("d6") String digit6) {

		String pin = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;

		String regularexpression = "^\\d{6}$";
		String errorMsg = "";

		boolean pattern = pin.matches(regularexpression);

		Wallet wallet = walletRepository.getById(id);

		if (pattern != true) {
			errorMsg = "Invalid Pin. Pin should include numeric numbers only.";
		}

		if (errorMsg != "") {
			model.addAttribute("errorMsg", errorMsg);
			model.addAttribute("verifyPin", pin);
			model.addAttribute("wallet", wallet);
			return "update_walletpin";
		}

		model.addAttribute("verifyPin", pin);
		model.addAttribute("wallet", wallet);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "confirm_update_walletpin";
	}

	@PostMapping("/wallet/saveupdatepin/{id}")
	public String saveUpdateWalletPin(@PathVariable("id") Integer id, Model model, @RequestParam("d1") String digit1,
			@RequestParam("d2") String digit2, @RequestParam("d3") String digit3, @RequestParam("d4") String digit4,
			@RequestParam("d5") String digit5, @RequestParam("d6") String digit6,
			@RequestParam("verifyPin") String verifyPin) {

		Wallet wallet = walletRepository.getById(id);

		String confirmPin = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;
		String matchMsg = "";
		if (!confirmPin.equals(verifyPin)) {
			matchMsg = "Pin do not match.";
		}

		if (matchMsg != "") {
			model.addAttribute("matchMsg", matchMsg);
			model.addAttribute("verifyPin", verifyPin);
			model.addAttribute("wallet", wallet);
			return "confirm_update_walletpin";
		}

		String encodedConfirmPin = passwordEncoder.encode(confirmPin);
		wallet.setPin(encodedConfirmPin);
		walletRepository.save(wallet);
		model.addAttribute("wallet", wallet);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "update_walletpin_success";
	}


}
