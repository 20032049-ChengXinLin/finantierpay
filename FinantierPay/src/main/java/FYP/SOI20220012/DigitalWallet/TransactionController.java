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
* Date created: 2022-May-29 10:36:07 am
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
* Date created: 2022-May-29 10:36:07 am
*
*/
@Controller
public class TransactionController {

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	WalletRepository walletRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	private NotificationsService notificationsService;
	
	@Autowired
	private NotificationsRepository notificationsRepository;

	@GetMapping("/transactionrecords")
	public String viewAllTransactionRecords(Model model,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "startMonthYear", defaultValue = "null") String startMonthYear,
			@RequestParam(value = "endMonthYear", defaultValue = "null") String endMonthYear,
			@RequestParam(value = "activity", defaultValue = "null") String activity,
			@RequestParam(value = "archive", defaultValue = "null") String archive,
			@RequestParam(value = "status", defaultValue = "null") String status,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "page", defaultValue = "10") int page) {

		LocalDate currentDate = LocalDate.now();
//		LocalDate nowMinus9Months = currentDate.minusMonths(12);
		Transaction firstTransaction = transactionRepository.findTopByOrderByDateTimeAsc();
		LocalDate firstTransactionDateTime = firstTransaction.getDateTime().toLocalDate();
		DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyy-MM", Locale.ENGLISH);
		String startMonth = firstTransactionDateTime.format(dateformatter);
		String endMonth = currentDate.format(dateformatter);
		if (startMonthYear.equals("null") && endMonthYear.equals("null")) {
			startMonthYear = startMonth;
			endMonthYear = endMonth;
		} else if (startMonthYear.equals("null") && !endMonthYear.equals("null")) {
			startMonthYear = startMonth;
		} else if (!startMonthYear.equals("null") && endMonthYear.equals("null")) {
			endMonthYear = endMonth;
		}

		Page<Transaction> transaction = null;
		Pageable pageable = PageRequest.of(pageNum - 1, page, Sort.by("dateTime").descending());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate start = LocalDate.parse(startMonthYear + "-01", formatter);
		LocalDate end = LocalDate.parse(endMonthYear + "-01", formatter);
		LocalDateTime startdateTime = LocalDateTime.of(start.getYear(), start.getMonthValue(), 1, 0, 0, 0);
		System.out.println(startdateTime);
		LocalDateTime enddateTime = LocalDateTime.of(end.getYear(), end.getMonthValue(), end.lengthOfMonth(),  23, 59, 59);
		System.out.println(enddateTime);

		if (activity.equals("null") && status.equals("null") && keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository.findAllByDateTimeBetweenAndIsDeletedOrderByDateTimeDesc(startdateTime,
					enddateTime, false, pageable);

		} else if (!activity.equals("null") && status.equals("null") && keyword.equals("") && archive.equals("null")) {

			transaction = transactionRepository.findAllByDateTimeBetweenAndIsDeletedAndActivityOrderByDateTimeDesc(
					startdateTime, enddateTime, false, activity, pageable);

		} else if (activity.equals("null") && !status.equals("null") && keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository.findAllByDateTimeBetweenAndIsDeletedAndStatusOrderByDateTimeDesc(
					startdateTime, enddateTime, false, status, pageable);

		} else if (status.equals("null") && activity.equals("null") && !keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository
					.findAllByDateTimeBetweenAndIsDeletedAndDescriptionLikeOrderByDateTimeDesc(startdateTime,
							enddateTime, false, "%" + keyword + "%", pageable);

		} else if (status.equals("null") && activity.equals("null") && keyword.equals("") && !archive.equals("null")) {
			transaction = transactionRepository.findAllByDateTimeBetweenAndIsDeletedAndArchiveOrderByDateTimeDesc(
					startdateTime, enddateTime, false, archive, pageable);

		} else if (!activity.equals("null") && !status.equals("null") && keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository
					.findAllByDateTimeBetweenAndIsDeletedAndStatusAndActivityOrderByDateTimeDesc(startdateTime,
							enddateTime, false, status, activity, pageable);

		} else if (!activity.equals("null") && status.equals("null") && !keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository
					.findAllByDateTimeBetweenAndIsDeletedAndActivityAndDescriptionLikeOrderByDateTimeDesc(startdateTime,
							enddateTime, false, activity, "%" + keyword + "%", pageable);

		} else if (!activity.equals("null") && status.equals("null") && keyword.equals("") && !archive.equals("null")) {
			transaction = transactionRepository
					.findAllByDateTimeBetweenAndIsDeletedAndActivityAndArchiveOrderByDateTimeDesc(startdateTime,
							enddateTime, false, activity, archive, pageable);

		} else if (activity.equals("null") && !status.equals("null") && !keyword.equals("") && archive.equals("null")) {
			System.out.println(status);
			transaction = transactionRepository
					.findAllByDateTimeBetweenAndIsDeletedAndStatusAndDescriptionLikeOrderByDateTimeDesc(startdateTime,
							enddateTime, false, status, "%" + keyword + "%", pageable);

		} else if (activity.equals("null") && !status.equals("null") && keyword.equals("") && !archive.equals("null")) {
			transaction = transactionRepository
					.findAllByDateTimeBetweenAndIsDeletedAndStatusAndArchiveOrderByDateTimeDesc(startdateTime,
							enddateTime, false, status, archive, pageable);

		} else if (activity.equals("null") && status.equals("null") && !keyword.equals("") && !archive.equals("null")) {

			transaction = transactionRepository
					.findAllByDateTimeBetweenAndIsDeletedAndDescriptionLikeAndArchiveOrderByDateTimeDesc(startdateTime,
							enddateTime, false, "%" + keyword + "%", archive, pageable);

		} else if (!status.equals("null") && !activity.equals("null") && !keyword.equals("")
				&& archive.equals("null")) {
			transaction = transactionRepository
					.findAllByDateTimeBetweenAndIsDeletedAndActivityAndStatusAndDescriptionLikeOrderByDateTimeDesc(
							startdateTime, enddateTime, false, activity, status, "%" + keyword + "%", pageable);

		} else if (status.equals("null") && !activity.equals("null") && !keyword.equals("")
				&& !archive.equals("null")) {

			transaction = transactionRepository
					.findAllByDateTimeBetweenAndIsDeletedAndActivityAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
							startdateTime, enddateTime, false, activity, "%" + keyword + "%", archive, pageable);

		} else if (!status.equals("null") && !activity.equals("null") && keyword.equals("")
				&& !archive.equals("null")) {

			transaction = transactionRepository
					.findAllByDateTimeBetweenAndIsDeletedAndStatusAndActivityAndArchiveOrderByDateTimeDesc(
							startdateTime, enddateTime, false, status, activity, archive, pageable);

		} else if (!status.equals("null") && activity.equals("null") && !keyword.equals("")
				&& !archive.equals("null")) {

			transaction = transactionRepository
					.findAllByDateTimeBetweenAndIsDeletedAndStatusAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
							startdateTime, enddateTime, false, status, "%" + keyword + "%", archive, pageable);

		} else if (!status.equals("null") && !activity.equals("null") && !keyword.equals("")
				&& !archive.equals("null")) {
			transaction = transactionRepository
					.findAllByDateTimeBetweenAndIsDeletedAndStatusAndActivityAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
							startdateTime, enddateTime, false, status, activity, "%" + keyword + "%", archive,
							pageable);
		}

		List<Transaction> transactionlist = transaction.getContent();
		model.addAttribute("transactionlist", transactionlist);

		model.addAttribute("startMonthYear", startMonthYear);
		model.addAttribute("endMonthYear", endMonthYear);
		model.addAttribute("activity", activity);
		model.addAttribute("archive", archive);
		model.addAttribute("status", status);

		if (!keyword.equals("")) {
			model.addAttribute("keyword", keyword);
		}

		model.addAttribute("totalPage", transaction.getTotalPages());
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("page", page);

		return "view_transaction_records";
	}

	@GetMapping("/transactionhistory")
	public String viewTransactionHistory(Model model,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "startMonthYear", defaultValue = "null") String startMonthYear,
			@RequestParam(value = "endMonthYear", defaultValue = "null") String endMonthYear,
			@RequestParam(value = "activity", defaultValue = "null") String activity,
			@RequestParam(value = "status", defaultValue = "null") String status,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "page", defaultValue = "10") int page) {
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);

		LocalDate currentDate = LocalDate.now();
		LocalDate nowMinus12Months = currentDate.minusMonths(12);
		DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyy-MM", Locale.ENGLISH);
		String startMonth = nowMinus12Months.format(dateformatter);
		String endMonth = currentDate.format(dateformatter);
		if (startMonthYear.equals("null") && endMonthYear.equals("null")) {
			startMonthYear = startMonth;
			endMonthYear = endMonth;
		} else if (startMonthYear.equals("null") && !endMonthYear.equals("null")) {
			startMonthYear = startMonth;
		} else if (!startMonthYear.equals("null") && endMonthYear.equals("null")) {
			endMonthYear = endMonth;
		}

		Page<Transaction> transaction = null;
		Pageable pageable = PageRequest.of(pageNum - 1, page, Sort.by("dateTime").descending());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate start = LocalDate.parse(startMonthYear + "-01", formatter);
		LocalDate end = LocalDate.parse(endMonthYear + "-01", formatter);
		LocalDateTime startdateTime = LocalDateTime.of(start.getYear(), start.getMonthValue(), 1, 0, 0, 0);
		System.out.println(startdateTime);
		LocalDateTime enddateTime = LocalDateTime.of(end.getYear(), end.getMonthValue(), end.lengthOfMonth(), 23, 59, 59);
		System.out.println(enddateTime);

		if (activity.equals("null") && status.equals("null") && keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenOrderByDateTimeDesc(
							loggedInAccountId, false, "No", startdateTime, enddateTime, pageable);

		} else if (!activity.equals("null") && status.equals("null") && keyword.equals("")) {

			transaction = transactionRepository
					.findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndActivityOrderByDateTimeDesc(
							loggedInAccountId, false, "No", startdateTime, enddateTime, activity, pageable);

		} else if (activity.equals("null") && !status.equals("null") && keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndStatusOrderByDateTimeDesc(
							loggedInAccountId, false, "No", startdateTime, enddateTime, status, pageable);

		} else if (status.equals("null") && activity.equals("null") && !keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndDescriptionLikeOrderByDateTimeDesc(
							loggedInAccountId, false, "No", startdateTime, enddateTime, "%" + keyword + "%", pageable);
		} else if (!activity.equals("null") && !status.equals("null") && keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndStatusAndActivityOrderByDateTimeDesc(
							loggedInAccountId, false, "No", startdateTime, enddateTime, status, activity, pageable);

		} else if (!activity.equals("null") && status.equals("null") && !keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndActivityAndDescriptionLikeOrderByDateTimeDesc(
							loggedInAccountId, false, "No", startdateTime, enddateTime, activity, "%" + keyword + "%",
							pageable);

		} else if (activity.equals("null") && !status.equals("null") && !keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndStatusAndDescriptionLikeOrderByDateTimeDesc(
							loggedInAccountId, false, "No", startdateTime, enddateTime, status, "%" + keyword + "%",
							pageable);

		} else if (!status.equals("null") && !activity.equals("null") && !keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndActivityAndStatusAndDescriptionLikeOrderByDateTimeDesc(
							loggedInAccountId, false, "No", startdateTime, enddateTime, activity, status,
							"%" + keyword + "%", pageable);
		}

		List<Transaction> transactionlist = transaction.getContent();
		model.addAttribute("transactionlist", transactionlist);

		model.addAttribute("startMonthYear", startMonthYear);
		model.addAttribute("endMonthYear", endMonthYear);
		model.addAttribute("activity", activity);
		model.addAttribute("status", status);

		if (!keyword.equals("")) {
			model.addAttribute("keyword", keyword);
		}

		model.addAttribute("totalPage", transaction.getTotalPages());
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("page", page);

		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "view_transaction_history";
	}

	@GetMapping("/transactionrecords/edit/{id}")
	public String editTransactionRecords(@PathVariable("id") String id, Model model) {
		Transaction transaction = transactionRepository.getById(id);
		model.addAttribute("Transaction", transaction);
		return "edit_transaction_records";
	}

	@PostMapping("/transactionrecords/edit/{id}")
	public String saveTransactionRecords(@PathVariable("id") String id, Transaction Transaction,
			RedirectAttributes redirectAttributes) {
		Transaction transaction = transactionRepository.getById(id);
		if (transaction.getPointsExpiryDate() != null) {
			if (transaction.getAmount() == Transaction.getAmount()
					&& transaction.getActivity().equals(Transaction.getActivity())
					&& transaction.getVoucher_value() == Transaction.getVoucher_value()
					&& transaction.getPoints_earned() == Transaction.getPoints_earned()
					&& transaction.getPointsExpiryDate().equals(Transaction.getPointsExpiryDate())
					&& transaction.getStatus().equals(Transaction.getStatus())
					&& transaction.getArchive().equals(Transaction.getArchive())
					&& transaction.getDescription().equals(Transaction.getDescription())) {
				redirectAttributes.addFlashAttribute("Warning", "No changes was made");
			} else {
				transactionRepository.save(Transaction);
				LocalDateTime currentDateTime = LocalDateTime.now();
				Notifications notifications = new Notifications();
				notifications.setAccount(transaction.getWallet().getAccount());
				notifications.setDateTime(currentDateTime);
				notifications.setTitle("Changes Was Made in Your Transaction History!");
				if (Transaction.getArchive().equals("Yes")) {
					notifications.setMessage("Your Transaction History for Transaction ID: "
							+ transaction.getTransactionId() + " has been changed. You will not be able to view them anymore");
				} else if (Transaction.getArchive().equals("No")) {
					notifications.setMessage("Your Transaction History for Transaction ID: "
							+ transaction.getTransactionId() + " has been changed.");
				}

				notificationsRepository.save(notifications);

				redirectAttributes.addFlashAttribute("success", "Transaction ID: " + id + " is successfully updated.");
			}
		} else if (transaction.getPointsExpiryDate() == null) {
			if (transaction.getAmount() == Transaction.getAmount()
					&& transaction.getActivity().equals(Transaction.getActivity())
					&& transaction.getVoucher_value() == Transaction.getVoucher_value()
					&& transaction.getPoints_earned() == Transaction.getPoints_earned()
					&& transaction.getStatus().equals(Transaction.getStatus())
					&& transaction.getArchive().equals(Transaction.getArchive())
					&& transaction.getDescription().equals(Transaction.getDescription())) {
				redirectAttributes.addFlashAttribute("Warning", "No changes was made");
			} else {
				transactionRepository.save(Transaction);
				LocalDateTime currentDateTime = LocalDateTime.now();
				Notifications notifications = new Notifications();
				notifications.setAccount(transaction.getWallet().getAccount());
				notifications.setDateTime(currentDateTime);
				notifications.setTitle("Changes Was Made in Your Transaction History!");
				if (Transaction.getArchive().equals("Yes")) {
					notifications.setMessage("Your Transaction History for Transaction ID: "
							+ transaction.getTransactionId() + " has been changed. You will not be able to view them anymore");
				} else if (Transaction.getArchive().equals("No")) {
					notifications.setMessage("Your Transaction History for Transaction ID: "
							+ transaction.getTransactionId() + " has been changed.");
				}

				notificationsRepository.save(notifications);
				redirectAttributes.addFlashAttribute("success", "Transaction ID: " + Transaction.getTransactionId() + " is successfully updated.");
			}
		}

		return "redirect:/transactionrecords";
	}

	@GetMapping("/transactionrecords/delete/{id}")
	public String deleteTransactionRecords(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {

		Transaction transaction = transactionRepository.getById(id);
		transaction.setDeleted(true);
		transactionRepository.save(transaction);
		redirectAttributes.addFlashAttribute("success", "Transaction ID: " + id + " successfully deleted.");

		return "redirect:/transactionrecords";
	}

	@GetMapping("/wallet/{id}/transactionhistory")
	public String viewIndividualWalletTransaction(@PathVariable("id") Integer id, Model model,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "startMonthYear", defaultValue = "null") String startMonthYear,
			@RequestParam(value = "endMonthYear", defaultValue = "null") String endMonthYear,
			@RequestParam(value = "activity", defaultValue = "null") String activity,
			@RequestParam(value = "status", defaultValue = "null") String status,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "page", defaultValue = "10") int page) {

		LocalDate currentDate = LocalDate.now();
		LocalDate nowMinus12Months = currentDate.minusMonths(12);
		DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyy-MM", Locale.ENGLISH);
		String startMonth = nowMinus12Months.format(dateformatter);
		String endMonth = currentDate.format(dateformatter);
		if (startMonthYear.equals("null") && endMonthYear.equals("null")) {
			startMonthYear = startMonth;
			endMonthYear = endMonth;
		} else if (startMonthYear.equals("null") && !endMonthYear.equals("null")) {
			startMonthYear = startMonth;
		} else if (!startMonthYear.equals("null") && endMonthYear.equals("null")) {
			endMonthYear = endMonth;
		}

		Page<Transaction> transaction = null;
		Pageable pageable = PageRequest.of(pageNum - 1, page, Sort.by("dateTime").descending());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate start = LocalDate.parse(startMonthYear + "-01", formatter);
		LocalDate end = LocalDate.parse(endMonthYear + "-01", formatter);
		LocalDateTime startdateTime = LocalDateTime.of(start.getYear(), start.getMonthValue(), 1, 0, 0, 0);
		System.out.println(startdateTime);
		LocalDateTime enddateTime = LocalDateTime.of(end.getYear(), end.getMonthValue(), end.lengthOfMonth(),  23, 59, 59);
		System.out.println(enddateTime);

		if (activity.equals("null") && status.equals("null") && keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenOrderByDateTimeDesc(id, false, "No",
							startdateTime, enddateTime, pageable);

		} else if (!activity.equals("null") && status.equals("null") && keyword.equals("")) {

			transaction = transactionRepository
					.findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndActivityOrderByDateTimeDesc(id,
							false, "No", startdateTime, enddateTime, activity, pageable);

		} else if (activity.equals("null") && !status.equals("null") && keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndStatusOrderByDateTimeDesc(id,
							false, "No", startdateTime, enddateTime, status, pageable);

		} else if (status.equals("null") && activity.equals("null") && !keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndDescriptionLikeOrderByDateTimeDesc(
							id, false, "No", startdateTime, enddateTime, "%" + keyword + "%", pageable);
		} else if (!activity.equals("null") && !status.equals("null") && keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndStatusAndActivityOrderByDateTimeDesc(
							id, false, "No", startdateTime, enddateTime, status, activity, pageable);

		} else if (!activity.equals("null") && status.equals("null") && !keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndActivityAndDescriptionLikeOrderByDateTimeDesc(
							id, false, "No", startdateTime, enddateTime, activity, "%" + keyword + "%", pageable);

		} else if (activity.equals("null") && !status.equals("null") && !keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndStatusAndDescriptionLikeOrderByDateTimeDesc(
							id, false, "No", startdateTime, enddateTime, status, "%" + keyword + "%", pageable);

		} else if (!status.equals("null") && !activity.equals("null") && !keyword.equals("")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndActivityAndStatusAndDescriptionLikeOrderByDateTimeDesc(
							id, false, "No", startdateTime, enddateTime, activity, status, "%" + keyword + "%",
							pageable);
		}

		List<Transaction> transactionlist = transaction.getContent();
		model.addAttribute("transactionlist", transactionlist);

		model.addAttribute("startMonthYear", startMonthYear);
		model.addAttribute("endMonthYear", endMonthYear);
		model.addAttribute("activity", activity);
		model.addAttribute("status", status);

		if (!keyword.equals("")) {
			model.addAttribute("keyword", keyword);
		}
		Wallet wallet = walletRepository.getById(id);
		model.addAttribute("wallet", wallet);
//		List<Transaction> AlltransactionList = transactionRepository.findByWallet_WalletIdAndIsDeletedOrderByDateTimeDesc(id, false);
//		model.addAttribute("Alltransactionlist", AlltransactionList);
		model.addAttribute("totalPage", transaction.getTotalPages());
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("page", page);
		model.addAttribute("walletId", id);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "view_wallet_transaction_history";
	}

	@GetMapping("/wallet/{id}/transactionrecords")
	public String viewIndividualWalletTransactionRecords(@PathVariable("id") Integer id, Model model,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "startMonthYear", defaultValue = "null") String startMonthYear,
			@RequestParam(value = "endMonthYear", defaultValue = "null") String endMonthYear,
			@RequestParam(value = "activity", defaultValue = "null") String activity,
			@RequestParam(value = "archive", defaultValue = "null") String archive,
			@RequestParam(value = "status", defaultValue = "null") String status,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "page", defaultValue = "10") int page) {

		LocalDate currentDate = LocalDate.now();
//		LocalDate nowMinus9Months = currentDate.minusMonths(12);
		Transaction firstTransaction = transactionRepository.findTopByOrderByDateTimeAsc();
		LocalDate firstTransactionDateTime = firstTransaction.getDateTime().toLocalDate();
		DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyy-MM", Locale.ENGLISH);
		String startMonth = firstTransactionDateTime.format(dateformatter);
		String endMonth = currentDate.format(dateformatter);
		if (startMonthYear.equals("null") && endMonthYear.equals("null")) {
			startMonthYear = startMonth;
			endMonthYear = endMonth;
		} else if (startMonthYear.equals("null") && !endMonthYear.equals("null")) {
			startMonthYear = startMonth;
		} else if (!startMonthYear.equals("null") && endMonthYear.equals("null")) {
			endMonthYear = endMonth;
		}

		Page<Transaction> transaction = null;
		Pageable pageable = PageRequest.of(pageNum - 1, page, Sort.by("dateTime").descending());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate start = LocalDate.parse(startMonthYear + "-01", formatter);
		LocalDate end = LocalDate.parse(endMonthYear + "-01", formatter);
		LocalDateTime startdateTime = LocalDateTime.of(start.getYear(), start.getMonthValue(), 1, 0, 0, 0);
		System.out.println(startdateTime);
		LocalDateTime enddateTime = LocalDateTime.of(end.getYear(), end.getMonthValue(), end.lengthOfMonth(),  23, 59, 59);
		System.out.println(enddateTime);

		if (activity.equals("null") && status.equals("null") && keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedOrderByDateTimeDesc(
					id, startdateTime, enddateTime, false, pageable);

		} else if (!activity.equals("null") && status.equals("null") && keyword.equals("") && archive.equals("null")) {

			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityOrderByDateTimeDesc(id,
							startdateTime, enddateTime, false, activity, pageable);

		} else if (activity.equals("null") && !status.equals("null") && keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusOrderByDateTimeDesc(id, startdateTime,
							enddateTime, false, status, pageable);

		} else if (status.equals("null") && activity.equals("null") && !keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndDescriptionLikeOrderByDateTimeDesc(id,
							startdateTime, enddateTime, false, "%" + keyword + "%", pageable);

		} else if (status.equals("null") && activity.equals("null") && keyword.equals("") && !archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndArchiveOrderByDateTimeDesc(id, startdateTime,
							enddateTime, false, archive, pageable);

		} else if (!activity.equals("null") && !status.equals("null") && keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndActivityOrderByDateTimeDesc(id,
							startdateTime, enddateTime, false, status, activity, pageable);

		} else if (!activity.equals("null") && status.equals("null") && !keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityAndDescriptionLikeOrderByDateTimeDesc(
							id, startdateTime, enddateTime, false, activity, "%" + keyword + "%", pageable);

		} else if (!activity.equals("null") && status.equals("null") && keyword.equals("") && !archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityAndArchiveOrderByDateTimeDesc(id,
							startdateTime, enddateTime, false, activity, archive, pageable);

		} else if (activity.equals("null") && !status.equals("null") && !keyword.equals("") && archive.equals("null")) {
			System.out.println(status);
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndDescriptionLikeOrderByDateTimeDesc(
							id, startdateTime, enddateTime, false, status, "%" + keyword + "%", pageable);

		} else if (activity.equals("null") && !status.equals("null") && keyword.equals("") && !archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndArchiveOrderByDateTimeDesc(id,
							startdateTime, enddateTime, false, status, archive, pageable);

		} else if (activity.equals("null") && status.equals("null") && !keyword.equals("") && !archive.equals("null")) {

			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
							id, startdateTime, enddateTime, false, "%" + keyword + "%", archive, pageable);

		} else if (!status.equals("null") && !activity.equals("null") && !keyword.equals("")
				&& archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityAndStatusAndDescriptionLikeOrderByDateTimeDesc(
							id, startdateTime, enddateTime, false, activity, status, "%" + keyword + "%", pageable);

		} else if (status.equals("null") && !activity.equals("null") && !keyword.equals("")
				&& !archive.equals("null")) {

			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
							id, startdateTime, enddateTime, false, activity, "%" + keyword + "%", archive, pageable);

		} else if (!status.equals("null") && !activity.equals("null") && keyword.equals("")
				&& !archive.equals("null")) {

			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndActivityAndArchiveOrderByDateTimeDesc(
							id, startdateTime, enddateTime, false, status, activity, archive, pageable);

		} else if (!status.equals("null") && activity.equals("null") && !keyword.equals("")
				&& !archive.equals("null")) {

			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
							id, startdateTime, enddateTime, false, status, "%" + keyword + "%", archive, pageable);

		} else if (!status.equals("null") && !activity.equals("null") && !keyword.equals("")
				&& !archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndActivityAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
							id, startdateTime, enddateTime, false, status, activity, "%" + keyword + "%", archive,
							pageable);
		}

		List<Transaction> transactionlist = transaction.getContent();
		model.addAttribute("transactionlist", transactionlist);

		Wallet wallet = walletRepository.getById(id);
		model.addAttribute("wallet", wallet);
		model.addAttribute("walletId", id);

		model.addAttribute("startMonthYear", startMonthYear);
		model.addAttribute("endMonthYear", endMonthYear);
		model.addAttribute("activity", activity);
		model.addAttribute("archive", archive);
		model.addAttribute("status", status);

		if (!keyword.equals("")) {
			model.addAttribute("keyword", keyword);
		}

		model.addAttribute("totalPage", transaction.getTotalPages());
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("page", page);

		return "view_wallet_transaction_records";
	}

	@GetMapping("/wallet/{id}/deleted/transactionrecords")
	public String viewIndividualDeletedWalletTransactionRecords(@PathVariable("id") Integer id, Model model,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "startMonthYear", defaultValue = "null") String startMonthYear,
			@RequestParam(value = "endMonthYear", defaultValue = "null") String endMonthYear,
			@RequestParam(value = "activity", defaultValue = "null") String activity,
			@RequestParam(value = "archive", defaultValue = "null") String archive,
			@RequestParam(value = "status", defaultValue = "null") String status,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "page", defaultValue = "10") int page) {

		LocalDate currentDate = LocalDate.now();
//		LocalDate nowMinus9Months = currentDate.minusMonths(12);
		Transaction firstTransaction = transactionRepository.findTopByOrderByDateTimeAsc();
		LocalDate firstTransactionDateTime = firstTransaction.getDateTime().toLocalDate();
		DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyy-MM", Locale.ENGLISH);
		String startMonth = firstTransactionDateTime.format(dateformatter);
		String endMonth = currentDate.format(dateformatter);
		if (startMonthYear.equals("null") && endMonthYear.equals("null")) {
			startMonthYear = startMonth;
			endMonthYear = endMonth;
		} else if (startMonthYear.equals("null") && !endMonthYear.equals("null")) {
			startMonthYear = startMonth;
		} else if (!startMonthYear.equals("null") && endMonthYear.equals("null")) {
			endMonthYear = endMonth;
		}

		Page<Transaction> transaction = null;
		Pageable pageable = PageRequest.of(pageNum - 1, page, Sort.by("dateTime").descending());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate start = LocalDate.parse(startMonthYear + "-01", formatter);
		LocalDate end = LocalDate.parse(endMonthYear + "-01", formatter);
		LocalDateTime startdateTime = LocalDateTime.of(start.getYear(), start.getMonthValue(), 1, 0, 0, 0);
		System.out.println(startdateTime);
		LocalDateTime enddateTime = LocalDateTime.of(end.getYear(), end.getMonthValue(), end.lengthOfMonth(), 23, 59, 59);
		System.out.println(enddateTime);

		if (activity.equals("null") && status.equals("null") && keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedOrderByDateTimeDesc(
					id, startdateTime, enddateTime, true, pageable);

		} else if (!activity.equals("null") && status.equals("null") && keyword.equals("") && archive.equals("null")) {

			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityOrderByDateTimeDesc(id,
							startdateTime, enddateTime, true, activity, pageable);

		} else if (activity.equals("null") && !status.equals("null") && keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusOrderByDateTimeDesc(id, startdateTime,
							enddateTime, true, status, pageable);

		} else if (status.equals("null") && activity.equals("null") && !keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndDescriptionLikeOrderByDateTimeDesc(id,
							startdateTime, enddateTime, true, "%" + keyword + "%", pageable);

		} else if (status.equals("null") && activity.equals("null") && keyword.equals("") && !archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndArchiveOrderByDateTimeDesc(id, startdateTime,
							enddateTime, false, archive, pageable);

		} else if (!activity.equals("null") && !status.equals("null") && keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndActivityOrderByDateTimeDesc(id,
							startdateTime, enddateTime, true, status, activity, pageable);

		} else if (!activity.equals("null") && status.equals("null") && !keyword.equals("") && archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityAndDescriptionLikeOrderByDateTimeDesc(
							id, startdateTime, enddateTime, true, activity, "%" + keyword + "%", pageable);

		} else if (!activity.equals("null") && status.equals("null") && keyword.equals("") && !archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityAndArchiveOrderByDateTimeDesc(id,
							startdateTime, enddateTime, true, activity, archive, pageable);

		} else if (activity.equals("null") && !status.equals("null") && !keyword.equals("") && archive.equals("null")) {
			System.out.println(status);
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndDescriptionLikeOrderByDateTimeDesc(
							id, startdateTime, enddateTime, true, status, "%" + keyword + "%", pageable);

		} else if (activity.equals("null") && !status.equals("null") && keyword.equals("") && !archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndArchiveOrderByDateTimeDesc(id,
							startdateTime, enddateTime, true, status, archive, pageable);

		} else if (activity.equals("null") && status.equals("null") && !keyword.equals("") && !archive.equals("null")) {

			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
							id, startdateTime, enddateTime, true, "%" + keyword + "%", archive, pageable);

		} else if (!status.equals("null") && !activity.equals("null") && !keyword.equals("")
				&& archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityAndStatusAndDescriptionLikeOrderByDateTimeDesc(
							id, startdateTime, enddateTime, true, activity, status, "%" + keyword + "%", pageable);

		} else if (status.equals("null") && !activity.equals("null") && !keyword.equals("")
				&& !archive.equals("null")) {

			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
							id, startdateTime, enddateTime, true, activity, "%" + keyword + "%", archive, pageable);

		} else if (!status.equals("null") && !activity.equals("null") && keyword.equals("")
				&& !archive.equals("null")) {

			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndActivityAndArchiveOrderByDateTimeDesc(
							id, startdateTime, enddateTime, true, status, activity, archive, pageable);

		} else if (!status.equals("null") && activity.equals("null") && !keyword.equals("")
				&& !archive.equals("null")) {

			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
							id, startdateTime, enddateTime, true, status, "%" + keyword + "%", archive, pageable);

		} else if (!status.equals("null") && !activity.equals("null") && !keyword.equals("")
				&& !archive.equals("null")) {
			transaction = transactionRepository
					.findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndActivityAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
							id, startdateTime, enddateTime, true, status, activity, "%" + keyword + "%", archive,
							pageable);
		}

		List<Transaction> transactionlist = transaction.getContent();
		model.addAttribute("transactionlist", transactionlist);

		Wallet wallet = walletRepository.getById(id);
		model.addAttribute("wallet", wallet);
		model.addAttribute("walletId", id);

		model.addAttribute("startMonthYear", startMonthYear);
		model.addAttribute("endMonthYear", endMonthYear);
		model.addAttribute("activity", activity);
		model.addAttribute("archive", archive);
		model.addAttribute("status", status);

		if (!keyword.equals("")) {
			model.addAttribute("keyword", keyword);
		}

		model.addAttribute("totalPage", transaction.getTotalPages());
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("page", page);

		return "view_wallet_deleted_transaction";
	}

	@GetMapping("/transaction/export/excel")
	public String exportToExcel(HttpServletResponse response) throws IOException {
		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=transactions_" + currentDateTime + ".xls";
		response.setHeader(headerKey, headerValue);

		List<Transaction> listTransaction = transactionRepository.findAll();
		System.out.println(listTransaction.size());
		TransactionExcelExporter excelExporter = new TransactionExcelExporter(listTransaction);

		excelExporter.export(response);

		return "export_success";
	}

}
