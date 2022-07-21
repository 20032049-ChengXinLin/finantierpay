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
* Date created: 2022-Jul-09 10:51:00 am
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 20032049
 *
 */
@Controller
public class VoucherController {

	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	VoucherRepository voucherRepository;
	
	@Autowired
	private NotificationsService notificationsService;
	
	@GetMapping("/voucher")
	public String viewVoucher(Model model, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "page", defaultValue = "5") int page) {
		// Get currently logged in user
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		Account account = accountRepository.getById(loggedInAccountId);
		
		Pageable pageable = PageRequest.of(pageNum - 1, page, Sort.by("voucherId").ascending());
		Page<Voucher> voucher = voucherRepository.findByAccount_AccountIdAndArchive(loggedInAccountId, "No", pageable);

		List<Voucher> voucherList = voucher.getContent();
		
		model.addAttribute("voucherList", voucherList);
		model.addAttribute("totalPage", voucher.getTotalPages());
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("page", page);
		
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);

		return "view_voucher";
	}
}
