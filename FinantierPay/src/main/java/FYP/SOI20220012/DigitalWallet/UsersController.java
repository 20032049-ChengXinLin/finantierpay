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
* Date created: 2022-May-25 3:40:45 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
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
public class UsersController {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private VoucherRepository voucherRepository;

	@Autowired
	private PointsEarnedRepository pointsEarnedRepository;

	@Autowired
	private PointsHistoryRepository pointsHistoryRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

	@GetMapping("/users")
	public String ViewUsers(Model model, @RequestParam(value = "membership", defaultValue = "null") String membership,
			@RequestParam(value = "isLocked", defaultValue = "1") boolean isLocked) {

		List<Account> listUsers = accountRepository.findByRole("ROLE_USER");
		model.addAttribute("listUsers", listUsers);

		return "view_users";
	}

	@GetMapping("/user/account")
	public String ViewUserAccountInfo(Model model,
			@RequestParam(value = "membership", defaultValue = "null") String membership,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) {
		System.out.println(keyword);
		System.out.println(membership);
		if (keyword.equals("") && membership.equals("null")) {
			List<Account> listUsers = accountRepository.findByRole("ROLE_USER");
			model.addAttribute("listUsers", listUsers);

		} else if (!keyword.equals("") && membership.equals("null")) {
			List<Account> listUsers = accountRepository.findByUserLikeAndRole(keyword);
			model.addAttribute("listUsers", listUsers);

		} else if (keyword.equals("") && !membership.equals("null")) {
			List<Account> listUsers = accountRepository.findByMembershipLevels(membership);
			model.addAttribute("listUsers", listUsers);

		}
		model.addAttribute("membership", membership);
		model.addAttribute("keyword", keyword);

		return "view_user_account";
	}

	@GetMapping("/user/voucher/{id}")
	public String ViewUserVoucher(@PathVariable("id") Integer id, Model model,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "page", defaultValue = "5") int page,
			@RequestParam(value = "startMonthYear", defaultValue = "null") String startMonthYear,
			@RequestParam(value = "endMonthYear", defaultValue = "null") String endMonthYear,
			@RequestParam(value = "archive", defaultValue = "null") String archive,
			@RequestParam(value = "status", defaultValue = "null") String status,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) {

		LocalDate currentDate = LocalDate.now();

		Voucher firstVoucher = voucherRepository.findTopByAndAccount_AccountIdOrderByVoucherIdAsc(id);

		if (firstVoucher != null) {
			LocalDate firstvoucherExpirydate = firstVoucher.getExpiryDate();
			DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyy-MM", Locale.ENGLISH);
			String startMonth = firstvoucherExpirydate.format(dateformatter);
			String endMonth = currentDate.format(dateformatter);
			if (startMonthYear.equals("null") && endMonthYear.equals("null")) {
				startMonthYear = startMonth;
				endMonthYear = endMonth;
			} else if (startMonthYear.equals("null") && !endMonthYear.equals("null")) {
				startMonthYear = startMonth;
			} else if (!startMonthYear.equals("null") && endMonthYear.equals("null")) {
				endMonthYear = endMonth;
			}
		}

		Pageable pageable = PageRequest.of(pageNum - 1, page, Sort.by("voucherId").descending());
		Page<Voucher> voucher = null;
		if (archive.equals("null") && status.equals("null") && keyword.equals("")) {
			voucher = voucherRepository.findByAccount_AccountId(id, pageable);

		} else if (!archive.equals("null") && status.equals("null") && keyword.equals("")) {

			voucher = voucherRepository.findByAccount_AccountIdAndArchive(id, archive, pageable);

		} else if (archive.equals("null") && !status.equals("null") && keyword.equalsIgnoreCase("")) {
			voucher = voucherRepository.findByAccount_AccountIdAndStatus(id, status, pageable);

		} else if (archive.equals("null") && status.equals("null") && !keyword.equalsIgnoreCase("")) {
			voucher = voucherRepository.findByAccount_AccountIdAndStoreNameLike(id, "%" + keyword + "%", pageable);

		} else if (!archive.equals("null") && !status.equals("null") && keyword.equalsIgnoreCase("")) {
			voucher = voucherRepository.findByAccount_AccountIdAndArchiveAndStatus(id, archive, status, pageable);

		} else if (!archive.equals("null") && status.equals("null") && !keyword.equalsIgnoreCase("")) {
			voucher = voucherRepository.findByAccount_AccountIdAndArchiveAndStoreNameLike(id, archive,
					"%" + keyword + "%", pageable);

		} else if (!archive.equals("null") && !status.equals("null") && !keyword.equalsIgnoreCase("")) {
			voucher = voucherRepository.findByAccount_AccountIdAndArchiveAndStatusAndStoreNameLike(id, archive, status,
					"%" + keyword + "%", pageable);
		}

		List<Voucher> voucherList = voucher.getContent();
		model.addAttribute("voucherList", voucherList);
		model.addAttribute("accountId", id);
		model.addAttribute("totalPage", voucher.getTotalPages());
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("page", page);

		model.addAttribute("startMonthYear", startMonthYear);
		model.addAttribute("endMonthYear", endMonthYear);
		model.addAttribute("archive", archive);
		model.addAttribute("status", status);
		model.addAttribute("keyword", keyword);

		return "view_user_voucher";
	}

	@GetMapping("/user/voucher/edit/{id}")
	public String editUserVoucher(@PathVariable("id") Integer id, Model model) {
		Voucher voucher = voucherRepository.getById(id);
		model.addAttribute("Voucher", voucher);

		return "edit_user_voucher";
	}

	@PostMapping("/user/voucher/edit/{id}")
	public String saveUpdatedUserVoucher(@PathVariable("id") Integer id, Voucher Voucher,
			RedirectAttributes redirectAttributes) {
		Voucher voucher = voucherRepository.getById(id);
		LocalDateTime currentDateTime = LocalDateTime.now();
		if (voucher.getExpiryDate().equals(Voucher.getExpiryDate())
				&& voucher.getStoreName().equals(Voucher.getStoreName())
				&& voucher.getVoucher_value() == Voucher.getVoucher_value()
				&& voucher.getStatus().equals(Voucher.getStatus())
				&& voucher.getArchive().equals(Voucher.getArchive())) {
			redirectAttributes.addFlashAttribute("warning", "No changes was made");
		} else {
	
			if (!voucher.getStatus().equals(Voucher.getStatus())) {
				if (Voucher.getStatus().equals("Expired")) {
					Notifications notifications = new Notifications();
					notifications.setAccount(voucher.getAccount());
					notifications.setDateTime(currentDateTime);

					notifications.setTitle("Sorry! Your voucher have expired :(");

					if (Voucher.getVoucher_value() < 1) {
						double value = Voucher.getVoucher_value() * 100;
						notifications.setMessage(
								"Sorry! Your " + value + "% voucher for " + Voucher.getStoreName().toUpperCase()
										+ " has expired as of " + currentDateTime.toLocalDate());

					} else if (Voucher.getVoucher_value() >= 1) {
						notifications.setMessage("Sorry! Your $" + Voucher.getVoucher_value() + " voucher for "
								+ Voucher.getStoreName().toUpperCase() + " has expired as of "
								+ currentDateTime.toLocalDate());
					}
					notificationsRepository.save(notifications);
				}
				
			}
			voucher.setExpiryDate(Voucher.getExpiryDate());
			voucher.setStoreName(Voucher.getStoreName());
			voucher.setVoucher_value(Voucher.getVoucher_value());
			voucher.setStatus(Voucher.getStatus());
			voucher.setArchive(Voucher.getArchive());
			voucher.setAccount(voucher.getAccount());
			voucherRepository.save(voucher);

			redirectAttributes.addFlashAttribute("success", "Voucher ID: " + id + " successfully updated.");
		}
		return "redirect:/user/voucher/" + voucher.getAccount().getAccountId();
	}

	@GetMapping("/user/pointhistory/{id}")
	public String ViewPointsHistory(@PathVariable("id") Integer id, Model model,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "page", defaultValue = "5") int page,
			@RequestParam(value = "startMonthYear", defaultValue = "null") String startMonthYear,
			@RequestParam(value = "endMonthYear", defaultValue = "null") String endMonthYear,
			@RequestParam(value = "archive", defaultValue = "null") String archive,
			@RequestParam(value = "status", defaultValue = "null") String status) {

		LocalDate currentDate = LocalDate.now();

		PointsHistory firstHistory = pointsHistoryRepository
				.findTopByAndPointsEarned_Account_AccountIdOrderByPointsEarnedAsc(id);
		if (firstHistory != null) {
			LocalDate firstpointsDateTime = firstHistory.getDateTime().toLocalDate();
			DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyy-MM", Locale.ENGLISH);
			String startMonth = firstpointsDateTime.format(dateformatter);
			String endMonth = currentDate.format(dateformatter);
			if (startMonthYear.equals("null") && endMonthYear.equals("null")) {
				startMonthYear = startMonth;
				endMonthYear = endMonth;
			} else if (startMonthYear.equals("null") && !endMonthYear.equals("null")) {
				startMonthYear = startMonth;
			} else if (!startMonthYear.equals("null") && endMonthYear.equals("null")) {
				endMonthYear = endMonth;
			}
		}
		Pageable pageable = PageRequest.of(pageNum - 1, page, Sort.by("pointsHistoryId").descending());
		Page<PointsHistory> pointsHistory = null;
		if (archive.equals("null") && status.equals("null")) {
			pointsHistory = pointsHistoryRepository.findByPointsEarned_Account_AccountIdOrderByDateTimeDesc(id,
					pageable);

		} else if (!archive.equals("null") && status.equals("null")) {

			pointsHistory = pointsHistoryRepository.findByPointsEarned_Account_AccountIdAndArchive(id, archive,
					pageable);

		} else if (archive.equals("null") && !status.equals("null")) {
			pointsHistory = pointsHistoryRepository.findByPointsEarned_Account_AccountIdAndStatus(id, status, pageable);

		} else if (!archive.equals("null") && !status.equals("null")) {
			pointsHistory = pointsHistoryRepository.findByPointsEarned_Account_AccountIdAndArchiveAndStatus(id, archive,
					status, pageable);
		}
		List<PointsHistory> pointsHistoryList = pointsHistory.getContent();
		model.addAttribute("pointsHistoryList", pointsHistoryList);
		model.addAttribute("accountId", id);
		model.addAttribute("totalPage", pointsHistory.getTotalPages());
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("page", page);

		model.addAttribute("startMonthYear", startMonthYear);
		model.addAttribute("endMonthYear", endMonthYear);
		model.addAttribute("archive", archive);
		model.addAttribute("status", status);

		return "view_user_pointsHistory";
	}

	@GetMapping("/user/pointsHistory/edit/{id}")
	public String editUserPointsHistory(@PathVariable("id") Integer id, Model model) {
		PointsHistory pointsHistory = pointsHistoryRepository.getById(id);
		model.addAttribute("PointsHistory", pointsHistory);

		return "edit_user_pointsHistory";
	}

	@PostMapping("/user/pointsHistory/edit/{id}")
	public String saveUpdatedPointsHistory(@PathVariable("id") Integer id, PointsHistory PointsHistory,
			RedirectAttributes redirectAttributes) {
		PointsHistory pointsHistory = pointsHistoryRepository.getById(id);
		LocalDateTime currentDateTime = LocalDateTime.now();
		if (pointsHistory.getTotalPoints() == PointsHistory.getTotalPoints()
				&& pointsHistory.getStatus().equals(PointsHistory.getStatus())
				&& pointsHistory.getArchive().equals(PointsHistory.getArchive())) {

			redirectAttributes.addFlashAttribute("warning", "No changes was made");
		} else {
			if (!pointsHistory.getStatus().equals(PointsHistory.getStatus())) {
				if (PointsHistory.getStatus().equals("Expired")) {
					Notifications notifications = new Notifications();
					notifications.setAccount(pointsHistory.getPointsEarned().getAccount());
					notifications.setDateTime(currentDateTime);
					notifications.setTitle("Your Points Have Expired :(");

					notifications.setMessage("Sorry! Your Points of " + pointsHistory.getTotalPoints()
							+ " has expired as of " + currentDateTime.toLocalDate() + ".");

					notificationsRepository.save(notifications);
				}
			}
			pointsHistory.setTotalPoints(PointsHistory.getTotalPoints());
			pointsHistory.setStatus(PointsHistory.getStatus());
			pointsHistory.setArchive(PointsHistory.getArchive());
			pointsHistory.setPointsEarned(pointsHistory.getPointsEarned());
			pointsHistoryRepository.save(pointsHistory);

			redirectAttributes.addFlashAttribute("success", "pointsHistory ID: " + id + " successfully updated.");
		}
		return "redirect:/user/pointhistory/" + pointsHistory.getPointsEarned().getAccount().getAccountId();
	}

	@GetMapping("/user/pointearned/{id}")
	public String ViewPointsEarned(@PathVariable("id") Integer id, Model model,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "page", defaultValue = "5") int page) {

		Pageable pageable = PageRequest.of(pageNum - 1, page, Sort.by("pointsEarnedId").descending());

		Page<PointsEarned> pointsEarned = pointsEarnedRepository.findByPointsEarned(id, pageable);
		List<PointsEarned> pointsEarnedList = pointsEarned.getContent();

		model.addAttribute("pointsEarnedList", pointsEarnedList);
		model.addAttribute("totalPage", pointsEarned.getTotalPages());
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("page", page);

		model.addAttribute("accountId", id);

		return "view_user_pointearned";
	}

	@GetMapping("/user/{id}/wallets")
	public String viewUserWallets(@PathVariable("id") Integer id, Model model) {
		List<Wallet> listUserWallets = walletRepository.findByAccount_AccountId(id);
		model.addAttribute("listUserWallets", listUserWallets);
		return "view_user_wallets";
	}

	@GetMapping("/user/edit/{id}")
	public String editUserMembership(@PathVariable("id") Integer id, Model model) {

		Account Account = accountRepository.getById(id);
		model.addAttribute("Account", Account);
		List<Country> countryList = countryRepository.findAll();
		model.addAttribute("countryList", countryList);
		return "edit_user_membership";
	}

	@PostMapping("/user/edit/{id}")
	public String saveUserMembership(@PathVariable("id") Integer id, Account Account, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {

		Account account = accountRepository.getById(id);

		String phone = "";
		String postal = "";
		if (Account.getCountry().getCountryId() == 1) {
			String regexPhone = "^$|^[689]\\d{7}$";
			boolean patternPhone = Account.getPhone().matches(regexPhone);
			if (patternPhone == false) {
				phone = "Mobile Number Wrong Format!";
			}
			String regexPostal = "^$|^\\d{6}$";
			boolean patternPostal = Account.getPostal_code().matches(regexPostal);
			if (patternPostal == false) {
				postal = "Postal Code Wrong Format!";
			}
		} else if (Account.getCountry().getCountryId() == 2) {
			String regexPhone = "^(\\+?6?01)[02-46-9][-][0-9]{7}$|^(\\+?6?01)[1][-][0-9]{8}$";
			boolean patternPhone = Account.getPhone().matches(regexPhone);
			if (patternPhone == false) {
				phone = "Mobile Number Wrong Format!";
			}
			String regexPostal = "^$|^\\d{5}$";
			boolean patternPostal = Account.getPostal_code().matches(regexPostal);
			if (patternPostal == false) {
				postal = "Postal Code Wrong Format!";
			}
		} else if (Account.getCountry().getCountryId() == 3) {
			String regexPhone = "^(08)(\\d{3,4}-?){2}\\d{2,3}$";
			boolean patternPhone = Account.getPhone().matches(regexPhone);
			if (patternPhone == false) {
				phone = "Mobile Number Wrong Format!";
			}
			String regexPostal = "^$|^\\d{5}$";
			boolean patternPostal = Account.getPostal_code().matches(regexPostal);
			if (patternPostal == false) {
				postal = "Postal Code Wrong Format!";
			}
		}

		if (phone != "" || postal != "") {
			List<Country> countryList = countryRepository.findAll();
			model.addAttribute("countryList", countryList);
			model.addAttribute("postal", postal);
			model.addAttribute("phone", phone);
			model.addAttribute("Account", Account);
			return "edit_user_membership";
		} else {
			if (account.getFirst_name().equals(Account.getFirst_name())
					&& account.getLast_name().equals(Account.getLast_name())
					&& account.getCountry().equals(Account.getCountry())
					&& account.getDate_of_birth().equals(Account.getDate_of_birth())
					&& account.getAddress().equals(Account.getAddress())
					&& account.getPostal_code().equals(Account.getPostal_code())
					&& account.getPhone().equals(Account.getPhone())
					&& account.getMembership_levels().equals(Account.getMembership_levels())
					&& account.getEmail().equals(Account.getEmail())
					&& account.getTotal_points() == Account.getTotal_points()
					&& account.getBalance_points() == Account.getBalance_points()) {
				redirectAttributes.addFlashAttribute("warning", "No changes was made");
			} else {
				account.setFirst_name(Account.getFirst_name());
				account.setLast_name(Account.getLast_name());
				account.setCountry(Account.getCountry());
				account.setDate_of_birth(Account.getDate_of_birth());
				account.setAddress(Account.getAddress());
				account.setPostal_code(Account.getPostal_code());
				account.setPhone(Account.getPhone());
				account.setMembership_levels(Account.getMembership_levels());
				account.setEmail(Account.getEmail());
				account.setBalance_points(Account.getBalance_points());
				account.setTotal_points(Account.getTotal_points());
				if (Account.getMembership_levels().equalsIgnoreCase("ROOKIE")) {
					account.setCashback_voucher(0.03);
				} else if (Account.getMembership_levels().equalsIgnoreCase("BRONZE")) {
					account.setCashback_voucher(0.05);
				} else if (Account.getMembership_levels().equalsIgnoreCase("SILVER")) {
					account.setCashback_voucher(0.10);
				} else if (Account.getMembership_levels().equalsIgnoreCase("GOLD")) {
					account.setCashback_voucher(0.15);
				}
				account.setTotal_points(Account.getTotal_points());
				account.setBalance_points(Account.getBalance_points());
				accountRepository.save(account);

				LocalDateTime currentDateTime = LocalDateTime.now();
				Notifications notifications = new Notifications();
				notifications.setAccount(account);
				notifications.setDateTime(currentDateTime);
				notifications.setTitle("Your account information has been updated.");

				notifications.setMessage(
						"Your account information has been updated. You can now view the updated information on account pages. Thank You!");

				notificationsRepository.save(notifications);
				redirectAttributes.addFlashAttribute("editSuccess", "Account ID: " + id + " successfully updated.");
			}
		}
		

		return "redirect:/user/account";
	}

	@GetMapping("/user/wallet/{id}")
	public String viewWalletAmount(@PathVariable("id") Integer id, Model model) {
		Wallet wallet = walletRepository.getById(id);
		model.addAttribute("wallet", wallet);

		return "user_wallet_amount";

	}

	@GetMapping("/user/wallet/edit/{id}")
	public String updateWalletAmount(@PathVariable("id") Integer id, Model model) {
		Wallet wallet = walletRepository.getById(id);
		model.addAttribute("wallet", wallet);

		return "edit_user_wallet_amount";

	}

	@PostMapping("/user/wallet/edit/{id}")
	public String saveWalletAmount(@PathVariable("id") Integer id, @RequestParam("totalAmt") double updatedWalletAmount,
			RedirectAttributes RedirectAttributes) {

		Wallet wallet = walletRepository.getById(id);
		LocalDateTime currentDateTime = LocalDateTime.now();
		double oldWalletAmount = wallet.getTotalAmount();
		wallet.setTotalAmount(updatedWalletAmount);

		if (updatedWalletAmount != oldWalletAmount) {
			Notifications notifications = new Notifications();
			notifications.setAccount(wallet.getAccount());
			notifications.setDateTime(currentDateTime);
			notifications.setTitle("Successfully Updated Wallet Amount!");
			notifications.setMessage("Your wallet amount for wallet ID: " + id + " has been changed.");
			notificationsRepository.save(notifications);

			RedirectAttributes.addFlashAttribute("success", "Wallet ID: " + id
					+ " successfully updated wallet amount from $" + oldWalletAmount + " to $" + updatedWalletAmount);
		}
		if (updatedWalletAmount == oldWalletAmount) {
			RedirectAttributes.addFlashAttribute("warning", "No update was made.");
		}

		return "redirect:/user/wallet/{id}";
	}
}