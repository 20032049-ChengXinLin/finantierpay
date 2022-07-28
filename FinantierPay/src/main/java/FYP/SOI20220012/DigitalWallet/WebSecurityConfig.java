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
 * Date created: 2022-May-02 8:46:34 pm 
 * 
 */
package FYP.SOI20220012.DigitalWallet;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * @author 20032049
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{


	@Autowired
	private CustomLogoutSuccessHandler logoutHandler;
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public AccountDetailsService accountDetailsService() {
		return new AccountDetailsService();
	}
	
	@Bean
	public static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepo = new JdbcTokenRepositoryImpl();
		tokenRepo.setDataSource(dataSource);
		return tokenRepo;
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(accountDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
	
	// Manually authenticate user with Spring Security
    @Bean("authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
    }

	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/register", "/verify", "/process_register","/about","/faq").permitAll()
		.antMatchers("/forgot_password", "/reset_password").permitAll()
		.antMatchers("/finantierPayPayment", "/finantierPaylogin", "/finantierPay/login").permitAll()
		.antMatchers("/chooseWalletToPay", "/chooseWalletTransfer", "/chooseWalletToTransfer/process_transfer", "/chooseWalletToTransfer/save", "/chooseWalletToTopUp", "/chooseWalletToTopUp/process_topup", "/walletpin", "/paymentsuccess").hasRole("USER")
		.antMatchers("/transaction_successful").permitAll()
		.antMatchers("/bootstrap/*/*").permitAll()
		.antMatchers("/images/*").permitAll()
		.antMatchers("/jquery/*").permitAll()
		.antMatchers("/uploads/products/*/*", "/images/products/*/*").permitAll()
		.antMatchers("/finantierPay/*").permitAll()
		.antMatchers("/font-awesome/*/*").permitAll()
		.antMatchers("/account", "/uploads/rewards/*/*").hasAnyRole("USER", "MERCHANT", "STAFF", "ADMIN")
		.antMatchers( "/membership", "/membership/points", "/wallet/create", "/wallet/confirmpin","/wallet/*/topup", "/wallet/process_topup", "/wallet/*", "/wallet/*/send", "/wallet/*/confirm", "/wallet/*/success", "/wallet/*/transactionhistory", "/wallet/save", "wallet/*/delete", "/rewards/redeem/*", "/voucher/redeem/*", "/voucher", "/pointshistory", "/wallet/current/*", "/wallet/updatepin/*" , "/wallet/confirmupdatepin/*", "/wallet/saveupdatepin/*", "/account/deleted/*").hasRole("USER")
		.antMatchers("/users", "/merchants", "/transactionrecords", "/user/*/wallets", "/merchant/*/wallets", "/users/edit/*", "/wallet/*/transactionsrecords",  "/reports", "/wallet/*/deleted/transactionrecords", "/help/update/*").hasAnyRole("STAFF", "ADMIN")
		.antMatchers("/notifications","/notifications/delete/*", "/notifications/delete", "/notifications/read/*", "/account/edit/*", "/account/password/edit/*", "/account/delete/*", "/wallet","/transactionhistory", "/account/*/wallet", "/account/*/wallet/*/confirm", "/account/*/wallet/*/success",  "/wallet/withdraw/", "/process_help", "/wallet/*/withdraw", "/wallet/process_withdraw", "deleted/account/*", "/deleted/account/no/*", "/qrcodeReador","/settings", "/request_help", "/proccess_help", "/wallet/*/withdraw", "/wallet/merchant/process_withdraw").hasAnyRole("USER", "MERCHANT")
		.antMatchers("/rewards/add", "/rewards/save").hasAnyRole("MERCHANT", "STAFF", "ADMIN")
		.antMatchers("/guidelines").hasRole("MERCHANT")
		.antMatchers("/", "/wallet/*/updateWalletLimit", "/wallet/*/walletLimit","/rewards").hasAnyRole("MERCHANT", "STAFF", "ADMIN", "USER")
		.antMatchers("/user/account", "/user/voucher/*", "/user/pointhistory/*", "/user/pointearned/*", "/transaction/export/excel", "/help", "/user/wallet/*").hasAnyRole("ADMIN", "STAFF")
		.antMatchers("/user/edit/*", "/merchant/edit/*", "/user/voucher/edit/*", "/user/pointsHistory/edit/*","/transactionrecords/edit/*","/transactionrecords/delete/*", "/user/wallet/edit/", "/help/edit/*", "/help/delete/*", "/merchant/wallet/*", "/merchant/wallet/edit/*").hasRole("ADMIN")
		.anyRequest().authenticated()
		.and()
		.formLogin()
        .loginPage("/login") 
        .defaultSuccessUrl("/", true)
        .permitAll()
		.and()
		.logout().logoutUrl("/logout")
        .logoutSuccessHandler(logoutHandler)
        .permitAll()
		.and()
		.rememberMe()
		.tokenRepository(persistentTokenRepository()).userDetailsService(accountDetailsService()).and()
		.exceptionHandling().accessDeniedPage("/403");
	}
}

