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
* Date created: 2022-May-21 1:39:41 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * @author 20032049
 *
 */
public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {
	
	public List<Notifications> findByAccount_AccountId(int account_id);
	
	public Page<Notifications> findByAccount_AccountIdAndIsDeletedOrderByNotificationsIdDesc(int account_id, boolean isDeleted, Pageable pageable);

	public List<Notifications> findByAccount_AccountIdAndIsDeletedOrderByNotificationsIdDesc(int account_id, boolean isDeleted);
	
	public List<Notifications> findByAccount_AccountIdAndIsReadAndIsDeletedOrderByNotificationsIdDesc(int account_id, boolean isRead, boolean isDeleted);
}
