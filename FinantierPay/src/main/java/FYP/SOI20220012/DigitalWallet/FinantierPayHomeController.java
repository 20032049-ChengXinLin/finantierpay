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
 * Date created: 2022-May-02 2:33:23 pm 
 * 
 */
package FYP.SOI20220012.DigitalWallet;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.zxing.WriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 20032049
 *
 */
@Controller
public class FinantierPayHomeController {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	private Logger logger = LoggerFactory.getLogger(FinantierPayHomeController.class);

	@Autowired
	private QRCodeService qrCodeService;

	@Autowired
	private NotificationsService notificationsService;

	@Autowired
	private NotificationsRepository notificationsRepository;

	@GetMapping("/")
	public String home(Model model) {
		// Get currently logged in user
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);
		List<Wallet> walletList = walletRepository.findByAccount_AccountIdAndIsDeleted(loggedInAccountId, false);

		if (account.getRole().equalsIgnoreCase("ROLE_USER")) {
			if (walletList.size() == 0) {
				return "redirect:/wallet/create";
			}
		}
		// Set Account as deleted after 30 days
		Account accountDates = accountRepository.getById(loggedInAccountId);
		if (accountDates.getDate_deleted() != null) {
			model.addAttribute("account", account);
			int unread = notificationsService.unreadNotificiations();
			model.addAttribute("unread", unread);
			return "confirm_account_deleted";
		}

		double totalamount = 0;
		for (int i = 0; i < walletList.size(); i++) {
			totalamount += walletList.get(i).getTotalAmount();
		}
		List<Transaction> transactionList = transactionRepository
				.findByWallet_Account_AccountIdAndIsDeletedAndArchiveOrderByDateTimeDesc(loggedInAccountId, false,
						"No");

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

		// QR Code LocalHost: URL Account link
		String byteArray = "https://finantierpay.azurewebsites.net/account/" + loggedInAccountId + "/wallet";

		byte[] image = new byte[0];
		try {

			// Generate and Return Qr Code in Byte Array
			image = QRCodeGenerator.getQRCodeImage(byteArray, 250, 250);

		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
		// Convert Byte Array into Base64 Encode String
		String qrcode = Base64.getEncoder().encodeToString(image);

		model.addAttribute("account", account);
		if (account.getRole().equals("ROLE_MERCHANT")) {
			List<Wallet> wallet = walletRepository.findByAccount_AccountIdAndIsDeleted(loggedInAccountId, false);
			// Merchant only has 1 Wallet
			model.addAttribute("wallet", wallet.get(0));
		}

		model.addAttribute("qrcode", qrcode);
		model.addAttribute("walletno", walletList.size());
		model.addAttribute("totalamount", totalamount);
		model.addAttribute("transactionList", transactionList);
		model.addAttribute("MonthlyDepositsAmountList", MonthlyDepositsAmountList);
		model.addAttribute("MonthlyCreditedAmountList", MonthlyCreditedAmountList);
		model.addAttribute("MonthList", allMonth);
		model.addAttribute("yearList", allyear);

		// Notifications
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		List<Notifications> notificationsList = notificationsRepository
				.findByAccount_AccountIdAndIsReadAndIsDeletedOrderByNotificationsIdDesc(loggedInAccountId, false,
						false);
		model.addAttribute("notificationsList", notificationsList);
		return "FinantierPay";
	}

	@PostMapping("/qrcodeReader")
	public String qrcodeReader(@RequestParam("qrCodeFile") MultipartFile qrCodeFile,
			RedirectAttributes redirectAttributes, Model model) {
		String pattern = "(.*?)\\.(jpeg|png)";
		String image = qrCodeFile.getOriginalFilename();
		boolean isvalid = Pattern.matches(pattern, image);
		if (!isvalid) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Invalid QR Code. QR Code image must be either PNG or JPEG");
			return "redirect:/";
		}
		try {
			String qrContent = qrCodeService.decodeQR(qrCodeFile.getBytes());
			System.out.println(qrContent);
			if (qrContent == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Invalid QR Code");
				return "redirect:/";
			} else {
				if (qrContent.contains("https://finantierpay.azurewebsites.net/")) {

					return "redirect:" + qrContent;
				} else {
					redirectAttributes.addFlashAttribute("errorMessage", "Invalid QR Code");
					return "redirect:/";
				}
			}
		} catch (IOException e) {
			// logger.error(e.getMessage(), e);
			redirectAttributes.addFlashAttribute("errorMessage", "Invalid QR Code");
		}

		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "redirect:/";
	}

	@GetMapping("/about")
	public String about(Model model) {

		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "about";
	}

	@GetMapping("/settings")
	public String Setting(Model model) {
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);
		model.addAttribute("account", account);

		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "settings";
	}

	@GetMapping("/faq")
	public String faq(Model model) {

		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "FAQ";
	}
}
