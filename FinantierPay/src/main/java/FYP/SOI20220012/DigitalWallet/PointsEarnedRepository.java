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
* Date created: 2022-Jul-08 8:48:22 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
/**
 * @author 20032049
 *
 */
public interface PointsEarnedRepository extends JpaRepository<PointsEarned, Integer>{

	@Query("SELECT p FROM PointsEarned p WHERE p.account.accountId = ?1" + " AND p.pointsEarned > 0" + " AND p.pointsExpiryDate >= CURRENT_DATE")
	public Page<PointsEarned> findByPointsEarned(int accountId, Pageable pageable);
	
	@Query("SELECT p FROM PointsEarned p WHERE p.account.accountId = ?1" + " AND p.pointsEarned > 0" + " AND p.pointsExpiryDate >= CURRENT_DATE")
	public List<PointsEarned> findByPointsEarned(int accountId);

	public PointsEarned findByAccount_AccountId(int accountId);
	
	public List<PointsEarned> findByPointsExpiryDate(LocalDate expirydate);
}
