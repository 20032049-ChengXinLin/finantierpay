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
* Date created: 2022-May-22 12:38:06 am
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 20008303
 *
 */
public interface WalletRepository extends JpaRepository<Wallet, Integer> {

	public Wallet findByWalletIdAndAccount_AccountIdAndIsDeleted(int walletId, int accountId, boolean isDeleted);
	
	public List<Wallet> findByAccount_RoleAndIsDeleted(String role, boolean isDeleted);
	
	public List<Wallet> findByAccount_AccountIdAndIsDeleted(int accountid, boolean isDeleted);
	
	public List<Wallet> findByAccount_AccountId(int accountid);
}
