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
* Date created: 2022-Jul-13 12:12:07 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 20008303
 *
 */

@Controller
public class ReportController {
	
	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private WalletRepository walletRepository;
	
	@GetMapping("/reports")
	public String reports(Model model) {
		int numOfUsers = 0;
		int numOfMerchants = 0;
		
		int count1 = 0;
		int count2to4 = 0;
		int count5nMore = 0;
		
		int rookieCount = 0;
		int bronzeCount = 0;
		int silverCount = 0;
		int goldCount = 0;
		
		List<Account> accountList = accountRepository.findAll();
		List<Integer> allUserwallet = new ArrayList<>();
		List<Integer> allUsermembership = new ArrayList<>();
		
		for (int i=1; i<= accountList.size(); i++) {
			if (accountList.get(i-1).getRole().equalsIgnoreCase("ROLE_USER")) {
				List<Wallet> walletList = walletRepository.findByAccount_AccountIdAndIsDeleted(i, false);

				if (walletList.size() == 1) {
					count1++;
				} else if (walletList.size() >= 2 && walletList.size() <= 4) {
					count2to4++;
				} else if (walletList.size() >= 5) {
					count5nMore++;
				}
				
				numOfUsers ++;
				
				if (accountList.get(i-1).getMembership_levels().equalsIgnoreCase("Rookie")) {
					rookieCount++;
				} else if (accountList.get(i-1).getMembership_levels().equalsIgnoreCase("Bronze")) {
					bronzeCount++;
				} else if (accountList.get(i-1).getMembership_levels().equalsIgnoreCase("Silver")) {
					silverCount++;
				} else if (accountList.get(i-1).getMembership_levels().equalsIgnoreCase("Gold")) {
					goldCount++;
				}
				
	
			} else if (accountRepository.getById(i).getRole().equalsIgnoreCase("ROLE_MERCHANT")) {
				numOfMerchants ++;
			}			
		}
		
		allUserwallet.add(0, count1);
		allUserwallet.add(1, count2to4);
		allUserwallet.add(2, count5nMore);
		
		allUsermembership.add(0, rookieCount);
		allUsermembership.add(1, bronzeCount);
		allUsermembership.add(2, silverCount);
		allUsermembership.add(3, goldCount);
		
		model.addAttribute("numOfUsers", numOfUsers);
		model.addAttribute("numOfMerchants", numOfMerchants);
		
		model.addAttribute("allUserwallet", allUserwallet);
		
		model.addAttribute("allUsermembership", allUsermembership);
		
		return "view_reports";
	}

}
