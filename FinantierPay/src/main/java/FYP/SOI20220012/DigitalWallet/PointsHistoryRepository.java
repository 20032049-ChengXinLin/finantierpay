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

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 20032049
 *
 */
public interface PointsHistoryRepository extends JpaRepository<PointsHistory, Integer>{

	public Page<PointsHistory> findByDateTimeBetweenAndPointsEarned_Account_AccountIdAndArchiveOrderByDateTimeDesc(LocalDateTime startdate,
			LocalDateTime enddate, int AccountId, String archive, Pageable pageable);

	public Page<PointsHistory> findByDateTimeBetweenAndPointsEarned_Account_AccountIdOrderByDateTimeDesc(LocalDateTime startdate,
			LocalDateTime enddate, int AccountId, Pageable pageable);

	public PointsHistory findTopByAndPointsEarned_Account_AccountIdOrderByPointsEarnedAsc(int accountid);
	
	public Page<PointsHistory> findByDateTimeBetweenAndPointsEarned_Account_AccountIdAndArchive(LocalDateTime startdate,
			LocalDateTime enddate, int accountid, String archive,Pageable pageable);
	
	public Page<PointsHistory> findByDateTimeBetweenAndPointsEarned_Account_AccountIdAndStatus(LocalDateTime startdate,
			LocalDateTime enddate, int accountid, String status,Pageable pageable);
	
	public Page<PointsHistory> findByDateTimeBetweenAndPointsEarned_Account_AccountIdAndArchiveAndStatus(LocalDateTime startdate,
			LocalDateTime enddate, int accountid, String archive, String status, Pageable pageable);

	public Page<PointsHistory> findByPointsEarned_Account_AccountIdAndArchiveOrderByDateTimeDesc(int loggedInAccountId,
			String archive, Pageable pageable);
}
