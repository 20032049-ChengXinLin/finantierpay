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
* Date created: 2022-May-28 11:32:47 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author 20032049
 *
 */
@Controller
public class MerchantsController {
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private NotificationsService notificationsService;
	
	@GetMapping("/merchants")
	public String ViewMerchant(Model model, @Param("keyword") String keyword) {
		if (keyword == null) {
			List<Account> listMerchants = accountRepository.findByRole("ROLE_MERCHANT");
			model.addAttribute("listMerchants", listMerchants);
		} else {
			List<Account> listMerchants = accountRepository.findByMerchantLikeAndRole(keyword);
			model.addAttribute("listMerchants", listMerchants);
		}
		
		model.addAttribute("keyword", keyword);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		
		return "view_merchants";
	}
	
	@GetMapping("/merchant/edit/{id}")
	public String editMerchant(@PathVariable("id") Integer id, Model model) {

		Account Account = accountRepository.getById(id);
		model.addAttribute("Account", Account);
		List<Country> countryList = countryRepository.findAll();
		model.addAttribute("countryList", countryList);
		return "edit_merchant";
	}

	@PostMapping("/merchant/edit/{id}")
	public String saveMerchant(@PathVariable("id") Integer id, Account Account, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {

		Account account = accountRepository.getById(id);

		String phone = "";
		String postal = "";
		if (account.getCountry().getCountryId() == 1) {
			String regexPhone = "^$|^[689]\\d{7}$";
			boolean patternPhone = account.getPhone().matches(regexPhone);
			if (patternPhone == false) {
				phone = "Mobile Number Wrong Format!";
			}
			String regexPostal = "^$|^\\d{6}$";
			boolean patternPostal = account.getPostal_code().matches(regexPostal);
			if (patternPostal == false) {
				postal = "Postal Code Wrong Format!";
			}
		} else if (account.getCountry().getCountryId() == 2) {
			String regexPhone = "^(\\+?6?01)[02-46-9][-][0-9]{7}$|^(\\+?6?01)[1][-][0-9]{8}$";
			boolean patternPhone = account.getPhone().matches(regexPhone);
			if (patternPhone == false) {
				phone = "Mobile Number Wrong Format!";
			}
			String regexPostal = "^$|^\\d{5}$";
			boolean patternPostal = account.getPostal_code().matches(regexPostal);
			if (patternPostal == false) {
				postal = "Postal Code Wrong Format!";
			}
		} else if (account.getCountry().getCountryId() == 3) {
			String regexPhone = "^(08)(\\d{3,4}-?){2}\\d{2,3}$";
			boolean patternPhone = account.getPhone().matches(regexPhone);
			if (patternPhone == false) {
				phone = "Mobile Number Wrong Format!";
			}
			String regexPostal = "^$|^\\d{5}$";
			boolean patternPostal = account.getPostal_code().matches(regexPostal);
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
			return "edit_merchant";
		} else {
			account.setFirst_name(Account.getFirst_name());
			account.setLast_name(Account.getLast_name());
			account.setCountry(Account.getCountry());
			account.setDate_of_birth(Account.getDate_of_birth());
			account.setAddress(Account.getAddress());
			account.setPostal_code(Account.getPostal_code());
			account.setPhone(Account.getPhone());
			account.setStoreName(Account.getStoreName());
			account.setEmail(Account.getEmail());
			accountRepository.save(account);
		}
		redirectAttributes.addFlashAttribute("editSuccess", "Account ID: " + id + " successfully updated.");

		return "redirect:/merchants";
	}
	
	
	@GetMapping("/merchant/{id}/wallets")
	public String viewUserWallets(@PathVariable("id") Integer id, Model model) {
		List<Wallet> listMerchantWallets = walletRepository.findByAccount_AccountIdAndIsDeleted(id, false);
		model.addAttribute("listMerchantWallets", listMerchantWallets);
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		
		return "view_merchant_wallets";
	}
	
	@GetMapping("/guidelines")
	public String guidelines(Model model) {
		int unread = notificationsService.unreadNotificiations();
		model.addAttribute("unread", unread);
		
		return "merchant_guidelines";
	}
}
