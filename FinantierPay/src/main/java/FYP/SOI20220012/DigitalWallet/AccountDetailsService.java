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
* Date created: 2022-May-05 12:30:26 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author 20032049
 *
 */
public class AccountDetailsService implements UserDetailsService{

	@Autowired
	private AccountRepository accountRepository;
	
	@Override
	public AccountDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountRepository.findByUsername(username);
		
		if (account == null) {
			throw new UsernameNotFoundException("Could not find user");
		} 
		return new AccountDetails(account);
	}

	public void updateResetPasswordToken(String token, String email) throws AccountNotFoundException {
    	Account account = accountRepository.findByEmail(email);
        if (account != null) {
        	account.setResetPasswordToken(token);
        	accountRepository.save(account);
        } else {
            throw new AccountNotFoundException("Could not find any account with the email " + email);
        }
    }
     
    public Account getByResetPasswordToken(String token) {
        return accountRepository.findByResetPasswordToken(token);
    }
     
    public void updatePassword(Account account, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        account.setPassword(encodedPassword);
         
        account.setResetPasswordToken(null);
        accountRepository.save(account);
    }
}

