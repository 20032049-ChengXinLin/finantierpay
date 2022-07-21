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
* Date created: 2022-May-05 12:30:59 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author 20032049
 *
 */
public interface AccountRepository extends JpaRepository<Account, Integer> {

	public Account findByUsername(String username);

	@Query("SELECT a FROM Account a WHERE a.username = ?1 and a.role='ROLE_USER'")
	public Account findByUser(String username);

	@Query("SELECT a FROM Account a WHERE a.email = ?1")

	// check user’s email when starts to use forgot password function
	public Account findByEmail(String email);

	// validate token when user clicks change password link in email
	public Account findByResetPasswordToken(String token);

	// Verification code when the user sign up account
	@Query("SELECT a FROM Account a WHERE a.verificationCode = ?1")
	public Account findByVerificationCode(String code);

	public List<Account> findByRole(String role);

	// Search Users
	@Query("SELECT a FROM Account a WHERE CONCAT(a.username, ' ') LIKE %?1% and a.role= 'ROLE_USER' ")
	public List<Account> findByUserLikeAndRole(String username);

	@Query("SELECT a FROM Account a WHERE a.membership_levels = ?1 and a.role= 'ROLE_USER' ")
	public List<Account> findByMembershipLevels(String membershiplevels);

	// Search Merchant
	@Query("SELECT a FROM Account a WHERE CONCAT(a.username, ' ') LIKE %?1% and a.role= 'ROLE_MERCHANT' ")
	public List<Account> findByMerchantLikeAndRole(String username);

	public Account findByAccountIdAndIsLocked(int loggedInAccountId, boolean isLocked);

	public List<Account> findByRoleAndIsLocked(String string, boolean b);

}
