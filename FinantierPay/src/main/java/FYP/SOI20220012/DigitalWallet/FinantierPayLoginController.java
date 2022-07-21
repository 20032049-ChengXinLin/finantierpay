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
* Date created: 2022-May-04 4:29:25 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 20032049
 *
 */
@Controller
public class FinantierPayLoginController {
	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PointsEarnedRepository pointsEarnedRepository;

	@Autowired
	private PointsHistoryRepository pointsHistoryRepository;

	@Autowired
	private VoucherRepository voucherRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private JavaMailSender javaMailSender;

	@GetMapping("/login")
	public String login() {
		// CurrentDate
		LocalDate currentDate = LocalDate.now();

		// Set Account as deleted after 30 days
		List<Account> accountDates = accountRepository.findAll();
		for (int p = 0; p < accountDates.size(); p++) {
			if (accountDates.get(p).getRole().equals("ROLE_USER")
					|| accountDates.get(p).getRole().equals("ROLE_MERCHANT")) {
				LocalDate dateDeleted = accountDates.get(p).getDate_deleted();
				System.out.println(dateDeleted);
				if (dateDeleted != null) {
					LocalDate nowPlus30Days = dateDeleted.plusDays(30);
					if (currentDate.compareTo(nowPlus30Days) > 0) {
						if (accountDates.get(p).isLocked() == true) {
							accountDates.get(p).setLocked(false);
							accountRepository.save(accountDates.get(p));
							String subject = "Successfully deleted your FinantierPay Account!";
							String body = "Dear " + accountDates.get(p).getUsername()
									+ ",\n\nYou have succesfully deleted your account.\nYou will not be able to login again.\nThank you.\n\nBest Regards, \nFinantierPay";
							String to = accountDates.get(p).getEmail();
							sendEmail(to, subject, body);
						}

					}
				}
			}
		}

		// Transaction Auto Archive Change to "No" After 1 Year from Current Date
		List<Transaction> transactionlist = transactionRepository.findAllByOrderByDateTimeDesc();

		LocalDate nowMinus12Months = currentDate.minusMonths(12);

		for (int i = 0; i < transactionlist.size(); i++) {
			LocalDate datetime = transactionlist.get(i).getDateTime().toLocalDate();
			if (nowMinus12Months.compareTo(datetime) > 0) {
				transactionlist.get(i).setArchive("Yes");
				transactionRepository.save(transactionlist.get(0));
			}
		}

		// Interest Earned
		Calendar calendar = Calendar.getInstance();
		int res = calendar.getActualMaximum(Calendar.DATE);
		LocalDateTime currentDateTime = LocalDateTime.now();

		List<Wallet> listWallets = walletRepository.findByAccount_RoleAndIsDeleted("ROLE_USER", false);

		if (currentDateTime.getDayOfMonth() == res) {

			for (int x = 0; x < listWallets.size(); x++) {
				System.out.println(currentDateTime.getDayOfMonth());
				Wallet wallet = walletRepository.getById(listWallets.get(x).getWalletId());
				System.out.println("Wallet ID:" + wallet.getWalletId());
				List<Transaction> transaction = transactionRepository
						.findByWallet_WalletIdAndIsDeletedOrderByDateTimeDesc(wallet.getWalletId(), false);
				int count = 0;
				System.out.println("Size " + transaction.size());
				for (int i = 0; i < transaction.size(); i++) {
					LocalDate transactionDateTime = transaction.get(i).getDateTime().toLocalDate();
					if (transactionDateTime.isEqual(currentDate)) {
						if (transaction.get(i).getDescription().equalsIgnoreCase("Interest Earned")) {
							count++;
						}
					}
				}
				if (count == 0) {
					System.out.println("count" + count);
					double currentAmt = wallet.getTotalAmount();
					double interestEarned = currentAmt * 0.02;
					double interestEarnedformatted = (double) Math.round(interestEarned * 100d) / 100d;
					double newAmt = currentAmt + interestEarned;
					double newAmtformatted = (double) Math.round(newAmt * 100d) / 100d;

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
					newFromTransaction.setActivity("Received");
					newFromTransaction.setAmount(interestEarnedformatted);
					newFromTransaction.setDateTime(dateTime);
					newFromTransaction.setStatus("Success");
					newFromTransaction.setArchive("No");
					newFromTransaction.setWallet(wallet);
					newFromTransaction.setDescription("Interest Earned");
					transactionRepository.save(newFromTransaction);

					wallet.setTotalAmount(newAmtformatted);
					walletRepository.save(wallet);

					Notifications notifications = new Notifications();
					notifications.setAccount(wallet.getAccount());
					notifications.setDateTime(currentDateTime);
					notifications.setTitle("Interest Earned!");

					notifications.setMessage("You have earned an interest of $" + interestEarnedformatted
							+ " for Wallet ID " + wallet.getWalletId() + " adding it up to a new wallet amount of $"
							+ newAmtformatted);

					notificationsRepository.save(notifications);
				}

			}

		}

		// Current DateTime
		LocalDateTime dateTime = LocalDateTime.now();

		// PointsEarned Set to 0 after points Expired
		List<PointsEarned> pointsEarnedList = pointsEarnedRepository.findAll();

		for (int i = 0; i < pointsEarnedList.size(); i++) {
			LocalDate expirydate = pointsEarnedList.get(i).getPointsExpiryDate();
			LocalDate alertMinus3days = expirydate.minusDays(3);
			LocalDate alertMinus2days = expirydate.minusDays(2);
			LocalDate alertMinus1days = expirydate.minusDays(1);
			int accountId = pointsEarnedList.get(i).getAccount().getAccountId();
			Account account = accountRepository.getById(accountId);
			List<Notifications> notificationsList = notificationsRepository.findByAccount_AccountId(accountId);
			int count = 0;
			if (pointsEarnedList.get(i).getPointsEarned() != 0) {
				for (int a = 0; a < notificationsList.size(); a++) {
					if (notificationsList.get(a).getDateTime().toLocalDate().equals(currentDate)) {
						if (notificationsList.get(a).getTitle().equals("Your Points is Expiry Soon... :(")) {
							count++;
						}
					}
				}
				double totalpoints = 0;
				List<PointsEarned> pointsExpirydate = pointsEarnedRepository.findByPointsExpiryDate(expirydate);
				for (int x = 0; x < pointsExpirydate.size(); x++) {
					totalpoints += pointsExpirydate.get(x).getPointsEarned();
				}
				double remainingpoints = account.getBalance_points() - totalpoints;
				if (count == 0) {
					if (currentDate.compareTo(alertMinus3days) == 0) {
						Notifications notifications = new Notifications();
						notifications.setAccount(account);
						notifications.setDateTime(currentDateTime);
						notifications.setTitle("Your Points is Expiry Soon... :(");
						notifications.setMessage("Your " + totalpoints
								+ " Points is Expiring in 3 days times. Please redeem your points before the "
								+ expirydate + ". Thank you!");
						notificationsRepository.save(notifications);
					} else if (currentDate.compareTo(alertMinus2days) == 0) {
						Notifications notifications = new Notifications();
						notifications.setAccount(account);
						notifications.setDateTime(currentDateTime);
						notifications.setTitle("Your Points is Expiry Soon... :(");
						notifications.setMessage("Your " + totalpoints
								+ " Points is Expiring in 2 days times. Please redeem your points before the "
								+ expirydate + ". Thank you!");
						notificationsRepository.save(notifications);
					} else if (currentDate.compareTo(alertMinus1days) == 0) {
						Notifications notifications = new Notifications();
						notifications.setAccount(account);
						notifications.setDateTime(currentDateTime);
						notifications.setTitle("Your Points is Expiry Soon... :(");
						notifications.setMessage("Your " + totalpoints
								+ " Points is Expiring in 1 day times. Please redeem your points before the "
								+ expirydate + ". Thank you!");
						notificationsRepository.save(notifications);
					} else if (currentDate.compareTo(expirydate) == 0) {
						Notifications notifications = new Notifications();
						notifications.setAccount(account);
						notifications.setDateTime(currentDateTime);
						notifications.setTitle("Your Points is Expiry Soon... :(");
						notifications.setMessage("Your " + totalpoints
								+ " Points is Expiring in within 1 day times. Please redeem your points before today. Thank you!");
						notificationsRepository.save(notifications);
					}
				}

				if (currentDate.compareTo(expirydate) > 0) {
					PointsHistory pointsHistory = new PointsHistory();
					pointsHistory.setPointsEarned(pointsEarnedList.get(i));
					pointsHistory.setDateTime(dateTime);
					pointsHistory.setStatus("Expired");
					pointsHistory.setTotalPoints(pointsEarnedList.get(i).getPointsEarned());
					pointsHistory.setArchive("No");
					pointsHistoryRepository.save(pointsHistory);

					double pointsDeducted = account.getBalance_points() - pointsEarnedList.get(i).getPointsEarned();
					account.setBalance_points(pointsDeducted);

					double totalpointsDeducted = account.getTotal_points() - pointsEarnedList.get(i).getPointsEarned();
					account.setTotal_points(totalpointsDeducted);
					accountRepository.save(account);

					pointsEarnedList.get(i).setPointsEarned(0);
					pointsEarnedRepository.save(pointsEarnedList.get(i));
					int countExpired = 0;
					for (int a = 0; a < notificationsList.size(); a++) {
						if (notificationsList.get(a).getDateTime().toLocalDate().equals(currentDate)) {
							if (notificationsList.get(a).getTitle().equals("Your Points Have Expired :(")) {
								countExpired++;
							}
						}
					}
					if (countExpired == 0) {
						Notifications notifications = new Notifications();
						notifications.setAccount(account);
						notifications.setDateTime(currentDateTime);
						notifications.setTitle("Your Points Have Expired :(");
						notifications.setMessage("Sorry! Your Points of " + totalpoints + " has expired as of "
								+ expirydate + ". You left with " + remainingpoints + " POINTS.");
						notificationsRepository.save(notifications);
					}
				}

			}
		}

		// Membership Levels Status - Drop 2 levels Down when the last Transaction
		// DateTime for Activity 'Sent' is more than 12 months.
		// Drop 1 level down when the last Transaction DateTime for Activity 'Sent' is
		// more than 9 months.
		List<Account> accountList = accountRepository.findByRole("ROLE_USER");

		for (int i = 0; i < accountList.size(); i++) {

			Transaction transaction = transactionRepository
					.findTopByWallet_Account_AccountIdAndActivityAndStatusOrderByDateTimeDesc(
							accountList.get(i).getAccountId(), "Sent", "Success");

			List<Notifications> notificationsList = notificationsRepository
					.findByAccount_AccountId(accountList.get(i).getAccountId());
			int count = 0;
			for (int a = 0; a < notificationsList.size(); a++) {
				if (notificationsList.get(a).getDateTime().toLocalDate().equals(currentDate)) {
					if (notificationsList.get(a).getTitle()
							.equals("Oh No! You have not sent money for more than 12 months.  :(")
							|| notificationsList.get(a).getTitle()
									.equals("Oh No! You have not sent money for more than 9 months.  :(")) {
						count++;
					}
				}
			}
			if (transaction != null) {
				LocalDate lastTransactionDateTime = transaction.getDateTime().toLocalDate();
				LocalDate nowPlus12Months = lastTransactionDateTime.plusMonths(12);
				LocalDate nowPlus9Months = lastTransactionDateTime.plusMonths(9);

				if (currentDate.compareTo(nowPlus9Months) > 0) {
					if (currentDate.compareTo(nowPlus12Months) > 0) {
						System.out.println("12");
						if (accountList.get(i).getBalance_points() != 0 || accountList.get(i).getTotal_points() != 0) {
							if (accountList.get(i).getMembership_levels().equalsIgnoreCase("GOLD")) {

								accountList.get(i).setBalance_points(0);
								accountList.get(i).setTotal_points(0);
								accountList.get(i).setMembership_levels("BRONZE");
								accountList.get(i).setCashback_voucher(0.05);
								accountRepository.save(accountList.get(i));

								Notifications notifications = new Notifications();
								notifications.setAccount(accountList.get(i));
								notifications.setDateTime(currentDateTime);
								notifications.setTitle("Sorry! Your membership levels have drop :(");
								notifications.setMessage("Your membership levels have drop to BRONZE as of "
										+ currentDate + ". Your cashback voucher now is 5%.");
								notificationsRepository.save(notifications);

							} else if (accountList.get(i).getMembership_levels().equalsIgnoreCase("SILVER")) {

								accountList.get(i).setBalance_points(0);
								accountList.get(i).setTotal_points(0);
								accountList.get(i).setMembership_levels("ROOKIE");
								accountList.get(i).setCashback_voucher(0.03);
								accountRepository.save(accountList.get(i));

								Notifications notifications = new Notifications();
								notifications.setAccount(accountList.get(i));
								notifications.setDateTime(currentDateTime);
								notifications.setTitle("Sorry! Your membership levels have drop :(");
								notifications.setMessage("Your membership levels have drop to ROOKIE as of "
										+ currentDate + ". Your cashback voucher now is 3%.");
								notificationsRepository.save(notifications);

							} else if (accountList.get(i).getMembership_levels().equalsIgnoreCase("BRONZE")) {

								accountList.get(i).setBalance_points(0);
								accountList.get(i).setTotal_points(0);
								accountList.get(i).setMembership_levels("ROOKIE");
								accountList.get(i).setCashback_voucher(0.03);
								accountRepository.save(accountList.get(i));

								Notifications notifications = new Notifications();
								notifications.setAccount(accountList.get(i));
								notifications.setDateTime(currentDateTime);
								notifications.setTitle("Sorry! Your membership levels have drop :(");
								notifications.setMessage("Your membership levels have drop to ROOKIE as of "
										+ currentDate + ". Your cashback voucher now is 3%.");
								notificationsRepository.save(notifications);
							}

						}
					} else {
						System.out.println("9");
						if (accountList.get(i).getBalance_points() != 0 || accountList.get(i).getTotal_points() != 0) {
							if (accountList.get(i).getMembership_levels().equalsIgnoreCase("GOLD")) {

								accountList.get(i).setBalance_points(0);
								accountList.get(i).setTotal_points(0);
								accountList.get(i).setMembership_levels("SILVER");
								accountList.get(i).setCashback_voucher(0.10);
								accountRepository.save(accountList.get(i));

								Notifications notifications = new Notifications();
								notifications.setAccount(accountList.get(i));
								notifications.setDateTime(currentDateTime);
								notifications.setTitle("Sorry! Your membership levels have drop :(");
								notifications.setMessage("Your membership levels have drop to SILVER as of "
										+ currentDate + ". Your cashback voucher now is 10%.");
								notificationsRepository.save(notifications);

							} else if (accountList.get(i).getMembership_levels().equalsIgnoreCase("SILVER")) {
								System.out.println("Silver");
								accountList.get(i).setBalance_points(0);
								accountList.get(i).setTotal_points(0);
								accountList.get(i).setMembership_levels("BRONZE");
								accountList.get(i).setCashback_voucher(0.05);
								accountRepository.save(accountList.get(i));

								Notifications notifications = new Notifications();
								notifications.setAccount(accountList.get(i));
								notifications.setDateTime(currentDateTime);
								notifications.setTitle("Sorry! Your membership levels have drop :(");
								notifications.setMessage("Your membership levels have drop to BRONZE as of "
										+ currentDate + ". Your cashback voucher now is 5%.");
								notificationsRepository.save(notifications);

							} else if (accountList.get(i).getMembership_levels().equalsIgnoreCase("BRONZE")) {
								System.out.println("Bronze");
								accountList.get(i).setBalance_points(0);
								accountList.get(i).setTotal_points(0);
								accountList.get(i).setMembership_levels("ROOKIE");
								accountList.get(i).setCashback_voucher(0.03);
								accountRepository.save(accountList.get(i));

								Notifications notifications = new Notifications();
								notifications.setAccount(accountList.get(i));
								notifications.setDateTime(currentDateTime);
								notifications.setTitle("Sorry! Your membership levels have drop :(");
								notifications.setMessage("Your membership levels have drop to ROOKIE as of "
										+ currentDate + ". Your cashback voucher now is 3%.");
								notificationsRepository.save(notifications);
							}

						}

					}
				}
			}

		}

		// Set Voucher to disabled after current date is more than expiry date
		List<Voucher> voucherList = voucherRepository.findAll();
		for (int i = 0; i < voucherList.size(); i++) {
			LocalDate expirydate = voucherList.get(i).getExpiryDate();
			LocalDate alertMinus3days = expirydate.minusDays(3);
			LocalDate alertMinus2days = expirydate.minusDays(2);
			LocalDate alertMinus1days = expirydate.minusDays(1);
			List<Notifications> notificationsList = notificationsRepository
					.findByAccount_AccountId(voucherList.get(i).getAccount().getAccountId());
			int count = 0;
			for (int a = 0; a < notificationsList.size(); a++) {
				if (notificationsList.get(a).getDateTime().toLocalDate().equals(currentDate)) {
					if (notificationsList.get(a).getTitle().equals("Your Voucher is Expiry Soon... :(")) {
						count++;
					}
				}
			}
			double num = 0;
			List<Voucher> voucherExpirydate = voucherRepository.findByExpiryDate(expirydate);
			for (int x = 0; x < voucherExpirydate.size(); x++) {
				num += 1;
			}
			System.out.println(count);
			System.out.println(num);
			if (count < num) {
				if (currentDate.compareTo(alertMinus3days) == 0) {
					Notifications notifications = new Notifications();
					notifications.setAccount(voucherList.get(i).getAccount());
					notifications.setDateTime(currentDateTime);
					notifications.setTitle("Your Voucher is Expiry Soon... :(");
					if (voucherList.get(i).getVoucher_value() < 1) {
						double value = voucherList.get(i).getVoucher_value() * 100;
						notifications.setMessage(
								"Your " + value + "% voucher for " + voucherList.get(i).getStoreName().toUpperCase()
										+ " is Expiring in 3 days times. Please use your voucher before the "
										+ expirydate + ". Thank you!");

					} else if (voucherList.get(i).getVoucher_value() >= 1) {
						notifications.setMessage("Your $" + voucherList.get(i).getVoucher_value() + " voucher for "
								+ voucherList.get(i).getStoreName().toUpperCase()
								+ " is Expiring in 3 days times. Please use your voucher before the " + expirydate
								+ ". Thank you!");
					}

					notificationsRepository.save(notifications);

				} else if (currentDate.compareTo(alertMinus2days) == 0) {
					Notifications notifications = new Notifications();
					notifications.setAccount(voucherList.get(i).getAccount());
					notifications.setDateTime(currentDateTime);
					notifications.setTitle("Your Voucher is Expiry Soon... :(");

					if (voucherList.get(i).getVoucher_value() < 1) {
						double value = voucherList.get(i).getVoucher_value() * 100;
						notifications.setMessage(
								"Your " + value + "% voucher for " + voucherList.get(i).getStoreName().toUpperCase()
										+ " is Expiring in 2 days times. Please use your voucher before the "
										+ expirydate + ". Thank you!");

					} else if (voucherList.get(i).getVoucher_value() >= 1) {
						notifications.setMessage("Your $" + voucherList.get(i).getVoucher_value() + " voucher for "
								+ voucherList.get(i).getStoreName().toUpperCase()
								+ " is Expiring in 2 days times. Please use your voucher before the " + expirydate
								+ ". Thank you!");
					}

					notificationsRepository.save(notifications);

				} else if (currentDate.compareTo(alertMinus1days) == 0) {
					Notifications notifications = new Notifications();
					notifications.setAccount(voucherList.get(i).getAccount());
					notifications.setDateTime(currentDateTime);
					notifications.setTitle("Your Voucher is Expiry Soon... :(");

					if (voucherList.get(i).getVoucher_value() < 1) {
						double value = voucherList.get(i).getVoucher_value() * 100;
						notifications.setMessage(
								"Your " + value + "% voucher for " + voucherList.get(i).getStoreName().toUpperCase()
										+ " is Expiring in 1 days times. Please use your voucher before the "
										+ expirydate + ". Thank you!");

					} else if (voucherList.get(i).getVoucher_value() >= 1) {
						notifications.setMessage("Your $" + voucherList.get(i).getVoucher_value() + " voucher for "
								+ voucherList.get(i).getStoreName().toUpperCase()
								+ " is Expiring in 1 days times. Please use your voucher before the " + expirydate
								+ ". Thank you!");
					}

					notificationsRepository.save(notifications);

				} else if (currentDate.compareTo(expirydate) == 0) {
					Notifications notifications = new Notifications();
					notifications.setAccount(voucherList.get(i).getAccount());
					notifications.setDateTime(currentDateTime);
					notifications.setTitle("Your Voucher is Expiry Soon... :(");

					if (voucherList.get(i).getVoucher_value() < 1) {
						double value = voucherList.get(i).getVoucher_value() * 100;
						notifications.setMessage(
								"Your " + value + "% voucher for " + voucherList.get(i).getStoreName().toUpperCase()
										+ " is Expiring within 1 day times. Please use your voucher before today, "
										+ expirydate + ". Thank you!");

					} else if (voucherList.get(i).getVoucher_value() >= 1) {
						notifications.setMessage("Your $" + voucherList.get(i).getVoucher_value() + " voucher for "
								+ voucherList.get(i).getStoreName().toUpperCase()
								+ " is Expiring within 1 day times. Please use your voucher before today, " + expirydate
								+ ". Thank you!");
					}
					notificationsRepository.save(notifications);
				}
			}

			if (currentDate.compareTo(expirydate) > 0) {
				voucherList.get(i).setStatus("Expired");
				voucherRepository.save(voucherList.get(i));

				int countExpired = 0;
				for (int a = 0; a < notificationsList.size(); a++) {
					if (notificationsList.get(a).getDateTime().toLocalDate().equals(currentDate)) {
						if (notificationsList.get(a).getTitle().equals("Sorry! Your voucher have expired :(")) {
							countExpired++;
						}
					}
				}
				double countnum = 0;
				List<Voucher> voucherAfterExpirydate = voucherRepository.findByExpiryDate(expirydate);
				for (int x = 0; x < voucherAfterExpirydate.size(); x++) {
					countnum += 1;
				}
				if (countExpired < countnum) {
					Notifications notifications = new Notifications();
					notifications.setAccount(voucherList.get(i).getAccount());
					notifications.setDateTime(currentDateTime);
					notifications.setTitle("Sorry! Your voucher have expired :(");

					if (voucherList.get(i).getVoucher_value() < 1) {
						double value = voucherList.get(i).getVoucher_value() * 100;
						notifications.setMessage("Sorry! Your " + value + "% voucher for "
								+ voucherList.get(i).getStoreName().toUpperCase() + " has expired as of " + expirydate);

					} else if (voucherList.get(i).getVoucher_value() >= 1) {
						notifications.setMessage("Sorry! Your $" + voucherList.get(i).getVoucher_value()
								+ " voucher for " + voucherList.get(i).getStoreName().toUpperCase()
								+ " has expired as of " + expirydate);
					}

					notificationsRepository.save(notifications);
				}
			}
		}

		// Archive Yes When the Points History Expiry date is more than 1 year.
		List<PointsHistory> pointsHistoryList = pointsHistoryRepository.findAll();
		for (int i = 0; i < pointsHistoryList.size(); i++) {
			LocalDate pointsdateTime = pointsHistoryList.get(i).getDateTime().toLocalDate();
			LocalDate nowPlus12Months = pointsdateTime.plusMonths(12);
			if (currentDate.compareTo(nowPlus12Months) > 0) {
				pointsHistoryList.get(i).setArchive("Yes");
				pointsHistoryRepository.save(pointsHistoryList.get(i));
			}
		}

		// Archive Yes When the Voucher Expiry date is more than 1 year.
		for (int i = 0; i < voucherList.size(); i++) {
			LocalDate expirydate = voucherList.get(i).getExpiryDate();
			LocalDate nowPlus12Months = expirydate.plusMonths(12);
			if (currentDate.compareTo(nowPlus12Months) > 0) {
				voucherList.get(i).setArchive("Yes");
				voucherRepository.save(voucherList.get(i));
			}
		}

		return "login";
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

	@GetMapping("/403")
	public String error403() {

		return "403";
	}
}
