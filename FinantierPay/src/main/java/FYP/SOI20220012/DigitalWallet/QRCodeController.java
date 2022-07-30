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
* Date created: 2022-Jun-01 11:54:49 am
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author 20032049
 *
 */
@Controller
public class QRCodeController {

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private NotificationsService notificationsService;
	
	@Autowired
	private JavaMailSender javaMailSender;

	@GetMapping("/account/{id}/wallet")
	public String viewQrCode(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes,
			@RequestParam(value = "totalAmt", defaultValue = "0") double amount) {

		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		if (id != loggedInAccountId) {
			List<Wallet> listloggedInWallets = walletRepository.findByAccount_AccountIdAndIsDeleted(loggedInAccountId, false);
			model.addAttribute("listloggedInWallets", listloggedInWallets);

			List<Wallet> listWallets = walletRepository.findByAccount_AccountIdAndIsDeleted(id, false);
			if (listWallets.size() != 0) {
				String errorAmt = "";
				if (amount < 0) {
					errorAmt = "Amount must not be a negative amount.";
				}
				Wallet Wallet = walletRepository.findByWalletIdAndAccount_AccountIdAndIsDeleted(listWallets.get(0).getWalletId(),
						id, false);
				model.addAttribute("errorAmt", errorAmt);
				model.addAttribute("Wallet", Wallet);
				model.addAttribute("WalletFound", "Wallet Found");
				model.addAttribute("totalAmt", amount);
			} else {
				model.addAttribute("NoWallet", "No Wallet Found");
			}
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "Sorry You Cannot Scan the QR Code.");
			return "redirect:/";
		}
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		
		return "send_account_money";
	}

	@PostMapping("/account/{id}/wallet/{walletId}/confirm")
	public String confirmTransferAmt(@PathVariable("id") Integer id, @PathVariable("walletId") int TransferWalletId,
			@RequestParam("walletId") String walletId, @RequestParam("totalAmt") double amount, Model model,
			RedirectAttributes redirectAttributes) {

		Wallet ToWallet = walletRepository.findByWalletIdAndAccount_AccountIdAndIsDeleted(TransferWalletId, id, false);
		String errorWalletId = "";
		Wallet wallet = null;
		if (walletId == "") {
			errorWalletId = "Please Select Wallet ID You would like to transfer.";
		} else {
			wallet = walletRepository.getById(Integer.parseInt(walletId));
		}

		if (errorWalletId != "") {
			redirectAttributes.addFlashAttribute("errorWalletId", errorWalletId);
			return "redirect:/account/{id}/wallet";
		}

		model.addAttribute("Wallet", wallet);
		model.addAttribute("ToWallet", ToWallet);
		model.addAttribute("totalAmt", amount);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		
		return "confirm_send";
	}

	@PostMapping("/account/{id}/wallet/{walletId}/success")
	public String successTransferAmt(@PathVariable("id") Integer id, @PathVariable("walletId") int walletId,
			@RequestParam("FromWalletId") int FromWalletId, @RequestParam("totalAmt") double amount, Model model) {
		// From Account Wallet
		Wallet FromWallet = walletRepository.getById(FromWalletId);

		// To Account Wallet
		Wallet ToWallet = walletRepository.findByWalletIdAndAccount_AccountIdAndIsDeleted(walletId, id, false);

		// Calculate Total Amount of Transaction in a day based on current DateTime
		List<Transaction> transaction = transactionRepository.findByWallet_WalletIdAndIsDeletedOrderByDateTimeDesc(FromWalletId, false);
		double totalAmt = 0;
		LocalDate currentdate = LocalDate.now();
		for (int i = 0; i < transaction.size(); i++) {
			LocalDate transactiondate = transaction.get(i).getDateTime().toLocalDate();
			if (currentdate.compareTo(transactiondate) == 0) {
				if (transaction.get(i).getStatus().equalsIgnoreCase("Success")
						&& transaction.get(i).getActivity().equalsIgnoreCase("Sent")) {
					totalAmt += transaction.get(i).getAmount();
					System.out.println(transaction.get(i).getAmount());
					System.out.println(transactiondate);
				}
			}
		}
		System.out.println(totalAmt);
		double total = totalAmt + amount;
		if (total > FromWallet.getPaymentLimit()) {
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
			newFailTransaction.setAmount(amount);
			newFailTransaction.setDateTime(dateTime);
			newFailTransaction.setStatus("Unsuccess");
			newFailTransaction.setArchive("No");
			newFailTransaction.setWallet(FromWallet);
			newFailTransaction
					.setDescription("Fail to Sent Money to " + ToWallet.getAccount().getUsername().toUpperCase());
			transactionRepository.save(newFailTransaction);

			// Notifications - FromUsers
			Notifications Fromnotifications = new Notifications();
			Fromnotifications.setAccount(FromWallet.getAccount());
			Fromnotifications.setDateTime(dateTime);
			Fromnotifications.setTitle("Failed To Sent Wallet Amount!");

			Fromnotifications.setMessage("You have failed to sent $" + amount + " to "
					+ ToWallet.getAccount().getUsername().toUpperCase() + " using wallet ID " + FromWallet.getWalletId()
					+ ". Your current balance remained $" + FromWallet.getTotalAmount() + ".");

			notificationsRepository.save(Fromnotifications);
			
			model.addAttribute("totalAmt", amount);
			model.addAttribute("Wallet", FromWallet);
			model.addAttribute("ToWallet", ToWallet);
			model.addAttribute("currentDateTime", dateTime);
			model.addAttribute("transactionId", FailtransactionId);
			model.addAttribute("status", newFailTransaction.getStatus());
			int unread = notificationsService.unreadNotificiations();
			model.addAttribute("unread", unread);
			
			return "send_fail";
			
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
			newFromTransaction.setAmount(amount);
			newFromTransaction.setDateTime(dateTime);
			newFromTransaction.setStatus("Success");
			newFromTransaction.setArchive("No");
			newFromTransaction.setWallet(FromWallet);
			newFromTransaction
					.setDescription("Successfully Sent Money to " + ToWallet.getAccount().getUsername().toUpperCase());
			transactionRepository.save(newFromTransaction);

			// Amount Deducted - LoggedInUsers
			double FromWalletAmt = FromWallet.getTotalAmount();
			double deductedAmt = FromWalletAmt - amount;
			double newdeductedValue = (double) Math.round(deductedAmt * 100d) / 100d;
			FromWallet.setTotalAmount(newdeductedValue);
			walletRepository.save(FromWallet);

			// Notifications - FromUsers
			Notifications Fromnotifications = new Notifications();
			Fromnotifications.setAccount(FromWallet.getAccount());
			Fromnotifications.setDateTime(dateTime);
			Fromnotifications.setTitle("Sent Wallet Amount Success!");

			Fromnotifications.setMessage("You have sent $" + amount + " to "
					+ ToWallet.getAccount().getUsername().toUpperCase() + " using wallet ID " + FromWallet.getWalletId()
					+ ". Your current balance is now $" + newdeductedValue + ".");

			notificationsRepository.save(Fromnotifications);

			// Send email - From Users
			String Fromsubject = "Successfully Sent Amount!";
			String Frombody = "Dear " + FromWallet.getAccount().getUsername().toUpperCase() + ",\n\n"
					+ "Transaction ID: " + FromtransactionId + "\n" + "You have successfully sent $" + amount + " to "
					+ ToWallet.getAccount().getUsername().toUpperCase() + " using wallet ID " + FromWallet.getWalletId()
					+ "\n" + "Total Amount left for this wallet is: $" + newdeductedValue
					+ "\n\nBest Regards, \nFinantierPay";
			String Fromto = FromWallet.getAccount().getEmail();
			sendEmail(Fromto, Fromsubject, Frombody);

			// Generating Random Unique Transaction Id
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
			newToTransaction.setAmount(amount);
			newToTransaction.setDateTime(dateTime);
			newToTransaction.setStatus("Success");
			newToTransaction.setArchive("No");
			newToTransaction.setWallet(ToWallet);
			newToTransaction.setDescription(
					"Successfully Received Money From " + FromWallet.getAccount().getUsername().toUpperCase());
			transactionRepository.save(newToTransaction);

			// Amount Added - ToUsers
			double ToWalletAmt = ToWallet.getTotalAmount();
			double AddAmt = ToWalletAmt + amount;
			double newAddedValue = (double) Math.round(AddAmt * 100d) / 100d;
			ToWallet.setTotalAmount(newAddedValue);
			walletRepository.save(ToWallet);

			// Notifications - ToUsers
			Notifications Tonotifications = new Notifications();
			Tonotifications.setAccount(ToWallet.getAccount());
			Tonotifications.setDateTime(dateTime);
			Tonotifications.setTitle("Received Wallet Amount Success!");

			Tonotifications.setMessage("You have received $" + amount + " from "
					+ FromWallet.getAccount().getUsername().toUpperCase() + " to wallet ID " + ToWallet.getWalletId()
					+ ". Your current balance is now $" +  newAddedValue + ".");

			notificationsRepository.save(Tonotifications);

			// Send email - To Users
			String Tosubject = "Successfully Received Amount!";
			String Tobody = "Dear " + ToWallet.getAccount().getUsername().toUpperCase() + ",\n\n" + "Transaction ID: "
					+ TotransactionId + "\n" + "You have received $" + amount + " from "
					+ FromWallet.getAccount().getUsername().toUpperCase() + " to wallet ID " + ToWallet.getWalletId()
					+ "\n" + "Total Balance: $" + newAddedValue + "\n\nBest Regards, \nFinantierPay";
			String To = ToWallet.getAccount().getEmail();
			sendEmail(To, Tosubject, Tobody);
			model.addAttribute("totalAmt", amount);
			model.addAttribute("Wallet", FromWallet);
			model.addAttribute("ToWallet", ToWallet);
			model.addAttribute("currentDateTime", dateTime);
			model.addAttribute("transactionId", FromtransactionId);
		}
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		
		return "send_success";
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
