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
* Date created: 2022-May-23 6:25:07 pm
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
public interface TransactionRepository extends JpaRepository<Transaction, String> {
	// Admin and Staff
	// Transaction Records DateTime Descending Order
	public List<Transaction> findAllByOrderByDateTimeDesc();

	public Page<Transaction> findAllByOrderByDateTimeDesc(Pageable Pageable);

//	// Search For Keyword - Transaction Records
//	@Query("SELECT t FROM Transaction t WHERE CONCAT(t.transactionId, ' ', t.wallet.walletId,' ', t.dateTime, ' ', t.activity, ' ',t.amount, ' ' ,t.voucher_value, ' ', t.status, ' ', t.description, ' ', t.archive, ' ', t.points_earned) LIKE %?1%")
//	public Page<Transaction> findAllByOrderByDateTimeDescAndSearch(String keyword, Pageable Pageable);
//
//	// Select Month - Transaction Records
//	@Query("select t from Transaction t WHERE YEAR(t.dateTime) = ?1 and MONTH(t.dateTime) = ?2")
//	public Page<Transaction> findAllByDateTimeBetweenOrderByDateTimeDesc(Integer year, Integer month,
//			Pageable Pageable);
//
//	// Select Month and Search For Keyword - Transaction Records
//	@Query("select t from Transaction t WHERE CONCAT(t.transactionId, ' ', t.wallet.walletId,' ', t.dateTime, ' ', t.activity, ' ',t.amount, ' ',t.voucher_value, ' ', t.status, ' ', t.description, ' ', t.archive, ' ', t.points_earned) LIKE %?1% "
//			+ " AND YEAR(t.dateTime) = ?2 and MONTH(t.dateTime) = ?3")
//	public Page<Transaction> SearchByDateTimeBetweenOrderByDateTimeDesc(String keyword, Integer year, Integer month,
//			Pageable Pageable);

	// User and Merchant
	// Transaction History DateTime Descending Order - All Wallets Based on Account
	public List<Transaction> findByWallet_Account_AccountIdAndIsDeletedAndArchiveOrderByDateTimeDesc(int accountId, boolean isDeleted, String archive);

	public Page<Transaction> findByWallet_Account_AccountIdAndIsDeletedAndArchiveOrderByDateTimeDesc(int accountId, String archive, boolean isDeleted,
			Pageable pageable);

//	// Search For Keyword - Transaction History (Account)
//	@Query("SELECT t FROM Transaction t WHERE t.wallet.account.accountId = ?1" + " AND t.archive = ?2"
//			+ " AND CONCAT(t.transactionId, ' ', t.wallet.walletId,' ', t.dateTime, ' ', t.activity, ' ',t.amount, ' ',t.voucher_value, ' ', t.status, ' ', t.description, ' ', t.archive, ' ', t.points_earned) LIKE %?3%")
//	public Page<Transaction> SearchWallet_Account_AccountIdAndArchiveOrderByDateTimeDesc(int accountId, String archive,
//			String keyword, Pageable Pageable);
//
//	// Select Month - Transaction History (Account)
//	@Query("select t from Transaction t WHERE t.wallet.account.accountId = ?1 AND t.archive =?2 AND YEAR(t.dateTime) = ?3 and MONTH(t.dateTime) = ?4")
//	public Page<Transaction> findByWallet_Account_AccountIdAndArchiveAndDateTimeBetweenOrderByDateTimeDesc(
//			int accountid, String archive, Integer year, Integer month, Pageable pageable);
//
//	// Select Month and Search For Keyword - Transaction History (Account)
//	@Query("select t from Transaction t WHERE t.wallet.account.accountId = ?1"
//			+ " AND CONCAT(t.transactionId, ' ', t.wallet.walletId,' ', t.dateTime, ' ', t.activity, ' ',t.amount, ' ', t.voucher_value, ' ', t.status, ' ',  t.description, ' ', t.archive, ' ', t.points_earned) LIKE %?2% "
//			+ " AND YEAR(t.dateTime) = ?3 and MONTH(t.dateTime) = ?4" + " AND t.archive = ?5")
//	public Page<Transaction> SearchWallet_Account_AccountIdAndArchiveByDateTimeBetweenOrderByDateTimeDesc(int accountid,
//			String keyword, Integer year, Integer month, String archive, Pageable Pageable);
//
	// Transaction History DateTime Descending Order - Individual Wallet
	public List<Transaction> findByWallet_WalletIdAndIsDeletedOrderByDateTimeDesc(int walletId, boolean isDeleted);

	public Page<Transaction> findByWallet_WalletIdAndIsDeletedAndArchiveOrderByDateTimeDesc(int walletId, boolean isDeleted, String archive,
			Pageable pageable);

	public List<Transaction> findByWallet_WalletIdAndIsDeletedAndArchiveOrderByDateTimeDesc(int walletId, boolean isDeleted, String archive);
//
//	// Search For Keyword - Transaction History (Individual Wallet)
//	@Query("SELECT t FROM Transaction t WHERE t.wallet.walletId = ?1" + " AND t.archive= ?2"
//			+ " AND CONCAT(t.transactionId, ' ', t.dateTime, ' ', t.activity, ' ',t.amount, ' ',t.voucher_value, ' ', t.status, ' ',  t.description, ' ', t.archive, ' ',t.points_earned) LIKE %?3%")
//	public Page<Transaction> SearchWallet_WalletIdAndArchiveOrderByDateTimeDesc(int walletId, String archive,
//			String keyword, Pageable pageable);
//
//	// Select Month - Transaction History (Individual Wallet)
//	@Query("select t from Transaction t WHERE t.wallet.walletId = ?1 AND t.archive=?2 AND YEAR(t.dateTime) = ?3 and MONTH(t.dateTime) = ?4")
//	public Page<Transaction> findByWallet_WalletIdAndArchiveAndDateTimeBetweenOrderByDateTimeDesc(int walletid,
//			String archive, Integer year, Integer month, Pageable pageable);
//
//	// Select Month and Search For Keyword - Transaction History (Individual Wallet)
//	@Query("select t from Transaction t WHERE t.wallet.walletId = ?1"
//			+ " AND CONCAT(t.transactionId, ' ', t.wallet.walletId,' ', t.dateTime, ' ', t.activity, ' ',t.amount, ' ', t.voucher_value, ' ', t.description, ' ', t.status, ' ', t.archive, ' ', t.points_earned) LIKE %?2% "
//			+ " AND YEAR(t.dateTime) = ?3 and MONTH(t.dateTime) = ?4" + " AND t.archive = ?5")
//	public Page<Transaction> SearchWallet_WalletIdAndArchiveByDateTimeBetweenOrderByDateTimeDesc(int walletid,
//			String keyword, Integer year, Integer month, String archive, Pageable Pageable);
//
////	
//	@Query("SELECT t FROM Transaction t WHERE t.wallet.walletId = ?1" + " AND t.dateTime = CURRENT_DATE")
//	public List<Transaction> findByWallet_WalletIdAndDateTime(int walletId);

	// Membership
	public Transaction findTopByWallet_Account_AccountIdAndActivityAndStatusOrderByDateTimeDesc(int accountId,
			String activity, String status);

	// Transaction - All
	public Transaction findTopByOrderByDateTimeAsc();

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedOrderByDateTimeDesc(LocalDateTime startdate,
			LocalDateTime enddate, boolean isDeleted, Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndActivityOrderByDateTimeDesc(LocalDateTime startdate,
			LocalDateTime enddate, boolean isDeleted, String activity, Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndStatusOrderByDateTimeDesc(LocalDateTime startdate,
			LocalDateTime enddate, boolean isDeleted, String status, Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndStatusAndActivityOrderByDateTimeDesc(
			LocalDateTime startdate, LocalDateTime enddate, boolean isDeleted, String status, String activity,
			Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndDescriptionLikeOrderByDateTimeDesc(
			LocalDateTime startdate, LocalDateTime enddate, boolean isDeleted, String description, Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndActivityAndStatusAndDescriptionLikeOrderByDateTimeDesc(
			LocalDateTime startdate, LocalDateTime enddate, boolean isDeleted, String activity, String status,
			String description, Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndActivityAndDescriptionLikeOrderByDateTimeDesc(
			LocalDateTime startdate, LocalDateTime enddate, boolean isDeleted, String activity, String description,
			Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndActivityAndArchiveOrderByDateTimeDesc(
			LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String activity, String archive,
			Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndStatusAndDescriptionLikeOrderByDateTimeDesc(
			LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String status, String keyword,
			Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndStatusAndArchiveOrderByDateTimeDesc(
			LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String status, String archive,
			Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
			LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String keyword, String archive,
			Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndActivityAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
			LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String activity, String keyword,
			String archive, Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndStatusAndActivityAndArchiveOrderByDateTimeDesc(
			LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String status, String activity,
			String archive, Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndStatusAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
			LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String status, String keyword,
			String archive, Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndStatusAndActivityAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
			LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String status, String activity,
			String keyword, String archive, Pageable pageable);

	// Transaction - By Wallet (For Staff and Admin)
	// Transaction - All
	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedOrderByDateTimeDesc(int walletId, LocalDateTime startdate,
			LocalDateTime enddate, boolean isDeleted, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityOrderByDateTimeDesc(int walletId, LocalDateTime startdate,
			LocalDateTime enddate, boolean isDeleted, String activity, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusOrderByDateTimeDesc(int walletId, LocalDateTime startdate,
			LocalDateTime enddate, boolean isDeleted, String status, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndActivityOrderByDateTimeDesc(
			int walletId, LocalDateTime startdate, LocalDateTime enddate, boolean isDeleted, String status, String activity,
			Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndDescriptionLikeOrderByDateTimeDesc(
			int walletId, LocalDateTime startdate, LocalDateTime enddate, boolean isDeleted, String description, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityAndStatusAndDescriptionLikeOrderByDateTimeDesc(
			int walletId, LocalDateTime startdate, LocalDateTime enddate, boolean isDeleted, String activity, String status,
			String description, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityAndDescriptionLikeOrderByDateTimeDesc(
			int walletId, LocalDateTime startdate, LocalDateTime enddate, boolean isDeleted, String activity, String description,
			Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityAndArchiveOrderByDateTimeDesc(
			int walletId, LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String activity, String archive,
			Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndDescriptionLikeOrderByDateTimeDesc(
			int walletId, LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String status, String keyword,
			Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndArchiveOrderByDateTimeDesc(
			int walletId, LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String status, String archive,
			Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
			int walletId, LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String keyword, String archive,
			Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndActivityAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
			int walletId, LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String activity, String keyword,
			String archive, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndActivityAndArchiveOrderByDateTimeDesc(
			int walletId, LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String status, String activity,
			String archive, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
			int walletId, LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String status, String keyword,
			String archive, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndStatusAndActivityAndDescriptionLikeAndArchiveOrderByDateTimeDesc(
			int walletId, LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String status, String activity,
			String keyword, String archive, Pageable pageable);
	

	public Page<Transaction> findByWallet_WalletIdAndDateTimeBetweenAndIsDeletedAndArchiveOrderByDateTimeDesc(
			Integer id, LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String archive,
			Pageable pageable);
	
	// Transaction - By Account
	public Page<Transaction> findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenOrderByDateTimeDesc(
			int accountId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate,
			Pageable pageable);

	public Page<Transaction> findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndActivityOrderByDateTimeDesc(
			int accountId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate,
			String activity, Pageable pageable);

	public Page<Transaction> findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndStatusOrderByDateTimeDesc(
			int accountId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate,
			String status, Pageable pageable);

	public Page<Transaction> findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndStatusAndActivityOrderByDateTimeDesc(
			int accountId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate,
			String status, String activity, Pageable pageable);

	public Page<Transaction> findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndDescriptionLikeOrderByDateTimeDesc(
			int accountId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate,
			String description, Pageable pageable);

	public Page<Transaction> findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndActivityAndStatusAndDescriptionLikeOrderByDateTimeDesc(
			int accountId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate,
			String activity, String status, String description, Pageable pageable);

	public Page<Transaction> findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndActivityAndDescriptionLikeOrderByDateTimeDesc(
			int accountId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate,
			String activity, String description, Pageable pageable);

	public Page<Transaction> findByWallet_Account_AccountIdAndIsDeletedAndArchiveAndDateTimeBetweenAndStatusAndDescriptionLikeOrderByDateTimeDesc(
			int accountId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate,
			String status, String description, Pageable pageable);

	public Page<Transaction> findAllByDateTimeBetweenAndIsDeletedAndArchiveOrderByDateTimeDesc(
			LocalDateTime startdateTime, LocalDateTime enddateTime, boolean isDeleted, String archive,
			Pageable pageable);

	// Transaction - By Wallet
	public Page<Transaction> findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenOrderByDateTimeDesc(
			int walletId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate,
			Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndActivityOrderByDateTimeDesc(
			int walletId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate,
			String activity, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndStatusOrderByDateTimeDesc(
			int walletId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate,
			String status, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndStatusAndActivityOrderByDateTimeDesc(
			int walletId,  boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate, String status,
			String activity, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndDescriptionLikeOrderByDateTimeDesc(
			int walletId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate, String description,
			Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndActivityAndStatusAndDescriptionLikeOrderByDateTimeDesc(
			int walletId,  boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate, String activity,
			String status, String description, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndActivityAndDescriptionLikeOrderByDateTimeDesc(
			int walletId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate, String activity,
			String description, Pageable pageable);

	public Page<Transaction> findByWallet_WalletIdAndIsDeletedAndArchiveAndDateTimeBetweenAndStatusAndDescriptionLikeOrderByDateTimeDesc(
			int walletId, boolean isDeleted, String archive, LocalDateTime startdate, LocalDateTime enddate, String status,
			String description, Pageable pageable);

}
