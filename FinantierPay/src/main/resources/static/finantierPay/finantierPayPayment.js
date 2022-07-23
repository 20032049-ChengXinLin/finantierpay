var cartTotal = document.getElementById("totalAmt").value;

/* Current URL */
var url = window.location.href;

/* Merchant Wallet ID*/
var link = document.getElementById('finantierPay').src;
var walletIdAndUsername = link.substring(link.lastIndexOf('?') + 1);

/* Total Amount to Pay*/
var totalAmt = document.getElementById('totalAmt').value;
/* FinantierPay Button */
var finantierPayLoginURL = "https://finantierpay.azurewebsites.net/finantierPayPayment?" + walletIdAndUsername + "&totalAmt=" + totalAmt + "&current-URL=" + url;
if (cartTotal == 0) {
	document.getElementById("finantierPayLink").className = "btn btn-success disabled";
	document.getElementById("finantierPayLink").innerHTML = "FinantierPay";
	document.getElementById("finantierPayLink").href = finantierPayLoginURL;
} else {
	document.getElementById("finantierPayLink").className = "btn btn-success";
	document.getElementById("finantierPayLink").innerHTML = "FinantierPay";
	document.getElementById("finantierPayLink").href = finantierPayLoginURL;
}

