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
* Date created: 2022-Jun-13 9:31:05 am
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author 20009334
 *
 */

@Controller
public class FPayPaymentController {

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private PointsEarnedRepository pointsEarnedRepository;

	@Autowired
	private PointsHistoryRepository pointsHistoryRepository;

	@Autowired
	private VoucherRepository voucherRepository;

	@Autowired
	private JavaMailSender javaMailSender;

	@Resource(name = "authenticationManager")
	private AuthenticationManager authManager;
	
	@GetMapping("/finantierPayPayment")
	public String finantierPayPayment(@RequestParam("walletId") int walletId, @RequestParam("storeName") String storeName,
			@RequestParam("totalAmt") double total, @RequestParam("Redirect-URL") String url, @RequestParam("current-URL") String currenturl, Model model) {

		System.out.println(storeName);
		List<Wallet> Walletlist = walletRepository.findAll();

		boolean a = false;
		boolean b = false;

		// Validate Merchant Wallet Id
		for (int i = 0; i < Walletlist.size(); i++) {
			if (Walletlist.get(i).getWalletId() == walletId) {
				a = true;
			}
		}

		if (a == false) {
			return currenturl;
		}
		// Validate Merchant Username
		Wallet wallet = walletRepository.getById(walletId);

		if (wallet.getAccount().getRole().equalsIgnoreCase("ROLE_MERCHANT")) {
			String storeNameRemoveWhiteSpace = wallet.getAccount().getStoreName().replace(" ", "").trim();
			if (storeNameRemoveWhiteSpace.equalsIgnoreCase(storeName.trim().toUpperCase())) {
				b = true;
			}
		}

		if (b == false) {
			System.out.println("hello");
			return currenturl;
		}
		System.out.println(total);
		// Validate Shopping cart
		if (total == 0) {
			return currenturl;
		}

		model.addAttribute("MerchantWalletId", walletId);
		model.addAttribute("storeName", storeName);
		model.addAttribute("url", url);
		model.addAttribute("currenturl", currenturl);
		model.addAttribute("totalAmt", total);

		// CurrentDate
		LocalDate currentDate = LocalDate.now();

		// Set Voucher to disabled after current date is more than expiry date
		List<Voucher> voucherList = voucherRepository.findAll();
		for (int i = 0; i < voucherList.size(); i++) {
			LocalDate expirydate = voucherList.get(i).getExpiryDate();
			if (currentDate.compareTo(expirydate) > 0) {
				voucherList.get(i).setStatus("Expired");
				voucherRepository.save(voucherList.get(i));
			}
		}

		return "finantierPayPayment";
	}
	
	@PostMapping("/finantierPaylogin")
	public String finantierPayLogin(@RequestParam("MerchantWalletId") int walletId, @RequestParam("storeName") String storeName,
			@RequestParam("totalAmt") double total, @RequestParam("Redirect-URL") String url, @RequestParam("current-URL") String currenturl, Model model) {

		System.out.println(storeName);
		List<Wallet> Walletlist = walletRepository.findAll();

		boolean a = false;
		boolean b = false;

		// Validate Merchant Wallet Id
		for (int i = 0; i < Walletlist.size(); i++) {
			if (Walletlist.get(i).getWalletId() == walletId) {
				a = true;
			}
		}

		if (a == false) {
			return currenturl;
		}
		// Validate Merchant Username
		Wallet wallet = walletRepository.getById(walletId);

		if (wallet.getAccount().getRole().equalsIgnoreCase("ROLE_MERCHANT")) {
			String storeNameRemoveWhiteSpace = wallet.getAccount().getStoreName().replace(" ", "").trim();
			if (storeNameRemoveWhiteSpace.equalsIgnoreCase(storeName.trim().toUpperCase())) {
				b = true;
			}
		}

		if (b == false) {
			System.out.println("hello");
			return currenturl;
		}
		System.out.println(total);
		// Validate Shopping cart
		if (total == 0) {
			return currenturl;
		}

		model.addAttribute("MerchantWalletId", walletId);
		model.addAttribute("storeName", storeName);
		model.addAttribute("url", url);
		model.addAttribute("totalAmt", total);
		model.addAttribute("currenturl", currenturl);
		// CurrentDate
		LocalDate currentDate = LocalDate.now();

		// Set Voucher to disabled after current date is more than expiry date
		List<Voucher> voucherList = voucherRepository.findAll();
		for (int i = 0; i < voucherList.size(); i++) {
			LocalDate expirydate = voucherList.get(i).getExpiryDate();
			if (currentDate.compareTo(expirydate) > 0) {
				voucherList.get(i).setStatus("Expired");
				voucherRepository.save(voucherList.get(i));
			}
		}

		return "finantierPayLogin";
	}

	@PostMapping("/finantierPay/login")
	public String FinantierPayLoginProcess(HttpServletRequest request, @RequestParam("MerchantWalletId") int walletId,
			@RequestParam("storeName") String storeName, @RequestParam("Redirect-URL") String url,
			@RequestParam("current-URL") String currenturl, @RequestParam("totalAmt") double totalAmt, @RequestParam("username") String username,
			@RequestParam("password") String password, Model model, RedirectAttributes redirectAttributes) {

		// Validate if input username and password is empty
		if (username.equals("") || password.equals("")) {
			model.addAttribute("LoginError", "Invalid username and password");
			model.addAttribute("MerchantWalletId", walletId);
			model.addAttribute("storeName", storeName);
			model.addAttribute("url", url);
			model.addAttribute("totalAmt", totalAmt);
			model.addAttribute("currenturl", currenturl);
			return "finantierPayLogin";
		}

		// Validate input username must be same as FinantierPay Account Username
		// database
		Account account = accountRepository.findByUser(username);
		if (account == null) {
			model.addAttribute("LoginError", "Invalid username and password");
			model.addAttribute("MerchantWalletId", walletId);
			model.addAttribute("storeName", storeName);
			model.addAttribute("url", url);
			model.addAttribute("totalAmt", totalAmt);
			model.addAttribute("currenturl", currenturl);
			return "finantierPayLogin";
		}

		// Validate input password encrypt must be same as the Username's password in
		// FinantierPay Account Database
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		if (passwordEncoder.matches(password, account.getPassword()) == false) {
			model.addAttribute("LoginError", "Invalid username and password");
			model.addAttribute("MerchantWalletId", walletId);
			model.addAttribute("storeName", storeName);
			model.addAttribute("url", url);
			model.addAttribute("totalAmt", totalAmt);
			model.addAttribute("currenturl", currenturl);
			return "finantierPayLogin";
		}

		// Validate Login User must be ROLE_USER Account Type
		boolean a = false;
		if (account.getRole().equalsIgnoreCase("ROLE_USER")) {
			a = true;
		}

		if (a == false) {
			model.addAttribute("LoginError", "Invalid username and password");
			model.addAttribute("MerchantWalletId", walletId);
			model.addAttribute("storeName", storeName);
			model.addAttribute("url", url);
			model.addAttribute("totalAmt", totalAmt);
			model.addAttribute("currenturl", currenturl);
			return "finantierPayLogin";
		}

		// Manually authenticate user with Spring Security
		UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
		Authentication auth = authManager.authenticate(authReq);
		SecurityContext sc = SecurityContextHolder.getContext();
		sc.setAuthentication(auth);
		HttpSession session = request.getSession(true);
		session.setAttribute("SPRING_SECURITY_CONTEXT", sc);
		
		model.addAttribute("MerchantWalletId", walletId);
		model.addAttribute("storeName", storeName);
		model.addAttribute("url", url);
		model.addAttribute("totalAmt", totalAmt);
		model.addAttribute("currenturl", currenturl);
		return "finantierPayPayment_LoginSuccess";
	}
	

	@PostMapping("/chooseWalletToPay")
	public String ProcessChooseWallet(@RequestParam("storeName") String storeName,
			@RequestParam("MerchantWalletId") int walletId, @RequestParam("Redirect-URL") String url,
			@RequestParam("current-URL") String currenturl, @RequestParam("totalAmt") double totalAmt,
			@RequestParam(value = "walletIdpayment", defaultValue = "0") int UserWalletId,
			@RequestParam(value = "voucherId", defaultValue = "0") int voucherId, Model model) {

		// Get account object by account's id
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);

		// Find Wallet Based on Logged In Users Account
		List<Wallet> walletlist = walletRepository.findByAccount_AccountIdAndIsDeleted(loggedInAccountId, false);

		// Find Voucher Based on LoggedInUsers Account
		List<Voucher> voucherlist = voucherRepository.findByAccount_AccountId(loggedInAccountId);
		
		// Find Voucher StoreName
		for (int i = 0; i < voucherlist.size(); i++) {
			String storeNameRemoveWhiteSpace = voucherlist.get(i).getStoreName().replace(" ", "").trim();
			if (storeNameRemoveWhiteSpace.equalsIgnoreCase(storeName.trim().toUpperCase())) {
				System.out.println(storeNameRemoveWhiteSpace);
				model.addAttribute("voucherStoreName", storeNameRemoveWhiteSpace);
			}
		}
		
		// Current Date
		LocalDate currentDate = LocalDate.now();

		model.addAttribute("storeName", storeName);
		model.addAttribute("MerchantWalletId", walletId);
		model.addAttribute("url", url);
		model.addAttribute("currenturl", currenturl);
		model.addAttribute("totalAmt", totalAmt);

		model.addAttribute("account", account);
		model.addAttribute("listWallets", walletlist);
		model.addAttribute("voucherlist", voucherlist);
		model.addAttribute("currentDate", currentDate);

		// Get Voucher Based on Voucher Id
		if (voucherId != 0) {
			Voucher voucher = voucherRepository.getById(voucherId);
			double totalamount = 0;
			if (voucher.getVoucher_value() < 1) {
				totalamount = totalAmt - (totalAmt * voucher.getVoucher_value());
			} else if (voucher.getVoucher_value() >= 1) {
				totalamount = totalAmt - voucher.getVoucher_value();
			}
			
			model.addAttribute("totalamount", totalamount);
			model.addAttribute("voucher", voucher);
		} else {
			model.addAttribute("totalamount", totalAmt);
		}
		model.addAttribute("voucherId", voucherId);
		model.addAttribute("UserWalletId", UserWalletId);

		return "chooseWalletToPay";
	}

	@PostMapping("/chooseWalletToTopUp")
	public String chooseWalletToTopUp(@RequestParam("storeName") String storeName,
			@RequestParam("MerchantWalletId") int MerchantWalletId, @RequestParam("Redirect-URL") String url,
			@RequestParam("current-URL") String currenturl, @RequestParam("totalAmt") double totalAmt,
			@RequestParam(value = "walletIdTopUp", defaultValue = "0") int walletIdTopUp,
			@RequestParam(value = "topUpAmt", defaultValue = "0") double topUpAmt, Model model) {

		// Get account object by account's id
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);

		// Find Wallet Based on Logged In Users Account
		List<Wallet> walletlist = walletRepository.findByAccount_AccountIdAndIsDeleted(loggedInAccountId, false);
		String errorAmt = "";
		if (topUpAmt < 0) {
			errorAmt = "Top up amount must not be a negative amount";
			model.addAttribute("errorAmt", errorAmt);
		} else if (topUpAmt == 0) {
			model.addAttribute("topUpAmt", 0);
		} else if (topUpAmt > 0) {
			model.addAttribute("topUpAmt", topUpAmt);
		}
		model.addAttribute("storeName", storeName);
		model.addAttribute("MerchantWalletId", MerchantWalletId);
		model.addAttribute("url", url);
		model.addAttribute("currenturl", currenturl);
		model.addAttribute("totalAmt", totalAmt);
		model.addAttribute("account", account);
		model.addAttribute("listWallets", walletlist);

		Wallet wallet = walletRepository.getById(walletIdTopUp);
		model.addAttribute("walletId", walletIdTopUp);
		model.addAttribute("wallet", wallet);
		return "chooseWalletToTopUp";
	}
	
	@PostMapping("/chooseWalletToTopUp/process_topup")
	public String processTopUp(@RequestParam("storeName") String storeName,
			@RequestParam("MerchantWalletId") int MerchantWalletId, @RequestParam("Redirect-URL") String url,
			@RequestParam("current-URL") String currenturl, @RequestParam("totalAmt") double totalAmt, @RequestParam("formtopupTotal") double topUpTotal,
			@RequestParam("walletIdTopUp") int walletIdTopUp, @RequestParam("transactionId") String transactionId,
			Model model) {

		// Create Transaction
		Transaction transaction = new Transaction();
		Wallet wallet = walletRepository.getById(walletIdTopUp);

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
		double newupdateWalletAmtValue = (double) Math.round(updateWalletAmt * 100d) / 100d;
		wallet.setTotalAmount(newupdateWalletAmtValue);
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
				+ ". Total balance for Wallet ID " + wallet.getWalletId() + " is now $" + newupdateWalletAmtValue + ".");

		notificationsRepository.save(notifications);
		String walletEmail = wallet.getAccount().getEmail();
		String username = wallet.getAccount().getUsername();
		// Pass info to view to display success page
		model.addAttribute("topUpTotal", topUpTotal);
		model.addAttribute("wallet", wallet);
		model.addAttribute("transactionId", transactionId);
		model.addAttribute("currentDateTime", currentDateTime);
		model.addAttribute("updateWalletAmt", updateWalletAmt);
		model.addAttribute("walletEmail", walletEmail);
		
		// Send email
		String subject = "Top-Up Successfully!";
		String body = "Dear " + username + ",\n\n" + "Transaction ID: " + transactionId + "\n"
				+ "You have successfully topped up $" + topUpTotal + " to Wallet ID " + wallet.getWalletId() + "\n"
				+ "Total Amount in this wallet: $" + newupdateWalletAmtValue + "\n\nBest Regards, \nFinantierPay";
		String to = walletEmail;
		sendEmail(to, subject, body);

		// Merchant
		model.addAttribute("storeName", storeName);
		model.addAttribute("MerchantWalletId", MerchantWalletId);
		model.addAttribute("url", url);
		model.addAttribute("totalAmt", totalAmt);
		model.addAttribute("currenturl", currenturl);
		return "payment_topup_success";
	}

	@PostMapping("/chooseWalletToTransfer")
	public String chooseWalletToTransfer(@RequestParam("storeName") String storeName,
			@RequestParam("MerchantWalletId") int MerchantWalletId, @RequestParam("Redirect-URL") String url,
			@RequestParam("current-URL") String currenturl, @RequestParam("totalAmt") double totalAmt,
			@RequestParam(value = "walletIdTransferTo", defaultValue = "0") int walletIdTransferTo,
			@RequestParam(value = "walletIdTransferFrom", defaultValue = "0") int walletIdTransferFrom,
			@RequestParam(value = "transferAmt", defaultValue = "0") double transferAmt, Model model) {

		// Get account object by account's id
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);

		// Find Wallet Based on Logged In Users Account
		List<Wallet> walletlist = walletRepository.findByAccount_AccountIdAndIsDeleted(loggedInAccountId,false);
		if (walletlist.size() < 2) {
			model.addAttribute("NoWallet", "No Wallet Found");
		} else {
			model.addAttribute("WalletFound", "Wallet Found");
		}
		model.addAttribute("storeName", storeName);
		model.addAttribute("MerchantWalletId", MerchantWalletId);
		model.addAttribute("url", url);
		model.addAttribute("totalAmt", totalAmt);
		model.addAttribute("account", account);
		model.addAttribute("listWallets", walletlist);
		model.addAttribute("currenturl", currenturl);
		
		model.addAttribute("FromWalletId", walletIdTransferFrom);
		model.addAttribute("ToWalletId", walletIdTransferTo);
		model.addAttribute("transferAmt", transferAmt);

		if (walletIdTransferFrom != 0) {
			Wallet FromWallet = walletRepository.getById(walletIdTransferFrom);
			model.addAttribute("FromWallet", FromWallet);
		}
		if (walletIdTransferTo != 0) {
			Wallet ToWallet = walletRepository.getById(walletIdTransferTo);

			model.addAttribute("ToWallet", ToWallet);
		}

		return "chooseWalletToTransfer";
	}

	@PostMapping("/chooseWalletToTransfer/process_transfer")
	public String confirmWalletToTransfer(@RequestParam("storeName") String storeName,
			@RequestParam("MerchantWalletId") int MerchantWalletId, @RequestParam("Redirect-URL") String url,
			@RequestParam("current-URL") String currenturl, @RequestParam("totalAmt") double totalAmt, @RequestParam("walletIdTransferTo") int walletIdTransferTo,
			@RequestParam("walletIdTransferFrom") int walletIdTransferFrom,
			@RequestParam("transferAmt") double transferAmt, Model model) {

		// Get account object by account's id
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);

		// Find Wallet Based on Logged In Users Account
		List<Wallet> walletlist = walletRepository.findByAccount_AccountIdAndIsDeleted(loggedInAccountId, false);

		model.addAttribute("storeName", storeName);
		model.addAttribute("MerchantWalletId", MerchantWalletId);
		model.addAttribute("url", url);
		model.addAttribute("currenturl", currenturl);
		model.addAttribute("totalAmt", totalAmt);
		model.addAttribute("account", account);
		model.addAttribute("listWallets", walletlist);

		model.addAttribute("FromWalletId", walletIdTransferFrom);
		model.addAttribute("ToWalletId", walletIdTransferTo);
		model.addAttribute("transferAmt", transferAmt);

		Wallet FromWallet = walletRepository.getById(walletIdTransferFrom);
		model.addAttribute("FromWallet", FromWallet);

		Wallet ToWallet = walletRepository.getById(walletIdTransferTo);

		model.addAttribute("ToWallet", ToWallet);

		return "confirm_chooseWalletToTransfer";
	}
	
	@PostMapping("/chooseWalletToTransfer/save")
	public String saveWalletToTransfer(@RequestParam("storeName") String storeName,
			@RequestParam("MerchantWalletId") int MerchantWalletId, @RequestParam("Redirect-URL") String url,
			@RequestParam("current-URL") String currenturl, @RequestParam("totalAmt") double totalAmt, @RequestParam("walletIdTransferTo") int walletIdTransferTo,
			@RequestParam("walletIdTransferFrom") int walletIdTransferFrom,
			@RequestParam("transferAmt") double transferAmt, Model model) {
		
		// From Wallet
		Wallet Fromwallet = walletRepository.getById(walletIdTransferFrom);

		// To Wallet
		Wallet ToWallet = walletRepository.getById(walletIdTransferTo);

		// Calculate Total Amount of Transaction in a day based on current DateTime
		List<Transaction> transaction = transactionRepository.findByWallet_WalletIdAndIsDeletedOrderByDateTimeDesc(walletIdTransferFrom, false);
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
		double amount = total + transferAmt;
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
			double newtransferAmtValue = (double) Math.round(transferAmt * 100d) / 100d;
			newFailTransaction.setAmount(newtransferAmtValue);
			newFailTransaction.setDateTime(dateTime);
			newFailTransaction.setStatus("Unsuccess");
			newFailTransaction.setArchive("No");
			newFailTransaction.setWallet(Fromwallet);
			newFailTransaction.setDescription("Failed To Sent Money to Your Wallet ID " + ToWallet.getWalletId());
			transactionRepository.save(newFailTransaction);
			
			model.addAttribute("wallet", Fromwallet);
			model.addAttribute("FromWalletId", walletIdTransferFrom);
			model.addAttribute("transferAmt", transferAmt);
			model.addAttribute("FailtransactionId", FailtransactionId);
//			model.addAttribute("TotransactionId", TotransactionId);
			model.addAttribute("currentDateTime", dateTime);
			model.addAttribute("status", newFailTransaction.getStatus());
			model.addAttribute("ToWalletId", walletIdTransferTo);
			
			model.addAttribute("storeName", storeName);
			model.addAttribute("MerchantWalletId", MerchantWalletId);
			model.addAttribute("url", url);
			model.addAttribute("totalAmt", totalAmt);
			model.addAttribute("currenturl", currenturl);
			return "send_payment_wallet_money_fail";
			
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
			double newtransferAmtValue = (double) Math.round(transferAmt * 100d) / 100d;
			newFromTransaction.setAmount(newtransferAmtValue);
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
			newToTransaction.setAmount(newtransferAmtValue);
			newToTransaction.setDateTime(dateTime);
			newToTransaction.setStatus("Success");
			newToTransaction.setArchive("No");
			newToTransaction.setWallet(ToWallet);
			newToTransaction.setDescription("Successfully Received Money From Your Wallet ID " + ToWallet.getWalletId());
			transactionRepository.save(newToTransaction);

			// From Wallet
			double FromTotalAmount = Fromwallet.getTotalAmount();
			double deductedAmount = FromTotalAmount - transferAmt;
			double newdeductedValue = (double) Math.round(deductedAmount * 100d) / 100d;
			Fromwallet.setTotalAmount(newdeductedValue);
			walletRepository.save(Fromwallet);

			// To Wallet
			double ToWalletAmount = ToWallet.getTotalAmount();
			double AddedAmount = ToWalletAmount + transferAmt;
			double newAddedValue = (double) Math.round(AddedAmount * 100d) / 100d;
			ToWallet.setTotalAmount(newAddedValue);
			walletRepository.save(ToWallet);

			model.addAttribute("FromWalletId", walletIdTransferFrom);
			model.addAttribute("FromWallet", Fromwallet);
			
			model.addAttribute("ToWalletId", walletIdTransferTo);
			model.addAttribute("transferAmt", transferAmt);
			model.addAttribute("ToWallet", ToWallet);
			model.addAttribute("FromtransactionId", FromtransactionId);
			model.addAttribute("TotransactionId", TotransactionId);
			model.addAttribute("currentDateTime", dateTime);
			
			model.addAttribute("storeName", storeName);
			model.addAttribute("MerchantWalletId", MerchantWalletId);
			model.addAttribute("url", url);
			model.addAttribute("totalAmt", totalAmt);
			model.addAttribute("currenturl", currenturl);
		}

		return "send_payment_wallet_money_success";
	}
	
	@PostMapping("/walletpin")
	public String walletPin(@RequestParam("storeName") String storeName, @RequestParam("MerchantWalletId") int MerchantWalletId,
			@RequestParam("Redirect-URL") String url, @RequestParam("current-URL") String currenturl, @RequestParam("totalAmt") double totalAmt,
			@RequestParam("totalamount") double totalamount, @RequestParam("UserWalletId") int UserWalletId,
			@RequestParam(value = "voucherId", defaultValue = "0") int voucherId,
			@RequestParam("cashback_voucher") double cashback, Model model) {

		// Get account object by account's id
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);
		// Get Voucher Based on Voucher Id
		if (voucherId != 0) {
			Voucher voucher = voucherRepository.getById(voucherId);
			model.addAttribute("totalamount", totalamount);
			model.addAttribute("voucher", voucher);
		} else {
			model.addAttribute("totalamount", totalAmt);
		}

		model.addAttribute("account", account);
		model.addAttribute("storeName", storeName);
		model.addAttribute("MerchantWalletId", MerchantWalletId);
		model.addAttribute("url", url);
		model.addAttribute("currenturl", currenturl);
		model.addAttribute("totalamount", totalamount);
		model.addAttribute("totalAmt", totalAmt);

		model.addAttribute("UserWalletId", UserWalletId);
		model.addAttribute("voucherId", voucherId);
		model.addAttribute("cashback_voucher", cashback);

		return "wallet_pin";
	}

	@PostMapping("/paymentsuccess")
	public String paymentSuccess(@RequestParam("storeName") String storeName,
			@RequestParam("MerchantWalletId") int MerchantWalletId, @RequestParam("Redirect-URL") String url,
			@RequestParam("current-URL") String currenturl, @RequestParam("totalAmt") double totalAmt, @RequestParam("totalamount") double totalamount,
			@RequestParam("UserWalletId") int UserWalletId,
			@RequestParam(value = "voucherId", defaultValue = "0") int voucherId,
			@RequestParam("cashback_voucher") double cashback, @RequestParam("d1") String digit1,
			@RequestParam("d2") String digit2, @RequestParam("d3") String digit3, @RequestParam("d4") String digit4,
			@RequestParam("d5") String digit5, @RequestParam("d6") String digit6, Model model) {

		// Get account object by account's id
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);

		// Empty pin
		String pin = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;
		String regularexpression = "^\\d{6}$";
		String errorMsg = "";

		boolean pattern = pin.matches(regularexpression);

		if (pattern != true) {
			errorMsg = "Invalid Pin. Pin should include numeric numbers only.";
		}

		if (errorMsg != "") {
			System.out.println("hello");
			model.addAttribute("account", account);
			model.addAttribute("storeName", storeName);
			model.addAttribute("MerchantWalletId", MerchantWalletId);
			model.addAttribute("url", url);
			model.addAttribute("currenturl", currenturl);
			model.addAttribute("totalAmt", totalAmt);

			model.addAttribute("UserWalletId", UserWalletId);
			model.addAttribute("voucherId", voucherId);
			model.addAttribute("cashback_voucher", cashback);

			model.addAttribute("errorMsg", errorMsg);

			// Get Voucher Based on Voucher Id
			if (voucherId != 0) {
				Voucher voucher = voucherRepository.getById(voucherId);
				model.addAttribute("totalamount", totalamount);
				model.addAttribute("voucher", voucher);
			} else {
				model.addAttribute("totalamount", totalAmt);
			}

			return "wallet_pin";
		}

		// fail to match pin password
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		Wallet UserWallet = walletRepository.getById(UserWalletId);

		if (passwordEncoder.matches(pin, UserWallet.getPin()) == false) {
			model.addAttribute("account", account);
			model.addAttribute("storeName", storeName);
			model.addAttribute("MerchantWalletId", MerchantWalletId);
			model.addAttribute("url", url);
			model.addAttribute("currenturl", currenturl);
			model.addAttribute("totalAmt", totalAmt);

			model.addAttribute("UserWalletId", UserWalletId);
			model.addAttribute("voucherId", voucherId);
			model.addAttribute("cashback_voucher", cashback);
			errorMsg = "Invalid Pin. Pin should include numeric numbers only.";
			model.addAttribute("errorMsg", errorMsg);

			// Get Voucher Based on Voucher Id
			if (voucherId != 0) {
				Voucher voucher = voucherRepository.getById(voucherId);
				model.addAttribute("totalamount", totalamount);
				model.addAttribute("voucher", voucher);
			} else {
				model.addAttribute("totalamount", totalAmt);
			}
			return "wallet_pin";
		}

		// Success
		Wallet MerchantWallet = walletRepository.getById(MerchantWalletId);

		// Calculate Total Amount of Transaction in a day based on current DateTime
		List<Transaction> transaction = transactionRepository.findByWallet_WalletIdAndIsDeletedOrderByDateTimeDesc(1, false);
		double totalAmount = 0;
		LocalDate currentdate = LocalDate.now();
		for (int i = 0; i < transaction.size(); i++) {
			LocalDate transactiondate = transaction.get(i).getDateTime().toLocalDate();
			if (currentdate.compareTo(transactiondate) == 0) {
				if (transaction.get(i).getStatus().equalsIgnoreCase("Success")
						&& transaction.get(i).getActivity().equalsIgnoreCase("Sent")) {
					totalAmount += transaction.get(i).getAmount();
					System.out.println(transaction.get(i).getAmount());
					System.out.println(transactiondate);
				}
			}
		}
		System.out.println(totalAmount);
		double total = totalAmount + totalamount;
		System.out.println(totalamount);
		if (total > UserWallet.getPaymentLimit()) {
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
			String Usertransaction = sbalpha.toString() + sbnumber.toString();
			char[] userarray = Usertransaction.toCharArray();
			for (int i = 0; i < userarray.length; i++) {
				int randomIndex = (int) (Math.random() * userarray.length);
				char temp = userarray[i];
				userarray[i] = userarray[randomIndex];
				userarray[randomIndex] = temp;
			}
			String FailedtransactionId = String.valueOf(userarray);

			// Membership Points Expiry date
			LocalDate currentDate = LocalDate.now();
			LocalDate expirydate = currentDate.plusMonths(6);

			// Transaction - User
			Transaction newFailedTransaction = new Transaction();
			newFailedTransaction.setTransactionId(FailedtransactionId);
			newFailedTransaction.setActivity("Sent");
			newFailedTransaction.setAmount(totalamount);
			newFailedTransaction.setDateTime(dateTime);
			newFailedTransaction.setStatus("Unsuccess");
			newFailedTransaction.setArchive("No");
			newFailedTransaction.setWallet(UserWallet);
			newFailedTransaction.setDescription(
					"Failed To Made Payment To " + MerchantWallet.getAccount().getStoreName().toUpperCase());
			// Voucher - User
			Voucher voucher = null;
			if (voucherId != 0) {
				voucher = voucherRepository.getById(voucherId);
				newFailedTransaction.setVoucher_value(voucher.getVoucher_value());
			}

			transactionRepository.save(newFailedTransaction);

			// Notifications - User
			Notifications Usernotifications = new Notifications();
			Usernotifications.setAccount(UserWallet.getAccount());
			Usernotifications.setDateTime(dateTime);
			Usernotifications.setTitle("Failed To Made Payment!");

			Usernotifications.setMessage(
					"You have failed to made payment to " + MerchantWallet.getAccount().getStoreName().toUpperCase()
							+ " $" + totalamount + " using wallet ID " + UserWallet.getWalletId()
							+ ". Current balance remained as $" + UserWallet.getTotalAmount());

			notificationsRepository.save(Usernotifications);

			model.addAttribute("url", url);
			model.addAttribute("currenturl", currenturl);
			model.addAttribute("totalAmt", totalAmt);
			model.addAttribute("totalamount", totalamount);
			model.addAttribute("wallet", UserWallet);
			model.addAttribute("walletId", UserWalletId);
			model.addAttribute("transactionId", newFailedTransaction.getTransactionId());
			model.addAttribute("storeName", storeName);
			model.addAttribute("status", newFailedTransaction.getStatus());
			model.addAttribute("dateTime", newFailedTransaction.getDateTime());
			model.addAttribute("MerchantWalletId", MerchantWalletId);

			return "payment_fail";
		} else {
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
			String Usertransaction = sbalpha.toString() + sbnumber.toString();
			char[] userarray = Usertransaction.toCharArray();
			for (int i = 0; i < userarray.length; i++) {
				int randomIndex = (int) (Math.random() * userarray.length);
				char temp = userarray[i];
				userarray[i] = userarray[randomIndex];
				userarray[randomIndex] = temp;
			}
			String UsertransactionId = String.valueOf(userarray);

			// Membership Points Expiry date
			LocalDate currentDate = LocalDate.now();
			LocalDate expirydate = currentDate.plusMonths(6);
			double newtotalamountValue = (double) Math.round(totalamount * 100d) / 100d;
			
			// Transaction - User
			Transaction newUserTransaction = new Transaction();
			newUserTransaction.setTransactionId(UsertransactionId);
			newUserTransaction.setActivity("Sent");
			newUserTransaction.setAmount(newtotalamountValue);
			newUserTransaction.setDateTime(dateTime);
			newUserTransaction.setStatus("Success");
			newUserTransaction.setArchive("No");
			newUserTransaction.setWallet(UserWallet);
			newUserTransaction.setDescription(
					"Payment Succesfully Made To " + MerchantWallet.getAccount().getStoreName().toUpperCase());
			// Voucher - User
			Voucher voucher = null;
			if (voucherId != 0) {
				voucher = voucherRepository.getById(voucherId);
				voucher.setStatus("Redeemed");
				voucherRepository.save(voucher);
				newUserTransaction.setVoucher_value(voucher.getVoucher_value());
			}
			double totalPointsEarned = totalamount * 0.10;
			double newtotalPointsValue = (double) Math.round(totalPointsEarned * 100d) / 100d;
			
			newUserTransaction.setPoints_earned(newtotalPointsValue);
			newUserTransaction.setPointsExpiryDate(expirydate);
			transactionRepository.save(newUserTransaction);

			// Points Earned
			PointsEarned pointsEarned = new PointsEarned();
			pointsEarned.setAccount(account);
			pointsEarned.setPointsExpiryDate(expirydate);
			pointsEarned.setPointsEarned(newtotalPointsValue);
			pointsEarnedRepository.save(pointsEarned);

			// Points History
			PointsHistory pointsHistory = new PointsHistory();
			pointsHistory.setPointsEarned(pointsEarned);
			pointsHistory.setTotalPoints(newtotalPointsValue);
			pointsHistory.setDateTime(dateTime);
			pointsHistory.setStatus("Earned");
			pointsHistory.setArchive("No");
			pointsHistoryRepository.save(pointsHistory);

			// Generating Random Unique Transaction Id - CashBack
			String CashBackalpha = "ABCDEFGHIJKLMNOPQRSTUVWXY";
			String CashBacknumber = "0123456789";
			Random CashBackrandom = new Random();
			StringBuilder CashBacksbalpha = new StringBuilder(11);
			StringBuilder CashBacksbnumber = new StringBuilder(6);
			for (int i = 0; i < 11; i++) {
				CashBacksbnumber.append(CashBacknumber.charAt(CashBackrandom.nextInt(CashBacknumber.length())));
			}
			for (int i = 0; i < 6; i++) {
				CashBacksbalpha.append(CashBackalpha.charAt(CashBackrandom.nextInt(CashBackalpha.length())));
			}
			// Sorting the Transaction Order
			String CashBacktransaction = CashBacksbalpha.toString() + CashBacksbnumber.toString();
			char[] CashBackarray = CashBacktransaction.toCharArray();
			for (int i = 0; i < CashBackarray.length; i++) {
				int randomIndex = (int) (Math.random() * CashBackarray.length);
				char temp = CashBackarray[i];
				CashBackarray[i] = CashBackarray[randomIndex];
				CashBackarray[randomIndex] = temp;
			}
			String CashBacktransactionId = String.valueOf(CashBackarray);
			double cashbackValue = cashback * totalamount; 
			double newcashBackValue = (double) Math.round(cashbackValue * 100d) / 100d;
			// Transaction - User
			Transaction newCashBackTransaction = new Transaction();
			newCashBackTransaction.setTransactionId(CashBacktransactionId);
			newCashBackTransaction.setActivity("Received");
			newCashBackTransaction.setAmount(newcashBackValue);
			newCashBackTransaction.setDateTime(dateTime);
			newCashBackTransaction.setStatus("Success");
			newCashBackTransaction.setArchive("No");
			newCashBackTransaction.setWallet(UserWallet);
			newCashBackTransaction.setDescription("Total CashBack Amount Earned");
			transactionRepository.save(newCashBackTransaction);

			// Amount Deducted - User
			double UserWalletAmt = UserWallet.getTotalAmount();
			double deductedAmt = UserWalletAmt - totalamount;
			double newdeductedValue = (double) Math.round(deductedAmt * 100d) / 100d;
			UserWallet.setTotalAmount(newdeductedValue + newcashBackValue);
			walletRepository.save(UserWallet);

			// Membership Points - User
			double UserMembershipPoints = account.getTotal_points();
			double UserEarnPoints = UserMembershipPoints + newtotalPointsValue;
			account.setTotal_points(UserEarnPoints);

			account.setBalance_points(account.getBalance_points() + newtotalPointsValue);

			// Change Membership status
			if (UserEarnPoints <= 100) {
				account.setMembership_levels("ROOKIE");
				account.setCashback_voucher(0.03);

			} else if (UserEarnPoints > 100 && UserEarnPoints <= 500) {
				account.setMembership_levels("BRONZE");
				account.setCashback_voucher(0.05);

			} else if (UserEarnPoints > 500 && UserEarnPoints <= 1000) {
				account.setMembership_levels("SILVER");
				account.setCashback_voucher(0.10);

			} else if (UserEarnPoints > 1000) {
				account.setMembership_levels("GOLD");
				account.setCashback_voucher(0.15);
			}

			accountRepository.save(account);

			// Notifications - User
			Notifications Usernotifications = new Notifications();
			Usernotifications.setAccount(UserWallet.getAccount());
			Usernotifications.setDateTime(dateTime);
			Usernotifications.setTitle("Successfully made payment!");

			Usernotifications.setMessage("You have made successfully payment to "
					+ MerchantWallet.getAccount().getStoreName().toUpperCase() + " $" + newtotalamountValue
					+ " using wallet ID " + UserWallet.getWalletId() + ". Current balance is now $" + newdeductedValue
					+ ". Cash Back amount earned is $" + newcashBackValue + ". Current membership status is "
					+ account.getMembership_levels() + ". Expiry date of your points is " + expirydate + ".");

			notificationsRepository.save(Usernotifications);

			// Send email - User
			String Usersubject = "Successfully made payment!";
			String Userbody = "Dear " + UserWallet.getAccount().getUsername().toUpperCase() + ",\n\n"
					+ "Transaction ID: " + UsertransactionId + "\n" + "You have successfully made payment $"
					+ newtotalamountValue + " to " + MerchantWallet.getAccount().getStoreName().toUpperCase()
					+ " using wallet ID " + UserWallet.getWalletId() + "\n" + "Total Amount left for this wallet is: $"
					+ newdeductedValue + "\n" + "Total cashback earned: $" + newcashBackValue
					+ "\nTotal membership points earned is: " + newtotalPointsValue + " points" + "\n"
					+ "Current membership status is: " + account.getMembership_levels() + ". \n"
					+ "Expiry date of your points is " + expirydate + "\n" + "\n\nBest Regards, \nFinantierPay";

			String Userto = UserWallet.getAccount().getEmail();
			sendEmail(Userto, Usersubject, Userbody);

			// Generating Random Unique Transaction Id
			Random Merchantrandom = new Random();
			StringBuilder Merchantsbalpha = new StringBuilder(11);
			StringBuilder Merchantsbnumber = new StringBuilder(6);
			for (int i = 0; i < 11; i++) {
				Merchantsbnumber.append(number.charAt(Merchantrandom.nextInt(number.length())));
			}
			for (int i = 0; i < 6; i++) {
				Merchantsbalpha.append(alpha.charAt(Merchantrandom.nextInt(alpha.length())));
			}
			String Merchanttransaction = Merchantsbalpha.toString() + Merchantsbnumber.toString();

			// Sorting the Transaction Order
			char[] Merchantarray = Merchanttransaction.toCharArray();
			for (int i = 0; i < Merchantarray.length; i++) {
				int randomIndex = (int) (Math.random() * Merchantarray.length);
				char temp = Merchantarray[i];
				Merchantarray[i] = Merchantarray[randomIndex];
				Merchantarray[randomIndex] = temp;
			}
			String MerchanttransactionId = String.valueOf(Merchantarray);
			Transaction newMerchantTransaction = new Transaction();
			newMerchantTransaction.setTransactionId(MerchanttransactionId);
			newMerchantTransaction.setActivity("Received");
			newMerchantTransaction.setAmount(totalAmt);
			newMerchantTransaction.setDateTime(dateTime);
			newMerchantTransaction.setStatus("Success");
			newMerchantTransaction.setArchive("No");
			newMerchantTransaction.setWallet(MerchantWallet);
			newMerchantTransaction
					.setDescription("Received Payment From " + UserWallet.getAccount().getUsername().toUpperCase()
							+ " On " + MerchantWallet.getAccount().getStoreName());
			transactionRepository.save(newMerchantTransaction);

			// Amount Added - Merchant
			double MerchantWalletAmt = MerchantWallet.getTotalAmount();
			double AddAmt = MerchantWalletAmt + totalAmt;
			double newAddedValue = (double) Math.round(AddAmt * 100d) / 100d;
			MerchantWallet.setTotalAmount(newAddedValue);
			walletRepository.save(MerchantWallet);
			double newtotalAmtValue = (double) Math.round(totalAmt * 100d) / 100d;
			
			// Notifications - Merchant
			Notifications Merchantnotifications = new Notifications();
			Merchantnotifications.setAccount(MerchantWallet.getAccount());
			Merchantnotifications.setDateTime(dateTime);
			Merchantnotifications.setTitle("Received Wallet amount Success!");

			Merchantnotifications.setMessage("You have received $" + newtotalAmtValue + " from "
					+ UserWallet.getAccount().getUsername().toUpperCase() + " to wallet ID "
					+ MerchantWallet.getWalletId() + " on " + MerchantWallet.getAccount().getStoreName().toUpperCase()
					+ ". Your current balance is now $" + newAddedValue + ".");

			notificationsRepository.save(Merchantnotifications);

			// Send email - Merchant
			String Merchantsubject = "Successfully Received Amount!";
			String Merchantbody = "Dear " + MerchantWallet.getAccount().getUsername().toUpperCase() + ",\n\n"
					+ "Transaction ID: " + MerchanttransactionId + "\n" + "You have received $" + newtotalAmtValue + " from "
					+ UserWallet.getAccount().getUsername().toUpperCase() + " to wallet ID "
					+ MerchantWallet.getWalletId() + " from " + MerchantWallet.getAccount().getStoreName().toUpperCase()
					+ "\n" + "Total Balance: $" + AddAmt + "\n\nBest Regards, \nFinantierPay";
			String Merchant = MerchantWallet.getAccount().getEmail();
			sendEmail(Merchant, Merchantsubject, Merchantbody);

			model.addAttribute("url", url);
			model.addAttribute("currenturl", currenturl);
			model.addAttribute("totalAmt", totalAmt);
			model.addAttribute("wallet", UserWallet);
			model.addAttribute("walletId", UserWalletId);
			model.addAttribute("transactionId", newUserTransaction.getTransactionId());
		}

		return "payment_success";
	}

	public void sendEmail(String to, String subject, String body) {
		SimpleMailMessage msg = new SimpleMailMessage();
		String fromAddress = "finantierpay@outlook.com";
		msg.setFrom(fromAddress);
		msg.setTo(to);
		msg.setSubject(subject);
		msg.setText(body);
		System.out.println("Sending");
		javaMailSender.send(msg);
		System.out.println("Sent");
	}

}
