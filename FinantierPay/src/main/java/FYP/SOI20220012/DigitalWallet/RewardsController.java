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
* Date created: 2022-May-14 3:32:08 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author 20032049
 *
 */
@Controller
public class RewardsController {

	@Autowired
	private RewardsRepository rewardsRepository;

	@Autowired
	private VoucherRepository voucherRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PointsEarnedRepository pointsEarnedRepository;
	
	@Autowired
	private PointsHistoryRepository pointsHistoryRepository;
	
	@Autowired
	private NotificationsService notificationsService;
	
	@Autowired
	private NotificationsRepository notificationsRepository;
	
	@GetMapping("/rewards")
	public String rewards(Model model) {

		// Get currently logged in user
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);

		List<Rewards> rewardslist = rewardsRepository.findAll();
		List<String> distinctStoreName = rewardsRepository.findDistinctStoreName();

		// Current Date
		LocalDate currentDate = LocalDate.now();
		LocalDate expirydate = currentDate.plusMonths(6);

		model.addAttribute("account", account);
		model.addAttribute("storeNameList", distinctStoreName);
		model.addAttribute("rewardslist", rewardslist);
		model.addAttribute("expirydate", expirydate);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "rewards";
	}

	@GetMapping("/rewards/redeem/{id}")
	public String redeemRewards(Model model, @PathVariable("id") int id) {

		Rewards reward = rewardsRepository.getById(id);
		model.addAttribute("reward", reward);

		// Current Date
		LocalDate currentDate = LocalDate.now();
		LocalDate expirydate = currentDate.plusMonths(6);
		model.addAttribute("expirydate", expirydate);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "rewards_redemption";
	}

	@PostMapping("/voucher/redeem/{id}")
	public String redeemSuccess(@PathVariable("id") int id, Model model) {

		// Get currently logged in user
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);

		Rewards reward = rewardsRepository.getById(id);

		// Current Date
		LocalDate currentDate = LocalDate.now();
		LocalDate expirydate = currentDate.plusMonths(6);

		// Deduct Membership Points
		double totalpoints = account.getBalance_points();
		List<PointsEarned> pointsEarned = pointsEarnedRepository.findByPointsEarned(loggedInAccountId);

		double points = reward.getPoints();
		
	
		
		// Current DateTime
		LocalDateTime dateTime = LocalDateTime.now();
		
		for (int i = 0; i < pointsEarned.size(); i++) {
			if (pointsEarned.get(i).getPointsEarned() != 0 && points != 0) {
				
				if (pointsEarned.get(i).getPointsEarned() >= points) {
					double deductedPoints = pointsEarned.get(i).getPointsEarned() - points;
					pointsEarned.get(i).setPointsEarned(deductedPoints);
					pointsEarnedRepository.save(pointsEarned.get(i));
					
					PointsHistory pointsHistory = new PointsHistory();
					pointsHistory.setDateTime(dateTime);
					pointsHistory.setPointsEarned(pointsEarned.get(i));
					pointsHistory.setStatus("Redeemed");
					pointsHistory.setTotalPoints(points);
					pointsHistory.setArchive("No");
					pointsHistoryRepository.save(pointsHistory);
					
					points = 0;
					
				} else if (pointsEarned.get(i).getPointsEarned() < points) {
					points = points - pointsEarned.get(i).getPointsEarned();
					PointsHistory pointsHistory = new PointsHistory();
					
					pointsHistory.setDateTime(dateTime);
					pointsHistory.setPointsEarned(pointsEarned.get(i));
					pointsHistory.setStatus("Redeemed");
					pointsHistory.setTotalPoints(pointsEarned.get(i).getPointsEarned());
					pointsHistory.setArchive("No");
					pointsHistoryRepository.save(pointsHistory);

					pointsEarned.get(i).setPointsEarned(0);
					pointsEarnedRepository.save(pointsEarned.get(i));
					
				}
			}
		}
		// deduct membership points
		double totalpointsDeducted = totalpoints - reward.getPoints();
		double newtotalpointsDeducted = (double) Math.round(totalpointsDeducted * 100d) / 100d;
		account.setBalance_points(newtotalpointsDeducted);
		accountRepository.save(account);
		
		// Redeem Voucher
		Voucher voucher = new Voucher();
		voucher.setAccount(account);
		voucher.setVoucher_value(reward.getValue());
		voucher.setStoreName(reward.getStoreName());
		voucher.setExpiryDate(expirydate);
		voucher.setStatus("Available");
		voucher.setArchive("No");
		voucherRepository.save(voucher);


		// Notifications
		Notifications notifications = new Notifications();
		notifications.setAccount(account);
		notifications.setDateTime(dateTime);
		notifications.setTitle("Successfully Redeemed Voucher!");

		notifications.setMessage("You have successfully redeemed $" + reward.getValue() + " "
				+ reward.getStoreName().toUpperCase() + " voucher. You are now left with " + newtotalpointsDeducted +" points. The expiry date of your voucher will be on " + expirydate + ". Please use your voucher before the expiry date.");

		notificationsRepository.save(notifications);
		
		model.addAttribute("voucher", voucher);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "redeem_success";
	}
	
}
