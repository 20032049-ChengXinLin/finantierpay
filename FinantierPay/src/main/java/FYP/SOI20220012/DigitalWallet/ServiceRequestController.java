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
* Date created: 2022-Jul-10 12:25:35 am
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
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
 * @author 20008303
 *
 */

@Controller
public class ServiceRequestController {

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private NotificationsService notificationsService;

	@GetMapping("/request_help")
	public String sendServiceRequests(Model model) {

		model.addAttribute("ServiceRequest", new ServiceRequest());
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "help";
	}

	@GetMapping("/help")
	public String viewServiceRequests(Model model, @RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "startMonthYear", defaultValue = "null") String startMonthYear,
			@RequestParam(value = "endMonthYear", defaultValue = "null") String endMonthYear,
			@RequestParam(value = "status", defaultValue = "null") String status,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "page", defaultValue = "10") int page) {

		LocalDate currentDate = LocalDate.now();
		ServiceRequest firstDateTime = serviceRequestRepository.findTopByOrderByDateTimeAsc();
		LocalDate firstServiceRequestDateTime = firstDateTime.getDateTime().toLocalDate();

		DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyy-MM", Locale.ENGLISH);
		String startMonth = "";
		if (firstDateTime == null) {
			startMonth = currentDate.format(dateformatter);
		} else {
			startMonth = firstServiceRequestDateTime.format(dateformatter);
		}
		String endMonth = currentDate.format(dateformatter);
		if (startMonthYear.equals("null") && endMonthYear.equals("null")) {
			startMonthYear = startMonth;
			endMonthYear = endMonth;
		} else if (startMonthYear.equals("null") && !endMonthYear.equals("null")) {
			startMonthYear = startMonth;
		} else if (!startMonthYear.equals("null") && endMonthYear.equals("null")) {
			endMonthYear = endMonth;
		}

		Page<ServiceRequest> serviceRequest = null;
		Pageable pageable = PageRequest.of(pageNum - 1, page, Sort.by("serviceRequestId").descending());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate start = LocalDate.parse(startMonthYear + "-01", formatter);
		LocalDate end = LocalDate.parse(endMonthYear + "-01", formatter);
		LocalDateTime startdateTime = LocalDateTime.of(start.getYear(), start.getMonthValue(), 1, 0, 0, 0);
		System.out.println(startdateTime);
		LocalDateTime enddateTime = LocalDateTime.of(end.getYear(), end.getMonthValue(), end.lengthOfMonth(), 0, 0, 0);
		System.out.println(enddateTime);

		if (status.equals("null") && keyword.equals("")) {
			serviceRequest = serviceRequestRepository.findAllByDateTimeBetweenOrderByDateTimeDesc(startdateTime,
					enddateTime, pageable);

		} else if (!status.equals("null") && keyword.equals("")) {
			serviceRequest = serviceRequestRepository.findAllByStatusAndDateTimeBetweenOrderByDateTimeDesc(status,
					startdateTime, enddateTime, pageable);

		} else if (status.equals("null") && !keyword.equals("")) {
			serviceRequest = serviceRequestRepository.findAllBetweenOrderByDateTimeDesc(keyword, startdateTime,
					enddateTime, pageable);

		} else if (!status.equals("null") && !keyword.equals("")) {
			serviceRequest = serviceRequestRepository.findAllByStatusAndKeywordAndDateTimeBetweenOrderByDateTimeDesc(
					keyword, status, startdateTime, enddateTime, pageable);

		}

		List<ServiceRequest> serviceRequestList = serviceRequest.getContent();
		model.addAttribute("serviceRequestList", serviceRequestList);

		model.addAttribute("startMonthYear", startMonthYear);
		model.addAttribute("endMonthYear", endMonthYear);
		model.addAttribute("status", status);

		if (!keyword.equals("")) {
			model.addAttribute("keyword", keyword);
		}

		model.addAttribute("totalPage", serviceRequest.getTotalPages());
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("page", page);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "view_service_requests";
	}

	@GetMapping("/help/edit/{id}")
	public String updateHelp(@PathVariable("id") int id, Model model) {
		ServiceRequest serviceRequest = serviceRequestRepository.getById(id);
		model.addAttribute("serviceRequest", serviceRequest);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "update_help";
	}

	@PostMapping("/help/edit/{id}")
	public String saveHelp(@PathVariable("id") int id, @RequestParam("status") String status,
			RedirectAttributes redirectAttributes) {
		ServiceRequest serviceRequest = serviceRequestRepository.getById(id);
		serviceRequest.setStatus(status);
		serviceRequestRepository.save(serviceRequest);
		redirectAttributes.addFlashAttribute("Success", "Service Request Id: " + id + " successfully updated.");
		return "redirect:/help";
	}

	@GetMapping("/help/delete/{id}")
	public String deleteTransactionRecords(@PathVariable("id") int id) {

		serviceRequestRepository.deleteById(id);

		return "redirect:/help";
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

	@PostMapping("/process_help")
	public String processHelp(@Valid ServiceRequest ServiceRequest, BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			return "help";
		}

		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();

		Account account = accountRepository.getById(loggedInAccountId);

		LocalDateTime currentDateTime = LocalDateTime.now();

		ServiceRequest.setDateTime(currentDateTime);
		ServiceRequest.setAccount(account);
		ServiceRequest.setStatus("In progress");
		serviceRequestRepository.save(ServiceRequest);

		Notifications notifications = new Notifications();
		notifications.setAccount(account);
		notifications.setDateTime(currentDateTime);
		notifications.setTitle("Help Message Sent!");

		notifications.setMessage("You have sent a help message regarding to the " + ServiceRequest.getTitle() + "."
				+ "It will take 3 to 5 working days to response, through your email, " + account.getEmail() + ".");

		notificationsRepository.save(notifications);

		String subject = "You have sent a Help message!";
		String body = "Dear " + account.getUsername()
				+ ",\n\nIt will take 3 to 5 working days to response on your feedback/question regarding to the "
				+ ServiceRequest.getTitle() + ".\nThank you for your patience.\n\nBest Regards, \nFinantierPay Help";
		String to = account.getEmail();
		sendEmail(to, subject, body);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		return "help_success";
	}

}
