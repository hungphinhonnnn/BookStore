package an.ph69924.bansach.models;

import java.util.List;

public class CoinResponse {
    private int coinBalance;
    private String error;
    private List<CoinTransaction> transactions;
    private CoinTransaction transaction;
    private User user;

    public int getCoinBalance() { return coinBalance; }
    public void setCoinBalance(int coinBalance) { this.coinBalance = coinBalance; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public List<CoinTransaction> getTransactions() { return transactions; }
    public void setTransactions(List<CoinTransaction> transactions) { this.transactions = transactions; }
    public CoinTransaction getTransaction() { return transaction; }
    public void setTransaction(CoinTransaction transaction) { this.transaction = transaction; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
