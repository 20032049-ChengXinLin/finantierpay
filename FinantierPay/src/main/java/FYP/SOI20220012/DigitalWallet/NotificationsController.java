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
* Date created: 2022-May-21 2:50:23 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * @author 20032049
 *
 */
@Controller
public class NotificationsController {
	
	@Autowired
	private NotificationsRepository notificationsRepository;
	
	@Autowired
	private NotificationsService notificationsService;

	@GetMapping("/notifications")
	public String notification(Model model, @Param("sortField") String sortField, @Param("sortDir") String sortDir,@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "page", defaultValue = "5") int page) {
		// Get currently logged in user
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Pageable pageable = PageRequest.of(pageNum - 1, page, Sort.by("notificationsId").descending());
		Page<Notifications> notifications = notificationsRepository.findByAccount_AccountIdAndIsDeletedOrderByNotificationsIdDesc(loggedInAccountId, false, pageable);
		List<Notifications> notificationsList = notifications.getContent();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		
		List<String> DateTime = new ArrayList<String>();
		
		for (int i = 0; i < notificationsList.size(); i++) {
			notificationsList.get(i).setRead(true);
			notificationsRepository.save(notificationsList.get(i));
			LocalDateTime datetime = notificationsList.get(i).getDateTime();
			String replace = "";
			if (datetime != null) {
				String strDate = formatter.format(datetime);
				LocalDateTime date = LocalDateTime.parse(strDate, formatter);
				replace = date.toString().replace("T", " ");
				DateTime.add(replace);
			}
			model.addAttribute("DateTime", DateTime);
		}

		model.addAttribute("notificationsList", notificationsList);
		model.addAttribute("size", notificationsList.size());
		model.addAttribute("totalPage", notifications.getTotalPages());
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("page", page);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		
		return "notifications";
	}

	@GetMapping("/notifications/read/{id}")
	public String readNotifications(@PathVariable("id") Integer id, Model model) {
		
		Notifications notification = notificationsRepository.getById(id);
		notification.setRead(true);
		notificationsRepository.save(notification);
		
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		
		return "redirect:/";
		
	}
	
	@GetMapping("/notifications/delete/{id}")
	public String deleteNotification(@PathVariable("id") Integer id, Model model) {
		Notifications notifications = notificationsRepository.getById(id);
		notifications.setDeleted(true);
		notificationsRepository.save(notifications);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		
		return "redirect:/notifications";
	}

	@GetMapping("/notifications/delete")
	public String deleteAllNotification(Model model) {
		// Get currently logged in user
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
						.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		List<Notifications> notificationsList = notificationsRepository.findByAccount_AccountId(loggedInAccountId);
		for (int i = 0; i < notificationsList.size(); i++) {
			int notificationsId = notificationsList.get(i).getNotificationsId();	
			notificationsList.get(i).setDeleted(true);
			notificationsRepository.save(notificationsList.get(i));
		}
		
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		
		return "redirect:/notifications";
	}
}
