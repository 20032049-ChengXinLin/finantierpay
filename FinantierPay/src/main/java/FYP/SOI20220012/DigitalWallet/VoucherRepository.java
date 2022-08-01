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
* Date created: 2022-Jul-04 9:13:32 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author 20032049
 *
 */
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
	

	public List<Voucher> findByAccount_AccountId(int accountid);
	

	public Page<Voucher> findByExpiryDateBetweenAndAccount_AccountIdAndArchive(LocalDate startdate,
			LocalDate enddate, int accountid, String archive, Pageable pageable);

	public List<Voucher> findByExpiryDate(LocalDate expirydate);
	
	public Page<Voucher> findByExpiryDateBetweenAndAccount_AccountId(LocalDate startdate,
			LocalDate enddate,int accountid, Pageable pageable);
	
	public Voucher findTopByAndAccount_AccountIdOrderByExpiryDateAsc(int accountid);
	
	public Voucher findTopByAndAccount_AccountIdOrderByExpiryDateDesc(int accountid);
	
	public Page<Voucher> findByExpiryDateBetweenAndAccount_AccountIdAndStatus(LocalDate startdate,
			LocalDate enddate,int accountid, String status,Pageable pageable);
	
	public Page<Voucher> findByExpiryDateBetweenAndAccount_AccountIdAndArchiveAndStatus(LocalDate startdate,
			LocalDate enddate,int accountid, String archive, String status, Pageable pageable);
	
	public Page<Voucher> findByExpiryDateBetweenAndAccount_AccountIdAndStoreNameLike(LocalDate startdate,
			LocalDate enddate,int accountid, String storeName, Pageable pageable);

	public Page<Voucher> findByExpiryDateBetweenAndAccount_AccountIdAndArchiveAndStoreNameLike(LocalDate startdate,
			LocalDate enddate,int accountid, String archive, String storeName, Pageable pageable);

	public Page<Voucher> findByExpiryDateBetweenAndAccount_AccountIdAndArchiveAndStatusAndStoreNameLike(LocalDate startdate,
			LocalDate enddate,int accountid, String archive, String status, String storeName, Pageable pageable);

	public Page<Voucher> findByAccount_AccountIdAndArchive(int loggedInAccountId, String archive, Pageable pageable);

}
