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
* Date created: 2022-Jul-14 4:25:10 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author 20032049
 *
 */
@Service
public class NotificationsService {

	@Autowired
	private NotificationsRepository notificationRepository;

	@Autowired
	private AccountRepository accountRepository;
	
	public int unreadNotificiations() {
		// Get currently logged in user
		AccountDetails loggedInAccount = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInAccountId = loggedInAccount.getAccount().getAccountId();
		
		Account account = accountRepository.getById(loggedInAccountId);
		
		List<Notifications> notificationlist = notificationRepository.findByAccount_AccountIdAndIsDeletedOrderByNotificationsIdDesc(loggedInAccountId, false);
		
		int unread = 0; 
		
		for (int i = 0; i < notificationlist.size(); i++) {
			if (notificationlist.get(i).isRead() == false) {
				unread ++;
			}
		}
		return unread;
	}
	
	
}
