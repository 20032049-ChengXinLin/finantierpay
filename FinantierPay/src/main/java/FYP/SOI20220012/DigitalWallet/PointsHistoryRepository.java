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
* Date created: 2022-Jul-09 2:46:13 pm
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
public interface PointsHistoryRepository extends JpaRepository<PointsHistory, Integer>{

	public Page<PointsHistory> findByPointsEarned_Account_AccountIdAndArchiveOrderByDateTimeDesc(int AccountId, String archive, Pageable pageable);

	public Page<PointsHistory> findByPointsEarned_Account_AccountIdOrderByDateTimeDesc(int AccountId, Pageable pageable);

	public PointsHistory findTopByAndPointsEarned_Account_AccountIdOrderByPointsEarnedAsc(int accountid);
	
	public Page<PointsHistory> findByPointsEarned_Account_AccountIdAndArchive(int accountid, String archive,Pageable pageable);
	
	public Page<PointsHistory> findByPointsEarned_Account_AccountIdAndStatus(int accountid, String status,Pageable pageable);
	
	public Page<PointsHistory> findByPointsEarned_Account_AccountIdAndArchiveAndStatus(int accountid, String archive, String status, Pageable pageable);
}
